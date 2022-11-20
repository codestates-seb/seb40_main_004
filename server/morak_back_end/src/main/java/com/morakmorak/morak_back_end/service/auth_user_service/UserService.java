package com.morakmorak.morak_back_end.service.auth_user_service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.UserQueryRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.exception.ErrorCode.NICKNAME_EXISTS;
import static com.morakmorak.morak_back_end.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public User findVerifiedUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
    }

    public void checkDuplicateNickname(String nickname) {
        if (userRepository.findUserByNickname(nickname).isPresent()) throw new BusinessLogicException(NICKNAME_EXISTS);
    }

    public UserDto.SimpleEditProfile editUserProfile(User request, Long userId) {
        User dbUser = findVerifiedUserById(userId);
        if (!request.getNickname().equals(dbUser.getNickname())) checkDuplicateNickname(request.getNickname());
        dbUser.editProfile(request);
        return userMapper.userToEditProfile(dbUser);
    }

    public ResponseMultiplePaging<UserDto.ResponseRanking> getUserRankList(PageRequest request) {
        Page<User> userRankPage = userQueryRepository.getRankData(request);
        List<UserDto.ResponseRanking> result = userMapper.toResponseRankDto(userRankPage.getContent());
        return new ResponseMultiplePaging<>(result, userRankPage);
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
