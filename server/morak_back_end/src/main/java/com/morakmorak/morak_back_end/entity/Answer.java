package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

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

    void isMappedWith(Article article) {
        this.article = article;
    }

    void isAcceptedWith(Review review) {
        if (this.isPicked) throw new BusinessLogicException(BAD_REQUEST);
        this.review = review;
        this.isPicked = true;
        this.article.closeThisArticle();
    }
    public boolean isPickedAnswer() {
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
}
