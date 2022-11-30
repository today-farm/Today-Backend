package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.crop.Crop;
import com.today.todayproject.domain.crop.repository.CropRepository;
import com.today.todayproject.domain.growncrop.GrownCrop;
import com.today.todayproject.domain.growncrop.repository.GrownCropRepository;
import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.dto.*;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
    private final CropRepository cropRepository;
    private final GrownCropRepository grownCropRepository;

    private static final int CROP_HARVEST_WRITE_COUNT = 7;

    //TODO : 하루에 한번만 포스트 작성 가능하도록 처리 -> 완료
    @Override
    public PostSaveResponseDto save(PostSaveDto postSaveDto, List<MultipartFile> uploadImgs, List<MultipartFile> uploadVideos) throws Exception {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));

        if (loginUser.getCanWritePost() == false) {
            throw new BaseException(BaseResponseStatus.POST_CAN_WRITE_ONLY_ONCE_A_DAY);
        }

        Post post = Post.builder()
                .todayFeeling(postSaveDto.getTodayFeeling())
                .writer(loginUser)
                .canPublicAccess(postSaveDto.getCanPublicAccess())
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

        // before로 변수 받는 이유 -> .getPostWriteCount, .getThisMonthHarvestCount로 if 문에서 비교하는데,
        // 조건문 끝나고 다음 조건갈 때 postWriteCount가 증가, .getThisMonthHarvestCount가 초기화되어 변하므로,
        // 변하기 전 상태로 비교하기 위해 미리 변수로 받아둠
        int beforeIncreasePostWriteCount = loginUser.getPostWriteCount();

        if (beforeIncreasePostWriteCount == 0) {
            Random random = new Random();
            Crop crop = Crop.builder()
                    .cropNumber(random.nextInt(10) + 1)
                    .createdMonth(LocalDateTime.now().getMonthValue())
                    .isHarvested(false)
                    .build();

            crop.confirmUser(loginUser);
            loginUser.addPostWriteCount();
            crop.updateCropStatus(loginUser.getPostWriteCount());
            cropRepository.save(crop);
        }
        if (beforeIncreasePostWriteCount != 0) {
            Crop findCrop = cropRepository.findByUserId(loginUser.getId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CROP));
            loginUser.addPostWriteCount();
            findCrop.updateCropStatus(loginUser.getPostWriteCount());

            int beforeThisMonthHarvestCount = loginUser.getThisMonthHarvestCount();

            // thieMonthHarvestCount increase 시 4가 되면 황금 작물 수확 (황금 작물 넘버 = -1)
            if (loginUser.getPostWriteCount() == CROP_HARVEST_WRITE_COUNT && beforeThisMonthHarvestCount == 3) {
                GrownCrop goldGrownCrop = GrownCrop.builder()
                        .cropNumber(-1)
                        .build();

                goldGrownCrop.confirmUser(loginUser);
                grownCropRepository.save(goldGrownCrop);
                loginUser.increaseThisMonthHarvestCount();
                cropRepository.delete(findCrop);
                loginUser.initPostWriteCount();
            }

            //  thieMonthHarvestCount increase 시 4가 아니면 일반 작물 수확
            if (loginUser.getPostWriteCount() == CROP_HARVEST_WRITE_COUNT && beforeThisMonthHarvestCount != 3) {
                GrownCrop grownCrop = GrownCrop.builder()
                        .cropNumber(findCrop.getCropNumber())
                        .build();

                grownCrop.confirmUser(loginUser);
                grownCropRepository.save(grownCrop);
                loginUser.increaseThisMonthHarvestCount();
                cropRepository.delete(findCrop);
                loginUser.initPostWriteCount();
            }
        }

        loginUser.updateRecentFeeling(postSaveDto.getTodayFeeling());
        loginUser.writePost();
        postRepository.save(post);
        List<Long> postQuestionIds = post.getPostQuestions().stream()
                .map(postQuestion -> postQuestion.getId())
                .collect(Collectors.toList());
        return new PostSaveResponseDto(post.getId(), postQuestionIds);
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
    public PostGetMonthInfoDto getUserMonthPostInfo(Long userId, int month) {
        List<Post> findPosts = postRepository.getPostByUserIdAndMonth(userId, month);

        return new PostGetMonthInfoDto(findPosts);
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

        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_LOGIN_USER));
        Crop findCrop = cropRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CROP));
        loginUser.deletePost();
        if (loginUser.getPostWriteCount() == 0) {
            cropRepository.delete(findCrop);
        }
    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
    public void initUserCanWritePost() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.initCanWritePost();
        }
    }
}
