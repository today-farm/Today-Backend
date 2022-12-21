package com.today.todayproject.domain.crop;

import java.util.Arrays;
import java.util.List;

public enum CropStatus {
    SEED(List.of(1)),
    SPROUT(List.of(2, 3)),
    GROWING_SPROUT(List.of(4, 5)),
    FRUIT_CROP(List.of(6)),
    HARVESTED_CROP(List.of(7));

    private List<Integer> postWriteCounts;

    CropStatus(List<Integer> postWriteCounts) {
        this.postWriteCounts = postWriteCounts;
    }

    public static CropStatus getCropStatusByPostWriteCount(int postWriteCount) {
        return Arrays.stream(values())
                .filter(value -> value.postWriteCounts.contains(postWriteCount))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] : 잘못된 postWriteCount입니다."));
    }
}
