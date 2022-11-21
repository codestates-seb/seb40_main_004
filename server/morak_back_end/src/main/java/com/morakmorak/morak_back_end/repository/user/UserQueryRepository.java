package com.morakmorak.morak_back_end.repository.user;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.entity.QAnswer.*;
import static com.morakmorak.morak_back_end.entity.QArticle.article;
import static com.morakmorak.morak_back_end.entity.QArticleLike.*;
import static com.morakmorak.morak_back_end.entity.QArticleTag.*;
import static com.morakmorak.morak_back_end.entity.QAvatar.*;
import static com.morakmorak.morak_back_end.entity.QCategory.*;
import static com.morakmorak.morak_back_end.entity.QComment.*;
import static com.morakmorak.morak_back_end.entity.QReview.*;
import static com.morakmorak.morak_back_end.entity.QUser.*;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @BatchSize(size = 50)
    public List<Article> get50RecentQuestions(Long userId) {
        return jpaQueryFactory.select(article)
                .from(article)
                .leftJoin(article.user, user)
                .leftJoin(article.user.avatar, avatar)
                .leftJoin(article.articleLikes, articleLike)
                .leftJoin(article.articleTags, articleTag)
                .leftJoin(article.answers, answer)
                .leftJoin(article.category, category)
                .leftJoin(article.articleTags, articleTag)
                .leftJoin(article.comments, comment)
                .where(article.user.id.eq(userId).and(article.category.name.eq(CategoryName.QNA)))
                .groupBy(article.id)
                .orderBy(article.id.desc())
                .limit(50)
                .fetch();
    }

    public List<ReviewDto.Response> getReceivedReviews(Long userId) {
        return jpaQueryFactory.select(new QReviewDto_Response(
                review.id, review.content, review.createdAt, review.answerer.id, review.answerer.nickname, review.answerer.grade))
                .from(review)
                .where(review.answerer.id.eq(userId))
                .fetch();
    }

    public UserDto.ResponseDashBoard getUserDashboardBasicInfo(Long userId) {
            return jpaQueryFactory.select(new QUserDto_ResponseDashBoard(
                            user.id, user.email, user.nickname, user.jobType, user.grade, user.point, user.github, user.blog, user.avatar.id, user.avatar.remotePath, user.avatar.originalFilename
                    ))
                    .from(user)
                    .where(user.id.eq(userId))
                    .fetchOne();

    }

    public Page<User> getRankData(Pageable pageable) {
        List<String> sortTypes = getSortTypes(pageable);

        List<User> result;

            result = jpaQueryFactory.select(user)
                    .from(article, user, answer)
                    .orderBy(getOrderSpecifier(sortTypes))
                    .groupBy(user.id)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

        return new PageImpl<>(result, pageable, getCount(user));
    }

    private OrderSpecifier<?> getOrderSpecifier(List<String> sortTypes) {

        if (sortTypes.size() == 0) return user.point.desc();

        switch (sortTypes.get(0)) {
            case "point" :
                return user.point.desc();
            case "articles" :
                return user.articles.size().desc();
            case "answers" :
                return user.answers.size().desc();
            case "likes" :
                return user.answers.any().answerLike.size().add(user.articles.any().articleLikes.size()).desc();
            default:
                return user.point.desc();
            }
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
