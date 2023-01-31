package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.dto.FriendInfoDto;
import com.today.todayproject.domain.friend.dto.FriendRequestInfoDto;
import com.today.todayproject.global.BaseException;

import java.util.List;

public interface FriendService {

    void add(Long toUserId) throws Exception;

    void delete(Long deleteToUserId) throws Exception;

    List<FriendInfoDto> getFriends(Long fromUserId) throws BaseException;

    void acceptFriendRequest(Long toUserId) throws BaseException;

    void refuseFriendRequest(Long toUserId) throws BaseException;

    List<FriendRequestInfoDto> getRequestedFriendUsers() throws BaseException;
}