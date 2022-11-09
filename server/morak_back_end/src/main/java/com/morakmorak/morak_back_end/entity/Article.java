package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.ArticleStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    private Integer clicks;

    private String title;

    private String content;

    private Boolean isClosed;

    private LocalDateTime expiredDate;

    private String mainFile;

    @Enumerated(EnumType.STRING)
    private ArticleStatus articleStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "article")
    private BookMark bookMark;

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<ArticleTag> articleTags = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Builder.Default
    @OneToMany(mappedBy="article")
    private List<File> files = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Comment> Comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Report> reports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article")
    private List<Review> reviews = new ArrayList<>();
}
