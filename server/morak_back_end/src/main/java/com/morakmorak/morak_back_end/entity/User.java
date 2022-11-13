package com.morakmorak.morak_back_end.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import com.morakmorak.morak_back_end.entity.enums.UserStatus;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private String provider;

    private Boolean isJobSeeker;

    private JobType jobType;

    private Boolean gender;

    private Integer zipCode;

    private String address;

    private Boolean receiveEmail;

    private String github;

    private String blog;

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
    @JsonManagedReference(value = "user")
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

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ArticleLike> articleLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<AnswerLike> answerLikes = new ArrayList<>();

    public User(String password) {
        this.password = password;
    }

    public String encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
        return this.password;
    }

    public boolean comparePassword(PasswordEncoder passwordEncoder, String otherPassword) {
        return passwordEncoder.matches(otherPassword, this.password);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public String injectUserInformationForToken(JwtTokenUtil jwtTokenUtil) {
        List<String> roles = this.userRoles
                .stream().map(e -> e.getRole().getRoleName().toString())
                .collect(Collectors.toList());

        if (roles.size() == 0) roles = List.of("Role_User");

        return jwtTokenUtil.createAccessToken(this.getEmail(), this.getId(), roles);
    }
}
