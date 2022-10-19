package com.today.todayproject.domain.post.video;

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
public class PostVideo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_video_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_question_id")
    private PostQuestion postQuestion;

    private String videoUrl;

    /**
     * 연관관계 편의 메소드
     */
    public void confirmPostQuestion(PostQuestion postQuestion) {
        this.postQuestion = postQuestion;
        postQuestion.getPostVideos().add(this);
    }
}
