package com.today.todayproject.global.s3.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface S3UploadService {

    // S3에 파일 업로드
    List<String> uploadFile(List<MultipartFile> multipartFiles);

    // File 확장자와 UUID로 URL 생성
    String createFileName(String fileName);

    // 파일 확장자 추출
    String getFileExtension(String fileName);
}
