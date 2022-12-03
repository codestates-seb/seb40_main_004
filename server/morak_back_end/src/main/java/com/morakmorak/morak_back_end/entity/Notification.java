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

    @Builder.Default
    private Boolean isChecked = Boolean.FALSE;

    private Long domainId;

    private String uri;

    @Enumerated(EnumType.STRING)
    private DomainType domainType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Notification of(String messsage, String uri, User receiver) {
        return Notification.builder()
                .message(messsage)
                .uri(uri)
                .user(receiver)
                .build();
    }

    public void mapUserAndNotification() {
        this.user.getNotifications().add(this);
    }

    public void changeCheckStatus() { this.isChecked = true; }
}
