package com.today.todayproject.domain.crop.repository;

import com.today.todayproject.domain.crop.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    Optional<Crop> findByUserId(Long userId);

    Optional<List<Crop>> findAllByCreatedMonth();
}
