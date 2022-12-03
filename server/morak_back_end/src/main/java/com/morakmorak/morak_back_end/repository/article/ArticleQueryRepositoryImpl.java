package com.morakmorak.morak_back_end.repository.article;

import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.repository.article.ArticleQueryRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import static com.morakmorak.morak_back_end.entity.enums.ArticleStatus.*;


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
                .where(categoryEq(category), (keywordEq(keyword, target)),
                        (article.articleStatus.eq(POSTING)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortEq(sort))
                .fetch();

        Long count = queryFactory
                .select(article.count())
                .from(article)
                .where(categoryEq(category)
                                ,(keywordEq(keyword, target))
                                ,(article.articleStatus.eq(POSTING))
                        )
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
                return article.title.contains(keyword).and(statusPosting());
            case "content":
                return article.content.contains(keyword).and(statusPosting());
            case "tag":
                return article.articleTags.any().tag.name.eq(TagName.valueOf(keyword)).and(statusPosting());
            case "bookmark":
                return article.bookmarks.any().user.id.eq(Long.parseLong(keyword)).and(statusPosting());
            case "titleAndContent":
                return article.title.contains(keyword).or(article.content.startsWith(keyword)).and(statusPosting());
            case "isChecked":
                return article.isClosed.eq(Boolean.valueOf(keyword)).and(statusPosting());
            case "titleAndContentIsCheckedTrue":
                return article.title.contains(keyword).or(article.content.startsWith(keyword))
                        .and(article.isClosed.eq(true)
                                .and(statusPosting()));
            case "titleAndContentIsCheckedFalse":
                return article.title.contains(keyword).or(article.content.startsWith(keyword))
                        .and(article.isClosed.eq(false)
                                .and(statusPosting()));
            default:
                return statusPosting();
        }
    }

    private BooleanExpression statusPosting() {
        return article.articleStatus.eq(POSTING);
    }

    private OrderSpecifier sortEq(String sort) {
        if (sort == null) {
            return article.id.desc();
        }
        switch (sort) {
            case "desc":
                return article.id.desc();
            case "asc":
                return article.id.asc();
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
