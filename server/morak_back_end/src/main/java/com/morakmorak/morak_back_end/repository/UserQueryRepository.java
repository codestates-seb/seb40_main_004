package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;

import static com.morakmorak.morak_back_end.entity.QAnswer.*;
import static com.morakmorak.morak_back_end.entity.QArticle.article;
import static com.morakmorak.morak_back_end.entity.QArticleLike.*;
import static com.morakmorak.morak_back_end.entity.QArticleTag.*;
import static com.morakmorak.morak_back_end.entity.QAvatar.*;
import static com.morakmorak.morak_back_end.entity.QCategory.*;
import static com.morakmorak.morak_back_end.entity.QComment.*;
import static com.morakmorak.morak_back_end.entity.QReview.*;
import static com.morakmorak.morak_back_end.entity.QUser.*;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;

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
}
