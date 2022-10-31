package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.dto.FriendSaveDto;
import com.today.todayproject.global.BaseException;

public interface FriendService {

    void add(FriendSaveDto friendSaveDto) throws Exception;

    void delete(Long friendId) throws Exception;
}