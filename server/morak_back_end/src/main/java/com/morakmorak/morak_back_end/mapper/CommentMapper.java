package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "comment.article.id", target = "articleId")
    @Mapping(source = "comment.user.id", target = "userInfo.userId")
    @Mapping(source = "comment.user.nickname", target = "userInfo.nickname")
    @Mapping(source = "comment.user.grade", target = "userInfo.grade")
    @Mapping(source = "comment.user.avatar.id", target = "avatar.avatarId")
    @Mapping(source = "comment.user.avatar.originalFilename", target = "avatar.filename")
    @Mapping(source = "comment.user.avatar.remotePath", target = "avatar.remotePath")
    CommentDto.Response commentToCommentDto(Comment comment);
}
