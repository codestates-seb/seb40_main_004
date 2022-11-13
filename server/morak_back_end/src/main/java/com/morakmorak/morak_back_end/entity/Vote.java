package com.morakmorak.morak_back_end.entity;


import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @Builder.Default
    private Integer count= 0;

    @OneToOne(mappedBy = "vote")
    private Article article;

    @OneToOne(mappedBy = "vote")
    private Answer answer;

    @OneToOne(mappedBy = "vote")
    private User user;


}
