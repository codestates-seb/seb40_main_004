package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.DepositStatus;
import com.morakmorak.morak_back_end.entity.enums.PayType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deposit extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String userNickname;

    private String userEmail;

    private String userPhone;

    private Integer depositRequest;

    private String deposit;

    private Integer depositSum;

    private Integer depositCash;

    private String depositContent;

    @Enumerated(EnumType.STRING)
    private PayType payType;

    private String depositPG;

    private String depositTNO;

    private Integer appNo;

    private String depositBankInfo;

    private LocalDateTime depositStartedAt;

    private LocalDateTime depositFinishedAt;

    @Enumerated(EnumType.STRING)
    private DepositStatus depositStatus;



}
