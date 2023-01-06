//package com.today.todayproject.domain.post.service;
//
//import com.today.todayproject.domain.crop.Crop;
//import com.today.todayproject.domain.crop.CropStatus;
//import com.today.todayproject.domain.crop.repository.CropRepository;
//import com.today.todayproject.domain.growncrop.GrownCrop;
//import com.today.todayproject.domain.growncrop.repository.GrownCropRepository;
//import com.today.todayproject.domain.post.Post;
//import com.today.todayproject.domain.post.dto.*;
//import com.today.todayproject.domain.post.question.PostQuestion;
//import com.today.todayproject.domain.post.question.dto.PostQuestionDto;
//import com.today.todayproject.domain.post.question.dto.PostQuestionUpdateDto;
//import com.today.todayproject.domain.post.repository.PostRepository;
//import com.today.todayproject.domain.user.Role;
//import com.today.todayproject.domain.user.User;
//import com.today.todayproject.domain.user.repository.UserRepository;
//import com.today.todayproject.domain.user.service.UserService;
//import com.today.todayproject.global.BaseException;
//import com.today.todayproject.global.BaseResponseStatus;
//import com.today.todayproject.global.util.GenerateDummy;
//import com.today.todayproject.global.util.SecurityUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.persistence.EntityManager;
//import javax.transaction.Transactional;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//class PostServiceImplTest {
//
//    @Autowired
//    EntityManager em;
//
//    @Autowired
//    PostService postService;
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    PostRepository postRepository;
//
//    @Autowired
//    CropRepository cropRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    GrownCropRepository grownCropRepository;
//
//    private int imgCount = 2;
//    private int videoCount = 2;
//
//    @BeforeEach
//    // 여러 로직들에 SecurityUtil.getLoginUserEmail()로 로그인한 유저의 이메일을 가져오는 기능을 사용하므로
//    // 회원 가입한 유저를 인증 객체로 설정해준다.
//    void setAuthenticatedUser() throws Exception {
//        User user = GenerateDummy.generateDummyUser("test1@naver.com", "1234", "KSH1",
//                "s3://imgUrl1", Role.USER);
//        em.persist(user);
//        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
//
//        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
//                .username("test1@naver.com")
//                .password("1234")
//                .roles(Role.USER.name())
//                .build();
//
//        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(
//                userDetailsUser, null, null));
//
//        SecurityContextHolder.setContext(emptyContext);
//    }
//
//    private MockMultipartFile generateMultipartFileImage(String originalFilename) throws IOException {
//        int dotIndex = originalFilename.lastIndexOf(".");
//        String contentType = originalFilename.substring(dotIndex + 1);
//        return new MockMultipartFile(
//                "images",
//                originalFilename,
//                contentType,
//                new FileInputStream("src/test/resources/testimage/" + originalFilename));
//    }
//
//    private MockMultipartFile generateMultipartFileVideo(String originalFilename) throws IOException {
//        int dotIndex = originalFilename.lastIndexOf(".");
//        String contentType = originalFilename.substring(dotIndex + 1);
//        return new MockMultipartFile(
//                "videos",
//                originalFilename,
//                contentType,
//                new FileInputStream("src/test/resources/testvideo/" + originalFilename));
//    }
//
//    private PostSaveResponseDto postSave(
//            String question, String content, String todayFeeling, boolean canPublicAccess) throws Exception {
//        List<MultipartFile> uploadImgs = getTwoUploadImgs();
//        List<MultipartFile> uploadVideos = getTwoUploadVideos();
//        PostQuestionDto postQuestionDto = new PostQuestionDto(question, content, imgCount, videoCount);
//        PostSaveDto postSaveDto = new PostSaveDto(List.of(postQuestionDto), todayFeeling, canPublicAccess);
//        return postService.save(postSaveDto, uploadImgs, uploadVideos);
//    }
//
//    private String extractCurrentDay() {
//        int dayOfMonth = LocalDateTime.now().getDayOfMonth();
//        String day = "";
//        if(dayOfMonth >= 1 && dayOfMonth < 10) {
//            day = "0" + dayOfMonth;
//        }
//        if(dayOfMonth >= 10) {
//            day = String.valueOf(dayOfMonth);
//        }
//        return day;
//    }
//
//    @Test
//    void 하루_작성_기능() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail()).orElse(null);
//        List<MultipartFile> uploadImgs = getTwoUploadImgs();
//        List<MultipartFile> uploadVideos = getTwoUploadVideos();
//        PostQuestionDto postQuestionDto = new PostQuestionDto(
//                "오늘의 날씨는?", "맑음", imgCount, videoCount);
//        PostSaveDto postSaveDto = new PostSaveDto(List.of(postQuestionDto), "happy", true);
//
//        //when
//        PostSaveResponseDto saveResponseDto = postService.save(postSaveDto, uploadImgs, uploadVideos);
//        Post findPost = postRepository.findById(saveResponseDto.getPostId()).orElse(null);
//        PostQuestion postQuestion = findPost.getPostQuestions().get(0);
//
//        //then
//        assertThat(findPost).isNotNull();
//        assertThat(findPost.getTodayFeeling()).isEqualTo("happy");
//        assertThat(findPost.getCanPublicAccess()).isTrue();
//        assertThat(postQuestion.getQuestion()).isEqualTo("오늘의 날씨는?");
//        assertThat(postQuestion.getContent()).isEqualTo("맑음");
//        assertThat(postQuestion.getPostImgUrls().size()).isEqualTo(imgCount);
//        assertThat(postQuestion.getPostVideoUrls().size()).isEqualTo(videoCount);
//        assertThat(loginUser.getCanWritePost()).isFalse();
//        assertThat(loginUser.getPostWriteCount()).isEqualTo(1);
//    }
//
//    private List<MultipartFile> getTwoUploadVideos() throws IOException {
//        List<MultipartFile> uploadVideos = new ArrayList<>();
//        uploadVideos.add(generateMultipartFileVideo("testVideo1.mp4"));
//        uploadVideos.add(generateMultipartFileVideo("testVideo2.mp4"));
//        return uploadVideos;
//    }
//
//    private List<MultipartFile> getTwoUploadImgs() throws IOException {
//        List<MultipartFile> uploadImgs = new ArrayList<>();
//        uploadImgs.add(generateMultipartFileImage("testImage1.jpeg"));
//        uploadImgs.add(generateMultipartFileImage("testImage2.png"));
//        return uploadImgs;
//    }
//
////    @Test
////    void 하루_작성_시_이미_오늘_작성했으면_예외_처리() throws Exception {
////        //given
////        postSave();
////
////        //when, then
////        Assertions.assertThatThrownBy(this::postSave)
////                .isInstanceOf(BaseException.class);
////    }
//
//    @Test
//    void 하루_작성_시_작성_횟수가_0이었으면_작물_생성() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail()).orElse(null);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//
//        //when
//        Crop findCrop = cropRepository.findByUserIdAndIsHarvested(loginUser.getId(), false)
//                .orElse(null);
//
//        //then
//        assertThat(findCrop).isNotNull();
//        assertThat(findCrop.getStatus()).isEqualTo(CropStatus.SEED);
//    }
//
//    @Test
//    void 하루_작성_시_작성_횟수에_맞게_작물_업데이트() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail()).orElse(null);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Crop beforePostCrop = cropRepository.findByUserIdAndIsHarvested(loginUser.getId(), false)
//                .orElse(null);
//        CropStatus beforePostCropStatus = beforePostCrop.getStatus();
//
//        //when
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Crop afterPostCrop = cropRepository.findByUserIdAndIsHarvested(loginUser.getId(), false)
//                .orElse(null);
//
//        //then
//        assertThat(beforePostCropStatus).isEqualTo(CropStatus.SEED);
//        assertThat(afterPostCrop.getStatus()).isEqualTo(CropStatus.SPROUT);
//    }
//
//    @Test
//    void 하루_작성_시_작성_횟수_7번이_되면_수확() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail()).orElse(null);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Crop beforePostCrop = cropRepository.findByUserIdAndIsHarvested(loginUser.getId(), false)
//                .orElse(null);
//        CropStatus beforePostCropStatus = beforePostCrop.getStatus();
//        Boolean beforePostCropIsHarvested = beforePostCrop.getIsHarvested();
//
//        //when
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Crop afterPostCrop = cropRepository.findByUserIdAndIsHarvested(loginUser.getId(), true)
//                .orElse(null);
//        CropStatus afterPostCropStatus = afterPostCrop.getStatus();
//        Boolean afterPostCropIsHarvested = afterPostCrop.getIsHarvested();
//
//        //then
//        assertThat(beforePostCropStatus).isEqualTo(CropStatus.FRUIT_CROP);
//        assertThat(afterPostCropStatus).isEqualTo(CropStatus.HARVESTED_CROP);
//        assertThat(beforePostCropIsHarvested).isFalse();
//        assertThat(afterPostCropIsHarvested).isTrue();
//    }
//
//    @Test
//    void 월별_작성한_하루_전체_조회_기능() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail()).orElse(null);
//        postSave("오늘의 날씨는?", "맑음", "happy", true);
//        postSave("오늘 먹은 음식은?", "돈까스", "normal", true);
//
//        //when
//        PostGetMonthInfoDto userMonthPostInfo =
//                postService.getUserMonthPostInfo(loginUser.getId(), LocalDateTime.now().getMonthValue());
//        List<PostInfoDto> postInfoDtos = userMonthPostInfo.getPostInfoDtos();
//        PostInfoDto post1InfoDto = postInfoDtos.get(0);
//        PostInfoDto post2InfoDto = postInfoDtos.get(1);
//        String currentDay = extractCurrentDay();
//
//        //then
//        assertThat(postInfoDtos.size()).isEqualTo(2);
//        assertThat(post1InfoDto.getCreationDay()).isEqualTo(currentDay);
//        assertThat(post1InfoDto.getPostQuestions().get(0).getQuestion()).isEqualTo("오늘의 날씨는?");
//        assertThat(post1InfoDto.getPostQuestions().get(0).getContent()).isEqualTo("맑음");
//        assertThat(post1InfoDto.getTodayFeeling()).isEqualTo("happy");
//        assertThat(post1InfoDto.getCanPublicAccess()).isTrue();
//        assertThat(post2InfoDto.getCreationDay()).isEqualTo(currentDay);
//        assertThat(post2InfoDto.getPostQuestions().get(0).getQuestion()).isEqualTo("오늘 먹은 음식은?");
//        assertThat(post2InfoDto.getPostQuestions().get(0).getContent()).isEqualTo("돈까스");
//        assertThat(post2InfoDto.getTodayFeeling()).isEqualTo("normal");
//        assertThat(post2InfoDto.getCanPublicAccess()).isTrue();
//    }
//
//    @Test
//    void 하루_수정_기능_게시글_관련_내용_이미지_영상_모두_수정() throws Exception {
//        //given
//        PostSaveResponseDto postSaveResponseDto =
//                postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Long postId = postSaveResponseDto.getPostId();
//        Long postQuestionId = postSaveResponseDto.getPostQuestionId().get(0);
//        Post findPost = postRepository.findById(postId).orElse(null);
//        PostQuestion postQuestion = findPost.getPostQuestions().get(0);
//        List<Long> deleteImgUrlIds = getDeleteImgUrlIds(findPost);
//        List<Long> deleteVideoUrlIds = getDeleteVideoUrlIds(findPost);
//
//        PostQuestionUpdateDto postQuestionUpdateDto = new PostQuestionUpdateDto(
//                postQuestionId, "흐림", deleteImgUrlIds, deleteVideoUrlIds, 2, 2);
//        PostUpdateDto postUpdateDto =
//                new PostUpdateDto(List.of(postQuestionUpdateDto), "normal", false);
//
//        //when
//        postService.update(postId, postUpdateDto, getUpdateImgs(), getUpdateVideos());
//
//        //then
//        assertThat(findPost.getTodayFeeling()).isEqualTo("normal");
//        assertThat(findPost.getCanPublicAccess()).isFalse();
//        assertThat(findPost.getPostImgUrls().size()).isEqualTo(3);
//        assertThat(findPost.getPostVideoUrls().size()).isEqualTo(3);
//        assertThat(postQuestion.getContent()).isEqualTo("흐림");
//    }
//
//    private List<Long> getDeleteImgUrlIds(Post findPost) {
//        List<Long> deleteImgUrlIds = new ArrayList<>();
//        Long deleteImgUrlId = findPost.getPostImgUrls().get(0).getId();
//        deleteImgUrlIds.add(deleteImgUrlId);
//        return deleteImgUrlIds;
//    }
//
//    private List<Long> getDeleteVideoUrlIds(Post findPost) {
//        List<Long> deleteVideoUrlIds = new ArrayList<>();
//        Long deleteImgUrlId = findPost.getPostVideoUrls().get(0).getId();
//        deleteVideoUrlIds.add(deleteImgUrlId);
//        return deleteVideoUrlIds;
//    }
//
//    private List<MultipartFile> getUpdateImgs() throws IOException {
//        List<MultipartFile> updateImgs = new ArrayList<>();
//        updateImgs.add(generateMultipartFileImage("testImage2.png"));
//        updateImgs.add(generateMultipartFileImage("testImage3.png"));
//        return updateImgs;
//    }
//
//    private List<MultipartFile> getUpdateVideos() throws IOException {
//        List<MultipartFile> updateVideos = new ArrayList<>();
//        updateVideos.add(generateMultipartFileVideo("testVideo2.mp4"));
//        updateVideos.add(generateMultipartFileVideo("testVideo3.mp4"));
//        return updateVideos;
//    }
//
//    @Test
//    void 하루_수정_기능_게시글_관련_내용만_수정() throws Exception {
//        //given
//        PostSaveResponseDto postSaveResponseDto =
//                postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Long postId = postSaveResponseDto.getPostId();
//        Long postQuestionId = postSaveResponseDto.getPostQuestionId().get(0);
//        Post findPost = postRepository.findById(postId).orElse(null);
//        PostQuestion postQuestion = findPost.getPostQuestions().get(0);
//
//        PostQuestionUpdateDto postQuestionUpdateDto = new PostQuestionUpdateDto(
//                postQuestionId, "흐림",
//                Collections.emptyList(), Collections.emptyList(), 0, 0);
//        PostUpdateDto postUpdateDto =
//                new PostUpdateDto(List.of(postQuestionUpdateDto), "normal", false);
//
//        //when
//        postService.update(postId, postUpdateDto, getUpdateImgs(), getUpdateVideos());
//
//        //then
//        assertThat(findPost.getTodayFeeling()).isEqualTo("normal");
//        assertThat(findPost.getCanPublicAccess()).isFalse();
//        assertThat(findPost.getPostImgUrls().size()).isEqualTo(imgCount);
//        assertThat(findPost.getPostVideoUrls().size()).isEqualTo(videoCount);
//        assertThat(postQuestion.getContent()).isEqualTo("흐림");
//    }
//
//    @Test
//    void 하루_수정_기능_이미지만_수정() throws Exception {
//        //given
//        PostSaveResponseDto postSaveResponseDto =
//                postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Long postId = postSaveResponseDto.getPostId();
//        Long postQuestionId = postSaveResponseDto.getPostQuestionId().get(0);
//        Post findPost = postRepository.findById(postId).orElse(null);
//        PostQuestion postQuestion = findPost.getPostQuestions().get(0);
//        List<Long> deleteImgUrlIds = getDeleteImgUrlIds(findPost);
//
//        PostQuestionUpdateDto postQuestionUpdateDto = new PostQuestionUpdateDto(
//                postQuestionId, "맑음",
//                deleteImgUrlIds, Collections.emptyList(), 0, 0);
//        PostUpdateDto postUpdateDto =
//                new PostUpdateDto(List.of(postQuestionUpdateDto), "happy", true);
//
//        //when
//        postService.update(postId, postUpdateDto, getUpdateImgs(), getUpdateVideos());
//
//        //then
//        assertThat(findPost.getTodayFeeling()).isEqualTo("happy");
//        assertThat(findPost.getCanPublicAccess()).isTrue();
//        assertThat(findPost.getPostImgUrls().size()).isEqualTo(1);
//        assertThat(findPost.getPostVideoUrls().size()).isEqualTo(videoCount);
//        assertThat(postQuestion.getContent()).isEqualTo("맑음");
//    }
//
//    @Test
//    void 하루_수정_기능_영상만_수정() throws Exception {
//        //given
//        PostSaveResponseDto postSaveResponseDto =
//                postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Long postId = postSaveResponseDto.getPostId();
//        Long postQuestionId = postSaveResponseDto.getPostQuestionId().get(0);
//        Post findPost = postRepository.findById(postId).orElse(null);
//        PostQuestion postQuestion = findPost.getPostQuestions().get(0);
//        List<Long> deleteVideoUrlIds = getDeleteVideoUrlIds(findPost);
//
//        PostQuestionUpdateDto postQuestionUpdateDto = new PostQuestionUpdateDto(
//                postQuestionId, "맑음",
//                Collections.emptyList(), deleteVideoUrlIds, 0, 0);
//        PostUpdateDto postUpdateDto =
//                new PostUpdateDto(List.of(postQuestionUpdateDto), "happy", true);
//
//        //when
//        postService.update(postId, postUpdateDto, getUpdateImgs(), getUpdateVideos());
//
//        //then
//        assertThat(findPost.getTodayFeeling()).isEqualTo("happy");
//        assertThat(findPost.getCanPublicAccess()).isTrue();
//        assertThat(findPost.getPostImgUrls().size()).isEqualTo(imgCount);
//        assertThat(findPost.getPostVideoUrls().size()).isEqualTo(1);
//        assertThat(postQuestion.getContent()).isEqualTo("맑음");
//    }
//
//    @Test
//    void 하루_삭제_기능() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//        PostSaveResponseDto postSaveResponseDto =
//                postSave("오늘의 날씨는?", "맑음", "happy", true);
//        Long postId = postSaveResponseDto.getPostId();
//
//        //when
//        postService.delete(postId);
//
//        //then
//        assertThat(loginUser.getPostWriteCount()).isEqualTo(0);
//        assertThatThrownBy(() -> postRepository.findById(postId).orElseThrow(
//                () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST)
//        ))
//                .isInstanceOf(BaseException.class);
//    }
//
//    @Nested
//    class 하루_삭제_시_작물_상태_업데이트_테스트 {
//
//        @Test
//        void 하루_작성_횟수_1회인_경우_삭제_시_작물도_삭제() throws Exception {
//            //given
//            User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//            PostSaveResponseDto postSaveResponseDto =
//                    postSave("오늘의 날씨는?", "맑음", "happy", true);
//            Long postId = postSaveResponseDto.getPostId();
//            Post findPost = postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));
//            Long cropId = findPost.getCrop().getId();
//
//            //when
//            postService.delete(postId);
//
//            //then
//            assertThat(loginUser.getPostWriteCount()).isEqualTo(0);
//            assertThatThrownBy(() -> postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST)
//            ))
//                    .isInstanceOf(BaseException.class);
//            assertThatThrownBy(() -> cropRepository.findById(cropId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_CROP)
//            ))
//                    .isInstanceOf(BaseException.class);
//        }
//
//        @Test
//        void 하루_작성_횟수_2회에서_삭제_시_상태_업데이트() throws Exception {
//            //given
//            User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            PostSaveResponseDto postSaveResponseDto2 =
//                    postSave("오늘의 날씨는?", "맑음", "happy", true);
//            Long postId = postSaveResponseDto2.getPostId();
//            Post findPost = postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));
//            Crop crop = findPost.getCrop();
//
//            //when
//            postService.delete(postId);
//
//            //then
//            assertThat(loginUser.getPostWriteCount()).isEqualTo(1);
//            assertThat(crop.getStatus()).isEqualTo(CropStatus.SEED);
//            assertThatThrownBy(() -> postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST)
//            ));
//        }
//
//        @Test
//        void 하루_작성_횟수_4회에서_삭제_시_상태_업데이트() throws Exception {
//            //given
//            User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            PostSaveResponseDto postSaveResponseDto4 =
//                    postSave("오늘의 날씨는?", "맑음", "happy", true);
//            Long postId = postSaveResponseDto4.getPostId();
//            Post findPost = postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));
//            Crop crop = findPost.getCrop();
//
//            //when
//            postService.delete(postId);
//
//            //then
//            assertThat(loginUser.getPostWriteCount()).isEqualTo(3);
//            assertThat(crop.getStatus()).isEqualTo(CropStatus.SPROUT);
//            assertThatThrownBy(() -> postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST)
//            ));
//        }
//
//        @Test
//        void 하루_작성_횟수_6회에서_삭제_시_상태_업데이트() throws Exception {
//            //given
//            User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            PostSaveResponseDto postSaveResponseDto6 =
//                    postSave("오늘의 날씨는?", "맑음", "happy", true);
//            Long postId = postSaveResponseDto6.getPostId();
//            Post findPost = postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));
//            Crop crop = findPost.getCrop();
//
//            //when
//            postService.delete(postId);
//
//            //then
//            assertThat(loginUser.getPostWriteCount()).isEqualTo(5);
//            assertThat(crop.getStatus()).isEqualTo(CropStatus.GROWING_SPROUT);
//            assertThatThrownBy(() -> postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST)
//            ));
//        }
//
//        @Test
//        void 하루_작성_횟수_7회로_현재_수확한_작물_하루_삭제_시_상태_업데이트() throws Exception {
//            //given
//            User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            postSave("오늘의 날씨는?", "맑음", "happy", true);
//            PostSaveResponseDto postSaveResponseDto7 =
//                    postSave("오늘의 날씨는?", "맑음", "happy", true);
//            Long postId = postSaveResponseDto7.getPostId();
//            Post findPost = postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST));
//            Crop crop = findPost.getCrop();
//
//            //when
//            postService.delete(postId);
//            List<GrownCrop> grownCrops = grownCropRepository.findAllByUserIdAndHarvestedMonth(
//                    loginUser.getId(), LocalDateTime.now().getMonthValue()
//            ).orElse(Collections.emptyList());
//
//            //then
//            assertThat(loginUser.getPostWriteCount()).isEqualTo(6);
//            assertThat(crop.getStatus()).isEqualTo(CropStatus.FRUIT_CROP);
//            assertThat(crop.getIsHarvested()).isFalse();
//            assertThat(grownCrops).isEmpty();
//            assertThatThrownBy(() -> postRepository.findById(postId).orElseThrow(
//                    () -> new BaseException(BaseResponseStatus.NOT_FOUND_POST)
//            ));
//        }
//    }
//
//    @Test
//    void 하루_조회_기능() throws Exception {
//        //given
//        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
//                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
//        PostSaveResponseDto canAccessPostSaveResponseDto =
//                postSave("오늘의 날씨는?", "맑음", "happy", true);
//        PostSaveResponseDto cannotAccessPostSaveResponseDto =
//                postSave("오늘의 날씨는?", "흐림", "sad", false);
//        Long canAccessPostId = canAccessPostSaveResponseDto.getPostId();
//        Long cannotAccessPostId = cannotAccessPostSaveResponseDto.getPostId();
//
//        //when
//        PostInfoDto canAccessPostInfo = postService.getPostInfo(canAccessPostId, loginUser.getId());
//        PostInfoDto cannotAccessPostInfo = postService.getPostInfo(cannotAccessPostId, loginUser.getId());
//
//        //then
//        assertThat(canAccessPostInfo.getCanPublicAccess()).isTrue();
//        assertThat(canAccessPostInfo.getTodayFeeling()).isEqualTo("happy");
//        assertThat(canAccessPostInfo.getPostQuestions().get(0).getContent()).isEqualTo("맑음");
//        assertThat(cannotAccessPostInfo.getCanPublicAccess()).isFalse();
//        assertThat(cannotAccessPostInfo.getTodayFeeling()).isEqualTo("sad");
//        assertThat(cannotAccessPostInfo.getPostQuestions().get(0).getContent()).isEqualTo("흐림");
//    }
//
//    @Test
//    void 비공개_하루일_때_조회하는_유저가_로그인한_유저가_아니면_예외_처리() throws Exception {
//        //given
//        PostSaveResponseDto cannotAccessPostSaveResponseDto =
//                postSave("오늘의 날씨는?", "흐림", "sad", false);
//        Long cannotAccessPostId = cannotAccessPostSaveResponseDto.getPostId();
//        User user = GenerateDummy.generateDummyUser("test2@naver.com", "1234", "KSH1",
//                "s3://imgUrl1", Role.USER);
//        em.persist(user);
//
//        //when, then
//        assertThatThrownBy(() -> postService.getPostInfo(cannotAccessPostId, user.getId()))
//                .isInstanceOf(BaseException.class);
//    }
//}