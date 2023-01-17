package com.today.todayproject.global.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploadServiceImpl implements S3UploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String deleteUrl = "https://haru-farm-resource-bucket.s3.ap-northeast-2.amazonaws.com/";

    private final AmazonS3Client amazonS3Client;

    @Override
    public String uploadFile(MultipartFile multipartFile) {

        // file에서 DB에 저장할 File URL 생성
        String fileName = createFileName(multipartFile.getOriginalFilename());

        // S3 업로드할 때 파라미터로 필요한 ObjectMetaata 생성
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        // S3에 업로드
        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        return fileName;
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
        List<String> fileNames = new ArrayList<>();

        multipartFiles.forEach(file -> {
            // file에서 DB에 저장할 File URL 생성
            String fileName = createFileName(file.getOriginalFilename());

            // S3 업로드할 때 파라미터로 필요한 ObjectMetaata 생성
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            // S3에 업로드
            try(InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            // file URL List에 파일 추가
            fileNames.add(fileName);
        });
        return fileNames;
    }

    // UUID + 확장자로 fileName 생성 메소드
    @Override
    public String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일의 확장자 추출 메소드
    @Override
    public String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ")입니다.");
        }

    }

    // DB에 저장된 URL이 fileName으로 들어옴 : https://todayproject-bucket.s3.ap-northeast-2.amazonaws.com/~~
    // deleteObject의 delete Key는 https://todayproject-bucket.s3.ap-northeast-2.amazonaws.com/을 제외한 ~~ 이므로 URL 수정
    public void deleteOriginalFile(String fileName) {
        String deleteFileName = fileName.replace(deleteUrl, "");
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, deleteFileName));
    }

    public void deleteOriginalFile(List<String> fileNames) {
        fileNames.forEach(
                file -> {
                    String deleteFileName = file.replace(deleteUrl, "");
                    amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, deleteFileName));
                });
    }
}
