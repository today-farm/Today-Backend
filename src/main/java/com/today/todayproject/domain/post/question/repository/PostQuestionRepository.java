package com.today.todayproject.domain.post.question.repository;

import com.today.todayproject.domain.post.question.PostQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostQuestionRepository extends JpaRepository<PostQuestion, Long> {

}
