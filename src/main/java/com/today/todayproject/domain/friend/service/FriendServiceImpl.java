package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.friend.dto.FriendInfoDto;
import com.today.todayproject.domain.friend.dto.FriendRequestInfoDto;
import com.today.todayproject.domain.friend.repository.FriendRepository;
import com.today.todayproject.domain.notification.NotificationType;
import com.today.todayproject.domain.notification.service.NotificationService;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

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

        checkDuplicateRequest(loginUser, friendUser);

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

        String notificationContent = loginUser.getNickname() + "님이 친구 요청을 보냈습니다.";
        notificationService.send(friendUser, NotificationType.FRIEND_REQUEST, notificationContent);
    }

    private void checkDuplicateRequest(User loginUser, User friendUser) throws BaseException {
        if (friendRepository.existsByFriendOwnerIdAndFriend(loginUser.getId(), friendUser)) {
            throw new BaseException(BaseResponseStatus.NOT_DUPLICATE_FRIEND_REQUEST);
        }
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
    public List<FriendInfoDto> getFriends(Long friendOwnerId) throws BaseException {
        checkInquiryUserIsLoginUser(friendOwnerId);
        // friendUserId가 FriendOwnerId인 데이터 찾기 (로그인된 유저와 친구되어 있는 친구 행 찾기)
        List<Friend> findFriends = friendRepository.findAllByFriendIdOrderByAreWeFriend(friendOwnerId)
                .orElse(Collections.emptyList());

        List<FriendInfoDto> friendInfoDtos = new ArrayList<>();

        for (Friend friendOfLoginUser : findFriends) {
            User friendUser = userRepository.findById(friendOfLoginUser.getFriendOwnerId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
            Long userId = friendUser.getId();
            String nickname = friendUser.getNickname();
            String profileImgUrl = friendUser.getProfileImgUrl();
            String recentFeeling = friendUser.getRecentFeeling();
            Boolean isFriend = friendOfLoginUser.getAreWeFriend();
            friendInfoDtos.add(new FriendInfoDto(userId, nickname, profileImgUrl, recentFeeling, isFriend));
        }
        return friendInfoDtos;
    }

    private void checkInquiryUserIsLoginUser(Long friendOwnerId) throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));
        if (!loginUser.getId().equals(friendOwnerId)) {
            throw new BaseException(BaseResponseStatus.NOT_ACCESS_FRIEND_LIST);
        }
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

    @Override
    public List<FriendRequestInfoDto> getRequestedFriendUsers(Long loginUserId) throws BaseException {
        checkInquiryUserIsLoginUser(loginUserId);
        // friendOwnerId가 로그인한 유저 Id고, areWeFriend가 false인 데이터 찾기
        // (로그인된 유저가 친구 요청을 받은 친구 찾기)
        List<Friend> findFriends = friendRepository.findAllByFriendOwnerIdAndAreWeFriendIsFalse(loginUserId)
                .orElse(Collections.emptyList());

        List<FriendRequestInfoDto> friendRequestInfoDtos = new ArrayList<>();

        for (Friend requestedFriend : findFriends) {
            User friendUser = requestedFriend.getFriend();
            Long userId = friendUser.getId();
            String nickname = friendUser.getNickname();
            String profileImgUrl = friendUser.getProfileImgUrl();
            friendRequestInfoDtos.add(new FriendRequestInfoDto(userId, nickname, profileImgUrl));
        }
        return friendRequestInfoDtos;
    }
}
