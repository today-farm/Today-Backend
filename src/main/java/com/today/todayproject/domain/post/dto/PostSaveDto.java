package com.today.todayproject.domain.post.dto;

import com.today.todayproject.domain.post.question.dto.PostQuestionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSaveDto {

    private List<PostQuestionDto> postQuestions;
    private String todayFeeling;
    private Boolean canPublicAccess;
}
