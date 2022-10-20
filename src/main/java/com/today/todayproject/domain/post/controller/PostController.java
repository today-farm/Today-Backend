package com.today.todayproject.domain.post.controller;

import com.today.todayproject.domain.post.dto.PostInfoDto;
import com.today.todayproject.domain.post.dto.PostSaveDto;
import com.today.todayproject.domain.post.service.PostService;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping("/save")
    public BaseResponse<String> save(
            @RequestPart PostSaveDto postSaveDto,
            @RequestPart(required = false) List<MultipartFile> uploadImgs,
            @RequestPart(required = false) List<MultipartFile> uploadVideos) throws Exception {
        postService.save(postSaveDto, uploadImgs, uploadVideos);
        return new BaseResponse<>("하루 작성에 성공하였습니다.");
    }

    @GetMapping("/{postId}")
    public BaseResponse<PostInfoDto> getPostInfo(@PathVariable("postId") Long postId) throws Exception {
        PostInfoDto postInfoDto = postService.getPostInfo(postId);
        return new BaseResponse<>(postInfoDto);
    }
}
