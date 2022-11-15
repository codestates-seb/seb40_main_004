package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.CommentDto;
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


    public Comment makeComment_v1(Long userId, Long articleId, String content) {
        User verifiedUser = userService.findVerifiedUserById(userId);
        Article verifiedArticle =articleService.findVerifiedArticle(articleId);
        Comment commentNotSaved = Comment.builder().user(verifiedUser)
                .article(verifiedArticle)
                .content(content).build();
        Comment savedComment = commentRepository.save(commentNotSaved);
        return savedComment;
    }



    public CommentDto.Reponse makeComment_v2(Long userId, Long articleId, Comment commentNotSaved) {
        User verifiedUser = userService.findVerifiedUserById(userId);
        commentNotSaved.injectUser(verifiedUser);

        Article verifiedArticle =articleService.findVerifiedArticle(articleId);
        commentNotSaved.injectArticle(verifiedArticle);

        Comment savedComment = commentRepository.save(commentNotSaved);
        return CommentDto.Reponse.builder()
                .userId(savedComment.getUser().getId())
                .nickname(savedComment.getUser().getNickname())
                .commentId(savedComment.getId())
                .articleId(savedComment.getArticle().getId())
                .content(savedComment.getContent())
                .build();
    }

//    방법 배우고 나서 수정하겠습니다
//    public void saveCommentWith() {
//        User
//    }

}
