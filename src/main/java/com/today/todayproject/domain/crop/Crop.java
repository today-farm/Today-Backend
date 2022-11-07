package com.today.todayproject.domain.crop;

import com.today.todayproject.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Crop {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CropStatus status;

    private int cropNumber;

    public void confirmUser(User user) {
        this.user = user;
        user.getCrops().add(this);
    }
}
