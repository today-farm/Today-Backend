package com.today.todayproject.domain.crop.repository;

import com.today.todayproject.domain.crop.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long>, CustomCropRepository {

    Optional<Crop> findByUserIdAndIsHarvested(Long userId, Boolean isHarvested);

    Optional<List<Crop>> findAllByCreatedMonthAndUserIdAndIsHarvested(int month, Long userId, Boolean isHarvested);

    int countByUserId(Long userId);

    Optional<Crop> findByUserIdAndCreatedMonthOrderByCreatedDateDesc(Long userId, int createdMonth);
}
