package com.today.todayproject.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostMonthlyCreationDateResponseDto {

    private List<Integer> postCreationDate;
}
