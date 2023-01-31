package com.today.todayproject.domain.friend.service;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.friend.dto.*;
import com.today.todayproject.domain.friend.repository.FriendRepositoryImpl;
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
    private final FriendRepositoryImpl friendRepository;
    private final NotificationService notificationService;

    /**
     * 친구 추가 시 로그인한 유저 <-> 친구 추가하는 유저
     * 유저 2명 모두 Friend를 생성해서 2개가 되어야하고,
     * 로그인한 유저는 친구 요청을 보냈다는 의미로 areWeFriend 필드가 true로 설정되어야 한다.
     * 친구 추가당한 유저는 친구 요청 대기 상태로, areWeFriend 필드가 false로 설정되어야 한다. (수락하면 true로 업데이트)
     */
    @Override
    public void add(Long toUserId) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        checkDuplicateRequest(loginUser, toUser);

        Friend friendOfFromUser = Friend.builder()
                .nickname(toUser.getNickname())
                .profileImgUrl(toUser.getProfileImgUrl())
                .recentFeeling(toUser.getRecentFeeling())
                .fromUserId(loginUser.getId())
                .areWeFriend(true)
                .build();

        Friend friendOfToUser = Friend.builder()
                .nickname(loginUser.getNickname())
                .profileImgUrl(loginUser.getProfileImgUrl())
                .recentFeeling(loginUser.getRecentFeeling())
                .fromUserId(toUser.getId())
                .areWeFriend(false)
                .build();

        friendOfToUser.confirmUser(loginUser);
        friendOfFromUser.confirmUser(toUser);

        friendRepository.save(friendOfToUser);
        friendRepository.save(friendOfFromUser);

        String notificationContent = loginUser.getNickname() + "님이 친구 요청을 보냈습니다.";
        notificationService.send(toUser, NotificationType.FRIEND_REQUEST, notificationContent);
    }

    private void checkDuplicateRequest(User fromUser, User toUser) throws BaseException {
        if (friendRepository.existsByFromUserIdAndToUser(fromUser.getId(), toUser)) {
            throw new BaseException(BaseResponseStatus.NOT_DUPLICATE_FRIEND_REQUEST);
        }
    }

    @Override
    public void delete(Long deleteToUserId) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User toUser = userRepository.findById(deleteToUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        friendRepository.deleteByToUserIdAndFromUserId(toUser.getId(), loginUser.getId());
        friendRepository.deleteByToUserIdAndFromUserId(loginUser.getId(), toUser.getId());
    }

    @Override
    public GetFriendsResponseDto getFriends(Long fromUserId) throws BaseException {
        checkInquiryUserIsLoginUser(fromUserId);

        List<Friend> allSendRequestFriends = friendRepository.findAllSendRequestFriends(fromUserId);
        List<Friend> allFriendsWithEachOther = friendRepository.findAllFriendsWithEachOther(fromUserId);

        List<SendRequestFriendInfoDto> sendRequestFriendInfoDtos =
                generateSendRequestFriendInfo(allSendRequestFriends);
        List<FriendWithEachOtherInfoDto> friendWithEachOtherInfoDtos =
                generateFriendWithEachOtherInfo(allFriendsWithEachOther);

        return new GetFriendsResponseDto(sendRequestFriendInfoDtos, friendWithEachOtherInfoDtos);
    }

    private void checkInquiryUserIsLoginUser(Long fromUserId) throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));
        if (!loginUser.getId().equals(fromUserId)) {
            throw new BaseException(BaseResponseStatus.NOT_ACCESS_FRIEND_LIST);
        }
    }

    private List<SendRequestFriendInfoDto> generateSendRequestFriendInfo(List<Friend> allSendRequestFriends)
            throws BaseException {
        List<SendRequestFriendInfoDto> sendRequestFriendInfoDtos = new ArrayList<>();
        for (Friend sendRequestFriend : allSendRequestFriends) {
            User requestFriend = userRepository.findById(sendRequestFriend.getFromUserId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

            sendRequestFriendInfoDtos.add(
                    new SendRequestFriendInfoDto(requestFriend.getId(),
                            requestFriend.getNickname(), requestFriend.getProfileImgUrl())
            );
        }
        return sendRequestFriendInfoDtos;
    }

    private List<FriendWithEachOtherInfoDto> generateFriendWithEachOtherInfo(List<Friend> allFriendsWithEachOther)
            throws BaseException {
        List<FriendWithEachOtherInfoDto> friendWithEachOtherInfoDtos = new ArrayList<>();
        for (Friend friendWithEachOther : allFriendsWithEachOther) {
            User friendWithEachOtherUser = userRepository.findById(friendWithEachOther.getToUser().getId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
            friendWithEachOtherInfoDtos.add(
                    new FriendWithEachOtherInfoDto(friendWithEachOtherUser.getId(),
                            friendWithEachOtherUser.getNickname(), friendWithEachOtherUser.getProfileImgUrl())
            );
        }
        return friendWithEachOtherInfoDtos;
    }

    @Override
    public void acceptFriendOneRequest(Long toUserId) throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        Friend friendOfFromUser = friendRepository.findByToUserIdAndFromUserId(toUserId, loginUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_FRIEND));

        friendOfFromUser.updateAreWeFriend(true);
    }

    @Override
    public void acceptFriendAllRequest() throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        List<Friend> allReceiveRequestFriends = friendRepository.findAllReceiveRequestFriends(loginUser.getId());
        for (Friend receiveRequestFriend : allReceiveRequestFriends) {
            receiveRequestFriend.updateAreWeFriend(true);
        }
    }

    @Override
    public void refuseFriendRequest(Long toUserId) throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        friendRepository.deleteByToUserIdAndFromUserId(toUser.getId(), loginUser.getId());
        friendRepository.deleteByToUserIdAndFromUserId(loginUser.getId(), toUser.getId());
    }

    @Override
    public FriendFindRequestResponseDto getRequestedFriendUsers() throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));
        Long loginUserId = loginUser.getId();
        // fromUserId가 로그인한 유저 Id고, areWeFriend가 false인 데이터 찾기
        // (로그인된 유저가 친구 요청을 받은 친구 찾기)
        List<Friend> allReceiveRequestFriends = friendRepository.findAllReceiveRequestFriends(loginUserId);

        List<ReceiveRequestFriendInfoDto> receiveRequestFriendInfoDtos =
                generateReceiveRequestFriendInfo(allReceiveRequestFriends);

        return new FriendFindRequestResponseDto(receiveRequestFriendInfoDtos);
    }

    private List<ReceiveRequestFriendInfoDto> generateReceiveRequestFriendInfo(List<Friend> allReceiveRequestFriends)
            throws BaseException {
        List<ReceiveRequestFriendInfoDto> receiveRequestFriendInfoDtos = new ArrayList<>();
        for (Friend receiveRequestFriend : allReceiveRequestFriends) {
            User friendWithEachOtherUser = userRepository.findById(receiveRequestFriend.getToUser().getId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
            receiveRequestFriendInfoDtos.add(
                    new ReceiveRequestFriendInfoDto(friendWithEachOtherUser.getId(),
                            friendWithEachOtherUser.getNickname(), friendWithEachOtherUser.getProfileImgUrl())
            );
        }
        return receiveRequestFriendInfoDtos;
    }
}
