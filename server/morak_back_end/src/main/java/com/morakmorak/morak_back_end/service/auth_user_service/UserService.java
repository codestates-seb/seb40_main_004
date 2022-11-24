package com.morakmorak.morak_back_end.service.auth_user_service;

import com.morakmorak.morak_back_end.dto.*;
import com.morakmorak.morak_back_end.entity.Article;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.enums.ActivityType;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.mapper.ArticleMapper;
import com.morakmorak.morak_back_end.mapper.TagMapper;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.user.UserQueryRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.morakmorak.morak_back_end.entity.enums.ActivityType.*;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;

@Service
@Transactional
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

    public ActivityDto.Detail findActivityHistoryOn(LocalDate date, Long userId) {
        findVerifiedUserById(userId);

        if (date.getYear() != LocalDate.now().getYear()) throw new BusinessLogicException(INVALID_DATE_RANGE);

        List<ActivityDto.Article> articles = userQueryRepository.getWrittenArticleHistoryOn(date, userId);
        List<ActivityDto.Article> answers = userQueryRepository.getWrittenAnswerHistoryOn(date, userId);
        List<ActivityDto.Comment> comments = userQueryRepository.getWrittenCommentHistoryOn(date, userId);

        long totalSize = articles.size() + answers.size() + comments.size();

        return ActivityDto.Detail.builder()
                .articles(articles)
                .answers(answers)
                .comments(comments)
                .total(totalSize)
                .build();
    }

    public UserDto.ResponseDashBoard findUserDashboard(Long userId) {
        LocalDate january1st = LocalDate.now().withDayOfYear(1);
        LocalDate december31st = LocalDate.now().withDayOfYear(365);
        
        UserDto.ResponseDashBoard userDashboardBasicInfo = userQueryRepository.getUserDashboardBasicInfo(userId);
        List<Article> recentQuestions = userQueryRepository.get50RecentQuestions(userId);
        List<ReviewDto.Response> receivedReviews = userQueryRepository.getReceivedReviews(userId);
        List<BadgeQueryDto> usersTop3Badges = userRepository.getUsersTop3Badges(userId);
        List<TagQueryDto> usersTop3Tags = userRepository.getUsersTop3Tags(userId);

        List<ActivityDto.Temporary> annualArticlesData = userQueryRepository.getUserArticlesDataBetween(january1st, december31st, userId);
        List<ActivityDto.Temporary> annualAnswersData = userQueryRepository.getUserAnswersDataBetween(january1st, december31st, userId);
        List<ActivityDto.Temporary> annualCommentsData = userQueryRepository.getUserCommentsDataBetween(january1st, december31st, userId);

        Map<LocalDate, Map<ActivityType, Long>> annualData = mapDataByCreatedDate(annualArticlesData, annualAnswersData, annualCommentsData);
        List<ActivityDto.Response> userActivitiesDataThisYear = combiningActivityData(annualData);
        List<ActivityDto.Response> sortedActivityData = userActivitiesDataThisYear.stream()
                .sorted(Comparator.comparing(ActivityDto.Response::getCreatedDate)).collect(Collectors.toList());

        List<ArticleDto.ResponseListTypeArticle> questionsDto = convertQuestionsToDto(recentQuestions);

        try {
            userDashboardBasicInfo.addRestInfo(usersTop3Tags, usersTop3Badges, questionsDto, sortedActivityData, receivedReviews);
            return userDashboardBasicInfo;
        } catch (NullPointerException e) {
            throw new BusinessLogicException(USER_NOT_FOUND);
        }
    }

    private List<ArticleDto.ResponseListTypeArticle> convertQuestionsToDto(List<Article> recentQuestions) {
        return recentQuestions.stream().map(e -> {
            List<TagDto.SimpleTag> tags = e.getArticleTags().stream().map(
                    t -> tagMapper.tagEntityToTagDto(t.getTag())
            ).collect(Collectors.toList());

            return articleMapper.articleToResponseSearchResultArticle(e, e.getComments().size(), e.getAnswers().size(), tags, e.getArticleLikes().size());
        }).collect(Collectors.toList());
    }

    private List<ActivityDto.Response> combiningActivityData(Map<LocalDate, Map<ActivityType, Long>> annualData) {
        return annualData.entrySet().stream().map(e -> {
                    Long articleCount = e.getValue().getOrDefault(ARTICLE, 0L);
                    Long answerCount = e.getValue().getOrDefault(ANSWER, 0L);
                    Long commentCount = e.getValue().getOrDefault(COMMENT, 0L);
                    Long total = articleCount + answerCount + commentCount;

                    return ActivityDto.Response.builder()
                            .articleCount(articleCount)
                            .answerCount(answerCount)
                            .commentCount(commentCount)
                            .total(total)
                            .createdDate(e.getKey())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    private Map<LocalDate, Map<ActivityType, Long>> mapDataByCreatedDate(List<ActivityDto.Temporary> annualArticlesData, List<ActivityDto.Temporary> annualAnswersData, List<ActivityDto.Temporary> annualCommentsData) {
        Map<LocalDate, Map<ActivityType, Long>> annualData = new HashMap<>();

        putAnnualData(annualArticlesData, annualData, ARTICLE);
        putAnnualData(annualAnswersData, annualData, ANSWER);
        putAnnualData(annualCommentsData, annualData, COMMENT);

        return annualData;
    }

    private void putAnnualData(List<ActivityDto.Temporary> temporaryList, Map<LocalDate, Map<ActivityType, Long>> annualData, ActivityType activityType) {
        temporaryList.forEach(
                e ->  {
                    if (!annualData.containsKey(e.getCreatedDate())) {
                        Map<ActivityType, Long> map = new HashMap<>();
                        map.put(activityType, e.getCount());
                        annualData.put(e.getCreatedDate(), map);
                    } else {
                        annualData.get(e.getCreatedDate()).put(activityType, e.getCount());
                    }
                }
        );
    }
}
