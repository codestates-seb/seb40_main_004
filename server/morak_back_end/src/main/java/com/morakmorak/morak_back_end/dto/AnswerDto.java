package com.morakmorak.morak_back_end.dto;

import com.morakmorak.morak_back_end.entity.Answer;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnswerDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestPostAnswer {
        @NotBlank
        @Length(min = 10)
        private String content;
        private List<FileDto.RequestFileWithId> fileIdList = new ArrayList<>();
//        private Long thumbnail; todo: 반영 여부 확인하기
    }
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SimpleResponsePostAnswer {
        private Long answerId;
        private UserDto.ResponseSimpleUserDto userInfo;
        private AvatarDto.SimpleResponse avatar;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        public static SimpleResponsePostAnswer of(Answer savedAnswer) {
            return SimpleResponsePostAnswer.builder()
                    .answerId(savedAnswer.getId())
                    .userInfo(UserDto.ResponseSimpleUserDto.of(savedAnswer.getUser()))
                    .avatar(AvatarDto.SimpleResponse.of(savedAnswer.getUser().getAvatar()))
                    .content(savedAnswer.getContent())
                    .createdAt(savedAnswer.getCreatedAt())
                    .lastModifiedAt(savedAnswer.getLastModifiedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseAnswerLike {
        private Long answerId;
        private Long userId;
        private Boolean isLiked;
        private Integer likeCount;
    }
}
