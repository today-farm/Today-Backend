package com.today.todayproject.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.friend.QFriend;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

import java.util.List;

import static com.today.todayproject.domain.friend.QFriend.friend;
import static com.today.todayproject.domain.user.QUser.user;

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory query;

    public CustomUserRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    /**
     * 유저 닉네임으로 검색 시 친구인 유저 먼저 뜨도록
     * 친구인 유저 반환하는 메소드 구현
     */
    @Override
    public Slice<User> searchFriendUserByUserNickname(Long loginUserId, Long lastFriendUserId,
                                                  String searchUserNickname, Pageable pageable) {
        List<User> friendUsers = getFriendUsers(loginUserId, lastFriendUserId, searchUserNickname, pageable);

        return checkLastPage(pageable, friendUsers);
    }


    private List<User> getFriendUsers(Long loginUserId, Long lastFriendUserId, String searchUserNickname, Pageable pageable) {
        return query.selectFrom(user)
                .leftJoin(user.friendList, friend)
                .where(
                        ltUserId(lastFriendUserId),

                    friend.fromUserId.eq(loginUserId)
                            .and(friend.areWeFriend.eq(true)),

                        userNicknameHasStr(searchUserNickname)
                )
                .orderBy(user.id.desc())
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    /**
     * 단순 유저 닉네임으로 검색해서 유저 반환하는 메소드 구현
     */
    @Override
    public Slice<User> searchUserByUserNickname(Long loginUserId, Long lastUserId, String searchUserNickname,
                                                int userPageSize) {

        PageRequest page = PageRequest.of(0, userPageSize);

        List<User> users = getUsers(loginUserId, lastUserId, searchUserNickname, userPageSize);

        return checkLastPage(page, users);
    }

    private List<User> getUsers(Long loginUserId, Long lastUserId, String searchUserNickname, int userPageSize) {
        return query.selectFrom(user)
                .leftJoin(user.friendList, friend)
                .where(
                        ltUserId(lastUserId),
                        user.id.ne(loginUserId),
                        friend.fromUserId.ne(loginUserId).or(friend.fromUserId.isNull()),
                        userNicknameHasStr(searchUserNickname)
                )
                .orderBy(user.id.desc())

                .limit(userPageSize + 1)
                .fetch();
    }

    /**
     * userNickname이 문자인지 검증
     * 문자라면, QUser.user.nickname.contains(userNickname) QueryDSL문 리턴
     * 문자가 아니라면, null 리턴
     */
    private BooleanExpression userNicknameHasStr(String userNickname) {

        return StringUtils.hasLength(userNickname) ? user.nickname.contains(userNickname) : null;
    }

    private BooleanExpression ltUserId(Long lastUserId) {
        if (lastUserId == null) {
            return null;
        }

        return user.id.lt(lastUserId);
    }

    private Slice<User> checkLastPage(Pageable pageable, List<User> fetchResults) {
        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤 페이지가 더 있는 것 -> hasNext = true로
        if (fetchResults.size() > pageable.getPageSize()) {
            hasNext = true;
            fetchResults.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(fetchResults, pageable, hasNext);
    }
}
