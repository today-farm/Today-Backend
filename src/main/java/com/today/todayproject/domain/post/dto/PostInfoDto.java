package com.today.todayproject.domain.post.dto;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.question.dto.PostQuestionInfoDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostInfoDto {

    private Long postId;
    private List<PostQuestionInfoDto> postQuestions = new ArrayList<>();
    private String todayFeeling;

    public PostInfoDto(Post post) {
        this.postId = post.getId();
        this.postQuestions = post.getPostQuestions().stream()
                .map(postQuestion -> new PostQuestionInfoDto(postQuestion))
                .collect(Collectors.toList());
        this.todayFeeling = post.getTodayFeeling();
    }
}
