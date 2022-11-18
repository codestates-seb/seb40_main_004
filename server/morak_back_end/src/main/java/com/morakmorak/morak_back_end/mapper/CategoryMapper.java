package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.entity.Category;

import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {


    default Category RequestUploadArticleToCategory(ArticleDto.RequestUploadArticle uploadArticle) {
        return Category.builder().name(uploadArticle.getCategory()).build();
    }

}
