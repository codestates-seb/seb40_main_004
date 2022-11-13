package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.entity.Category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    //    @Mapping(source = "category" , target = "category")
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "articleList", ignore = true)
    default Category RequestUploadArticleToCategory(ArticleDto.RequestUploadArticle uploadArticle) {
        return Category.builder().categoryName(uploadArticle.getCategory()).build();
    }

}
