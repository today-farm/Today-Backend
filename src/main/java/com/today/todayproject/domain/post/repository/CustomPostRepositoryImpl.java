package com.today.todayproject.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.today.todayproject.domain.post.Post;
import com.today.todayproject.domain.post.QPost;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.today.todayproject.domain.post.QPost.post;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory query;

    public CustomPostRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> getPostByUserIdAndMonth(Long userId, int month) {
        return query.selectFrom(post)
                .where(
                        post.writer.id.eq(userId),
                        post.createdDate.month().eq(month)
                )
                .orderBy(post.createdDate.dayOfMonth().asc())
                .fetch();
    }
}
