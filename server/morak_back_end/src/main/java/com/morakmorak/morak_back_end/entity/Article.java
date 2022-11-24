package com.morakmorak.morak_back_end.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@ToString(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTime {

    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Builder.Default
    @ToString.Include
    private Integer clicks = 0;

    @ToString.Include
    private String title;

    @ToString.Include
    @Column(length = 20000)
    private String content;

    @ToString.Include
    @Builder.Default
    private Boolean isClosed = Boolean.FALSE;

    @ToString.Include
    private Long thumbnail;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ArticleStatus articleStatus = ArticleStatus.POSTING;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(updatable = false,name = "created_date")
    private LocalDate createDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = CascadeType.PERSIST)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ArticleTag> articleTags = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<File> files = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    @JsonManagedReference(value = "article")
    private List<ArticleLike> articleLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Answer> answers = new ArrayList<>();


    public void injectUserForMapping(User user) {
        if (this.user != null) {
            this.user.getArticles().remove(this);
        }

        this.user = user;
        user.getArticles().add(this);
    }

    public void injectCategoryForMapping(Category category) {
        this.category = category;
        if (!category.getArticleList().contains(this)) {
            category.getArticleList().add(this);
        }
    }

    public void updateArticleElement(Article article) {
        Optional.ofNullable(article.getTitle()).ifPresent(presentTitle -> {
            this.title = presentTitle;
        });
        Optional.ofNullable(article.getContent()).ifPresent(presentContent -> {
            this.content = presentContent;
        });
        Optional.ofNullable(article.getThumbnail()).ifPresent(presentThumbnail -> {
            this.thumbnail = presentThumbnail;
        });
    }

    public void changeArticleStatus(ArticleStatus articleStatus) {
        this.articleStatus = articleStatus;
    }

    public void closeThisArticle() {
        this.isClosed = true;
    }

    public void addAnswer(Answer answer) {
        if (answer.getArticle() != null) throw new BusinessLogicException(ErrorCode.BAD_REQUEST);
        this.answers.add(answer);
        answer.isMappedWith(this);
    }
    public Boolean isClosedArticle() {
        if (this.isClosed) {
            return true;
        }return false;
    }

    public Boolean isQuestion() {
        if (this.category.getName() == CategoryName.QNA) {
            return true;
        }return false;
    }

    public Boolean statusIsPosting() {
        if (this.articleStatus == ArticleStatus.POSTING) {
            return true;
        }
        return false;
    }

    public void injectReview(Review review) {
        this.reviews.add(review);
    }
}
