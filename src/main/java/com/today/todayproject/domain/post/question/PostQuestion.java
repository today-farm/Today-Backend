package com.today.todayproject.domain.post.question;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.content.PostContent;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String question;

    @OneToMany(mappedBy = "postQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImgUrl> postImgUrls = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_content_id")
    private PostContent postContent;

    /**
     * 연관관계 메소드
     */
    public void confirmPost(Post post) {
        this.post = post;
        post.getPostQuestionList().add(this);
    }
}
