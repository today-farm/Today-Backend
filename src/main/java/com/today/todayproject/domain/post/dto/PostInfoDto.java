package com.today.todayproject.domain.post.dto;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.question.dto.PostQuestionInfoDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostInfoDto {

    private Long postId;
    private List<PostQuestionInfoDto> postQuestions = new ArrayList<>();
    private String creationDay;
    private String todayFeeling;

    public PostInfoDto(Post post) {
        this.postId = post.getId();
        this.postQuestions = post.getPostQuestions().stream()
                .map(postQuestion -> new PostQuestionInfoDto(postQuestion))
                .collect(Collectors.toList());
        this.creationDay = convertLocalDateTimeFormatToDay(post.getCreatedDate());
        this.todayFeeling = post.getTodayFeeling();
    }

    private String convertLocalDateTimeFormatToDay(LocalDateTime createdDate) {
        int dayOfMonth = createdDate.getDayOfMonth();
        String day = "";
        if(dayOfMonth >= 1 && dayOfMonth < 10) day = "0" + dayOfMonth;
        if(dayOfMonth >= 10) day = String.valueOf(dayOfMonth);
        return day;
    }
}
