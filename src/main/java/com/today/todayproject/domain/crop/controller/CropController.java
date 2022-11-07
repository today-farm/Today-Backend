package com.today.todayproject.domain.crop.controller;

import com.today.todayproject.domain.crop.dto.CropGetThisMonthMyCropsResponseDto;
import com.today.todayproject.domain.crop.service.CropService;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crop")
public class CropController {

    private final CropService cropService;

    @GetMapping("/this-month-my-crops")
    public BaseResponse<CropGetThisMonthMyCropsResponseDto> getThisMonthMyCrops() throws BaseException {
        return new BaseResponse<>(cropService.getThisMonthMyCrops());
    }
}
