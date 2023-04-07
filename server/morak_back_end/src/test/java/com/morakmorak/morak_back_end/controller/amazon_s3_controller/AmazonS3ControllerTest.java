package com.morakmorak.morak_back_end.controller.amazon_s3_controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.morakmorak.morak_back_end.config.SecurityTestConfig;
import com.morakmorak.morak_back_end.controller.AmazonS3Controller;
import com.morakmorak.morak_back_end.controller.ExceptionController;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.webHook.ErrorNotificationGenerator;
import com.morakmorak.morak_back_end.security.resolver.JwtArgumentResolver;
import com.morakmorak.morak_back_end.service.AmazonS3StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.morakmorak.morak_back_end.config.ApiDocumentUtils.*;
import static com.morakmorak.morak_back_end.exception.ErrorCode.*;
import static com.morakmorak.morak_back_end.util.SecurityTestConstants.*;
import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AmazonS3Controller.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class AmazonS3ControllerTest {
    @MockBean
    AmazonS3StorageService amazonS3StorageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtArgumentResolver jwtArgumentResolver;

    @MockBean
    ErrorNotificationGenerator errorNotificationGenerator;


    @Test
    @DisplayName("정상 요청 시 200OK와 preSignedURL을 반환한다")
    void getS3AvatarUrl() throws Exception {
        //given
        String preSignedUrl = "https://aws.filename.png/secret=aD@!DSSFZcs";
        AvatarDto.ResponseS3Url response = AvatarDto.ResponseS3Url.builder()
                .avatarId(ID1)
                .preSignedUrl(preSignedUrl)
                .build();

        given(amazonS3StorageService.saveAvatar(any())).willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/users/profiles/avatars")
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform.andExpect(status().isOk())
                .andDo(document("avatar_업로드url_요청성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("avatarId").description("등록된 avatarId"),
                                fieldWithPath("preSignedUrl").description("해당 경로로 파일을 업로드해야 합니다.")
                        ))
                );
    }

    @Test
    @DisplayName("요청 받은 토큰의 id로 유저를 찾을 수 없을 경우 404 반환")
    void getS3AvatarUrl_failed() throws Exception {
        //given
        given(amazonS3StorageService.saveAvatar(any())).willThrow(new BusinessLogicException(USER_NOT_FOUND));

        //when
        ResultActions perform = mockMvc.perform(get("/users/profiles/avatars")
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(document("avatar_업로드url_요청실패_404",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("업로드 중 s3에서 예외가 발생할 경우409 반환")
    void getS3AvatarUrl_failed2() throws Exception {
        //given
        given(amazonS3StorageService.saveAvatar(any())).willThrow(new BusinessLogicException(CAN_NOT_ACCESS_S3));

        //when
        ResultActions perform = mockMvc.perform(get("/users/profiles/avatars")
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document("avatar_업로드url_요청실패_409",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("정상 요청 시 200OK와 preSignedURL을 반환한다")
    void getS3FileUrl() throws Exception {
        //given
        String preSignedUrl = "https://aws.filename.png/secret=aD@!DSSFZcs";
        FileDto.ResponseFileDto response = FileDto.ResponseFileDto.builder()
                .fileId(ID1)
                .preSignedUrl(preSignedUrl)
                .build();

        given(amazonS3StorageService.saveFile()).willReturn(response);

        //when
        ResultActions perform = mockMvc.perform(get("/files")
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform.andExpect(status().isOk())
                .andDo(document("file_업로드url_요청성공_200",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("fileId").description("등록된 file Id"),
                                fieldWithPath("preSignedUrl").description("해당 경로로 파일을 업로드해야 합니다.")
                        ))
                );
    }

    @Test
    @DisplayName("업로드 중 s3에서 예외가 발생할 경우 409 반환")
    void getS3FileUrl_failed() throws Exception {
        //given
        given(amazonS3StorageService.saveFile()).willThrow(new BusinessLogicException(CAN_NOT_ACCESS_S3));

        //when
        ResultActions perform = mockMvc.perform(get("/files")
                .header(JWT_HEADER, ACCESS_TOKEN));

        //then
        perform.andExpect(status().isConflict())
                .andDo(document("file_업로드url_요청실패_409",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @Test
    @DisplayName("삭제 요청 시 204 반환")
    void deleteAvatar_success() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(delete("/users/profiles/avatars")
                .header(JWT_HEADER, ACCESS_TOKEN));
        //then
        perform.andExpect(status().isNoContent())
                .andDo(document(
                        "avatar_삭제_요청성공_204",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));

        verify(amazonS3StorageService, times(1)).deleteAvatar(any());
    }

    @Test
    @DisplayName("요청 받은 id를 통해 아바타 객체를 찾지 못할 경우 404 반환")
    void deleteAvatar_failed1() throws Exception {
        //given
        willThrow(new BusinessLogicException(FILE_NOT_FOUND)).given(amazonS3StorageService).deleteAvatar(any());
        //when
        ResultActions perform = mockMvc.perform(delete("/users/profiles/avatars")
                .header(JWT_HEADER, ACCESS_TOKEN));
        //then
        perform.andExpect(status().isNotFound())
                .andDo(document(
                        "avatar_삭제_요청실패_404",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));

        verify(amazonS3StorageService, times(1)).deleteAvatar(any());
    }

    @Test
    @DisplayName("s3 예외로 인해 수행할 수 없을 경우 409 반환")
    void deleteAvatar_failed2() throws Exception {
        //given
        willThrow(new BusinessLogicException(CAN_NOT_ACCESS_S3)).given(amazonS3StorageService).deleteAvatar(any());
        //when
        ResultActions perform = mockMvc.perform(delete("/users/profiles/avatars")
                .header(JWT_HEADER, ACCESS_TOKEN));
        //then
        perform.andExpect(status().isConflict())
                .andDo(document(
                        "avatar_삭제_요청실패_409",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));

        verify(amazonS3StorageService, times(1)).deleteAvatar(any());
    }
}
