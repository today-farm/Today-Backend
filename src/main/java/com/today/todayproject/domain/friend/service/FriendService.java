package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.dto.FriendFindRequestResponseDto;
import com.today.todayproject.domain.friend.dto.GetFriendsResponseDto;
import com.today.todayproject.domain.friend.dto.ReceiveRequestFriendInfoDto;
import com.today.todayproject.global.BaseException;

import java.util.List;

public interface FriendService {

    void add(Long toUserId) throws Exception;

    void delete(Long deleteToUserId) throws Exception;

    GetFriendsResponseDto getFriends(Long fromUserId) throws BaseException;

    void acceptFriendRequest(Long toUserId) throws BaseException;

    void refuseFriendRequest(Long toUserId) throws BaseException;

    FriendFindRequestResponseDto getRequestedFriendUsers() throws BaseException;
}