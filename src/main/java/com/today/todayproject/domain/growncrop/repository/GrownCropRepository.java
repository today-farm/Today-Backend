package com.today.todayproject.domain.growncrop.repository;


import com.today.todayproject.domain.growncrop.GrownCrop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrownCropRepository extends JpaRepository<GrownCrop, Long> {

    Optional<List<GrownCrop>> findAllByUserIdAndHarvestedMonth(Long userId, int harvestedMonth);
}
