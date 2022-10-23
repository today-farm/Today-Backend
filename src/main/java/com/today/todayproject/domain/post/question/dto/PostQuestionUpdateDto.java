package com.today.todayproject.domain.post.question.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostQuestionUpdateDto {

    private Long questionId;
    private String content;
    private int imgCount;
    private int videoCount;
}
