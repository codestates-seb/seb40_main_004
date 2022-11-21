package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.config.JpaQueryFactoryConfig;
import com.morakmorak.morak_back_end.dto.NotificationDto;
import com.morakmorak.morak_back_end.entity.Notification;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.notification.NotificationQueryRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(JpaQueryFactoryConfig.class)
public class NotificationRepositoryTest {
    NotificationQueryRepository notificationQueryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory jpaQueryFactory;
    @Autowired
    NotificationRepository notificationRepository;

    User user = User.builder().build();
    Long userId;

    @BeforeEach
    void init() {
        notificationQueryRepository = new NotificationQueryRepository(jpaQueryFactory);
        userId = userRepository.save(user).getId();

        for (int i=0; i<30; i++) {
            Notification notice = Notification.builder()
                    .user(user)
                    .isChecked(Boolean.TRUE)
                    .message(CONTENT1)
                    .domainId(ID1)
                    .build();

            notice.mapUserAndNotification();
            em.persist(notice);
        }

        em.flush();
    }

    @Test
    @DisplayName("요청한 페이지만큼의 객체를 반환한다. 1")
    void getNotifications1() {
        List<Notification> all = notificationRepository.findAll();
        System.out.println("전체" + all.size());
        all.stream().forEach(
                e-> System.out.println(e.getId())
        );
        all.stream().forEach(
                e-> System.out.println(e.getCreatedAt())
        );
        all.stream().forEach(
                e-> System.out.println(e.getUser().getId())
        );
        //given
        //when
        PageRequest pageable = PageRequest.of(0, 30);
        Page<NotificationDto.SimpleResponse> result = notificationQueryRepository.getNotifications(userId, pageable);

        //then
        Assertions.assertThat(result.getContent().size()).isEqualTo(30);
    }

    @Test
    @DisplayName("요청한 페이지만큼의 객체를 반환한다. 2")
    void getNotifications2() {
        List<Notification> all = notificationRepository.findAll();
        System.out.println("전체" + all.size());
        all.stream().forEach(
                e-> System.out.println(e.getId())
        );
        all.stream().forEach(
                e-> System.out.println(e.getCreatedAt())
        );
        all.stream().forEach(
                e-> System.out.println(e.getUser().getId())
        );
        //given
        //when
        PageRequest pageable = PageRequest.of(0, 10);
        Page<NotificationDto.SimpleResponse> result = notificationQueryRepository.getNotifications(userId, pageable);

        //then
        Assertions.assertThat(result.getContent().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("날짜순으로 정렬되어 0번의 객체가 제일 나중에 저장된 객체다.")
    void getNotifications3() {
        //given
        //when
        PageRequest pageable = PageRequest.of(0, 30);
        Page<NotificationDto.SimpleResponse> result = notificationQueryRepository.getNotifications(userId, pageable);

        //then
        Assertions.assertThat(result.getContent().get(0).getCreatedAt().isAfter(result.getContent().get(10).getCreatedAt())).isTrue();
        Assertions.assertThat(result.getContent().get(0).getCreatedAt().isAfter(result.getContent().get(20).getCreatedAt())).isTrue();
        Assertions.assertThat(result.getContent().get(0).getCreatedAt().isAfter(result.getContent().get(29).getCreatedAt())).isTrue();
        Assertions.assertThat(result.getContent().get(10).getCreatedAt().isAfter(result.getContent().get(20).getCreatedAt())).isTrue();
        Assertions.assertThat(result.getContent().get(20).getCreatedAt().isAfter(result.getContent().get(29).getCreatedAt())).isTrue();
    }
}
