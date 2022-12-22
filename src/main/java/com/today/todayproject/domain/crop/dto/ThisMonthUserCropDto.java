package com.today.todayproject.domain.crop.dto;

import com.today.todayproject.domain.growncrop.repository.GrownCropInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThisMonthUserCropDto {

    private List<CropInfoDto> growingCrops;
    private List<GrownCropInfoDto> harvestedCrops;
}
