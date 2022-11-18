package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Category;
import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findCategoryByName(CategoryName category);

    @Query("select c from Category c where c.name = :category ")
    Optional<Category> findCategoryByCategoryName(String category);

}
