package com.morakmorak.morak_back_end.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.morakmorak.morak_back_end.domain.PointCalculator;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import com.morakmorak.morak_back_end.entity.enums.UserStatus;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.util.JwtTokenUtil;
import io.netty.util.internal.StringUtil;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.entity.enums.Grade.*;
import static com.morakmorak.morak_back_end.entity.enums.JobType.DEFAULT;
import static com.morakmorak.morak_back_end.entity.enums.UserStatus.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private String phone;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = RUNNING;

    private String infoMessage;

    @Builder.Default
    private Integer point = 0;

    private String provider;

    private Boolean isJobSeeker;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobType jobType = DEFAULT;

    private Boolean gender;

    private Integer zipCode;

    private String address;

    private Boolean receiveEmail;

    private String github;

    private String blog;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Grade grade = CANDLE;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "avartar_id")
    private Avatar avatar;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Answer> answers = new ArrayList<>();

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
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Article> articles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Review> writtenReviews = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Review> receivedReviews = new ArrayList<>();

    @JsonManagedReference(value = "user")
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<ArticleLike> articleLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
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

    public String injectUserInformationForAccessToken(JwtTokenUtil jwtTokenUtil) {
        return jwtTokenUtil.createAccessToken(this.getEmail(), this.getId(), getRoles(), this.getNickname());
    }

    public String injectUserInformationForRefreshToken(JwtTokenUtil jwtTokenUtil) {
        return jwtTokenUtil.createRefreshToken(this.getEmail(), this.getId(), getRoles(), this.getNickname());
    }

    private List<String> getRoles() {
        List<String> roles = this.userRoles
                .stream().map(e -> e.getRole().getRoleName().toString())
                .collect(Collectors.toList());

        if (roles.size() == 0) roles = List.of("ROLE_USER");
        return roles;
    }

    public void injectAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void editProfile(User request) {
        if (StringUtil.isNullOrEmpty(request.getNickname())) throw new BusinessLogicException(ErrorCode.BAD_REQUEST);

        this.infoMessage = request.infoMessage;
        this.github = request.github;
        this.blog = request.blog;
        this.nickname = request.nickname;
        this.jobType = request.jobType;
    }

    public void addArticle(Article article) {
        this.articles.add(article);
    }

    public void addPoint(Object object, PointCalculator pointCalculator) {
        this.point += pointCalculator.calculatePaymentPoint(object);
        updateGrade();
    }

    public void minusPoint(Object object, PointCalculator pointCalculator) {
        this.point -= pointCalculator.calculatePaymentPoint(object);
        updateGrade();
    }

    private Grade checkGradeUpdatable() {
        if (this.point >= 20000) {
            return MORAKMORAK;
        } else if (this.point >= 10000) {
            return BONFIRE;
        } else if (this.point >= 5000) {
            return CANDLE;
        } else {
            return MATCH;
        }
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    private void updateGrade() {
        this.grade = checkGradeUpdatable();
    }

    public void changeAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void changeStatus(UserStatus status) {
        this.userStatus = status;
    }

    public Boolean checkIfRemovedOrBlockedUser() {
        return this.getUserStatus().equals(BLOCKED) || this.getUserStatus().equals(DELETED);
    }

    public void setRandomEmail() {
        this.email += UUID.randomUUID().toString();
    }

    public void deleteAvatar() {
        this.avatar = null;
    }
    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }
    public void sendPoint(Integer pointToSend) {
        this.point -=pointToSend;
    }
    public void receivePoint(Integer pointToReceive) {
        this.point +=pointToReceive;
    }
}
