package com.morakmorak.morak_back_end.entity;


import com.morakmorak.morak_back_end.entity.enums.BadgeName;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long id;

    private BadgeName badgeName;

    @Builder.Default
    @OneToMany(mappedBy = "badge")
    private List<ReviewBadge> reviewBadges = new ArrayList<>();
}
