package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.repository.UserQueryRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;

    public User findVerifiedUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    public UserDto.ResponseDashBoard findUserDashboard(Long userId) {
        int year = LocalDate.now().getYear();

        UserDto.ResponseDashBoard userDashboardBasicInfo = userQueryRepository.getUserDashboardBasicInfo(userId);
        List<Article> recentQuestions = userQueryRepository.get50RecentQuestions(userId);
        List<ReviewDto.Response> receivedReviews = userQueryRepository.getReceivedReviews(userId);
        List<BadgeQueryDto> usersTop3Badges = userRepository.getUsersTop3Badges(userId);
        List<TagQueryDto> usersTop3Tags = userRepository.getUsersTop3Tags(userId);
        List<ActivityQueryDto> userActivitiesOnThisYear = userRepository.getUserActivitiesOnThisYear(Date.valueOf(year + "-01-01"), Date.valueOf(year + 1 + "-01-01"), userId);

        List<ArticleDto.ResponseListTypeArticle> questionsDto = recentQuestions.stream().map(e -> {
            List<TagDto.SimpleTag> tags = e.getArticleTags().stream().map(
                    t -> tagMapper.tagEntityToTagDto(t.getTag())
            ).collect(Collectors.toList());

            return articleMapper.articleToResponseSearchResultArticle(e, e.getComments().size(), e.getAnswers().size(), tags, e.getArticleLikes().size());
        }).collect(Collectors.toList());

        try {
            userDashboardBasicInfo.addRestInfo(usersTop3Tags, usersTop3Badges, questionsDto, userActivitiesOnThisYear, receivedReviews);
            return userDashboardBasicInfo;
        } catch (NullPointerException e) {
            throw new BusinessLogicException(USER_NOT_FOUND);
        }
    }
}
