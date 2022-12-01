package com.today.todayproject.domain.crop.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.today.todayproject.domain.crop.QCrop.crop;

@Repository
public class CustomCropRepositoryImpl implements CustomCropRepository {

    private final JPAQueryFactory query;

    public CustomCropRepositoryImpl(EntityManager em) {
        query = new JPAQueryFactory(em);
    }

    @Override
    public void deleteAllByPastMonth() {
        int pastMonth = extractPastMonth();
        query.delete(crop)
                .where(crop.createdMonth.eq(pastMonth))
                .execute();
    }

    private int extractPastMonth() {
        int thisMonth = LocalDateTime.now().getMonthValue();
        int pastMonth = thisMonth - 1;
        if (pastMonth == 0) {
            pastMonth = 12;
        }
        return pastMonth;
    }
}
