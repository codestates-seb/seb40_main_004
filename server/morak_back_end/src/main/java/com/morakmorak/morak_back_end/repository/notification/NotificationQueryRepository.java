package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.dto.QNotificationDto_SimpleResponse;
import com.morakmorak.morak_back_end.entity.QNotification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.morakmorak.morak_back_end.entity.QNotification.*;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<NotificationDto.SimpleResponse> getNotifications(Long userId, Pageable pageable) {
        return queryFactory.select(new QNotificationDto_SimpleResponse(
                notification.id, notification.message, notification.isChecked, notification.createdAt
        ))
                .where(notification.user.id.eq(userId))
                .from(notification)
                .groupBy(notification.id)
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
