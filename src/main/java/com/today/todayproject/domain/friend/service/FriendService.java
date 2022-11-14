package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.dto.FriendInfoDto;
import com.today.todayproject.global.BaseException;

import java.util.List;

public interface FriendService {

    void add(Long friendId) throws Exception;

    void delete(Long deleteFriendUserId) throws Exception;

    List<FriendInfoDto> getFriends(Long friendOwnerId);

    void acceptFriendRequest(Long opponentFriendId) throws BaseException;

    void refuseFriendRequest(Long opponentFriendId) throws BaseException;
}