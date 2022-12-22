package com.today.todayproject.domain.crop.service;

import com.today.todayproject.domain.crop.Crop;
import com.today.todayproject.domain.crop.CropStatus;
import com.today.todayproject.domain.crop.dto.CropInfoDto;
import com.today.todayproject.domain.crop.repository.CropRepository;
import com.today.todayproject.domain.growncrop.GrownCrop;
import com.today.todayproject.domain.growncrop.repository.GrownCropInfoDto;
import com.today.todayproject.domain.growncrop.repository.GrownCropRepository;
import com.today.todayproject.domain.user.User;
import com.today.todayproject.domain.crop.dto.ThisMonthUserCropDto;
import com.today.todayproject.domain.user.repository.UserRepository;
import com.today.todayproject.global.BaseException;
import com.today.todayproject.global.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final GrownCropRepository grownCropRepository;

    @Override
    public ThisMonthUserCropDto getThisMonthUserCrop(Long userId) throws BaseException {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));


        List<Crop> findCrops = cropRepository.findAllByCreatedMonthAndUserIdAndIsHarvested(
                        LocalDateTime.now().getMonthValue(), findUser.getId(), false)
                .orElse(Collections.emptyList());

        List<GrownCrop> findGrownCrops = grownCropRepository.findAllByUserIdAndHarvestedMonth(
                        findUser.getId(), LocalDateTime.now().getMonthValue())
                .orElse(Collections.emptyList());

        List<CropInfoDto> cropInfoDtos = Collections.emptyList();
        List<GrownCropInfoDto> grownCropInfoDtos = Collections.emptyList();

        if (!findCrops.isEmpty()) {
            cropInfoDtos = generateCropInfoDto(findCrops);
        }

        if (!findGrownCrops.isEmpty()) {
            grownCropInfoDtos = generateGrownCropInfoDto(findGrownCrops);
        }

        return new ThisMonthUserCropDto(cropInfoDtos, grownCropInfoDtos);
    }

    private List<CropInfoDto> generateCropInfoDto(List<Crop> findCrops) {
        return findCrops.stream()
                .map(findCrop -> {
                    int cropNumber = findCrop.getCropNumber();
                    CropStatus cropStatus = findCrop.getStatus();
                    return new CropInfoDto(cropNumber, cropStatus);
                }).collect(Collectors.toList());
    }

    private List<GrownCropInfoDto> generateGrownCropInfoDto(List<GrownCrop> findGrownCrops) {
        return findGrownCrops.stream()
                .map(GrownCropInfoDto::new).collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 3 1 * ?", zone = "Asia/Seoul")
    public void deletePastCrop() {
        cropRepository.deleteAllByPastMonth();
    }
}
