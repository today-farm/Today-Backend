package com.today.todayproject.domain.post.controller;

import com.today.todayproject.domain.post.dto.PostInfoDto;
import com.today.todayproject.domain.post.dto.PostSaveDto;
import com.today.todayproject.domain.post.dto.PostSaveResponseDto;
import com.today.todayproject.domain.post.dto.PostUpdateDto;
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
    public BaseResponse<PostSaveResponseDto> save(
            @RequestPart PostSaveDto postSaveDto,
            @RequestPart(required = false) List<MultipartFile> uploadImgs,
            @RequestPart(required = false) List<MultipartFile> uploadVideos) throws Exception {
        List<Long> postQuestionIds = postService.save(postSaveDto, uploadImgs, uploadVideos);
        PostSaveResponseDto postSaveResponseDto = new PostSaveResponseDto(postQuestionIds);
        return new BaseResponse<>(postSaveResponseDto);
    }

    @GetMapping("/{postId}")
    public BaseResponse<PostInfoDto> getPostInfo(@PathVariable("postId") Long postId) throws Exception {
        PostInfoDto postInfoDto = postService.getPostInfo(postId);
        return new BaseResponse<>(postInfoDto);
    }

    @PatchMapping("/update/{postId}")
    public BaseResponse<String> update(
            @PathVariable("postId") Long postId,
            @RequestPart PostUpdateDto postUpdateDto,
            @RequestPart List<MultipartFile> updateImgs,
            @RequestPart List<MultipartFile> updateVideos) throws Exception {
        postService.update(postId, postUpdateDto, updateImgs, updateVideos);
        return new BaseResponse<>("하루 수정에 성공하였습니다.");
    }

    @DeleteMapping("/delete/{postId}")
    public BaseResponse<String> delete(@PathVariable("postId") Long postId) throws Exception {
        postService.delete(postId);
        return new BaseResponse<>("하루 삭제에 성공하였습니다.");
    }
}
