package com.morakmorak.morak_back_end.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(updatable = false,name = "created_date")
    private LocalDate createDate;

    @Builder.Default
    @OneToMany(mappedBy = "comment")
    private List<Report> reports = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void mapArticleAndUser() {
        if (this.article == null) {
            this.answer.getComments().add(this);
            this.user.getComments().add(this);
        }

        if (this.answer == null) {
            this.article.getComments().add(this);
            this.user.getComments().add(this);
        }
    }

    public Comment injectArticle(Article verifiedArticle) {
        this.article = verifiedArticle;
        article.getComments().add(this);
        return this;
    }

    public Comment injectUser(User verifiedUSer) {
        this.user = verifiedUSer;
        verifiedUSer.getComments().add(this);
        return this;
    }

    public boolean hasPermissionWith(User userWhoAccess) {
        return this.user.getId() == userWhoAccess.getId() ? true : false;
    }

    public Comment updateContent(String newContent) {
        this.content = newContent;
        return this;
    }
}
