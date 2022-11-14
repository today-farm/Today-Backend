package com.today.todayproject.domain.friend;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.user.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Friend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id")
    private User friend; // 친구인 유저 id (ex : 4번)

    private Long friendOwnerId; // 로그인한 유저 id (ex : 1번)

    @ColumnDefault("false")
    private Boolean areWeFriend; // 서로 친구인지 확인하는 필드 (친구 요청 수락된 상태인지)

    private String nickname; // 닉네임

    private String profileImgUrl; // 프로필 사진

    private String recentFeeling; // 최근 감정 상태


    /**
     * 연관관계 메소드
     */
    public void confirmUser(User friend) {
        this.friend = friend;
        friend.getFriendList().add(this);
    }

    public void updateAreWeFriend(boolean updateAreWeFriend) {
        this.areWeFriend = updateAreWeFriend;
    }
}
