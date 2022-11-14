//package com.today.todayproject;
//
//import com.today.todayproject.domain.friend.Friend;
//import com.today.todayproject.domain.friend.repository.FriendRepository;
//import com.today.todayproject.domain.post.Post;
//import com.today.todayproject.domain.post.question.PostQuestion;
//import com.today.todayproject.domain.post.repository.PostRepository;
//import com.today.todayproject.domain.user.Role;
//import com.today.todayproject.domain.user.User;
//import com.today.todayproject.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//
//import static java.lang.Long.parseLong;
//import static java.lang.String.format;
//import static java.lang.String.valueOf;
//
////더미 데이터 생성 클래스
//@RequiredArgsConstructor
//@Component
//public class InitService{
//
//    private final Init init;
//
//    @PostConstruct
//    public void init() {
//        init.save();
//    }
//
//    @RequiredArgsConstructor
//    @Component
//    @Slf4j
//    private static class Init{
//        private final UserRepository userRepository;
//
//        private final PostRepository postRepository;
//        private final FriendRepository friendRepository;
//
//        @Transactional
//        public void save() {
//            PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//
//            //== 멤버 저장 ==//
//            userRepository.save(User.builder().email("email1@naver.com").password(delegatingPasswordEncoder.encode("password1")).nickname("user1").role(Role.USER).build());
//            userRepository.save(User.builder().email("email2@naver.com").password(delegatingPasswordEncoder.encode("password1")).nickname("user2").role(Role.USER).build());
//            userRepository.save(User.builder().email("email3@naver.com").password(delegatingPasswordEncoder.encode("password1")).nickname("user3").role(Role.USER).build());
//            userRepository.save(User.builder().email("email4@naver.com").password(delegatingPasswordEncoder.encode("password1")).nickname("user4").role(Role.USER).build());
//
//            User user1 = userRepository.findById(1L).orElse(null);
//            User user2 = userRepository.findById(2L).orElse(null);
//            User user3 = userRepository.findById(3L).orElse(null);
//            User user4 = userRepository.findById(4L).orElse(null);
//            log.info("회원 더미데이터 저장 성공, 회원1 : {}", user1);
//
//            //== 게시글 저장 ==//
//            for(int i = 1; i<=10; i++ ) {
//                Post post = Post.builder().todayFeeling("happy")
//                        .writer(userRepository.findById((long) (i % 4 + 1)).orElse(null))
//                        .build();
//
//                for(int j = 1; j <= 3; j++) {
//                    PostQuestion postQuestion = PostQuestion.builder()
//                            .question("question" + j)
//                            .content("content" + j)
//                            .build();
//                    postQuestion.confirmPost(post);
//                }
//                user1.updateRecentFeeling("happy");
//                user2.updateRecentFeeling("happy");
//                user3.updateRecentFeeling("happy");
//                user4.updateRecentFeeling("happy");
//                postRepository.save(post);
//            }
//            Post post1 = postRepository.findById(1L).orElse(null);
//            log.info("게시글 더미데이터 저장 성공, 게시글1 : {}", post1);
//
//            //== 친구 관계 설정 ==//
//            Friend friendOfLoginUser = Friend.builder().nickname("user2").recentFeeling("happy").friendOwnerId(1L).build();
//            Friend friendOfFriendUser = Friend.builder().nickname("user1").recentFeeling("happy").friendOwnerId(2L).build();
//
//            friendOfLoginUser.confirmUser(user2);
//            friendOfFriendUser.confirmUser(user1);
//
//            friendRepository.save(friendOfLoginUser);
//            friendRepository.save(friendOfFriendUser);
//
//        }
//    }
//}