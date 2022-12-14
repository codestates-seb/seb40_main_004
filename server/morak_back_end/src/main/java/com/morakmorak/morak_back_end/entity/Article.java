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

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<File> files = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article",
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article",fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    @JsonManagedReference(value = "article")
    private List<ArticleLike> articleLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Answer> answers = new ArrayList<>();


    public void injectTo(User user) {
        if (this.user != null) {
            this.user.getArticles().remove(this);
        }

        this.user = user;
        user.getArticles().add(this);
    }

    public void changeArticle(Article article) {
        if (!article.getTitle().isEmpty()) {
            this.title = article.getTitle();
        }
        if (!article.getContent().isEmpty()) {
            this.content = article.getContent();
        }
        if (article.getThumbnail() != null) {
            this.thumbnail = article.getThumbnail();
        }
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
        return this.isClosed;
    }

    public Boolean isQuestion() {
        return this.category.getName() == CategoryName.QNA;
    }

    public Boolean statusIsPosting() {
        return this.articleStatus == ArticleStatus.POSTING;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public Article plusClicks() {
        this.clicks++;
        return this;
    }
}
