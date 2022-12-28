package com.today.todayproject.global.util;

import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public class GenerateDummy {

    public static User generateDummyUser(String email, String password, String nickname,
                                         String profileImgUrl, Role role) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .role(role)
                .emailAuth(true)
                .build();
    }

    public static List<Friend> generateDummyFriend(User friendOwnerUser, User friendUser) {
        List<Friend> friends = new ArrayList<>();
        // 친구 요청한 친구 Row 생성
        generateRequestFriendAndAddFriends(friendOwnerUser, friendUser, friends);

        // 친구 요청 받은 친구 Row 생성
        generateRequestedFriendAndAddFriends(friendOwnerUser, friendUser, friends);
        return friends;
    }

    private static void generateRequestedFriendAndAddFriends(User friendOwnerUser, User friendUser, List<Friend> friends) {
        friends.add(Friend.builder()
                .friendOwnerId(friendUser.getId())
                .friend(friendOwnerUser)
                .areWeFriend(false)
                .nickname(friendOwnerUser.getNickname())
                .build());
    }

    private static void generateRequestFriendAndAddFriends(User friendOwnerUser, User friendUser, List<Friend> friends) {
        friends.add(Friend.builder()
                .friendOwnerId(friendOwnerUser.getId())
                .friend(friendUser)
                .areWeFriend(true)
                .nickname(friendUser.getNickname())
                .build());
    }
}
