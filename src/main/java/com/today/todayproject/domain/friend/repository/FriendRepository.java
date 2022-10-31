package com.today.todayproject.domain.friend.repository;

import com.today.todayproject.domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    void deleteByFriendUserId(Long friendUserId);
}
