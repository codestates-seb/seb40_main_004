package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)

class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("INFO 카테고리의 이름으로 카테고리 객체를 찾아내는 테스트 ")
    public void findByCategoryNameByString_INFO() throws Exception{
        //given
        Category category = Category.builder().name(CategoryName.INFO).articleList(List.of(Article.builder().build())).build();
        categoryRepository.save(category);
        //when
        Category dbCategory = categoryRepository.findCategoryByName(category.getName()).orElseThrow();
        //then
        assertThat(dbCategory.getName()).isEqualTo(category.getName());
     }
    @Test
    @DisplayName("QNA 카테고리의 이름으로 카테고리 객체를 찾아내는 테스트  ")
    public void findByCategoryNameByString_QNA() throws Exception{
        //given
        Category category = Category.builder().name(CategoryName.QNA).articleList(List.of(Article.builder().build())).build();
        categoryRepository.save(category);
        //when
        Category dbCategory = categoryRepository.findCategoryByName(category.getName()).orElseThrow();
        //then
        assertThat(dbCategory.getName()).isEqualTo(category.getName());
    }

}