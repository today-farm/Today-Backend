package com.today.todayproject.domain.user.repository;

import com.today.todayproject.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomUserRepository {

    Slice<User> searchFriendUserByUserNickname(Long loginUserId, Long lastFriendUserId, String searchUserNickname, Pageable pageable);

    Slice<User> searchUserByUserNickname(Long lastUserId, String searchUserNickname, int userPageSize);
}
