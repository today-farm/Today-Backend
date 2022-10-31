package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.Friend;
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

    /**
     * 친구 추가 시 로그인한 유저 <-> 친구 추가하는 유저
     * 유저 2명 모두 Friend를 생성해서 2개가 되어야하고, 서로의 friends에 추가되어야한다!!
     */
    @Override
    public void add(Long friendId) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        Friend friendOfFriendUser = Friend.builder()
                .nickname(friendUser.getNickname())
                .profileImgUrl(friendUser.getProfileImgUrl())
                .recentFeeling(friendUser.getRecentFeeling())
                .friendOwnerId(loginUser.getId())
                .build();

        Friend friendOfLoginUser = Friend.builder()
                .nickname(loginUser.getNickname())
                .profileImgUrl(loginUser.getProfileImgUrl())
                .recentFeeling(loginUser.getRecentFeeling())
                .friendOwnerId(friendUser.getId())
                .build();

        friendOfFriendUser.confirmUser(friendUser);
        friendOfLoginUser.confirmUser(loginUser);

        friendRepository.save(friendOfFriendUser);
        friendRepository.save(friendOfLoginUser);
    }

    @Override
    public void delete(Long deleteFriendUserId) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User friendUser = userRepository.findById(deleteFriendUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        friendRepository.deleteByFriendIdAndAndFriendOwnerId(friendUser.getId(), loginUser.getId());
        friendRepository.deleteByFriendIdAndAndFriendOwnerId(loginUser.getId(), friendUser.getId());
    }

}
