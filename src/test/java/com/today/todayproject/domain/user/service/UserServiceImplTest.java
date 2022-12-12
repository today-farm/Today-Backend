package com.today.todayproject.domain.user.service;

import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.UserSignUpRequestDto;
import com.today.todayproject.domain.user.dto.UserUpdateRequestDto;
import com.today.todayproject.domain.user.dto.UserWithdrawRequestDto;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;

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


    private UserSignUpRequestDto generateUserSignUpRequestDto() {
        return new UserSignUpRequestDto("test1@gmail.com", "password1", "KSH");
    }

    private MockMultipartFile generateMultipartFileImage() throws IOException {
        return new MockMultipartFile(
                "images",
                "testImage1.jpeg",
                "jpeg",
                new FileInputStream("src/test/resources/testImage/testImage1.jpeg"));
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
        assertThat(findUser.getProfileImgUrl()).isNull();
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
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(changeNickname, changePassword);
        userService.updateUser(userUpdateRequestDto, changeProfileImg);

        //then
        assertThat(findUser.getNickname()).isNotEqualTo(beforeNickname);
        assertThat(findUser.getNickname()).isEqualTo(changeNickname);
        assertThat(passwordEncoder.matches(beforePassword, findUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(changePassword, findUser.getPassword())).isTrue();
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
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(changeNickname, null);
        userService.updateUser(userUpdateRequestDto, null);

        //then
        assertThat(findUser.getNickname()).isNotEqualTo(beforeNickname);
        assertThat(findUser.getNickname()).isEqualTo(changeNickname);
    }

    @Test
    void 회원_수정_비밀번호만_수정() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);
        User findUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforePassword = findUser.getPassword();

        //when
        String changePassword = "changePassword1";
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(null, changePassword);
        userService.updateUser(userUpdateRequestDto, null);

        //then
        assertThat(passwordEncoder.matches(beforePassword, findUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(changePassword, findUser.getPassword())).isTrue();
    }

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
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(changeNickname, null);

        //then
        assertThrows(BaseException.class, () -> userService.updateUser(userUpdateRequestDto, null));
    }

    @Test
    void 회원_수정_비밀번호_수정_시_기존_비밀번호와_변경할_비밀번호가_같을_시_예외_처리() throws Exception {
        //given
        UserSignUpRequestDto userSignUpRequestDto = generateUserSignUpRequestDto();
        setAuthenticatedUser(userSignUpRequestDto);

        //when
        String changePassword = "password1";
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(null, changePassword);

        //then
        assertThrows(BaseException.class, () -> userService.updateUser(userUpdateRequestDto, null));
    }

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
}