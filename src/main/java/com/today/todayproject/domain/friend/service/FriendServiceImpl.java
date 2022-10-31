package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.friend.dto.FriendSaveDto;
import com.today.todayproject.domain.friend.repository.FriendRepository;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Override
    public void add(FriendSaveDto friendSaveDto) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        Friend friend = Friend.builder()
                .nickname(friendSaveDto.getNickname())
                .profileImgUrl(friendSaveDto.getProfileImgUrl())
                .recentFeeling(friendSaveDto.getRecentFeeling())
                .build();

        friend.confirmUser(loginUser);

        friendRepository.save(friend);
    }

    @Override
    public void delete(Long friendId) throws Exception {
        Friend findFriend = friendRepository.findById(friendId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_FRIEND));

        friendRepository.delete(findFriend);
    }

}
