package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.dto.PostInfoDto;
import com.today.todayproject.domain.post.dto.PostSaveDto;
import com.today.todayproject.domain.post.dto.PostUpdateDto;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import com.today.todayproject.domain.post.question.PostQuestion;
import com.today.todayproject.domain.post.question.dto.PostQuestionDto;
import com.today.todayproject.domain.post.question.dto.PostQuestionUpdateDto;
import com.today.todayproject.domain.post.question.repository.PostQuestionRepository;
import com.today.todayproject.domain.post.repository.PostRepository;
import com.today.todayproject.domain.post.video.PostVideoUrl;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.s3.service.S3UploadService;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService{

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostQuestionRepository postQuestionRepository;
    private final S3UploadService s3UploadService;

    //TODO : 하루에 한번만 포스트 작성 가능하도록 처리
    @Override
    public List<Long> save(PostSaveDto postSaveDto, List<MultipartFile> uploadImgs, List<MultipartFile> uploadVideos) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        Post post = Post.builder()
                .todayFeeling(postSaveDto.getTodayFeeling())
                .writer(loginUser)
                .build();

        List<PostQuestionDto> postQuestions = postSaveDto.getPostQuestions();
        // Questions(질문들) 돌면서 content, imgUrl, videoUrl 설정
        postQuestions.stream().forEach(postQuestionDto -> {
            String question = postQuestionDto.getQuestion();
            String content = postQuestionDto.getContent();
            int imgCount = postQuestionDto.getImgCount();
            int videoCount = postQuestionDto.getVideoCount();
            PostQuestion postQuestion = PostQuestion.builder()
                    .question(question)
                    .content(content)
                    .build();

            postQuestion.confirmPost(post);

            // imgCount에 맞게 돌면서 extractImgUrls에 ImgUrl 담기 -> Post의 URL에서 Question의 ImgUrl를 담은 리스트
            if(imgCount != 0) {
                List<String> extractImgUrls = new ArrayList<>(); // 해당 Question의 URL 리스트 선언
                List<String> imgUrls = s3UploadService.uploadFiles(uploadImgs);
                for(int imgUrlIndex = 0; imgUrlIndex < imgCount; imgUrlIndex++) {
                    extractImgUrls.add(imgUrls.get(0));
                    imgUrls.remove(0);
                }
                addImgUrl(extractImgUrls, post, postQuestion);
            }

            // imgCount에 맞게 돌면서 extractVideoUrls에 VideoUrl 담기 -> Post의 URL에서 Question의 VideoUrl를 담은 리스트
            if(videoCount != 0) {
                List<String> extractVideoUrls = new ArrayList<>(); // 해당 Question의 Video URL 리스트 선언
                List<String> videoUrls = s3UploadService.uploadFiles(uploadVideos);
                for(int videoUrlIndex = 0; videoUrlIndex < videoCount; videoUrlIndex++) {
                    extractVideoUrls.add(videoUrls.get(0));
                    videoUrls.remove(0);
                }
                addVideoUrl(extractVideoUrls, post, postQuestion);
            }
        });
        loginUser.updateRecentFeeling(postSaveDto.getTodayFeeling());
        postRepository.save(post);
        List<Long> postQuestionIds = post.getPostQuestions().stream()
                .map(postQuestion -> postQuestion.getId())
                .collect(Collectors.toList());
        return postQuestionIds;
    }

    // 파라미터로 들어온 이미지 URL 리스트(ImgUrls)를 forEach로 PostImgUrl을 생성하고, 연관관계 설정
    private void addImgUrl(List<String> extractImgUrls, Post post, PostQuestion postQuestion) {
        extractImgUrls.stream().forEach(imgUrl -> {
                PostImgUrl postImgUrl = PostImgUrl.builder().imgUrl(imgUrl).build();
                postImgUrl.confirmPost(post);
                postImgUrl.confirmPostQuestion(postQuestion);
        });
    }

    // 파라미터로 들어온 영상 URL 리스트(VideoUrls)를 forEach로 PostVideoUrl을 생성하고, 연관관계 설정
    private void addVideoUrl(List<String> extractVideoUrls, Post post, PostQuestion postQuestion) {
        extractVideoUrls.stream().forEach(videoUrl -> {
                PostVideoUrl postVideoUrl = PostVideoUrl.builder().videoUrl(videoUrl).build();
                postVideoUrl.confirmPost(post);
                postVideoUrl.confirmPostQuestion(postQuestion);
        });
    }

    @Override
    public PostInfoDto getPostInfo(Long postId) throws Exception {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));

        return new PostInfoDto(findPost);
    }

    @Override
    public void update(Long postId, PostUpdateDto postUpdateDto,
                       List<MultipartFile> updateImgs, List<MultipartFile> updateVideos) throws Exception {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));

        postUpdateDto.getPostQuestions().stream()
                .forEach(postQuestionUpdateDto -> {
                    try {
                        PostQuestion findPostQuestion = postQuestionRepository.findById(postQuestionUpdateDto.getQuestionId())
                                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_POST_QUESTION));
                        findPostQuestion.updateContent(postQuestionUpdateDto.getContent());
                        int imgCount = postQuestionUpdateDto.getImgCount();
                        int videoCount = postQuestionUpdateDto.getVideoCount();
                        if(imgCount != 0) updateImgUrl(findPostQuestion, findPost, updateImgs, imgCount);
                        if(videoCount != 0) updateVideoUrl(findPostQuestion, findPost, updateVideos, videoCount);
                    } catch (BaseException e) {
                        e.printStackTrace();
                    }
                });
        findPost.updateTodayFeeling(postUpdateDto.getTodayFeeling());
    }

    public void updateImgUrl(PostQuestion postQuestion, Post post, List<MultipartFile> updateImgs, int imgCount) {
        postQuestion.getPostImgUrls().stream()
                        .forEach(postImgUrl -> {
                            s3UploadService.deleteOriginalFile(postImgUrl.getImgUrl());
                            postQuestion.removeImgUrl(postImgUrl);
                        });

        List<String> updateImgUrls = new ArrayList<>();
        for(int imgIndex = 0; imgIndex < imgCount; imgIndex++) {
            String updateImgUrl = s3UploadService.uploadFile(updateImgs.get(0));
            updateImgUrls.add(updateImgUrl);
            updateImgs.remove(0);
        }

        updateImgUrls.stream()
               .forEach(imgUrl -> {
                    PostImgUrl postImgUrl = PostImgUrl.builder().imgUrl(imgUrl).build();
                    postImgUrl.confirmPost(post);
                    postImgUrl.confirmPostQuestion(postQuestion);
                });
    }

    public void updateVideoUrl(PostQuestion postQuestion, Post post, List<MultipartFile> updateVideos, int videoCount) {
        postQuestion.getPostVideoUrls().stream()
                .forEach(postVideoUrl -> {
                    s3UploadService.deleteOriginalFile(postVideoUrl.getVideoUrl());
                    postQuestion.removeVideoUrl(postVideoUrl);
                });

        List<String> updateVideoUrls = new ArrayList<>();
        for(int videoIndex = 0; videoIndex < videoCount; videoIndex++) {
            String updateVideoUrl = s3UploadService.uploadFile(updateVideos.get(0));
            updateVideoUrls.add(updateVideoUrl);
            updateVideos.remove(0);
        }

        updateVideoUrls.stream()
                .forEach(videoUrl -> {
                    PostVideoUrl postVideoUrl = PostVideoUrl.builder().videoUrl(videoUrl).build();
                    postVideoUrl.confirmPost(post);
                    postVideoUrl.confirmPostQuestion(postQuestion);
                });
    }

    @Override
    public void delete(Long postId) throws Exception {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_POST_QUESTION));

        findPost.getPostImgUrls().stream()
                        .forEach(postImgUrl -> s3UploadService.deleteOriginalFile(postImgUrl.getImgUrl()));

        findPost.getPostVideoUrls().stream()
                        .forEach(postVideoUrl -> s3UploadService.deleteOriginalFile(postVideoUrl.getVideoUrl()));

        postRepository.delete(findPost);
    }

    @Override
    public List<String> getCreationDates(Long userId) throws Exception {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        List<Post> findPosts = postRepository.findAllByWriterIdOrderByCreatedDateAsc(findUser.getId())
                .orElse(Collections.emptyList());

        return findPosts.stream()
                .map(findPost -> {
                    LocalDateTime createdDate = findPost.getCreatedDate();
                    String year = String.valueOf(createdDate.getYear());
                    String month = String.valueOf(createdDate.getMonthValue());
                    int dayOfMonth = createdDate.getDayOfMonth();
                    String day = "";
                    if(dayOfMonth >= 1 && dayOfMonth < 10) day = "0" + dayOfMonth;
                    if(dayOfMonth >= 10) day = String.valueOf(dayOfMonth);
                    return year + "-" + month + "-" + day;
                })
                .collect(Collectors.toList());
    }
}
