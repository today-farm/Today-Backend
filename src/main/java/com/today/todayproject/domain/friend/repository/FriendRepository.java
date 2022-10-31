package com.today.todayproject.domain.friend.repository;

import com.today.todayproject.domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
