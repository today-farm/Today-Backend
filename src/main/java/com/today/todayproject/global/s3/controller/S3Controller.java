package com.today.todayproject.global.s3.controller;

import com.today.todayproject.global.BaseResponse;
import com.today.todayproject.global.s3.dto.S3ConvertDto;
import com.today.todayproject.global.s3.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final S3UploadService s3UploadService;

    @PostMapping("/convert-url")
    public BaseResponse<S3ConvertDto> multipartFileConvertToString(@RequestPart(required = false) List<MultipartFile> imgFilesToConvert,
                                                     @RequestPart(required = false) List<MultipartFile> videoFilesToConvert) {
        List<String> imgUrls = s3UploadService.uploadFiles(imgFilesToConvert);
        List<String> videoUrls = s3UploadService.uploadFiles(videoFilesToConvert);
        S3ConvertDto s3ConvertDto = new S3ConvertDto(imgUrls, videoUrls);
        return new BaseResponse<>(s3ConvertDto);
    }
}
