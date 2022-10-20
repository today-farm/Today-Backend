package com.today.todayproject.domain.post.question;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import com.today.todayproject.domain.post.video.PostVideoUrl;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String question;

    private String content;

    @OneToMany(mappedBy = "postQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImgUrl> postImgUrls = new ArrayList<>();

    @OneToMany(mappedBy = "postVideo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVideoUrl> postVideoUrls = new ArrayList<>();

    /**
     * 연관관계 메소드
     */
    public void confirmPost(Post post) {
        this.post = post;
        post.getPostQuestions().add(this);
    }
}
