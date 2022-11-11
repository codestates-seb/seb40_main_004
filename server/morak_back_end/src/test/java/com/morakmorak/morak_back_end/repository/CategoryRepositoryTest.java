package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("카테고리의 이름으로 카테고리 객체를 찾아내는 테스트 ")
    public void findByCategoryNameByString() throws Exception{
        //given
        Category category = Category.builder().categoryName("test").articleList(List.of(Article.builder().build())).build();
        categoryRepository.save(category);
        //when
        Category dbCategory = categoryRepository.findCategoryByCategoryName(category.getCategoryName()).orElseThrow();
        //then
        assertThat(dbCategory.getCategoryName()).isEqualTo(category.getCategoryName());
     }

}