package com.today.todayproject.domain.post.dto;

import com.today.todayproject.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostGetMonthInfoDto {

    private List<PostInfoDto> postInfoDtos = new ArrayList<>();

    public PostGetMonthInfoDto(List<Post> posts) {
        posts.forEach(post -> {
            PostInfoDto postInfoDto = new PostInfoDto(post);
            postInfoDtos.add(postInfoDto);
        });
    }
}
