package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
