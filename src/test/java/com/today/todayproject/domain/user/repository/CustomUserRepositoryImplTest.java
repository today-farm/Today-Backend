package com.today.todayproject.domain.user.repository;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.GenerateDummy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomUserRepositoryImplTest {

    @Autowired
    UserRepository userRepository;

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

    private static final int FIND_SIZE =  5;

    @BeforeEach
    void setUp() {
        saveUsers();
        saveFriends();
    }

    private void saveUsers() {
        user1 = GenerateDummy.generateDummyUser("test1@gmail.com", "1234", "KSH1",
                "s3://imgUrl1", Role.USER);
        user2 = GenerateDummy.generateDummyUser("test2@gmail.com", "1234", "KSH2",
                "s3://imgUrl2", Role.USER);
        user3 = GenerateDummy.generateDummyUser("test3@gmail.com", "1234", "KSH3",
                "s3://imgUrl3", Role.USER);
        user4 = GenerateDummy.generateDummyUser("test4@gmail.com", "1234", "KSH4",
                "s3://imgUrl4", Role.USER);
        user5 = GenerateDummy.generateDummyUser("test5@gmail.com", "1234", "KSH5",
                "s3://imgUrl5", Role.USER);
        user6 = GenerateDummy.generateDummyUser("test6@gmail.com", "1234", "KSH6",
                "s3://imgUrl6", Role.USER);
        user7 = GenerateDummy.generateDummyUser("test7@gmail.com", "1234", "KSH7",
                "s3://imgUrl7", Role.USER);
        user8 = GenerateDummy.generateDummyUser("test8@gmail.com", "1234", "KSH8",
                "s3://imgUrl8", Role.USER);
        user9 = GenerateDummy.generateDummyUser("test9@gmail.com", "1234", "KSH9",
                "s3://imgUrl9", Role.USER);
        user10 = GenerateDummy.generateDummyUser("test10@gmail.com", "1234", "KSH10",
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

    @Test
    void 회원_닉네임으로_친구인_유저_조회_처음_요청_시() {
        //given
        Long loginUserId = user1.getId();
        Long lastFriendUserId = null;
        String searchUserNickname = "KSH";
        Pageable pageable = PageRequest.of(0, FIND_SIZE);

        //when
        Slice<User> findFriendUsers = userRepository.searchFriendUserByUserNickname(
                loginUserId, lastFriendUserId, searchUserNickname, pageable);

        //then
        assertThat(findFriendUsers.hasNext()).isFalse();
        assertThat(findFriendUsers.getContent().size()).isEqualTo(1);
        assertThat(findFriendUsers.getContent().contains(user2)).isTrue();
    }

    @Test
    void 회원_닉네임으로_친구인_유저_조회_처음_요청_아닐_시() {
        Long loginUserId = user1.getId();
        Long lastFriendUserId = user2.getId();
        String searchUserNickname = "KSH";
        Pageable pageable = PageRequest.of(0, FIND_SIZE);

        //when
        Slice<User> findFriendUsers = userRepository.searchFriendUserByUserNickname(
                loginUserId, lastFriendUserId, searchUserNickname, pageable);

        //then
        assertThat(findFriendUsers.hasNext()).isFalse();
        assertThat(findFriendUsers.getContent().isEmpty()).isTrue();
    }

    @Test
    void 회원_닉네임으로_유저_조회_처음_요청() {
        //given
        Long loginUserId = user1.getId();
        Long lastFriendUserId = null;
        String searchUserNickname = "KSH";
        Pageable pageable = PageRequest.of(0, FIND_SIZE);
        Slice<User> findFriendUsers = userRepository.searchFriendUserByUserNickname(
                loginUserId, lastFriendUserId, searchUserNickname, pageable);
        int friendUserSize = findFriendUsers.getContent().size();

        Long lastUserId = null;
        int userPageSize = FIND_SIZE - friendUserSize;

        //when
        Slice<User> findUsers = userRepository.searchUserByUserNickname(
                loginUserId, lastUserId, searchUserNickname, userPageSize);

        //then
        assertThat(findUsers.hasNext()).isTrue();
        assertThat(findUsers.getContent().size()).isEqualTo(userPageSize);
        assertThat(findUsers.getContent()).contains(user10, user9, user8, user7);
    }

    @Test
    void 회원_닉네임으로_유저_조회_처음_요청_아니고_뒤에_조회할_데이터_추가로_있을_시() {
        //given
        Long loginUserId = user1.getId();
        Long lastFriendUserId = user2.getId();
        String searchUserNickname = "KSH";
        Pageable pageable = PageRequest.of(0, FIND_SIZE);
        Slice<User> findFriendUsers = userRepository.searchFriendUserByUserNickname(
                loginUserId, lastFriendUserId, searchUserNickname, pageable);
        int friendUserSize = findFriendUsers.getContent().size();

        Long lastUserId = user9.getId();
        int userPageSize = FIND_SIZE - friendUserSize;

        //when
        Slice<User> findUsers = userRepository.searchUserByUserNickname(
                loginUserId, lastUserId, searchUserNickname, userPageSize);

        //then
        assertThat(findUsers.hasNext()).isTrue();
        assertThat(findUsers.getContent().size()).isEqualTo(userPageSize);
        assertThat(findUsers.getContent()).contains(user8, user7, user6, user5, user4);
    }

    @Test
    void 회원_닉네임으로_유저_조회_처음_요청_아니고_뒤에_조회할_데이터_추가로_없을_시() {
        //given
        Long loginUserId = user1.getId();
        Long lastFriendUserId = user2.getId();
        String searchUserNickname = "KSH";
        Pageable pageable = PageRequest.of(0, FIND_SIZE);
        Slice<User> findFriendUsers = userRepository.searchFriendUserByUserNickname(
                loginUserId, lastFriendUserId, searchUserNickname, pageable);
        int friendUserSize = findFriendUsers.getContent().size();

        Long lastUserId = user7.getId();
        int userPageSize = FIND_SIZE - friendUserSize;

        //when
        Slice<User> findUsers = userRepository.searchUserByUserNickname(
                loginUserId, lastUserId, searchUserNickname, userPageSize);

        //then
        assertThat(findUsers.hasNext()).isFalse();
        assertThat(findUsers.getContent().size()).isEqualTo(4);
        assertThat(findUsers.getContent()).contains(user6, user5, user4, user3);
    }
}