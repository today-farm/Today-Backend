package com.today.todayproject.domain.friend.repository;

import com.today.todayproject.domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    void deleteByFriendIdAndFriendOwnerId(Long friendUserId, Long friendOwnerId);

    Optional<List<Friend>> findAllByFriendId(Long friendUserId);

    Optional<Friend> findByFriendIdAndFriendOwnerId(Long friendId, Long friendOwnerId);
}
