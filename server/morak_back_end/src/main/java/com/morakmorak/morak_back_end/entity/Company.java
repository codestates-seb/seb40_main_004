package com.morakmorak.morak_back_end.entity;


import com.morakmorak.morak_back_end.entity.enums.JobPayType;
import com.morakmorak.morak_back_end.entity.enums.JobType;

import javax.persistence.*;
import java.time.LocalDateTime;

public class Company extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inicis_log_id")
    private Long id;

    private String companyName;

    private String description;

    private String companyEmail;

    private String employeeNumber;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private JobPayType jobPayType;

    private Integer maxCarer;

    private Integer minCarer;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime workingStartMonth;

    private String tel;

    private LocalDateTime closed;

    private String city;

    private String techStack;

    private String logo;

    private String image1;

    private String image2;

    private String image3;

    private String image4;

    private String image5;
}

