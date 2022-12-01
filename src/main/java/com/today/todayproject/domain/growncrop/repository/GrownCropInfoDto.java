package com.today.todayproject.domain.growncrop.repository;

import com.today.todayproject.domain.growncrop.GrownCrop;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrownCropInfoDto {

    private int cropNumber;
    private Boolean isGoldGrownCrop;

    public GrownCropInfoDto(GrownCrop grownCrop) {
        this.cropNumber = grownCrop.getCropNumber();
        if (grownCrop.getCropNumber() == -1) {
            this.isGoldGrownCrop = true;
        }
        if (grownCrop.getCropNumber() != -1) {
            this.isGoldGrownCrop = false;
        }
    }
}
