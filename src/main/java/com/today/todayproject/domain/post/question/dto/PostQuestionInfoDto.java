package com.today.todayproject.domain.post.question.dto;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.imgurl.dto.PostImgUrlDto;
import com.today.todayproject.domain.post.question.PostQuestion;
import com.today.todayproject.domain.post.video.dto.PostVideoUrlDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostQuestionInfoDto {

    private String question;
    private String content;
    private List<PostImgUrlDto> postImgUrls = new ArrayList<>();
    private List<PostVideoUrlDto> postVideoUrls = new ArrayList<>();
    private String todayFeeling;

    public PostQuestionInfoDto(PostQuestion postQuestion, Post post) {
        this.question = postQuestion.getQuestion();
        this.content = postQuestion.getContent();
        this.postImgUrls = postQuestion.getPostImgUrls().stream()
                .map(postImgUrl -> new PostImgUrlDto(postImgUrl))
                .collect(Collectors.toList());
        this.postVideoUrls = postQuestion.getPostVideoUrls().stream()
                .map(postVideoUrl -> new PostVideoUrlDto(postVideoUrl))
                .collect(Collectors.toList());
        this.todayFeeling = post.getTodayFeeling();
    }
}
