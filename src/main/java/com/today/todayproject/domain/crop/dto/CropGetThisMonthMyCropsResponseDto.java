package com.today.todayproject.domain.crop.dto;

import com.today.todayproject.domain.crop.Crop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropGetThisMonthMyCropsResponseDto {

    private List<CropInfoDto> crops;

}
