package com.morakmorak.morak_back_end.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.morakmorak.morak_back_end.exception.ErrorCode.BAD_REQUEST;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    private String content;

    @Builder.Default
    private Boolean isPicked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "review_id")
    private Review review;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(updatable = false,name = "created_date")
    private LocalDate createDate;

    @Builder.Default
    @OneToMany(mappedBy = "answer")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "answer")
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "answer")
    private List<File> files = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "answer")
    private List<AnswerLike> answerLike = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "answer")
    private List<Bookmark> bookmarks = new ArrayList<>();//북마크로직에 답변 북마크 로직 추가 필요

    void isMappedWith(Article article) {
        this.article = article;
    }

    void isAcceptedWith(Review review) {
        if (this.isPicked) throw new BusinessLogicException(BAD_REQUEST);
        this.review = review;
        this.isPicked = true;
        this.article.closeThisArticle();
    }
    public Boolean isPickedAnswer() {
        if (this.isPicked) {
            return true;
        }return false;
    }

    public Answer injectUser(User verifiedUser) {
        this.user = verifiedUser;
        verifiedUser.getAnswers().add(this);
        return this;
    }
    public Answer injectArticle(Article verifiedArticle) {
        this.article = verifiedArticle;
        verifiedArticle.getAnswers().add(this);
        return this;
    }
    public Answer updateAnswer(Answer answerChanges) {
        Optional.ofNullable(answerChanges.getContent())
                .ifPresent(changedContent-> {
            this.content= changedContent;
        });
        return this;
    }

    public boolean hasPermissionWith(User userWhoAccess) {
        return this.user.getId() == userWhoAccess.getId() ? true : false;
    }

    public void injectReview(Review newReview) {
        this.review = newReview;
    }
}
