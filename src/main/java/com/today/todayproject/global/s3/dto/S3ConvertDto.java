package com.today.todayproject.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3ConvertDto {

    private List<String> imgUrls;
    private List<String> videoUrls;
}
