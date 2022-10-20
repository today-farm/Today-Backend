package com.today.todayproject.domain.post.imgurl.dto;

import com.today.todayproject.domain.post.imgurl.PostImgUrl;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostImgUrlDto {

    private Long postImgUrlId;
    private String postImgUrl;

    public PostImgUrlDto(PostImgUrl postImgUrl) {
        this.postImgUrlId = postImgUrl.getId();
        this.postImgUrl = postImgUrl.getImgUrl();
    }
}
