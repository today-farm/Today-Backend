package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.post.dto.*;
import com.today.todayproject.global.BaseException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    PostSaveResponseDto save(PostSaveDto postSaveDto, List<MultipartFile> uploadImgs, List<MultipartFile> uploadVideos) throws Exception;

    PostInfoDto getPostInfo(Long postId) throws Exception; // 상세 조회

    PostGetMonthInfoDto getUserMonthPostInfo(Long userId, int month); // 그 달의 유저가 작성한 하루 전체 조회

    void update(Long postId, PostUpdateDto postUpdateDto,
                List<MultipartFile> updateImgs, List<MultipartFile> updateVideos) throws Exception;

    void delete(Long postId) throws Exception;

}
