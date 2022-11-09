package com.morakmorak.morak_back_end.controller;

import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.util.SecurityTestConfig;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
@Import(SecurityTestConfig.class)
@WebMvcTest({AuthController.class, UserMapper.class, ExceptionController.class})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class AuthController_Mail_Test {
}
