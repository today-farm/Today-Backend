package com.today.todayproject.domain.post.question.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostQuestionUpdateDto {

    private Long questionId;
    private String content;
    private List<Long> deleteImgUrlId;
    private List<Long> deleteVideoUrlId;
    private int addImgCount;
    private int addVideoCount;
}
