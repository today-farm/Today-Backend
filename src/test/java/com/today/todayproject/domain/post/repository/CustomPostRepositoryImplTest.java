package com.today.todayproject.domain.post.repository;

import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.user.Role;
import com.today.todayproject.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomPostRepositoryImplTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test1@gamil.com")
                .password("password1")
                .nickname("KSH1")
                .role(Role.USER)
                .build();

        post = Post.builder()
                .writer(user)
                .build();
        em.persist(user);
        em.persist(post);
    }

    @Test
    void 유저_아이디와_달로_포스트_목록_가져오기() {
        //given
        Long userId = user.getId();
        int month = LocalDateTime.now().getMonthValue();

        //when
        List<Post> postsByUserIdAndMonth = postRepository.getPostByUserIdAndMonth(userId, month);

        //then
        assertThat(postsByUserIdAndMonth).isNotEmpty();
        assertThat(postsByUserIdAndMonth.size()).isEqualTo(1);
        assertThat(postsByUserIdAndMonth.get(0).getWriter()).isEqualTo(user);
    }
}