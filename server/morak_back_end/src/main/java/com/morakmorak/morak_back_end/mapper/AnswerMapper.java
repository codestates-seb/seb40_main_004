package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {

    AnswerDto.ResponseAnswerLike makingResponseAnswerLikeDto(Long answerId, Long userId, Boolean isLiked, Integer likeCount);
    @Mapping(source="answer.id",target="answerId")
    @Mapping(source="answer.content",target="content")
    @Mapping(source="answer.user.id",target="userInfo.userId")
    @Mapping(source="answer.user.nickname",target="userInfo.nickname")
    @Mapping(source="answer.user.grade",target="userInfo.grade")
    @Mapping(source="answer.user.avatar.id",target="avatar.avatarId")
    @Mapping(source = "answer.user.avatar.originalFilename", target = "avatar.filename")
    @Mapping(source="answer.user.avatar.remotePath",target="avatar.remotePath")
    @Mapping(source="answer.createdAt",target="createdAt")
    AnswerDto.ResponseListTypeAnswer answerToResponseListTypeAnswer(Answer answer, Boolean isPicked, Boolean isLiked, Integer answerLikeCount, CommentDto.Response commentPreview, Integer commentCount);
    @Mapping(source="answer.article.id",target="articleId")
    @Mapping(source="answer.id",target="answerId")
    @Mapping(source="answer.content",target="content")
    @Mapping(source="answer.user.id",target="userInfo.userId")
    @Mapping(source="answer.user.nickname",target="userInfo.nickname")
    @Mapping(source="answer.user.grade",target="userInfo.grade")
    AnswerDto.ResponseUserAnswerList answerToResponseUserAnswerList(Answer answer,Boolean isPicked,Integer answerLikeCount,Integer commentCount );

}
