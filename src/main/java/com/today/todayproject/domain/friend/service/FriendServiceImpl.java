package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.friend.dto.FriendInfoDto;
import com.today.todayproject.domain.friend.repository.FriendRepository;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    /**
     * 친구 추가 시 로그인한 유저 <-> 친구 추가하는 유저
     * 유저 2명 모두 Friend를 생성해서 2개가 되어야하고,
     * 로그인한 유저는 친구 요청을 보냈다는 의미로 areWeFriend 필드가 true로 설정되어야 한다.
     * 친구 추가당한 유저는 친구 요청 대기 상태로, areWeFriend 필드가 false로 설정되어야 한다. (수락하면 true로 업데이트)
     */
    @Override
    public void add(Long friendId) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        Friend friendOfLoginUser = Friend.builder()
                .nickname(friendUser.getNickname())
                .profileImgUrl(friendUser.getProfileImgUrl())
                .recentFeeling(friendUser.getRecentFeeling())
                .friendOwnerId(loginUser.getId())
                .areWeFriend(true)
                .build();

        Friend friendOfFriendUser = Friend.builder()
                .nickname(loginUser.getNickname())
                .profileImgUrl(loginUser.getProfileImgUrl())
                .recentFeeling(loginUser.getRecentFeeling())
                .friendOwnerId(friendUser.getId())
                .areWeFriend(false)
                .build();

        friendOfFriendUser.confirmUser(loginUser);
        friendOfLoginUser.confirmUser(friendUser);

        friendRepository.save(friendOfFriendUser);
        friendRepository.save(friendOfLoginUser);
    }

    @Override
    public void delete(Long deleteFriendUserId) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User friendUser = userRepository.findById(deleteFriendUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        friendRepository.deleteByFriendIdAndFriendOwnerId(friendUser.getId(), loginUser.getId());
        friendRepository.deleteByFriendIdAndFriendOwnerId(loginUser.getId(), friendUser.getId());
    }

    @Override
    public List<FriendInfoDto> getFriends(Long friendOwnerId) {
        List<Friend> findFriends = friendRepository.findAllByFriendOwnerId(friendOwnerId)
                .orElse(Collections.emptyList());

        return findFriends.stream()
                .map(findFriend -> {
                    User friendUser = findFriend.getFriend();
                    Long userId = friendUser.getId();
                    String nickname = friendUser.getNickname();
                    String profileImgUrl = friendUser.getProfileImgUrl();
                    String recentFeeling = friendUser.getRecentFeeling();
                    return new FriendInfoDto(userId, nickname, profileImgUrl, recentFeeling);
                }).collect(Collectors.toList());
    }

    @Override
    public void acceptFriendRequest(Long opponentFriendId) throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        Friend friendOfLoginUser = friendRepository.findByFriendIdAndFriendOwnerId(opponentFriendId, loginUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_FRIEND));

        friendOfLoginUser.updateAreWeFriend(true);
    }

    @Override
    public void refuseFriendRequest(Long opponentFriendId) throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User friendUser = userRepository.findById(opponentFriendId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        friendRepository.deleteByFriendIdAndFriendOwnerId(friendUser.getId(), loginUser.getId());
        friendRepository.deleteByFriendIdAndFriendOwnerId(loginUser.getId(), friendUser.getId());
    }
}
