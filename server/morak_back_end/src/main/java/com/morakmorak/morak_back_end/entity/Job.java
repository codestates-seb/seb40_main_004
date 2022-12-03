package com.morakmorak.morak_back_end.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @Column(name="name", unique = true)
    private String name;

    private String state;

    private String careerRequirement;

    private Date startDate;

    private Date endDate;

    private String url;

    private Instant createdAt;

    private Instant lastModifiedAt;

    public Job(String name, String state, String careerRequirement, Date startDate, Date endDate, String url) {
        this.name = name;
        this.state = state;
        this.careerRequirement = careerRequirement;
        this.startDate = startDate;
        this.endDate = endDate;
        this.url = url;
    }
}
