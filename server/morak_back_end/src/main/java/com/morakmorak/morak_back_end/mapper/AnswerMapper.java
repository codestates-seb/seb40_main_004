package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.ArticleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {

    AnswerDto.ResponseAnswerLike makingResponseAnswerLikeDto(Long answerId, Long userId, Boolean isLiked, Integer likeCount);

}
