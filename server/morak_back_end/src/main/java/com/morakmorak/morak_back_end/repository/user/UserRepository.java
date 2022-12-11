package com.morakmorak.morak_back_end.repository.user;

import com.morakmorak.morak_back_end.dto.ActivityQueryDto;
import com.morakmorak.morak_back_end.dto.BadgeQueryDto;
import com.morakmorak.morak_back_end.dto.TagQueryDto;
import com.morakmorak.morak_back_end.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByNickname(String nickname);

    @Query(
            nativeQuery = true,
            value =                 "    select a.cnt as articleCount, b.cnt as answerCount, c.cnt as commentCount, (a.cnt + b.cnt + c.cnt) as total, d.dd as date\n" +
                    "    from (select str_to_date(created_at,'%Y-%m-%d') as ca1, user_id, count(*) as cnt\n" +
                    "         from article\n" +
                    "         where str_to_date(created_at,'%Y-%m-%d') >= str_to_date(:start,'%Y-%m-%d')\n" +
                    "         and str_to_date(created_at,'%Y-%m-%d') < str_to_date(:end, '%Y-%m-%d')\n" +
                    "         and article.user_id = :id\n" +
                    "         group by ca1) as a,\n" +
                    "        (select str_to_date(created_at,'%Y-%m-%d') as ca2, user_id, count(*) as cnt\n" +
                    "         from answer\n" +
                    "         where str_to_date(created_at,'%Y-%m-%d') >= str_to_date(:start,'%Y-%m-%d')\n" +
                    "         and str_to_date(created_at,'%Y-%m-%d') < str_to_date(:end, '%Y-%m-%d')\n" +
                    "         and answer.user_id = :id\n" +
                    "         group by ca2) as b,\n" +
                    "        (select str_to_date(created_at,'%Y-%m-%d') as ca3, user_id, count(*) as cnt\n" +
                    "         from comment\n" +
                    "         where str_to_date(created_at,'%Y-%m-%d') >= str_to_date(:start,'%Y-%m-%d')\n" +
                    "         and str_to_date(created_at,'%Y-%m-%d') < str_to_date(:end, '%Y-%m-%d')\n" +
                    "         and comment.user_id = :id\n" +
                    "         group by ca3) as c,\n" +
                    "        (select str_to_date(date,'%Y-%m-%d') as dd\n" +
                    "         from calendar\n" +
                    "         where str_to_date(date,'%Y-%m-%d') >= str_to_date(:start,'%Y-%m-%d')\n" +
                    "         and str_to_date(date,'%Y-%m-%d') < str_to_date(:end, '%Y-%m-%d')\n" +
                    "         group by date) as d\n" +
                    "    where a.ca1 = d.dd or b.ca2 = d.dd or c.ca3 = d.dd"
    )
    List<ActivityQueryDto> getUserActivitiesOnThisYear(@Param("start") Date start,
                                                       @Param("end") Date end,
                                                       @Param("id") Long id);

    @Query(
            nativeQuery = true,
            value = "select rank() over (order by count(*) desc) as ranking, a.tag_id, a.name\n" +
                    "from (select tag.tag_id, tag.name\n" +
                    "      from tag\n" +
                    "               inner join article_tag on article_tag.tag_id = tag.tag_id\n" +
                    "               inner join article on article.article_id = article_tag.article_id\n" +
                    "               inner join user on user.user_id = article.user_id\n" +
                    "      where user.user_id = :id\n" +
                    "      group by tag_id\n" +
                    "      union all\n" +
                    "      select t.tag_id, t.name\n" +
                    "      from tag as t\n" +
                    "               inner join article_tag as at on at.tag_id = t.tag_id\n" +
                    "               inner join article as a on a.article_id = at.article_id\n" +
                    "               inner join answer on answer.article_id = a.article_id\n" +
                    "               inner join user as u on u.user_id = answer.user_id\n" +
                    "      where u.user_id = :id\n" +
                    "      group by t.tag_id) as a\n" +
                    "group by a.tag_id, a.name\n" +
                    "limit 3;"
    )
    List<TagQueryDto> getUsersTop3Tags(@Param("id") Long id);

    @Query(
            nativeQuery = true,
            value = "select rank() over (order by count(*)), b.badge_id, b.name\n" +
                    "from badge as b\n" +
                    "inner join review_badge as rb on rb.badge_id = b.badge_id\n" +
                    "inner join review as r on r.review_id = rb.review_id\n" +
                    "inner join user as u on r.receiver_id = u.user_id\n" +
                    "where u.user_id = :id\n" +
                    "group by b.badge_id, b.name\n" +
                    "limit 3;"
    )
    List<BadgeQueryDto> getUsersTop3Badges(@Param("id") Long id);

    @Query(
            nativeQuery = true,
            value = "select ranked.ranking\n" +
                    "from (\n" +
                    "    select user_id, rank() over (order by user.point desc) as 'ranking'\n" +
                    "    from user\n" +
                    "     ) ranked\n" +
                    "where ranked.user_id = :id"
    )
    Long getUserRank(@Param("id") Long id);
}
