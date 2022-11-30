package com.today.todayproject.domain.crop.service;

import com.today.todayproject.domain.crop.dto.CropInfoDto;
import com.today.todayproject.global.BaseException;

import java.util.List;

public interface CropService {

    List<CropInfoDto> getThisMonthMyCrops() throws BaseException;

    void deletePastCrop();
}
