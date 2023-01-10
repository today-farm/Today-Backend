package com.today.todayproject.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.UserSignUpRequestDto;
import com.today.todayproject.domain.user.dto.UserUpdateMyInfoRequestDto;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.domain.user.service.UserService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.GenerateDummy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EntityManager em;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    private static final String BEARER = "Bearer ";

    private String email = "test1@gmail.com";
    private String password = "password1!";
    private String nickname = "KSH";

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${image.defaultProfileImageUrl}")
    private String defaultProfileImageUrl;

    void userAndFriendSetUp() throws Exception {
        saveUsersAndFriends();
    }

    private void saveUsersAndFriends() throws Exception {
        User user1 = GenerateDummy.generateDummyUser("test1@naver.com", "password1234!", "KSH1",
                "s3://imgUrl1", Role.USER);
        User user2 = GenerateDummy.generateDummyUser("test2@naver.com", "password1234!", "KSH2",
                "s3://imgUrl2", Role.USER);
        User user3 = GenerateDummy.generateDummyUser("test3@naver.com", "password1234!", "KSH3",
                "s3://imgUrl3", Role.USER);
        User user4 = GenerateDummy.generateDummyUser("test4@naver.com", "password1234!", "KSH4",
                "s3://imgUrl4", Role.USER);
        User user5 = GenerateDummy.generateDummyUser("test5@naver.com", "password1234!", "KSH5",
                "s3://imgUrl5", Role.USER);
        User user6 = GenerateDummy.generateDummyUser("test6@naver.com", "password1234!", "KSH6",
                "s3://imgUrl6", Role.USER);
        User user7 = GenerateDummy.generateDummyUser("test7@naver.com", "password1234!", "KSH7",
                "s3://imgUrl7", Role.USER);
        User user8 = GenerateDummy.generateDummyUser("test8@naver.com", "password1234!", "KSH8",
                "s3://imgUrl8", Role.USER);
        User user9 = GenerateDummy.generateDummyUser("test9@naver.com", "password1234!", "KSH9",
                "s3://imgUrl9", Role.USER);
        User user10 = GenerateDummy.generateDummyUser("test10@naver.com", "password1234!", "KSH10",
                "s3://imgUrl10", Role.USER);

        signUpDummyData(user1);
        signUpDummyData(user2);
        signUpDummyData(user3);
        signUpDummyData(user4);
        signUpDummyData(user5);
        signUpDummyData(user6);
        signUpDummyData(user7);
        signUpDummyData(user8);
        signUpDummyData(user9);
        signUpDummyData(user10);
        User findUser2 = userRepository.findByEmail("test2@naver.com").orElse(null);
        saveFriends(user1, findUser2);
    }

    private void saveFriends(User requestUser, User requestedUser) throws Exception {
        Long requestedUserId = requestedUser.getId();
        log.info("requestedUserId : {}", requestedUserId);
        String accessToken = getAccessTokenByLogin(requestUser.getEmail(), requestUser.getPassword());
        mockMvc.perform(
                post("/friend/add/{friendId}", requestedUserId)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());
    }

    private void signUpDummyData(User user) throws Exception {
        String signUpDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(user.getEmail(), user.getPassword(), user.getNickname()));
        signUpNoProfileSuccess(generateSignUpDtoFile(signUpDto));
    }

    private MockMultipartFile generateMultipartFileImage() throws IOException {
        return new MockMultipartFile(
                "profileImg",
                "testImage1.jpeg",
                "jpeg",
                new FileInputStream("src/test/resources/testimage/testImage1.jpeg"));
    }

    private MockMultipartFile generateUpdateMultipartFileImage() throws IOException {
        return new MockMultipartFile(
                "profileImg",
                "testImage2.png",
                "png",
                new FileInputStream("src/test/resources/testimage/testImage2.png"));
    }


    private void signUpProfileSuccess(MockMultipartFile userSignUpRequestDto) throws Exception {
        mockMvc.perform(
                        multipart("/sign-up")
                                .file(generateMultipartFileImage())
                                .file(userSignUpRequestDto))
                .andExpect(status().isOk());
    }

    private void signUpNoProfileSuccess(MockMultipartFile userSignUpRequestDto) throws Exception {
        mockMvc.perform(
                        multipart("/sign-up")
                                .file(userSignUpRequestDto))
                .andExpect(status().isOk());
    }

    private void signUpFail(MockMultipartFile signUpDto, String errorMessage) throws Exception {
        mockMvc.perform(
                        multipart("/sign-up")
                                .file(generateMultipartFileImage())
                                .file(signUpDto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    private MockMultipartFile generateSignUpDtoFile(String dto) {
        return new MockMultipartFile("userSignUpRequestDto",
                "userSignUpRequestDto", "application/json",
                dto.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile generateUpdateMyInfoDtoFile(String dto) {
        return new MockMultipartFile("userUpdateMyInfoRequestDto",
                "userUpdateMyInfoRequestDto", "application/json",
                dto.getBytes(StandardCharsets.UTF_8));
    }

    private String getAccessTokenByLogin(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email).orElse(null);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("password", password);

        MvcResult result = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userMap)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    @Test
    void 프로필_사진_존재_시_회원_가입_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        MockMultipartFile userSignUpRequestDto = new MockMultipartFile("userSignUpRequestDto",
                "userSignUpRequestDto", "application/json",
                signUpDto.getBytes(StandardCharsets.UTF_8));

        //when
        signUpProfileSuccess(userSignUpRequestDto);

        //then
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(findUser).isNotNull();
        assertThat(findUser.getProfileImgUrl()).isNotNull();
        assertThat(findUser.getEmail()).isEqualTo(email);
        assertThat(passwordEncoder.matches(password, findUser.getPassword())).isTrue();
        assertThat(findUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    void 프로필_사진_없을_시_회원_가입_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        MockMultipartFile userSignUpRequestDto = new MockMultipartFile("userSignUpRequestDto",
                "userSignUpRequestDto", "application/json",
                signUpDto.getBytes(StandardCharsets.UTF_8));

        //when
        signUpNoProfileSuccess(userSignUpRequestDto);

        //then
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(findUser).isNotNull();
        assertThat(findUser.getProfileImgUrl()).isEqualTo(defaultProfileImageUrl);
        assertThat(findUser.getEmail()).isEqualTo(email);
        assertThat(passwordEncoder.matches(password, findUser.getPassword())).isTrue();
        assertThat(findUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    void 회원_가입_항목_하나라도_null이면_회원_가입_실패() throws Exception {
        //given
        String emailNullDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(null, password, nickname));
        String passwordNullDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, null, nickname));
        String nicknameNullDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, null));

        //when
        signUpFail(generateSignUpDtoFile(emailNullDto), "이메일을 입력해주세요.");
        signUpFail(generateSignUpDtoFile(passwordNullDto), "비밀번호를 입력해주세요.");
        signUpFail(generateSignUpDtoFile(nicknameNullDto), "닉네임을 입력해주세요.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void 회원_가입_항목_하나라도_빈_값이면_회원_가입_실패() throws Exception {
        //given
        String emailNullDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto("", password, nickname));
        String passwordNullDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, "", nickname));
        String nicknameNullDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, ""));

        //when
        signUpFail(generateSignUpDtoFile(emailNullDto), "이메일을 입력해주세요.");
        signUpFail(generateSignUpDtoFile(passwordNullDto), "비밀번호를 입력해주세요.");
        signUpFail(generateSignUpDtoFile(nicknameNullDto), "닉네임을 입력해주세요.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.com", "12@aaaa", "test"})
    void 회원_가입_시_이메일이_이메일_형식이_아니면_회원_가입_실패(String email) throws Exception {
        //given
        String wrongEmailRegexDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, nickname));

        //when
        signUpFail(generateSignUpDtoFile(wrongEmailRegexDto), "이메일 형식에 맞게 입력해주세요.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a1!", "aaaaaaaaaa2", "22222222222!", "abbbbbbbbbbb"})
    void 회원_가입_시_비밀번호가_영어_숫자_특수문자가_포함된_8자_이상이_아니면_회원_가입_실패(String password) throws Exception {
        //given
        String wrongSizePasswordDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, nickname));

        //when
        signUpFail(generateSignUpDtoFile(wrongSizePasswordDto),
                "비밀번호는 숫자, 영어, 특수문자가 1개 이상 포함된 8자 이상이어야합니다.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"11.", "11!", "!!!", "abc12@"})
    void 회원_가입_시_닉네임이_숫자_한글_영어가_아니면_회원_가입_실패(String nickname) throws Exception {
        //given
        String wrongRegexNicknameDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, nickname));

        //when
        signUpFail(generateSignUpDtoFile(wrongRegexNicknameDto), "닉네임은 숫자, 한글, 영어만 가능합니다.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "aabbccddeef"})
    void 회원_가입_시_닉네임이_2자_이상_8자_이하가_아니면_회원_가입_실패(String nickname) throws Exception {
        //given
        String wrongRegexNicknameDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, nickname));

        //when
        signUpFail(generateSignUpDtoFile(wrongRegexNicknameDto), "닉네임은 2자 이상 8자 이하여야합니다.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void 회원_수정_닉네임_프로필_사진_모두_수정_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin(email, password);
        User beforeUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeUpdateProfileImgUrl = beforeUpdateUser.getProfileImgUrl();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateMyInfoRequestDto(nickname+"123"));
        MockMultipartFile generatedUpdateDto = generateUpdateMyInfoDtoFile(updateDto);

        //when
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/user/update-my-info")
                        .file(generateUpdateMultipartFileImage())
                        .file(generatedUpdateDto)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());

        //then
        User afterUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(afterUpdateUser.getNickname()).isNotEqualTo(nickname);
        assertThat(afterUpdateUser.getNickname()).isEqualTo(nickname+"123");
        assertThat(afterUpdateUser.getProfileImgUrl()).isNotEqualTo(beforeUpdateProfileImgUrl);
    }

    @Test
    void 회원_수정_닉네임만_수정_시_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin(email, password);

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateMyInfoRequestDto(nickname+"123"));
        MockMultipartFile generatedUpdateDto = generateUpdateMyInfoDtoFile(updateDto);

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update-my-info")
                                .file(generatedUpdateDto)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());

        //then
        User afterUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(afterUpdateUser.getNickname()).isNotEqualTo(nickname);
        assertThat(afterUpdateUser.getNickname()).isEqualTo(nickname+"123");
    }

//    @Test
//    void 회원_수정_비밀번호만_수정_시_성공() throws Exception {
//        //given
//        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
//        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
//        String accessToken = getAccessTokenByLogin(email, password);
//
//        String updateDto = objectMapper.writeValueAsString(
//                new UserUpdateMyInfoRequestDto(null, password + "123"));
//        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);
//
//        //when
//        mockMvc.perform(
//                        multipart(HttpMethod.PATCH, "/user/update-my-info")
//                                .file(generatedUpdateDto)
//                                .header(accessHeader, BEARER + accessToken))
//                .andExpect(status().isOk());
//
//        //then
//        User afterUpdateUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//        assertThat(passwordEncoder.matches(password + "123", afterUpdateUser.getPassword())).isTrue();
//    }

    @Test
    void 회원_수정_프로필_사진만_수정_시_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin(email, password);
        User beforeUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeUpdateProfileImgUrl = beforeUpdateUser.getProfileImgUrl();

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update-my-info")
                                .file(generateUpdateMultipartFileImage())
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());

        //then
        User afterUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(afterUpdateUser.getProfileImgUrl()).isNotEqualTo(beforeUpdateProfileImgUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123!", "@@@", "aa-"})
    void 회원_수정_수정할_닉네임이_숫자_한글_영어가_아니면_수정_실패(String changeNickname) throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin(email, password);

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateMyInfoRequestDto(changeNickname));
        MockMultipartFile generatedUpdateDto = generateUpdateMyInfoDtoFile(updateDto);

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update-my-info")
                                .file(generatedUpdateDto)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정할 닉네임은 숫자, 한글, 영어만 가능합니다."));

        //then
        User updateFailUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(updateFailUser.getNickname()).isNotEqualTo(changeNickname);
        assertThat(updateFailUser.getNickname()).isEqualTo(nickname);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "가", "a", "가나다라마바사아자차카타파하"})
    void 회원_수정_수정할_닉네임이_2자_이상_8자_이하가_아니면_수정_실패(String changeNickname) throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin(email, password);

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateMyInfoRequestDto(changeNickname));
        MockMultipartFile generatedUpdateDto = generateUpdateMyInfoDtoFile(updateDto);

        //when
        mockMvc.perform(
                    multipart(HttpMethod.PATCH, "/user/update-my-info")
                            .file(generatedUpdateDto)
                            .header(accessHeader, BEARER + accessToken))
            .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정할 닉네임은 2자 이상 8자 이하여야합니다."));



        //then
        User updateFailUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(updateFailUser.getNickname()).isNotEqualTo(changeNickname);
        assertThat(updateFailUser.getNickname()).isEqualTo(nickname);
    }

//    @ParameterizedTest
//    @ValueSource(strings = {"1a!", "12a!", "123aa!"})
//    void 회원_수정_수정할_비밀번호가_8자_이상이_아니면_예외_처리(String changePassword) throws Exception {
//        //given
//        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
//        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
//        String accessToken = getAccessTokenByLogin(email, password);
//
//        String updateDto = objectMapper.writeValueAsString(
//                new UserUpdateMyInfoRequestDto(null, changePassword));
//        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);
//
//        //when
//        mockMvc.perform(
//                multipart(HttpMethod.PATCH, "/user/update-my-info")
//                        .file(generatedUpdateDto)
//                        .header(accessHeader, BEARER + accessToken))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("비밀번호는 숫자, 영어, 특수문자가 1개 이상 포함된 8자 이상이어야합니다."));
//
//        //then
//        User updateFailUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//        assertThat(passwordEncoder.matches(changePassword, updateFailUser.getPassword())).isFalse();
//        assertThat(passwordEncoder.matches(password, updateFailUser.getPassword())).isTrue();
//    }

    @Test
    void 처음_요청_시_유저_조회_성공() throws Exception {
        //given
        userAndFriendSetUp();
        String accessToken = getAccessTokenByLogin("test1@naver.com", "password1234!");
        String searchUserNickname = "KSH";

        //when, then
        User loginUser = userRepository.findByEmail("test1@naver.com")
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        mockMvc.perform
                (get("/user/search")
                        .param("loginUserId", String.valueOf(loginUser.getId()))
                        .param("searchUserNickname", searchUserNickname)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void 처음_요청_아닐_시_유저_조회_성공() throws Exception {
        //given
        userAndFriendSetUp();
        String accessToken = getAccessTokenByLogin("test1@naver.com", "password1234!");
        String searchUserNickname = "KSH";

        User loginUser = userRepository.findByEmail("test1@naver.com")
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        User lastFriendUser = userRepository.findByEmail("test2@naver.com")
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        User lastUser = userRepository.findByEmail("test7@naver.com")
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        //when, then

        mockMvc.perform
                        (get("/user/search")
                                .param("loginUserId", String.valueOf(loginUser.getId()))
                                .param("lastFriendUserId", String.valueOf(lastFriendUser.getId()))
                                .param("lastUserId", String.valueOf(lastUser.getId()))
                                .param("searchUserNickname", searchUserNickname)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());
    }
}