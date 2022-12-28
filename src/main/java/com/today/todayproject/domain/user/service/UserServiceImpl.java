package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.crop.repository.CropRepository;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.email.EmailAuth;
import com.today.todayproject.global.email.dto.AuthenticationCodeEmailDto;
import com.today.todayproject.global.email.dto.IssueTempPasswordEmailDto;
import com.today.todayproject.global.email.repository.EmailAuthRepository;
import com.today.todayproject.global.email.service.EmailService;
import com.today.todayproject.global.s3.service.S3UploadService;
import com.today.todayproject.domain.user.dto.UserSignUpRequestDto;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;
    private final EmailService emailService;

    /**
     * 회원 가입 로직
     */
    @Override
    public Long signUp(UserSignUpRequestDto userSignUpRequestDto, MultipartFile profileImg) throws Exception {

        if(userRepository.findByEmail(userSignUpRequestDto.getEmail()).isPresent()) {
            throw new BaseException(BaseResponseStatus.EXIST_EMAIL);
        }

        if(userRepository.findByNickname(userSignUpRequestDto.getNickname()).isPresent()) {
            throw new BaseException(BaseResponseStatus.EXIST_NICKNAME);
        }

        // profile 사진이 있다면, User build 시 profile도 추가
        if(profileImg != null) {
            String profileImgUrl = s3UploadService.uploadFile(profileImg);

            User user = User.builder()
                    .email(userSignUpRequestDto.getEmail())
                    .password(userSignUpRequestDto.getPassword())
                    .nickname(userSignUpRequestDto.getNickname())
                    .profileImgUrl(profileImgUrl)
                    .role(Role.USER)
                    .emailAuth(false)
                    .build();

            user.encodePassword(passwordEncoder);
            User saveUser = userRepository.save(user);
            sendAuthenticationCodeEmail(userSignUpRequestDto, user);
            return saveUser.getId();
        } else {
            // profile 사진이 없다면, User build 시 profile null로 추가
            User user = User.builder()
                    .email(userSignUpRequestDto.getEmail())
                    .password(userSignUpRequestDto.getPassword())
                    .nickname(userSignUpRequestDto.getNickname())
                    .profileImgUrl(null)
                    .role(Role.USER)
                    .emailAuth(false)
                    .build();

            log.info("profileImg : {}", profileImg);
            user.encodePassword(passwordEncoder);
            User saveUser = userRepository.save(user);
            sendAuthenticationCodeEmail(userSignUpRequestDto, user);
            return saveUser.getId();
        }
    }

    private void sendAuthenticationCodeEmail(UserSignUpRequestDto userSignUpRequestDto, User user) throws MessagingException {
        EmailAuth emailAuth = generateEmailAuth(userSignUpRequestDto);
        AuthenticationCodeEmailDto authenticationCodeEmailDto =
                emailService.generateAuthenticationCodeEmailDto(user.getEmail(), emailAuth.getAuthCode());
        emailService.sendAuthenticationCodeEmail(authenticationCodeEmailDto);
    }

    private EmailAuth generateEmailAuth(UserSignUpRequestDto userSignUpRequestDto) {
        return emailAuthRepository.save(
                EmailAuth.builder()
                        .email(userSignUpRequestDto.getEmail())
                        .authCode(getAuthenticationCode())
                        .expired(false)
                        .build());
    }

    private int getAuthenticationCode() {
        return Integer.parseInt(RandomStringUtils.randomNumeric(6));
    }

    @Override
    public void confirmEmailAuthCode(UserEmailAuthCodeDto userEmailAuthCodeDto) throws Exception {
        EmailAuth emailAuth = emailAuthRepository.findValidAuthByEmail(
                        userEmailAuthCodeDto.getEmail(), userEmailAuthCodeDto.getAuthCode(), LocalDateTime.now())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_VALID_AUTH_CODE));
        User findUser = userRepository.findByEmail(userEmailAuthCodeDto.getEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        emailAuth.useAuthCode();
        findUser.emailAuthSuccess();
    }

    /**
     * 회원 정보 수정 로직
     */
    // TODO : Optional의 ifPresent로 로직 변경, 3중 if문 없애기
    @Override
    public void updateUser(UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImg) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        String currentNickname = loginUser.getNickname();
        if (userUpdateRequestDto != null) {
            if(userUpdateRequestDto.getChangeNickname() != null) {
                // 기존 닉네임과 변경할 닉네임이 같을 때 예외 처리
                if (currentNickname.equals(userUpdateRequestDto.getChangeNickname())) {
                    throw new BaseException(BaseResponseStatus.SAME_NICKNAME);
                }
                loginUser.updateNickname(userUpdateRequestDto.getChangeNickname());
            }


            if(userUpdateRequestDto.getChangePassword() != null) {
                // 기존 비밀번호와 변경할 비밀번호가 같을 때 예외 처리
                if (loginUser.matchPassword(passwordEncoder, userUpdateRequestDto.getChangePassword())) {
                    throw new BaseException(BaseResponseStatus.SAME_CURRENT_CHANGE_PASSWORD);
                }
                loginUser.updatePassword(passwordEncoder, userUpdateRequestDto.getChangePassword());
            }
        }

        if(profileImg != null) {
            // 현재 프로필 사진이 기본이라면 S3 삭제 X, 있을 때만 S3에서 삭제
            if(loginUser.getProfileImgUrl() != null) {
                s3UploadService.deleteOriginalFile(loginUser.getProfileImgUrl());
            }
            String changeProfileImgUrl = s3UploadService.uploadFile(profileImg);
            loginUser.updateProfileImgUrl(changeProfileImgUrl);
        }

    }


    /**
     * 회원 탈퇴 로직
     */
    @Override
    public void withdraw(UserWithdrawRequestDto userWithdrawRequestDto) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        boolean isSamePassword = loginUser
                .matchPassword(passwordEncoder, userWithdrawRequestDto.getCurrentPassword());
        if(!isSamePassword) {
            throw new BaseException(BaseResponseStatus.WRONG_CURRENT_PASSWORD);
        }
        String deleteProfileImgUrl = loginUser.getProfileImgUrl();
        s3UploadService.deleteOriginalFile(deleteProfileImgUrl);
        userRepository.delete(loginUser);
    }

    public UserGetPagingDto searchUsers(Pageable pageable, UserSearchDto userSearchDto) {
        Long loginUserId = userSearchDto.getLoginUserId();
        Long lastFriendUserId = userSearchDto.getLastFriendUserId();
        Long lastUserId = userSearchDto.getLastUserId();
        String searchUserNickname = userSearchDto.getSearchUserNickname();
        UserGetFriendUserInfoDto userGetFriendUserInfoDto = new UserGetFriendUserInfoDto();
        UserGetUserInfoDto userGetUserInfoDto = new UserGetUserInfoDto();

        Slice<User> friendUsers = userRepository.searchFriendUserByUserNickname(loginUserId, lastFriendUserId,
                searchUserNickname, pageable);

        if (friendUsers.getContent().size() < pageable.getPageSize()) {
            Slice<User> searchFriendUsers = userRepository.searchFriendUserByUserNickname(loginUserId, lastFriendUserId,
                    searchUserNickname, pageable);

            userGetFriendUserInfoDto = getUserGetFriendUserInfoDto(searchFriendUsers);

            int userPageSize = pageable.getPageSize() - friendUsers.getContent().size();
            Slice<User> searchUsers = userRepository.searchUserByUserNickname(loginUserId, lastUserId,
                    searchUserNickname, userPageSize);

            userGetUserInfoDto = getUserGetUserInfoDto(searchUsers);
        }

        if (friendUsers.getContent().size() >= pageable.getPageSize()) {
            Slice<User> searchFriendUsers = userRepository.searchFriendUserByUserNickname(loginUserId, lastFriendUserId,
                    searchUserNickname, pageable);

            userGetFriendUserInfoDto = getUserGetFriendUserInfoDto(searchFriendUsers);
            userGetUserInfoDto = new UserGetUserInfoDto(Collections.emptyList());
        }

        return new UserGetPagingDto(userGetFriendUserInfoDto, userGetUserInfoDto);
    }

    private UserGetUserInfoDto getUserGetUserInfoDto(Slice<User> searchUsers) {
        List<UserSearchInfoDto> userInfos = searchUsers.stream()
                .map(user -> {
                    log.info("user id : {}", user.getId());
                    int cropCount = cropRepository.countByUserId(user.getId());
                    return new UserSearchInfoDto(user, cropCount);
                }).collect(Collectors.toList());

        UserGetUserInfoDto userGetUserInfoDto =
                new UserGetUserInfoDto(userInfos, searchUsers);
        return userGetUserInfoDto;
    }

    private UserGetFriendUserInfoDto getUserGetFriendUserInfoDto(Slice<User> searchFriendUsers) {
        List<UserSearchInfoDto> friendUserInfos = searchFriendUsers.stream()
                .map(friendUser -> {
                    log.info("friendUser id : {}", friendUser.getId());
                    int cropCount = cropRepository.countByUserId(friendUser.getId());
                    return new UserSearchInfoDto(friendUser, cropCount);
                }).collect(Collectors.toList());

        UserGetFriendUserInfoDto userGetFriendUserInfoDto =
                new UserGetFriendUserInfoDto(friendUserInfos, searchFriendUsers);
        return userGetFriendUserInfoDto;
    }

    @Override
    public void sendTempPasswordEmail(UserFindPasswordDto userFindPasswordDto) throws Exception {
        User findUser = userRepository.findByEmail(userFindPasswordDto.getCheckEmail())
                .orElse(null);
        if (findUser == null) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_USER_FIND_PASSWORD_EMAIL);
        }

        IssueTempPasswordEmailDto issueTempPasswordEmailDto =
                emailService.generateIssueTempPasswordEmailDtoAndChangePassword(findUser, passwordEncoder);
        emailService.sendIssueTempPasswordEmail(issueTempPasswordEmailDto);
    }


    @Override
    @Scheduled(cron = "0 0 3 1 * ?", zone = "Asia/Seoul")
    public void initPostWriteCount() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.initPostWriteCount();
        }
    }

    @Override
    @Scheduled(cron = "0 0 3 1 * ?", zone = "Asia/Seoul")
    public void initThisMonthHarvestCount() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.initThisMonthHarvestCount();
        }
    }
}
