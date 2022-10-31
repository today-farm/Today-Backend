package com.today.todayproject.domain.friend.service;

public interface FriendService {

    void add(Long friendId) throws Exception;

    void delete(Long deleteFriendUserId) throws Exception;
}