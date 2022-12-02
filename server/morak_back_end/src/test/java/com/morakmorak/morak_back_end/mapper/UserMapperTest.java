package com.morakmorak.morak_back_end.mapper;


import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.JobType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    UserMapper userMapper;

    @BeforeEach
    public void init() {
        userMapper = new UserMapperImpl();
    }

    @Test
    @DisplayName("RequestLoginDto to UserEntity Test")
    public void test1() {
        //given
        AuthDto.RequestLogin dto = AuthDto.RequestLogin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .build();

        //when
        User user = userMapper.toLoginEntity(dto);

        //then
        assertThat(user.getEmail()).isEqualTo(EMAIL1);
        assertThat(user.getPassword()).isEqualTo(PASSWORD1);
    }

    @Test
    @DisplayName("RequestJoinDto to UserEntity Test")
    public void test2() {
        //given
        AuthDto.RequestJoin dto = AuthDto.RequestJoin.builder()
                .email(EMAIL1)
                .password(PASSWORD1)
                .nickname(NICKNAME1)
                .build();

        //when
        User user = userMapper.toJoinEntity(dto);

        //then
        assertThat(user.getEmail()).isEqualTo(EMAIL1);
        assertThat(user.getPassword()).isEqualTo(PASSWORD1);
        assertThat(user.getNickname()).isEqualTo(NICKNAME1);
    }

    @Test
    public void EditProfileToEntity() {
        //given
        UserDto.SimpleEditProfile dto = UserDto.SimpleEditProfile.builder()
                .blog(TISTORY_URL)
                .github(GITHUB_URL)
                .jobType(JobType.DEVELOPER)
                .infoMessage(CONTENT1)
                .nickname(NICKNAME1)
                .build();

        //when
        User user = userMapper.EditProfileToEntity(dto);

        //then
        assertThat(user.getBlog()).isEqualTo(dto.getBlog());
        assertThat(user.getGithub()).isEqualTo(dto.getGithub());
        assertThat(user.getJobType()).isEqualTo(dto.getJobType());
        assertThat(user.getInfoMessage()).isEqualTo(dto.getInfoMessage());
        assertThat(user.getNickname()).isEqualTo(dto.getNickname());
    }

    @Test
    public void userToEditProfile() {
        //given
        User user = User.builder()
                .blog(TISTORY_URL)
                .github(GITHUB_URL)
                .jobType(JobType.DEVELOPER)
                .infoMessage(CONTENT1)
                .nickname(NICKNAME1)
                .build();
        //when
        UserDto.SimpleEditProfile dto = userMapper.userToEditProfile(user);
        //then
        assertThat(user.getBlog()).isEqualTo(dto.getBlog());
        assertThat(user.getGithub()).isEqualTo(dto.getGithub());
        assertThat(user.getJobType()).isEqualTo(dto.getJobType());
        assertThat(user.getInfoMessage()).isEqualTo(dto.getInfoMessage());
        assertThat(user.getNickname()).isEqualTo(dto.getNickname());
    }

    @Test
    public void userToUserRankTest() {
        //given
        User user = User.builder()
                .point(100)
                .nickname(NICKNAME1)
                .avatar(Avatar.builder()
                        .id(ID2)
                        .remotePath(CONTENT1)
                        .originalFilename(CONTENT2)
                        .build())
                .grade(Grade.VIP)
                .jobType(JobType.DEVELOPER)
                .id(ID1)
                .build();

        Article article = Article.builder().user(user).build();
        article.injectUserForMapping(user);

        Comment comment = Comment.builder().user(user).article(article).build();
        comment.mapArticleAndUser();

        ArticleLike.builder().article(article).build();

        //when
        List<UserDto.ResponseRanking> responseRankings = userMapper.toResponseRankDto(List.of(user));

        //then
        assertThat(responseRankings.get(0).getAnswerCount()).isEqualTo(0);
        assertThat(responseRankings.get(0).getNickname()).isEqualTo(NICKNAME1);
        assertThat(responseRankings.get(0).getAvatar().getAvatarId()).isEqualTo(ID2);
        assertThat(responseRankings.get(0).getGrade()).isEqualTo(user.getGrade());
        assertThat(responseRankings.get(0).getJobType()).isEqualTo(user.getJobType());
        assertThat(responseRankings.get(0).getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("toResponsePoint 작동 테스트")
    void toResponsePoint() {
        //given
        User user = User.builder()
                .point(100)
                .nickname(NICKNAME1)
                .avatar(Avatar.builder()
                        .id(ID2)
                        .remotePath(CONTENT1)
                        .originalFilename(CONTENT2)
                        .build())
                .grade(Grade.VIP)
                .jobType(JobType.DEVELOPER)
                .id(ID1)
                .build();

        UserDto.ResponsePoint target = userMapper.toResponsePoint(user);

        Assertions.assertThat(target.getPoint()).isEqualTo(user.getPoint());
        Assertions.assertThat(target.getUserInfo().getUserId()).isEqualTo(user.getId());
        Assertions.assertThat(target.getUserInfo().getNickname()).isEqualTo(user.getNickname());
        Assertions.assertThat(target.getUserInfo().getGrade()).isEqualTo(user.getGrade());
        Assertions.assertThat(target.getAvatar().getAvatarId()).isEqualTo(user.getAvatar().getId());
        Assertions.assertThat(target.getAvatar().getRemotePath()).isEqualTo(user.getAvatar().getRemotePath());
        Assertions.assertThat(target.getAvatar().getFilename()).isEqualTo(user.getAvatar().getOriginalFilename());

    }
}