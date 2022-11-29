package com.today.todayproject.domain.crop.service;

import com.today.todayproject.domain.crop.Crop;
import com.today.todayproject.domain.crop.CropStatus;
import com.today.todayproject.domain.crop.dto.CropGetThisMonthMyCropsResponseDto;
import com.today.todayproject.domain.crop.dto.CropInfoDto;
import com.today.todayproject.domain.crop.repository.CropRepository;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import com.today.todayproject.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    @Override
    public CropGetThisMonthMyCropsResponseDto getThisMonthMyCrops() throws BaseException {
        User loginUser = userRepository.findByEmail(SecurityUtil.getLoginUserEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));


        List<Crop> findCrops = cropRepository.findAllByCreatedMonthAndUserId(
                LocalDateTime.now().getMonthValue(), loginUser.getId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_CROP));

        List<CropInfoDto> cropInfoDtos = findCrops.stream()
                .map(findCrop -> {
                    int cropNumber = findCrop.getCropNumber();
                    CropStatus cropStatus = findCrop.getStatus();
                    return new CropInfoDto(cropNumber, cropStatus);
                }).collect(Collectors.toList());

        return new CropGetThisMonthMyCropsResponseDto(cropInfoDtos);
    }

    @Override
    @Scheduled(cron = "0 0 3 1 * ?", zone = "Asia/Seoul")
    public void deletePastCrop() {
        cropRepository.deleteAllByPastMonth();
    }
}
