package com.today.todayproject.domain.crop.controller;

import com.today.todayproject.domain.crop.dto.ThisMonthUserCropDto;
import com.today.todayproject.domain.crop.service.CropService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    /**
     * 이번 달 유저 작물들 조회 API(메인 페이지)
     */
    @GetMapping("/crop/this-month-user-crops/{userId}")
    public BaseResponse<ThisMonthUserCropDto> getThisMonthUserCrops(
            @PathVariable("userId") Long userId) throws BaseException {
        ThisMonthUserCropDto thisMonthMyCrop = cropService.getThisMonthUserCrop(userId);
        return new BaseResponse<>(thisMonthMyCrop);
    }
}
