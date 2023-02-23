package com.today.todayproject.domain.friend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.friend.QFriend;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.today.todayproject.domain.friend.QFriend.friend;


@Repository
public class CustomFriendRepositoryImpl implements CustomFriendRepository{
    private final JPAQueryFactory query;

    public CustomFriendRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    @Override
    public List<Friend> findAllSendRequestFriends(Long loginUserId) {
        return query.selectFrom(friend)
                .where(
                        friend.toUser.id.eq(loginUserId),
                        friend.areWeFriend.eq(false)
                )
                .fetch();
    }

    @Override
    public List<Friend> findAllFriendsWithEachOther(Long loginUserId) {
        QFriend selfFriend = new QFriend("selfFriend");

        return query.selectFrom(selfFriend)
                .join(friend)
                .on(friend.toUser.id.eq(selfFriend.fromUserId))
                .where(
                        friend.fromUserId.eq(loginUserId),
                        friend.areWeFriend.eq(true),
                        selfFriend.areWeFriend.isTrue()
                )
                .fetch();
    }

    @Override
    public List<Friend> findAllReceiveRequestFriends(Long loginUserId) {
        return query.selectFrom(friend)
                .where(
                        friend.fromUserId.eq(loginUserId),
                        friend.areWeFriend.eq(false)
                )
                .fetch();
    }
}
