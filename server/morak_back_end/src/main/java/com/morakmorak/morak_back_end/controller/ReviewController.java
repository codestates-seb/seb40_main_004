package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.BadgeDto;
import com.morakmorak.morak_back_end.dto.ReviewDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Review;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.ReviewService;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/articles/{article-id}/answers/{answer-id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto.ResponseDetailReview postReviewOnAnswer(@Valid @RequestBody ReviewDto.RequestPostReview request,
                                                             @RequestUser UserDto.UserInfo userInfo,
                                                             @PathVariable("article-id") Long articleId,
                                                             @PathVariable("answer-id") Long answerId) {
        Review reviewWithoutBadges = Review.builder().content(request.getContent()).point(request.getPoint().orElseGet(() -> 0)).build();
        List<BadgeDto.SimpleBadge> badgeDtoList = request.getBadges();
        if (CollectionUtils.isEmpty(badgeDtoList) || badgeDtoList.size() > 3) {
            throw new BusinessLogicException(ErrorCode.BAD_REQUEST);
        }
       return reviewService.createReview(articleId, userInfo.getId(), answerId, badgeDtoList, reviewWithoutBadges);
    }

    @PostMapping("/users/{user-id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto.ResponseSimpleReview postReviewOnAnswer(@Valid @RequestBody ReviewDto.RequestPostReview request,
                                   @RequestUser UserDto.UserInfo userInfo,
                                   @PathVariable("user-id")Long receiverId ) {
        Review reviewWithoutBadges = Review.builder().content(request.getContent()).point(request.getPoint().orElseGet(() -> 0)).build();
        List<BadgeDto.SimpleBadge> badgeDtoList = request.getBadges();
        if (CollectionUtils.isEmpty(badgeDtoList) || badgeDtoList.size() > 3) {
            throw new BusinessLogicException(ErrorCode.BAD_REQUEST);
        }
        return reviewService.createReview(userInfo.getId(),receiverId, badgeDtoList, reviewWithoutBadges);
    }

}

