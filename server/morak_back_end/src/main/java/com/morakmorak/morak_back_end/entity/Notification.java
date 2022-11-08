package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.DomainType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long senderId;

    private String message;

    private Boolean isChecked;

    private long domainId;

    private String uri;

    private DomainType domainType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
