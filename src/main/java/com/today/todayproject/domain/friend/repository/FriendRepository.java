package com.today.todayproject.domain.friend.repository;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    void deleteByFriendIdAndFriendOwnerId(Long friendUserId, Long friendOwnerId);

    Optional<List<Friend>> findAllByFriendIdOrderByAreWeFriend(Long friendUserId);

    Optional<Friend> findByFriendIdAndFriendOwnerId(Long friendId, Long friendOwnerId);

    boolean existsByFriendOwnerIdAndFriend (Long friendOwnerId, User friend);

//    boolean existsByFriendOwnerIdAndFriendAndAreWeFriend (Long friendOwnerId, User friend, boolean areWeFriend);
}
