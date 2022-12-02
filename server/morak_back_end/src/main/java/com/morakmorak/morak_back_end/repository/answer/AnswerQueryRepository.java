package com.morakmorak.morak_back_end.repository.answer;

import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.morakmorak.morak_back_end.entity.QAnswer.answer;

@Repository
@RequiredArgsConstructor
public class AnswerQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Answer> findAllByArticleId_PickedFirst(Long articleId, Pageable pageable) {
        List<Answer> result;
        result = jpaQueryFactory.select(answer)
                .from(answer)
                .where(answer.article.id.eq(articleId))
                .orderBy(answer.isPicked.desc(), answer.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(result, pageable, getCount(answer));
    }
    private Long getCount(EntityPathBase<?> entity) {
        return jpaQueryFactory.select(entity.count())
                .from(entity)
                .fetchOne();
    }

    public Page<Answer> findAnswersByUserId(Long userId, Pageable pageable) {
        List<Answer> result;
        result = jpaQueryFactory.select(answer)
                .from(answer)
                .where(answer.user.id.eq(userId).and(answer.article.articleStatus.eq(ArticleStatus.POSTING)))
                .orderBy(answer.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(result, pageable, getCount(answer));
    }

}
