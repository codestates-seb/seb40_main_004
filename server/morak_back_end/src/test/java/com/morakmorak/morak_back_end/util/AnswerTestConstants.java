package com.morakmorak.morak_back_end.util;

import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.*;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.entity.enums.Grade;
import com.morakmorak.morak_back_end.entity.enums.TagName;

import java.util.List;

import static com.morakmorak.morak_back_end.util.ArticleTestConstants.REQUEST_FILE_WITH_IDS;
import static com.morakmorak.morak_back_end.util.TestConstants.*;

public class AnswerTestConstants {
    public final static Avatar AVATAR = Avatar.builder().id(ID1).originalFilename("randomfilename").remotePath("randomremotepath").build();
    public final static Avatar AVATAR_NOT_SAVED = Avatar.builder().originalFilename("randomfilename").remotePath("randomremotepath").build();

    public final static User USER1 = User.builder().id(ID1).email(EMAIL1).name(NAME1).nickname(NICKNAME1).grade(Grade.GOLD).avatar(AVATAR).build();
    public final static AnswerDto.SimpleResponsePostAnswer VALID_ANSWER_RESPONSE = AnswerDto.SimpleResponsePostAnswer.builder()
            .answerId(1L)
            .userInfo(UserDto.ResponseSimpleUserDto.of(USER1))
            .avatar(AvatarDto.SimpleResponse.of(AVATAR))
            .content(CONTENT1)
            .createdAt(NOW_TIME)
            .lastModifiedAt(NOW_TIME)
            .build();
    public final static Article ARTICLE_BY_USER1 = Article.builder().title("안녕하세요 타이틀입니다. 잘 부탁드립니다. 타이틀은 신경씁니다.")
            .content("콘텐트입니다. 잘부탁드립니다.")
            .user(USER1)
            .thumbnail(1L)
            .files(List.of(File.builder().id(1L).article(Article.builder().id(1L).build()).build(),
                    File.builder().id(2L).article(Article.builder().id(1L).build()).build()))
            .articleTags(List.of(ArticleTag.builder().tag(Tag.builder().name(TagName.JAVA).build()).build()))
            .category(Category.builder().name(CategoryName.QNA).build())
            .files(List.of(File.builder().article(Article.builder().id(1L).build()).build()))
            .build();

    public final static User USER1_NOT_SAVED = User.builder().email(EMAIL1).name(NAME1).nickname(NICKNAME1).grade(Grade.GOLD).avatar(AVATAR).build();
    public final static User USER2_NOT_SAVED = User.builder().email(EMAIL2).name(NAME2).nickname(NICKNAME2).grade(Grade.GOLD).avatar(AVATAR).build();
    public final static AnswerDto.RequestPostAnswer VALID_ANSWER_REQUEST = AnswerDto.RequestPostAnswer.builder()
            .content(CONTENT3).fileIdList(REQUEST_FILE_WITH_IDS)
            .build();
    public final static com.morakmorak.morak_back_end.entity.Answer BOOKMARKED_ANSWER = Answer.builder().user(USER1_NOT_SAVED).content("유효한 내용의 컨텐츠입니다. 15자 이상이니 안심하세요").build();
    public final static Bookmark BOOKMARK_USER1_ANSWER = Bookmark.builder().user(USER1)
            .answer(BOOKMARKED_ANSWER).build();

}
