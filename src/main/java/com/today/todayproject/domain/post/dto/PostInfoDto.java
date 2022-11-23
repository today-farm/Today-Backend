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
    private String creationDate;
    private List<PostQuestionInfoDto> postQuestions = new ArrayList<>();
    private String todayFeeling;

    public PostInfoDto(Post post) {
        this.postId = post.getId();
        this.creationDate = convertLocalDateTimeFormat(post);
        this.postQuestions = post.getPostQuestions().stream()
                .map(postQuestion -> new PostQuestionInfoDto(postQuestion))
                .collect(Collectors.toList());
        this.todayFeeling = post.getTodayFeeling();
    }

    private String convertLocalDateTimeFormat(Post post) {
        LocalDateTime createdDate = post.getCreatedDate();
        String year = String.valueOf(createdDate.getYear());
        String month = String.valueOf(createdDate.getMonthValue());
        int dayOfMonth = createdDate.getDayOfMonth();
        String day = "";
        if(dayOfMonth >= 1 && dayOfMonth < 10) day = "0" + dayOfMonth;
        if(dayOfMonth >= 10) day = String.valueOf(dayOfMonth);
        return year + "-" + month + "-" + day;
    }
}
