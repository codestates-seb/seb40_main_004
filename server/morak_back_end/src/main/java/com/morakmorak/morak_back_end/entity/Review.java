package com.morakmorak.morak_back_end.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "questioner_id")
    private User questioner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name ="answerer_id")
    private User answerer;

    private String content;

    @OneToOne(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Answer answer;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.PERSIST)
    private List<ReviewBadge> reviewBadges = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    public void mapArticleAndUsers() {
        this.questioner.getWrittenReviews().add(this);
        this.answerer.getReceivedReviews().add(this);
        this.answer.isAcceptedWith(this);
    }
}
