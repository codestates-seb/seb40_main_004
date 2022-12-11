package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findVerifiedCategoryByName(CategoryName categoryName) {
       return categoryRepository.findCategoryByName(categoryName)
               .orElseThrow(() -> new BusinessLogicException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
