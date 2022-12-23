package com.today.todayproject.domain.post.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostQuestionDto {

    private String question;
    private String content;
    private int imgCount;
    private int videoCount;
}
