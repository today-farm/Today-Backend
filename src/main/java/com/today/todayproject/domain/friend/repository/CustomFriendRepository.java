package com.today.todayproject.domain.friend.repository;

import com.today.todayproject.domain.friend.Friend;

import java.util.List;

public interface CustomFriendRepository {

    List<Friend> findAllSendRequestFriends(Long loginUserId);

    List<Friend> findAllFriendsWithEachOther(Long loginUserId);
}
