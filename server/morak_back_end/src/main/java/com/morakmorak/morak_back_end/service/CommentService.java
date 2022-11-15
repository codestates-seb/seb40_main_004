package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.CommentDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.Comment;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.repository.CommentRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleService articleService;
    private final UserService userService;

    public CommentDto.ReponsePost makeComment(Long userId, Long articleId, Comment commentNotSaved) {
        User verifiedUser = userService.findVerifiedUserById(userId);
        commentNotSaved.injectUser(verifiedUser);

        Article verifiedArticle =articleService.findVerifiedArticle(articleId);
        commentNotSaved.injectArticle(verifiedArticle);

        Comment savedComment = commentRepository.save(commentNotSaved);
        return CommentDto.ReponsePost.builder()
                .userInfo(UserDto.ResponseForCommentUserInfo.of(savedComment.getUser()))
                .avatar(AvatarDto.SimpleResponse.of(savedComment.getUser().getAvatar()))
                .commentId(savedComment.getId())
                .articleId(savedComment.getArticle().getId())
                .content(savedComment.getContent())
                .build();
    }

}
