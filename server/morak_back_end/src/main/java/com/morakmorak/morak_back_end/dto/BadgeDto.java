package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Review;
import com.morakmorak.morak_back_end.entity.ReviewBadge;
import com.morakmorak.morak_back_end.entity.enums.BadgeName;
import io.jsonwebtoken.lang.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

public class BadgeDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleBadge {
        @NotBlank
        private Long badgeId;
        private BadgeName name;

        public SimpleBadge(Long badgeId, BadgeName name) {
            this.badgeId = badgeId;
            this.name = name;
        }


        public static SimpleBadge of(ReviewBadge reviewBadge) {
            if ( reviewBadge == null ) {
                return null;
            }
            SimpleBadge.SimpleBadgeBuilder simpleBadge = SimpleBadge.builder();

            simpleBadge.badgeId( reviewBadge.getBadge().getId() );
            simpleBadge.name( reviewBadge.getBadge().getName() );

            return simpleBadge.build();
        }

        public static List<SimpleBadge> badgeDtoListFrom(Review review) {
            if (Collections.isEmpty(review.getReviewBadges())) {
                return java.util.Collections.emptyList();
            }
            return review.getReviewBadges().stream().map(reviewBadge -> {
                SimpleBadge simpleBadge = SimpleBadge.of(reviewBadge);
                return simpleBadge;
            }).collect(Collectors.toList());
        }

    }
}