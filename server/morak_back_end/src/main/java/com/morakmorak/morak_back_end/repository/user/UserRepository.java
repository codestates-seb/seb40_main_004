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
            value = "select tag.tag_id, tag.name\n" +
                    "from\n" +
                    "     (select at.tag_id, count(*) as cnt\n" +
                    "     from (select article_id from article where article.user_id = :id) as a,\n" +
                    "          (select answer.article_id as article_id2 from answer where answer.user_id = :id) as b,\n" +
                    "     article_tag as at\n" +
                    "     where at.article_id = a.article_id or at.article_id = b.article_id2\n" +
                    "     group by at.tag_id\n" +
                    "     order by cnt desc\n" +
                    "     limit 3" +
                    "     ) as tag_ids,\n" +
                    "    tag\n" +
                    "where tag_ids.tag_id = tag.tag_id"
    )
    List<TagQueryDto> getUsersTop3Tags(@Param("id") Long id);

    @Query(
            nativeQuery = true,
            value = "select ba.badge_id as badge_id, ba.name as name\n" +
                    "from\n" +
                    "    (select review_badge.badge_id, count(*) as cnt\n" +
                    "     from review_badge\n" +
                    "     inner join review r on r.review_id = review_badge.review_id\n" +
                    "     where r.receiver_id = :id\n" +
                    "     group by review_badge.badge_id\n" +
                    "     order by cnt desc\n" +
                    "     limit 3) as b,\n" +
                    "    badge as ba\n" +
                    "where ba.badge_id = b.badge_id;\n"
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
