package com.today.todayproject.domain.post.service;

import com.today.todayproject.domain.crop.Crop;
import com.today.todayproject.domain.crop.repository.CropRepository;
import com.today.todayproject.domain.growncrop.GrownCrop;
import com.today.todayproject.domain.growncrop.repository.GrownCropRepository;
import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.dto.*;
import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import com.today.todayproject.domain.post.imgurl.dto.PostImgUrlDto;
import com.today.todayproject.domain.post.imgurl.repository.PostImgUrlRepository;
import com.today.todayproject.domain.post.question.PostQuestion;
import com.today.todayproject.domain.post.question.dto.PostQuestionDto;
import com.today.todayproject.domain.post.question.dto.PostQuestionUpdateDto;
import com.today.todayproject.domain.post.question.repository.PostQuestionRepository;
import com.today.todayproject.domain.post.repository.PostRepository;
import com.today.todayproject.domain.post.video.PostVideoUrl;
import com.today.todayproject.domain.post.video.dto.PostVideoUrlDto;
import com.today.todayproject.domain.post.video.repository.PostVideoUrlRepository;
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
    private final PostImgUrlRepository postImgUrlRepository;
    private final PostVideoUrlRepository postVideoUrlRepository;

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

            PostQuestion postQuestion = PostQuestion.builder()
                    .question(question)
                    .content(content)
                    .build();

            postQuestion.confirmPost(post);

            int addImgCount = postQuestionDto.getImgCount();
            int addVideoCount = postQuestionDto.getVideoCount();

            if(addImgCount != 0) {
                addImgsAndConfirmPost(uploadImgs, post, postQuestion, addImgCount);
            }

            if(addVideoCount != 0) {
                addVideosAndConfirmPost(uploadVideos, post, postQuestion, addVideoCount);
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
                        .harvestedMonth(LocalDateTime.now().getMonthValue())
                        .build();

                goldGrownCrop.confirmUser(loginUser);
                grownCropRepository.save(goldGrownCrop);
                loginUser.increaseThisMonthHarvestCount();
                findCrop.harvest();
                loginUser.initPostWriteCount();
            }

            //  thieMonthHarvestCount increase 시 4가 아니면 일반 작물 수확
            if (loginUser.getPostWriteCount() == CROP_HARVEST_WRITE_COUNT && beforeThisMonthHarvestCount != 3) {
                GrownCrop grownCrop = GrownCrop.builder()
                        .cropNumber(findCrop.getCropNumber())
                        .harvestedMonth(LocalDateTime.now().getMonthValue())
                        .build();

                grownCrop.confirmUser(loginUser);
                grownCropRepository.save(grownCrop);
                loginUser.increaseThisMonthHarvestCount();
                findCrop.harvest();
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

    private void addVideosAndConfirmPost(List<MultipartFile> uploadVideos, Post post, PostQuestion postQuestion, int addVideoCount) {
        List<PostVideoUrl> postVideoUrls = addVideos(uploadVideos, addVideoCount);
        for (PostVideoUrl postVideoUrl : postVideoUrls) {
            confirmVideoUrlPostAndPostQuestion(post, postQuestion, postVideoUrl);
        }
    }

    private void addImgsAndConfirmPost(List<MultipartFile> uploadImgs, Post post, PostQuestion postQuestion, int addImgCount) {
        List<PostImgUrl> postImgUrls = addImgs(uploadImgs, addImgCount);
        for (PostImgUrl postImgUrl : postImgUrls) {
            confirmImgUrlPostAndPostQuestion(post, postQuestion, postImgUrl);
        }
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
                       List<MultipartFile> addImgs, List<MultipartFile> addVideos) throws Exception {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));

        for (PostQuestionUpdateDto postQuestionUpdateDto : postUpdateDto.getPostQuestions()) {
            PostQuestion findPostQuestion = postQuestionRepository.findById(postQuestionUpdateDto.getQuestionId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_POST_QUESTION));
            findPostQuestion.updateContent(postQuestionUpdateDto.getContent());

            // 삭제
            deleteImgs(findPostQuestion, postQuestionUpdateDto.getDeleteImgUrlId());
            deleteVideos(findPostQuestion, postQuestionUpdateDto.getDeleteVideoUrlId());

            int addImgCount = postQuestionUpdateDto.getAddImgCount();
            int addVideoCount = postQuestionUpdateDto.getAddVideoCount();

            // 추가
            if (addImgCount != 0) {
                addImgsAndConfirmPost(addImgs, findPost, findPostQuestion, addImgCount);
            }

            if (addVideoCount != 0) {
                addVideosAndConfirmPost(addVideos, findPost, findPostQuestion, addVideoCount);
            }
        }
        findPost.updateTodayFeeling(postUpdateDto.getTodayFeeling());
        findPost.updateCanPublicAccess(postUpdateDto.getCanPublicAccess());
    }

    private void confirmImgUrlPostAndPostQuestion(Post findPost, PostQuestion findPostQuestion,
                                                  PostImgUrl postImgUrl) {
        postImgUrl.confirmPost(findPost);
        postImgUrl.confirmPostQuestion(findPostQuestion);
    }

    private void confirmVideoUrlPostAndPostQuestion(Post findPost, PostQuestion findPostQuestion,
                                                    PostVideoUrl postVideoUrl) {
        postVideoUrl.confirmPost(findPost);
        postVideoUrl.confirmPostQuestion(findPostQuestion);
    }

    private void deleteImgs(PostQuestion postQuestion, List<Long> deleteImgUrlIds) throws BaseException {
        for (Long deleteImgUrlId : deleteImgUrlIds) {
            PostImgUrl findPostImgUrl = postImgUrlRepository.findById(deleteImgUrlId)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_IMG));
            postQuestion.removeImgUrl(findPostImgUrl);
            s3UploadService.deleteOriginalFile(findPostImgUrl.getImgUrl());
        }
    }

    private void deleteVideos(PostQuestion postQuestion, List<Long> deleteVideoIds) throws BaseException {
        for (Long deleteVideoUrlId : deleteVideoIds) {
            PostVideoUrl findPostVideoUrl = postVideoUrlRepository.findById(deleteVideoUrlId)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_VIDEO));
            postQuestion.removeVideoUrl(findPostVideoUrl);
            s3UploadService.deleteOriginalFile(findPostVideoUrl.getVideoUrl());
        }
    }

    private List<PostImgUrl> addImgs(List<MultipartFile> addImgs, int addImgCount) {
        List<PostImgUrl> addPostImgUrls = new ArrayList<>();
        List<String> addImgUrls = s3UploadService.uploadFiles(addImgs);
        for (int addIndex = 0; addIndex < addImgCount; addIndex++) {
            PostImgUrl postImgUrl = PostImgUrl.builder().imgUrl(addImgUrls.get(0)).build();
            addImgUrls.remove(0);
            addPostImgUrls.add(postImgUrl);
        }
        return addPostImgUrls;
    }

    private List<PostVideoUrl> addVideos(List<MultipartFile> addVideos, int addVideoCount) {
        List<PostVideoUrl> addPostVideoUrls = new ArrayList<>();
        List<String> addVideoUrls = s3UploadService.uploadFiles(addVideos);
        for (int addIndex = 0; addIndex < addVideoCount; addIndex++) {
            PostVideoUrl postVideoUrl = PostVideoUrl.builder().videoUrl(addVideoUrls.get(0)).build();
            addVideoUrls.remove(0);
            addPostVideoUrls.add(postVideoUrl);
        }
        return addPostVideoUrls;
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
