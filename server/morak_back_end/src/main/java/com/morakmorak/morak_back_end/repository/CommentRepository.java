package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllCommentsByArticleId(Long articleId);

    Optional<Comment> findByUserId(Long userId);

    Collection<Comment> findAllCommentsByAnswerId(Long answerId);
}
