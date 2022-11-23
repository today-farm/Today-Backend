package com.today.todayproject.domain.post.repository;

import com.today.todayproject.domain.post.Post;

import java.util.List;

public interface CustomPostRepository {

    List<Post> getPostByUserIdAndMonth(Long userId, int month);
}
