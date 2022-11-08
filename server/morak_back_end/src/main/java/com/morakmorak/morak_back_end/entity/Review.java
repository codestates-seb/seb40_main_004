package com.morakmorak.morak_back_end.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "message")
    private String message;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "review")
    private Answer answer;

    @Builder.Default
    @OneToMany(mappedBy = "review")
    private List<ReviewBadge> reviewBadges = new ArrayList<>();
}
