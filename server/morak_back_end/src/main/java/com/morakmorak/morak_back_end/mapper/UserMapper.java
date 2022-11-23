package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.security.oauth.Oauth2UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toLoginEntity(AuthDto.RequestLogin dto);
    User toJoinEntity(AuthDto.RequestJoin dto);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "nickname")
    @Mapping(source = "provider", target = "provider")
    User toOAuthEntity(Oauth2UserDto dto);

    User EditProfileToEntity(UserDto.SimpleEditProfile dto);

    UserDto.SimpleEditProfile userToEditProfile(User user);

    @Mapping(source = "id", target = "userId")
    UserDto.Redis userToRedisUser(User user);

    @Mapping(source = "userId", target = "id")
    User redisUserToUser(UserDto.Redis dto);

    default List<UserDto.ResponseRanking> toResponseRankDto(List<User> user) {
        return user.stream().map(
                e ->
                        UserDto.ResponseRanking.builder()
                                .point(e.getPoint())
                                .grade(e.getGrade())
                                .jobType(e.getJobType())
                                .nickname(e.getNickname())
                                .userId(e.getId())
                                .infoMessage(e.getInfoMessage())
                                .answerCount((long) e.getAnswers().size())
                                .articleCount((long) e.getArticles().size())
                                .likeCount((long) (
                                        e.getAnswers().stream().mapToInt(a -> a.getAnswerLike().size()).sum() +
                                                e.getArticles().stream().mapToInt(ar -> ar.getArticleLikes().size()).sum()
                                ))
                                .avatar(
                                        e.getAvatar() != null ? AvatarDto.SimpleResponse.builder()
                                                .avatarId(e.getAvatar().getId())
                                                .filename(e.getAvatar().getOriginalFilename())
                                                .remotePath(e.getAvatar().getRemotePath())
                                                .build() : null
                                )
                                .build()
        ).collect(Collectors.toList());
    }
}
