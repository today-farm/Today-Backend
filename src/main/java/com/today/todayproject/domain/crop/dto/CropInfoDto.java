package com.today.todayproject.domain.crop.dto;

import com.today.todayproject.domain.crop.CropStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropInfoDto {

    private int cropNumber;
    private CropStatus cropStatus;
}
