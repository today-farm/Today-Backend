package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.*;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.GenerateDummy;
import com.today.todayproject.global.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserServiceImplTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;
    private User user7;
    private User user8;
    private User user9;
    private User user10;

    @Value("${image.defaultProfileImageUrl}")
    private String defaultProfileImageUrl;

    private static final int SEARCH_SIZE = 5;

    void userAndFriendSetUp() {
        saveUsers();
        saveFriends();
    }

    private void saveUsers() {
        user1 = GenerateDummy.generateDummyUser("test1@naver.com", "1234", "KSH1",
                "s3://imgUrl1", Role.USER);
        user2 = GenerateDummy.generateDummyUser("test2@naver.com", "1234", "KSH2",
                "s3://imgUrl2", Role.USER);
        user3 = GenerateDummy.generateDummyUser("test3@naver.com", "1234", "KSH3",
                "s3://imgUrl3", Role.USER);
        user4 = GenerateDummy.generateDummyUser("test4@naver.com", "1234", "KSH4",
                "s3://imgUrl4", Role.USER);
        user5 = GenerateDummy.generateDummyUser("test5@naver.com", "1234", "KSH5",
                "s3://imgUrl5", Role.USER);
        user6 = GenerateDummy.generateDummyUser("test6@naver.com", "1234", "KSH6",
                "s3://imgUrl6", Role.USER);
        user7 = GenerateDummy.generateDummyUser("test7@naver.com", "1234", "KSH7",
                "s3://imgUrl7", Role.USER);
        user8 = GenerateDummy.generateDummyUser("test8@naver.com", "1234", "KSH8",
                "s3://imgUrl8", Role.USER);
        user9 = GenerateDummy.generateDummyUser("test9@naver.com", "1234", "KSH9",
                "s3://imgUrl9", Role.USER);
        user10 = GenerateDummy.generateDummyUser("test10@naver.com", "1234", "KSH10",
                "s3://imgUrl10", Role.USER);

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);
        em.persist(user5);
        em.persist(user6);
        em.persist(user7);
        em.persist(user8);
        em.persist(user9);
        em.persist(user10);
    }

    private void saveFriends() {
        List<Friend> friends = GenerateDummy.generateDummyFriend(user1, user2);
        for (Friend friend : friends) {
            em.persist(friend);
        }
    }

    private UserSignUpRequestDto generateUserSignUpRequestDto() {
        return new UserSignUpRequestDto("test1@gmail.com", "password1", "KSH");
    }

    private MockMultipartFile generateMultipartFileImage() throws IOException {
        return new MockMultipartFile(
                "images",
                "testImage1.jpeg",
                "jpeg",
                new FileInputStream("src/test/resources/testimage/testImage1.jpeg"));
    }

    // 여러 로직들에 SecurityUtil.getLoginUserEmail()로 로그인한 유저의 이메일을 가져오는 기능을 사용하므로
    // 회원 가입한 유저를 인증 객체로 설정해준다.
    private void setAuthenticatedUser(UserSignUpRequestDto userSignUpRequestDto) throws Exception {
        MockMultipartFile profileImg = generateMultipartFileImage();
        userService.signUp(userSignUpRequestDto, profileImg);
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(userSignUpRequestDto.getEmail())
                .password(userSignUpRequestDto.getPassword())
                .roles(Role.USER.name())
                .build();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                userDetailsUser, null, null));

        SecurityContextHolder.setContext(emptyContext);
    }


    @Test
    void 회원_가입_성공_프로필_사진_있을_때_프로필_사진_필드_not_null() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        MockMultipartFile profileImg = generateMultipartFileImage();

        //when, then
        Long savedUserId = userService.signUp(userSignUpRequestDto, profileImg);
        User findUser = userRepository.findById(savedUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo(savedUserId);
        assertThat(findUser.getEmail()).isEqualTo(userSignUpRequestDto.getEmail());
        assertThat(findUser.getNickname()).isEqualTo(userSignUpRequestDto.getNickname());
        assertThat(findUser.getProfileImgUrl()).isNotNull();
        assertThat(findUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void 회원_가입_성공_프로필_사진_없을_때_프로필_사진_필드_null() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        MockMultipartFile profileImg = null;

        //when, then
        Long savedUserId = userService.signUp(userSignUpRequestDto, profileImg);
        User findUser = userRepository.findById(savedUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo(savedUserId);
        assertThat(findUser.getEmail()).isEqualTo(userSignUpRequestDto.getEmail());
        assertThat(findUser.getNickname()).isEqualTo(userSignUpRequestDto.getNickname());
        assertThat(findUser.getProfileImgUrl()).isEqualTo(defaultProfileImageUrl);
        assertThat(findUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void 회원_가입_시_중복된_이메일이_있으면_회원_가입_실패() throws Exception {
        //given
        User testUser = User.builder().email("test1@gmail.com").build();
        userRepository.save(testUser);
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        MockMultipartFile profileImg = generateMultipartFileImage();

        //when, then
        BaseResponseStatus errorStatus = assertThrows(BaseException.class, () -> userService.signUp(userSignUpRequestDto, profileImg))
                .getStatus();
        assertThat(errorStatus).isEqualTo(BaseResponseStatus.EXIST_EMAIL);
    }

    @Test
    void 회원_가입_시_중복된_닉네임이_있으면_회원_가입_실패() throws Exception {
        //given
        User testUser = User.builder().nickname("KSH").build();
        userRepository.save(testUser);
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        MockMultipartFile profileImg = generateMultipartFileImage();

        //when, then
        BaseResponseStatus errorStatus = assertThrows(BaseException.class, () -> userService.signUp(userSignUpRequestDto, profileImg))
                .getStatus();
        assertThat(errorStatus).isEqualTo(BaseResponseStatus.EXIST_NICKNAME);
    }

    @Test
    void 회원_수정_닉네임_비밀번호_프로필_사진_모두_수정() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);
        User findUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeNickname = findUser.getNickname();
        String beforePassword = findUser.getPassword();
        String beforeProfileImgUrl = findUser.getProfileImgUrl();

        //when
        String changeNickname = "changeKSH";
        String changePassword = "changePassword1";
        MockMultipartFile changeProfileImg = generateMultipartFileImage();
        UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto = new UserUpdateMyInfoRequestDto(changeNickname);
        userService.updateUser(userUpdateMyInfoRequestDto, changeProfileImg);

        //then
        assertThat(findUser.getNickname()).isNotEqualTo(beforeNickname);
        assertThat(findUser.getNickname()).isEqualTo(changeNickname);
        assertThat(findUser.getProfileImgUrl()).isNotEqualTo(beforeProfileImgUrl);
    }

    @Test
    void 회원_수정_닉네임만_수정() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);
        User findUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeNickname = findUser.getNickname();

        //when
        String changeNickname = "changeKSH";
        UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto = new UserUpdateMyInfoRequestDto(changeNickname);
        userService.updateUser(userUpdateMyInfoRequestDto, null);

        //then
        assertThat(findUser.getNickname()).isNotEqualTo(beforeNickname);
        assertThat(findUser.getNickname()).isEqualTo(changeNickname);
    }

//    @Test
//    void 회원_수정_비밀번호만_수정() throws Exception {
//        //given
//        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
//        setAuthenticatedUser(userSignUpRequestDto);
//        User findUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//        String beforePassword = findUser.getPassword();
//
//        //when
//        String changePassword = "changePassword1";
//        UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto = new UserUpdateMyInfoRequestDto(null, changePassword);
//        userService.updateUser(userUpdateMyInfoRequestDto, null);
//
//        //then
//        assertThat(passwordEncoder.matches(beforePassword, findUser.getPassword())).isFalse();
//        assertThat(passwordEncoder.matches(changePassword, findUser.getPassword())).isTrue();
//    }

    @Test
    void 회원_수정_프로필_사진만_수정() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);
        User findUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeProfileImgUrl = findUser.getProfileImgUrl();

        //when
        MockMultipartFile changeProfileImg = generateMultipartFileImage();
        userService.updateUser(null, changeProfileImg);

        //then
        assertThat(findUser.getProfileImgUrl()).isNotEqualTo(beforeProfileImgUrl);
    }

    @Test
    void 회원_수정_닉네임_수정_시_기존_닉네임과_변경할_닉네임이_같을_시_예외_처리() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);

        //when
        String changeNickname = "KSH";
        UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto = new UserUpdateMyInfoRequestDto(changeNickname);

        //then
        assertThrows(BaseException.class, () -> userService.updateUser(userUpdateMyInfoRequestDto, null));
    }

//    @Test
//    void 회원_수정_비밀번호_수정_시_기존_비밀번호와_변경할_비밀번호가_같을_시_예외_처리() throws Exception {
//        //given
//        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
//        setAuthenticatedUser(userSignUpRequestDto);
//
//        //when
//        String changePassword = "password1";
//        UserUpdateMyInfoRequestDto userUpdateMyInfoRequestDto = new UserUpdateMyInfoRequestDto(null, changePassword);
//
//        //then
//        assertThrows(BaseException.class, () -> userService.updateUser(userUpdateMyInfoRequestDto, null));
//    }

    @Test
    void 회원_탈퇴_성공() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);

        //when, then
        UserWithdrawRequestDto userWithdrawRequestDto = new UserWithdrawRequestDto("password1");
        assertThrows(BaseException.class, () -> {
            userService.withdraw(userWithdrawRequestDto);
            userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        });
    }

    @Test
    void 회원_탈퇴_현재_비밀번호_불일치로_실패() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);

        //when, then
        UserWithdrawRequestDto userWithdrawRequestDto = new UserWithdrawRequestDto("password1234");
        assertThrows(BaseException.class, () -> userService.withdraw(userWithdrawRequestDto));
    }

    @Test
    void 유저_검색_성공_처음_요청_시() {
        //given
        userAndFriendSetUp();
        UserSearchDto userSearchDto = new UserSearchDto(user1.getId(), null, null, "KSH");
        Pageable pageable = PageRequest.of(0, SEARCH_SIZE);

        //when
        UserGetPagingDto userGetPagingDto = userService.searchUsers(pageable, userSearchDto);
        UserGetUserInfoDto userInfos = userGetPagingDto.getUserInfos();
        UserGetFriendUserInfoDto friendInfos = userGetPagingDto.getFriendInfos();
        int searchFriendSize = friendInfos.getFriendUserInfos().size();
        int searchUserSize = SEARCH_SIZE - searchFriendSize;

        //then
        assertThat(userInfos.getHasNext()).isTrue();
        assertThat(friendInfos.getHasNext()).isFalse();
        assertThat(userInfos.getUserInfos().size()).isEqualTo(searchUserSize);
        assertThat(userInfos.getUserInfos().get(0).getUserId()).isEqualTo(user10.getId());
    }

    @Test
    void 유저_검색_성공_처음_요청_아닐_시() {
        //given
        userAndFriendSetUp();
        UserSearchDto userSearchDto = new UserSearchDto(
                user1.getId(), user2.getId(), user7.getId(), "KSH");
        Pageable pageable = PageRequest.of(0, SEARCH_SIZE);

        //when
        UserGetPagingDto userGetPagingDto = userService.searchUsers(pageable, userSearchDto);
        UserGetUserInfoDto userInfos = userGetPagingDto.getUserInfos();
        UserGetFriendUserInfoDto friendInfos = userGetPagingDto.getFriendInfos();

        //then
        assertThat(userInfos.getHasNext()).isFalse();
        assertThat(friendInfos.getHasNext()).isFalse();
        assertThat(userInfos.getUserInfos().size()).isEqualTo(4);
        assertThat(userInfos.getUserInfos().get(0).getUserId()).isEqualTo(user6.getId());
    }
}