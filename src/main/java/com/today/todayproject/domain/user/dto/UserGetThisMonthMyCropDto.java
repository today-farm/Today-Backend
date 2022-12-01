package com.today.todayproject.domain.user.dto;

import com.today.todayproject.domain.crop.dto.CropInfoDto;
import com.today.todayproject.domain.growncrop.repository.GrownCropInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetThisMonthMyCropDto {

    private List<CropInfoDto> growingCrops;
    private List<GrownCropInfoDto> harvestedCrops;
}
