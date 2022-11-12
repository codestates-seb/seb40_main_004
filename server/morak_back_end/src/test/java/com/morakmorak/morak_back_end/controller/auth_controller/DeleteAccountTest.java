package com.morakmorak.morak_back_end.controller.auth_controller;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.exception.ErrorCode.ONLY_TEST_CODE;
import static com.morakmorak.morak_back_end.util.ApiDocumentUtils.getDocumentRequest;
import static com.morakmorak.morak_back_end.util.ApiDocumentUtils.getDocumentResponse;
import static com.morakmorak.morak_back_end.util.TestConstants.PASSWORD1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteAccountTest extends AuthControllerTest {
    @Test
    @DisplayName("회원 탈퇴 요청 시 요청값이 유효성 검사에 실패하면 400 Bad Request를 반환한다.")
    void requestWithdrawal_failed1() throws Exception {
        //given
        AuthDto.RequestWithdrawal request = AuthDto.RequestWithdrawal
                .builder()
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.deleteAccount(anyString(), any())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc.perform(delete("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                        "회원_탈퇴_씰패_400",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("password").description("패스워드 유효성 검사 실패 (null이거나 공백, 혹은 알 수 없는 문자열 오류)")
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 요청 시 확인 비밀번호가 데이터베이스 비밀번화와 다른 경우 409 Conflict를 반환한다.")
    void requestWithdrawal_failed2() throws Exception {
        //given
        AuthDto.RequestWithdrawal request = AuthDto.RequestWithdrawal
                .builder()
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.deleteAccount(anyString(), any())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc.perform(delete("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                        "회원_탈퇴_실패_409",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("password").description("올바르지 않은 (database 등록값과 다른) 패스워드")
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 요청 시 확인 비밀번호가 데이터베이스 비밀번호와 같고 유효성 검사에 통과한 경우 회원 정보를 삭제하고 204 NoContent 반환")
    void requestWithdrawal_success() throws Exception {
        //given
        AuthDto.RequestWithdrawal request = AuthDto.RequestWithdrawal
                .builder()
                .password(PASSWORD1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        given(authService.deleteAccount(anyString(), any())).willThrow(new BusinessLogicException(ONLY_TEST_CODE));

        //when
        ResultActions perform = mockMvc.perform(delete("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(document(
                        "회원_탈퇴_성공_204",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("password").description("올바르지 않은 (database 등록값과 다른) 패스워드")
                        )
                ));
    }
}
