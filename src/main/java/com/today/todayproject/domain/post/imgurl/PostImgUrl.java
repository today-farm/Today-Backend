package com.today.todayproject.domain.post.imgurl;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.question.PostQuestion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostImgUrl extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_imgurl_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_question_id")
    private PostQuestion postQuestion;

    private String imgUrl;

    /**
     * 연관관계 메소드
     */
    public void confirmPost(Post post) {
        this.post = post;
        post.getPostImgUrlList().add(this);
    }

    public void confirmPostQuestion(PostQuestion postQuestion) {
        this.postQuestion = postQuestion;
        postQuestion.getPostImgUrls().add(this);
    }
}
