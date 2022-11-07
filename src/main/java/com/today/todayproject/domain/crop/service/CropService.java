package com.today.todayproject.domain.crop.service;

import com.today.todayproject.domain.crop.dto.CropGetThisMonthMyCropsResponseDto;
import com.today.todayproject.global.BaseException;

public interface CropService {

    CropGetThisMonthMyCropsResponseDto getThisMonthMyCrops() throws BaseException;
}
