package com.today.todayproject.domain.crop.service;

import com.today.todayproject.domain.crop.dto.ThisMonthUserCropDto;
import com.today.todayproject.global.BaseException;

public interface CropService {

    ThisMonthUserCropDto getThisMonthUserCrop(Long userId) throws BaseException;

    void deletePastCrop();
}
