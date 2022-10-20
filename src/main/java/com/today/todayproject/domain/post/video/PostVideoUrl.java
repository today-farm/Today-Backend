package com.today.todayproject.domain.post.video;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.question.PostQuestion;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostVideoUrl {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_video_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_question_id")
    private PostQuestion postQuestion;

    private String videoUrl;

    /**
     * 연관관계 편의 메소드
     */
    public void confirmPost(Post post) {
        this.post = post;
        post.getPostVideoUrls().add(this);
    }

    public void confirmPostQuestion(PostQuestion postQuestion) {
        this.postQuestion = postQuestion;
        postQuestion.getPostVideoUrls().add(this);
    }
}
