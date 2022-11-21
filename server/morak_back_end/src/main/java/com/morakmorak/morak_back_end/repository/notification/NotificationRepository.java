package com.morakmorak.morak_back_end.repository.notification;

import com.morakmorak.morak_back_end.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
