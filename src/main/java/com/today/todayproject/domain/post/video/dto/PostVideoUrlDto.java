package com.today.todayproject.domain.post.video.dto;

import com.today.todayproject.domain.post.video.PostVideoUrl;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostVideoUrlDto {

    private Long postVideoUrlId;
    private String postVideoUrl;

    public PostVideoUrlDto(PostVideoUrl postVideoUrl) {
        this.postVideoUrlId = postVideoUrl.getId();
        this.postVideoUrl = postVideoUrl.getVideoUrl();
    }
}
