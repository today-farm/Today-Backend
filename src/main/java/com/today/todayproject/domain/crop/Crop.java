package com.today.todayproject.domain.crop;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Crop extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crop_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int createdMonth;

    @Enumerated(EnumType.STRING)
    private CropStatus status;

    private int cropNumber;
    private Boolean isHarvested;

    public void confirmUser(User user) {
        this.user = user;
        user.confirmCrop(this);
    }

    public void updateCropStatus(int userPostWriteCount) {
        if (userPostWriteCount == 1) {
            this.status = CropStatus.SEED;
        }
        if (userPostWriteCount == 2 || userPostWriteCount == 3 || userPostWriteCount == 4) {
            this.status = CropStatus.SPROUT;
        }
        if (userPostWriteCount == 5 || userPostWriteCount == 6) {
            this.status = CropStatus.GROWING_SPROUT;
        }
        if (userPostWriteCount == 7) {
            this.status = CropStatus.GROWN_SPROUT;
        }
    }
}
