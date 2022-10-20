package com.today.todayproject.domain.post;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.post.content.PostContent;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import com.today.todayproject.domain.post.question.PostQuestion;
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
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String todayFeeling;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImgUrl> postImgUrls = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVideoUrl> postVideoUrls = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostContent> postContents = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostQuestion> postQuestions = new ArrayList<>();

}
