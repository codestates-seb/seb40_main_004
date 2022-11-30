package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.dto.ActivityDto;
import com.morakmorak.morak_back_end.dto.AnswerDto;
import com.morakmorak.morak_back_end.dto.ResponseMultiplePaging;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.auth_user_service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    private final Integer MAX_SIZE = 50;

    @GetMapping("/{user-id}/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.ResponseDashBoard getDashboard(@PathVariable(name = "user-id") Long id) {
        return userService.findUserDashboard(id);
    }

    @PatchMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.SimpleEditProfile patchProfile(@Valid @RequestBody UserDto.SimpleEditProfile request, @RequestUser UserDto.UserInfo userInfo) {
        return userService.editUserProfile(userMapper.EditProfileToEntity(request), userInfo.getId());
    }

    @GetMapping("/ranks")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<UserDto.ResponseRanking> getUserRanking(@Param("q") String q,
                                                                          @Param("page") Integer page,
                                                                          @Param("size") Integer size) {
        PageRequest pageRequest = getPageRequest(q, page, size);
        return userService.getUserRankList(pageRequest);
    }

    @GetMapping("/{user-id}/dashboard/activities/{date}")
    @ResponseStatus(HttpStatus.OK)
    public ActivityDto.Detail getUserActivityDetail(@PathVariable(name = "user-id") Long userId,
                                                    @PathVariable(name = "date") String date) {
        try {
            return userService.findActivityHistoryOn(LocalDate.parse(date), userId);
        } catch (DateTimeParseException e) {
            throw new BusinessLogicException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    private PageRequest getPageRequest(String sort, Integer page, Integer size) {
        Sort s;
        s = (!StringUtils.hasText(sort)) ? Sort.by("point") : Sort.by(sort);
        page = Math.max(page - 1, 0);
        size = Math.min(size, MAX_SIZE);

        return PageRequest.of(page, size, s);
    }


    @GetMapping("/{user-id}/answers")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMultiplePaging<AnswerDto.ResponseUserAnswerList> getUserAnswerList(@PathVariable ("user-id") Long userId,
                                                                                      @Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                      @Positive @RequestParam(value = "size", defaultValue = "50" ) int size
                                                                                      ) {
        return userService.getUserAnswerList(userId, page - 1, size);
    }

}
