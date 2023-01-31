package com.today.todayproject.domain.friend.repository;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    void deleteByToUserIdAndFromUserId(Long toUserId, Long fromUserId);

    Optional<List<Friend>> findAllByToUserIdOrderByAreWeFriend(Long toUserId);

    Optional<Friend> findByToUserIdAndFromUserId(Long friendId, Long fromUserId);

    boolean existsByFromUserIdAndToUser(Long fromUserId, User toUser);

    Optional<List<Friend>> findAllByFromUserIdAndAreWeFriendIsFalse(Long fromUserId);

    Friend findByFromUserId(Long fromUserId);

//    boolean existsByFromUserIdAndFriendAndAreWeFriend (Long FromUserId, User friend, boolean areWeFriend);
}
