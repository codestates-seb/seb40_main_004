package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
