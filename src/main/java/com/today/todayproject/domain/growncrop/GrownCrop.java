package com.today.todayproject.domain.growncrop;

import com.today.todayproject.domain.BaseTimeEntity;
import com.today.todayproject.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GrownCrop extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int cropNumber;

    public void confirmUser(User user) {
        this.user = user;
        user.getGrownCrops().add(this);
    }
}
