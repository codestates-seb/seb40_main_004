package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.domain.NotificationGenerator;
import com.morakmorak.morak_back_end.dto.BadgeDto;
import com.morakmorak.morak_back_end.dto.ReviewDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.BadgeRepository;
import com.morakmorak.morak_back_end.repository.ReviewRepository;
import com.morakmorak.morak_back_end.repository.notification.NotificationRepository;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ArticleService articleService;
    private final UserService userService;
    private final AnswerService answerService;
    private final BadgeRepository badgeRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;

    public ReviewDto.ResponseDetailReview createReview(Long articleId, Long userId, Long answerId, List<BadgeDto.SimpleBadge> badgeDtoList, Review reviewWithoutBadges) {
        User verifiedRequestUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle = articleService.findVerifiedArticle(articleId);
        Answer verifiedAnswer = answerService.findVerifiedAnswerById(answerId);
        User articleAuthor = verifiedArticle.getUser();
        User receiver = verifiedAnswer.getUser();
        checkRequestUserIsAuthor(verifiedArticle, verifiedRequestUser);
        checkReceiverIsSameUser(verifiedRequestUser.getId(), receiver.getId());
        checkRemainingPoints(verifiedRequestUser, reviewWithoutBadges.getPoint());

        if (checkReviewPrequisites(verifiedArticle)) {

            injectBadgesOnReview(reviewWithoutBadges, badgeDtoList);
            donatePoint(verifiedRequestUser, receiver, reviewWithoutBadges.getPoint());
            reviewWithoutBadges.mapAnswer(verifiedAnswer).mapArticle(verifiedArticle).changeAnswerArticleStatus();
            Review reviewNotSaved = reviewWithoutBadges.addSender(verifiedRequestUser).addReciever(receiver);

            NotificationGenerator generator = NotificationGenerator.of(reviewNotSaved);
            Notification notification = generator.generateNotification();
            notificationRepository.save(notification);
            return ReviewDto.ResponseDetailReview.of(reviewRepository.save(reviewNotSaved));

        }else{throw new BusinessLogicException(ErrorCode.UNABLE_TO_REVIEW);}
    }

    public ReviewDto.ResponseSimpleReview createReview(Long senderId, Long receiverId, List<BadgeDto.SimpleBadge> badgeDtoList, Review reviewWithoutBadges ) {
        User verifiedSender = userService.findVerifiedUserById(senderId);
        User verifiedReceiver = userService.findVerifiedUserById(receiverId);

        checkRemainingPoints(verifiedSender, reviewWithoutBadges.getPoint());
        donatePoint(verifiedSender, verifiedReceiver, reviewWithoutBadges.getPoint());
        Review reviewNotSaved = reviewWithoutBadges.addSender(verifiedSender).addReciever(verifiedReceiver);

        NotificationGenerator generator = NotificationGenerator.of(reviewNotSaved);
        Notification notification = generator.generateNotification();
        notificationRepository.save(notification);

        return ReviewDto.ResponseSimpleReview.of(reviewRepository.save(reviewNotSaved));
    }
    public Boolean checkRequestUserIsAuthor(Article verifiedArticle, User verifiedUser) {
        if (!verifiedArticle.getUser().getId().equals(verifiedUser.getId())) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER);
        }
        return true;
    }

     Boolean checkReviewPrequisites(Article verifiedArticle) {
        if (verifiedArticle.isQuestion() && !verifiedArticle.isClosedArticle()
                && verifiedArticle.statusIsPosting()) {
            return true;
        }
        throw new BusinessLogicException(ErrorCode.UNABLE_TO_REVIEW);
    }

    public Boolean checkRemainingPoints(User user, Integer point) {
        Integer remainingPoints = user.getPoint() - point;
        if (remainingPoints < 0) {
            throw new BusinessLogicException(ErrorCode.BAD_REQUEST);
        }
        return true;
    }

    public void checkReceiverIsSameUser(Long senderId, Long receiverId) {
        if (Objects.equals(senderId,receiverId)) {
            throw new BusinessLogicException(ErrorCode.UNABLE_TO_REVIEW);
        }
    }

    @Transactional
    public void donatePoint(User sender, User receiver, Integer point) {
        sender.sendPoint(point);
        receiver.receivePoint(point);
    }

    public void injectBadgesOnReview(Review review, List<BadgeDto.SimpleBadge> badgeDtoList) {
        badgeDtoList.stream().forEach(badgeDto -> {
            Badge dbBadge = badgeRepository.findById(badgeDto.getBadgeId())
                    .orElseThrow(()-> new BusinessLogicException(ErrorCode.BADGE_NOT_FOUND));
            ReviewBadge reviewBadge = ReviewBadge.builder().review(review).badge(dbBadge).build();
            reviewBadge.mapBadgeAndReview();
        });
    }
}

