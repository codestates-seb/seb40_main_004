package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {
    private final JPAQueryFactory queryFactory;

//    public List<NotificationDto.SimpleResponse> getNotifications(Long userId) {
//
//        queryFactory.select()
//    }
}
