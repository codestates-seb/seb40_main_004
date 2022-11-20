package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.morakmorak.morak_back_end.entity.QAnswer.answer;
import static com.morakmorak.morak_back_end.entity.QArticle.article;
import static com.morakmorak.morak_back_end.entity.QArticleLike.articleLike;
import static com.morakmorak.morak_back_end.entity.QArticleTag.articleTag;
import static com.morakmorak.morak_back_end.entity.QBookmark.bookmark;
import static com.morakmorak.morak_back_end.entity.QComment.comment;
import static com.morakmorak.morak_back_end.entity.QFile.file;
import static com.morakmorak.morak_back_end.entity.QReview.review;
import static com.morakmorak.morak_back_end.entity.QTag.tag;
import static com.morakmorak.morak_back_end.entity.QUser.user;


@Repository
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ArticleQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Page<Article> search(String category, String keyword, String target, String sort, Pageable pageable) {
        List<Article> result = queryFactory
                .select(article)
                .from(article)
                .leftJoin(article.user, user)
                .leftJoin(article.articleTags, articleTag)
                .fetchJoin()
                .leftJoin(articleTag.tag, tag)
                .fetchJoin()
                .leftJoin(article.category, QCategory.category)
                .fetchJoin()
                .leftJoin(article.user.answers, answer)
                .leftJoin(article.user.comments, comment)
                .leftJoin(articleTag.article.articleLikes, articleLike)
                .leftJoin(article.bookmarks, bookmark)
                .leftJoin(article.answers, answer)
                .leftJoin(article.reviews, review)
                .leftJoin(article.files, file)
                .where(categoryEq(category),
                        keywordEq(keyword, target))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortEq(sort))
                .fetch();

        Long count = queryFactory
                .select(article.count())
                .from(article)
                .leftJoin(article.user, user)
                .leftJoin(article.articleTags, articleTag)
                .leftJoin(articleTag.tag, tag)
                .leftJoin(article.category, QCategory.category)
                .leftJoin(article.user.answers, answer)
                .leftJoin(article.user.comments, comment)
                .leftJoin(articleTag.article.articleLikes, articleLike)
                .leftJoin(article.bookmarks, bookmark)
                .leftJoin(article.answers, answer)
                .leftJoin(article.reviews, review)
                .leftJoin(article.files, file)
                .where(categoryEq(category),
                        keywordEq(keyword, target))
                .orderBy(sortEq(sort))
                .fetchOne();


        return new PageImpl<>(result, pageable, count);
    }

    private BooleanExpression categoryEq(String category) {
        return category != null ? article.category.name.eq(CategoryName.valueOf(category)) : null;
    }

    private BooleanExpression keywordEq(String keyword, String target) {
        return keyword != null ? targetEqWithKeyword(keyword, target) : null;
    }

    private BooleanExpression targetEqWithKeyword(String keyword, String target) {
        if (target == null) {
            return null;
        }
        switch (target) {
            case "title":
                return article.title.contains(keyword);
            case "content":
                return article.content.contains(keyword);
            case "tag":
            return article.articleTags.any().tag.name.in(TagName.valueOf(keyword));
            case "bookmark":
                 article.bookmarks.any().user.id.eq(Long.parseLong(keyword));
            case "titleAndContent":
                return article.title.contains(keyword).or(article.content.contains(keyword));
            default:
                return null;
        }
    }

    private OrderSpecifier sortEq(String sort) {
        if (sort == null) {
            return article.id.desc();
        }
        switch (sort) {
            case "desc":
                return article.id.desc();
            case "asc":
                article.id.asc();
            case "comment-desc":
                return article.comments.size().desc();
            case "comment-asc":
                return article.comments.size().asc();
            case "like-desc":
                return article.articleLikes.size().desc();
            case "like-asc":
                return article.articleLikes.size().asc();
            case "answer-desc":
                return article.answers.size().desc();
            case "answer-asc":
                return article.answers.size().asc();
            default:
                article.id.desc();
        }
        return article.id.desc();
    }
}
