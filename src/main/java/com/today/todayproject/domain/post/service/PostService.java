package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.post.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostSaveResponseDto save(PostSaveDto postSaveDto, List<MultipartFile> uploadImgs, List<MultipartFile> uploadVideos) throws Exception;

    PostGetMonthInfoDto getUserMonthPostInfo(Long userId, int month);

    void update(Long postId, PostUpdateDto postUpdateDto,
                List<MultipartFile> updateImgs, List<MultipartFile> updateVideos) throws Exception;

    void delete(Long postId) throws Exception;
}
