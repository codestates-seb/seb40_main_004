package com.morakmorak.morak_back_end.repository.answer;

import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.entity.QAnswer.answer;

@Repository
public class AnswerQueryRepositoryImpl implements AnswerQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public AnswerQueryRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Answer> getAnswersPickedFirst(Long articleId, Pageable pageable) {

        List<Answer> result;

        result = jpaQueryFactory
                .select(answer)
                .from(answer)
                .where(answer.article.id.eq(articleId))
                .orderBy(answer.isPicked.desc(), answer.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(result, pageable, getCount(answer));
    }
    @Override
    public Page<Answer> findAnswersByUserId(Long userId,Pageable pageable) {

        List<Answer> result;
        result = jpaQueryFactory.selectFrom(answer)
                .where(answer.user.id.eq(userId).and(answer.article.articleStatus.eq(ArticleStatus.POSTING)))
                .orderBy(answer.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(result, pageable, getCount(answer));
    }



    private List<String> getSortTypes(Pageable pageable) {
        return pageable
                .getSort()
                .stream()
                .map(Sort.Order::getProperty)
                .collect(Collectors.toList());
    }

    private Long getCount(EntityPathBase<?> entity) {
        return jpaQueryFactory.select(entity.count())
                .from(entity)
                .fetchOne();
    }
}
