package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.post.dto.PostSaveDto;
import com.today.todayproject.global.BaseException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    void save(PostSaveDto postSaveDto, List<MultipartFile> uploadImgs, List<MultipartFile> uploadVideos) throws Exception;
}
