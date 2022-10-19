package com.today.todayproject.domain.post.content;

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
public class PostContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(mappedBy = "postContent", fetch = FetchType.LAZY)
    private PostQuestion postQuestion;

    private String content;

    /**
     * 연관관계 메소드
     */
    public void confirmPost(Post post) {
        this.post = post;
        post.getPostContents().add(this);
    }

    public void setPostQuestion(PostQuestion postQuestion) {
        this.postQuestion = postQuestion;
    }
}
