package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.UserStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String infoMessage;

    private Integer point;

    private String oauthProvider;

    private Boolean isJobSeeker;

    private Boolean isDeveloper;

    private Boolean gender;

    private Integer zipCode;

    private String address;

    private Boolean receiveEmail;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avartar_id")
    private Avatar avatar;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserSkillStack> userSkillStacks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Answer> answers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<ChatIn> chatIns = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Chat> chats = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<GuestBoard> guestBoards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Article> articles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Deposit> deposits = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();


    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<BookMark> bookMarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();


}
