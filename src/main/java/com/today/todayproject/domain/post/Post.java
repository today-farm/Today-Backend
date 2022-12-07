package com.today.todayproject.domain.post;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.crop.Crop;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import com.today.todayproject.domain.post.question.PostQuestion;
import com.today.todayproject.domain.post.video.PostVideoUrl;
import com.today.todayproject.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    private String todayFeeling;

    private Boolean canPublicAccess;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImgUrl> postImgUrls = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostVideoUrl> postVideoUrls = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostQuestion> postQuestions = new ArrayList<>();

    public void updateTodayFeeling(String todayFeeling) {
        this.todayFeeling = todayFeeling;
    }
    public void updateCanPublicAccess(Boolean canPublicAccess) {
        this.canPublicAccess = canPublicAccess;
    }

    /**
     * 연관관계 메소드
     */
    public void confirmWriter(User writer) {
        this.writer = writer;
        writer.getPosts().add(this);
    }

    public void confirmCrop(Crop crop) {
        this.crop = crop;
        crop.getPosts().add(this);
    }
}
