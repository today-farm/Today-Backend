package com.today.todayproject.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.dto.UserSignUpRequestDto;
import com.today.todayproject.domain.user.dto.UserUpdateRequestDto;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.domain.user.service.UserService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
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
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    private static final String BEARER = "Bearer ";

    private String email = "test1@gmail.com";
    private String password = "password1";
    private String nickname = "KSH";

    @Value("${jwt.access.header}")
    private String accessHeader;

    private MockMultipartFile generateMultipartFileImage() throws IOException {
        return new MockMultipartFile(
                "profileImg",
                "testImage1.jpeg",
                "jpeg",
                new FileInputStream("src/test/resources/testImage/testImage1.jpeg"));
    }

    private MockMultipartFile generateUpdateMultipartFileImage() throws IOException {
        return new MockMultipartFile(
                "profileImg",
                "testImage2.png",
                "png",
                new FileInputStream("src/test/resources/testImage/testImage2.png"));
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

    private MockMultipartFile generateUpdateDtoFile(String dto) {
        return new MockMultipartFile("userUpdateRequestDto",
                "userUpdateRequestDto", "application/json",
                dto.getBytes(StandardCharsets.UTF_8));
    }

    private String getAccessTokenByLogin() throws Exception {
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
        assertThat(findUser.getProfileImgUrl()).isNull();
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
    @ValueSource(strings = {"1", "12", "123", "aabbccddeeffg"})
    void 회원_가입_시_비밀번호가_4자_이상_12자_이하가_아니면_회원_가입_실패(String password) throws Exception {
        //given
        String wrongSizePasswordDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, nickname));

        //when
        signUpFail(generateSignUpDtoFile(wrongSizePasswordDto), "비밀번호는 4자 이상 12자 이하여야합니다.");

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
    void 회원_가입_시_닉네임이_2자_이상_10자_이하가_아니면_회원_가입_실패(String nickname) throws Exception {
        //given
        String wrongRegexNicknameDto = objectMapper.writeValueAsString(
                new UserSignUpRequestDto(email, password, nickname));

        //when
        signUpFail(generateSignUpDtoFile(wrongRegexNicknameDto), "닉네임은 2자 이상 10자 이하여야합니다.");

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void 회원_수정_닉네임_비밀번호_프로필_사진_모두_수정_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin();
        User beforeUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeUpdateProfileImgUrl = beforeUpdateUser.getProfileImgUrl();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateRequestDto(nickname+"123", password+"123"));
        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);

        //when
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/user/update")
                        .file(generateUpdateMultipartFileImage())
                        .file(generatedUpdateDto)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());

        //then
        User afterUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(afterUpdateUser.getNickname()).isNotEqualTo(nickname);
        assertThat(passwordEncoder.matches(password, afterUpdateUser.getPassword())).isFalse();
        assertThat(afterUpdateUser.getNickname()).isEqualTo(nickname+"123");
        assertThat(passwordEncoder.matches(password+"123", afterUpdateUser.getPassword())).isTrue();
        assertThat(afterUpdateUser.getProfileImgUrl()).isNotEqualTo(beforeUpdateProfileImgUrl);
    }

    @Test
    void 회원_수정_닉네임만_수정_시_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateRequestDto(nickname+"123", null));
        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update")
                                .file(generatedUpdateDto)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());

        //then
        User afterUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(afterUpdateUser.getNickname()).isNotEqualTo(nickname);
        assertThat(afterUpdateUser.getNickname()).isEqualTo(nickname+"123");
    }

    @Test
    void 회원_수정_비밀번호만_수정_시_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateRequestDto(null, password + "123"));
        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update")
                                .file(generatedUpdateDto)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk());

        //then
        User afterUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(passwordEncoder.matches(password + "123", afterUpdateUser.getPassword())).isTrue();
    }

    @Test
    void 회원_수정_프로필_사진만_수정_시_성공() throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin();
        User beforeUpdateUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        String beforeUpdateProfileImgUrl = beforeUpdateUser.getProfileImgUrl();

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update")
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
        String accessToken = getAccessTokenByLogin();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateRequestDto(changeNickname, null));
        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);

        //when
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/user/update")
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
    void 회원_수정_수정할_닉네임이_2자_이상_10자_이하가_아니면_수정_실패(String changeNickname) throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateRequestDto(changeNickname, null));
        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);

        //when
        mockMvc.perform(
                    multipart(HttpMethod.PATCH, "/user/update")
                            .file(generatedUpdateDto)
                            .header(accessHeader, BEARER + accessToken))
            .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정할 닉네임은 2자 이상 10자 이하여야합니다."));



        //then
        User updateFailUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(updateFailUser.getNickname()).isNotEqualTo(changeNickname);
        assertThat(updateFailUser.getNickname()).isEqualTo(nickname);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "123", "abcdefghijklm"})
    void 회원_수정_수정할_비밀번호가_4자_이상_12자_이하가_아니면_예외_처리(String changePassword) throws Exception {
        //given
        String signUpDto = objectMapper.writeValueAsString(new UserSignUpRequestDto(email, password, nickname));
        signUpProfileSuccess(generateSignUpDtoFile(signUpDto));
        String accessToken = getAccessTokenByLogin();

        String updateDto = objectMapper.writeValueAsString(
                new UserUpdateRequestDto(null, changePassword));
        MockMultipartFile generatedUpdateDto = generateUpdateDtoFile(updateDto);

        //when
        mockMvc.perform(
                multipart(HttpMethod.PATCH, "/user/update")
                        .file(generatedUpdateDto)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정할 비밀번호는 4자 이상 12자 이하여야합니다."));

        //then
        User updateFailUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(passwordEncoder.matches(changePassword, updateFailUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password, updateFailUser.getPassword())).isTrue();
    }
}