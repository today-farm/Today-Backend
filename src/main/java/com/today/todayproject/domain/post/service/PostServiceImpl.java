package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.dto.PostSaveDto;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import com.today.todayproject.domain.post.question.PostQuestion;
import com.today.todayproject.domain.post.question.dto.PostQuestionDto;
import com.today.todayproject.domain.post.repository.PostRepository;
import com.today.todayproject.domain.post.video.PostVideoUrl;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.s3.service.S3UploadService;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final S3UploadService s3UploadService;

    public void save(PostSaveDto postSaveDto, List<MultipartFile> uploadImgs, List<MultipartFile> uploadVideos) throws Exception {
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

            if(imgCount != 0) {
                List<String> extractImgUrls = new ArrayList<>(); // 해당 Question의 URL 리스트 선언
                List<String> imgUrls = s3UploadService.uploadFiles(uploadImgs);
                for(int imgUrlIndex = 0; imgUrlIndex < imgCount; imgUrlIndex++) {
                    extractImgUrls.add(imgUrls.get(0));
                    imgUrls.remove(0);
                }
                addImgUrl(extractImgUrls, post, postQuestion);
            }

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
        postRepository.save(post);
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
}
