package com.today.todayproject.domain.user.repository;

import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void after() {
        em.clear();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void 회원_저장_성공() throws BaseException {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();

        //when
        User savedUser = userRepository.save(user);

        //then
        User findUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        assertThat(savedUser).isEqualTo(user);
        assertThat(savedUser).isEqualTo(findUser);
    }

    @Test
    void 회원_ID로_회원_찾기_기능() throws BaseException {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when
        User findUser = userRepository.findById(user.getId()).
                orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        //then
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    void 회원_ID로_회원_찾기_기능_일치하는_회원_ID가_없으면_예외_처리() {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when, then
        assertThatThrownBy(() -> userRepository.findById(0L).orElseThrow(() ->
                new BaseException(BaseResponseStatus.NOT_FOUND_USER)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 회원_이메일로_회원_찾기_기능() throws BaseException {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when
        User findUser = userRepository.findByEmail(user.getEmail()).
                orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        //then
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    void 회원_이메일로_회원_찾기_기능_일치하는_회원_이메일이_없으면_예외_처리() {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when, then
        assertThatThrownBy(() -> userRepository.findByEmail("eamil1@naver.com").orElseThrow(() ->
                new BaseException(BaseResponseStatus.NOT_FOUND_EMAIL)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 회원_닉네임으로_회원_찾기_기능() throws BaseException {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when
        User findUser = userRepository.findByNickname(user.getNickname()).
                orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        //then
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    void 회원_닉네임으로_회원_찾기_기능_일치하는_회원_닉네임이_없으면_예외_처리() {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when, then
        assertThatThrownBy(() -> userRepository.findByNickname("KKK").orElseThrow(() ->
                new BaseException(BaseResponseStatus.NOT_FOUND_USER)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 회원_업데이트_기능() throws BaseException {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        userRepository.save(user);

        //when
        User findUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        findUser.updateNickname("KSH1");
        findUser.updatePassword(passwordEncoder, "password2");

        //then
        User afterUpdateFindUser = userRepository.findById(findUser.getId()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        assertThat(afterUpdateFindUser.getNickname()).isEqualTo("KSH1");
        assertThat(afterUpdateFindUser.matchPassword(passwordEncoder, "password2")).isTrue();
    }

    @Test
    void 회원_삭제_기능() {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        User savedUser = userRepository.save(user);

        //when
        userRepository.delete(savedUser);

        //then
        assertThatThrownBy(() -> userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 회원_리프레시_토큰으로_회원_찾기_기능() throws BaseException {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        User savedUser = userRepository.save(user);
        savedUser.updateRefreshToken("refresh1234");

        //when
        User findUser = userRepository.findByRefreshToken(savedUser.getRefreshToken()).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        //then
        assertThat(findUser).isEqualTo(savedUser);
    }

    @Test
    void 회원_리프레시_토큰으로_회원_찾기_기능_리프레시_토큰이_일치하지_않으면_예외_발생() {
        //given
        User user = User.builder().email("test1@gmail.com").nickname("KSH").password("password1").build();
        User savedUser = userRepository.save(user);
        savedUser.updateRefreshToken("refresh1234");

        //when, then
        assertThatThrownBy(() -> userRepository.findByRefreshToken("refresh1111").orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_USER)))
                .isInstanceOf(BaseException.class);
    }
}