package com.today.todayproject.domain.user;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.crop.Crop;
import com.today.todayproject.domain.friend.Friend;
import com.today.todayproject.domain.growncrop.GrownCrop;
import com.today.todayproject.domain.post.Post;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "USERS")
@Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email; // 이메일

    private String password; // 패스워드

    private String nickname; // 닉네임

   private String profileImgUrl; // 프로필 사진

    private String recentFeeling; // 최근 하루 작성 감정

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Crop> crops = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Friend> friendList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GrownCrop> grownCrops = new ArrayList<>();

    private int postWriteCount;

    @Builder.Default
    private Boolean canWritePost = true;

    private int thisMonthHarvestCount;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    private static final int CROP_INIT_WRITE_COUNT =0;


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }

    // == 패스워드 암호화 == //
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    // 파라미터로 들어온 비밀번호와 현재 비밀번호가 같으면 true, 다르면 false
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getPassword());
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String changePassword) {
        this.password = passwordEncoder.encode(changePassword);
    }

    public void updateNickname(String changeNickname) {
        this.nickname = changeNickname;
    }

    public void updateProfileImgUrl(String changeProfileImgUrl) {
        this.profileImgUrl = changeProfileImgUrl;
    }

    public void updateRecentFeeling(String changeRecentFeeling) {
        this.recentFeeling = changeRecentFeeling;
    }

    public void addPostWriteCount() {
        this.postWriteCount++;
    }

    public void deletePost() {
        this.postWriteCount--;
    }

    public void initPostWriteCount() {
        this.postWriteCount = 0;
    }

    public void initCanWritePost() {
        this.canWritePost = true;
    }

    public void writePost() {
        this.canWritePost = false;
    }

    public void increaseThisMonthHarvestCount() {
        thisMonthHarvestCount++;
    }

    public void initThisMonthHarvestCount() {
        thisMonthHarvestCount = 0;
    }
}
