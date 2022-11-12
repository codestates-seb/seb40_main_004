package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.util.ArticleTestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleMapperTest {
    @Autowired
    ArticleMapper articleMapper;

    @Test
    @DisplayName("requestUpdateArticleToEntity 매퍼 작동 테스트")
    public void requestUpdateArticleToEntityTest() throws Exception{
        //given
        ArticleDto.RequestUpdateArticle requestUpdateArticle = ArticleTestConstants.REQUEST_UPDATE_ARTICLE;

        //when
        Article article = articleMapper.requestUpdateArticleToEntity(requestUpdateArticle, 1L);
        //then
        assertThat(article.getId()).isEqualTo(1L);
        assertThat(article.getContent()).isEqualTo(requestUpdateArticle.getContent());
        assertThat(article.getThumbnail()).isEqualTo(requestUpdateArticle.getThumbnail());

     }
}