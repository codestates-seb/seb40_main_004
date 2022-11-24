package com.morakmorak.morak_back_end.service.amazon_s3_service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.user.AvatarRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import com.morakmorak.morak_back_end.service.AmazonS3StorageService;
import com.morakmorak.morak_back_end.util.TestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.Optional;

import static com.morakmorak.morak_back_end.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.given;

@ExtendWith(MockitoExtension.class)
public class AmazonS3StorageServiceTest {
    @Mock
    AmazonS3 amazonS3;

    @Mock
    AvatarRepository avatarRepository;

    @Mock
    FileRepository fileRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GeneratePresignedUrlRequest generatePresignedUrlRequest;

    @InjectMocks
    AmazonS3StorageService amazonS3StorageService;

    private final String PRESIGNED_URL = "https://amazon.dsssd.png/secret=ASDDDWW!@##";
    private final String BUCKET_NAME = "bucket";
    private final Long EXP_TIME = 1000 * 60 * 2L;

    @Test
    @DisplayName("SDKClient 접속 중 예외 발생 시 BusinessLogicException 발생")
    void saveAvatar_failed1() {
        //given
        BDDMockito.given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).willThrow(SdkClientException.class);

        //when SdkClientException
        assertThatThrownBy(() -> amazonS3StorageService.saveAvatar(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("해당 유저아이디로 유저 데이터를 찾지 못하면 businessLogicException 발생")
    void saveAvatar_failed2() throws Exception {
        //given
        BDDMockito.given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).willReturn(new URL(PRESIGNED_URL));
        BDDMockito.given(userRepository.findById(anyLong())).willThrow(BusinessLogicException.class);

        //when
        //then
        assertThatThrownBy(() -> amazonS3StorageService.saveAvatar(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("해당 유저아이디로 유저를 찾을 경우")
    void saveAvatar_success() throws Exception {
        //given
        User user = User.builder().build();
        BDDMockito.given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).willReturn(new URL(PRESIGNED_URL));
        BDDMockito.given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        //when
        AvatarDto.ResponseS3Url result = amazonS3StorageService.saveAvatar(ID1);
        //then
        assertThat(result.getPreSignedUrl()).isEqualTo(PRESIGNED_URL);
    }

    @Test
    @DisplayName("예외가 발생하지 않고 파일이 저장될 경우")
    void saveFile_success() throws Exception {
        //given
        File file = File.builder().id(ID1).build();
        BDDMockito.given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).willReturn(new URL(PRESIGNED_URL));
        BDDMockito.given(fileRepository.save(any())).willReturn(file);
        //when
        FileDto.ResponseFileDto responseFileDto = amazonS3StorageService.saveFile();
        //then
        assertThat(responseFileDto.getPreSignedUrl()).isEqualTo(PRESIGNED_URL);
    }

    @Test
    @DisplayName("해당 아이디로 avatar를 조회할 수 없을 경우 BusinessLogicException 발생")
    void deleteAvatar_failed1() throws Exception {
        //given
        BDDMockito.given(avatarRepository.findByUserId(any())).willReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> amazonS3StorageService.deleteAvatar(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("s3에서 객체를 삭제하는 과정에 예외가 발생하는 경우 BusinessLogicException을 던진다.")
    void deleteAvatar_failed2() throws Exception {
        //given
        Avatar avatar = Avatar.builder().build();
        BDDMockito.given(avatarRepository.findByUserId(any())).willReturn(Optional.of(avatar));
        BDDMockito.willThrow(AmazonServiceException.class).given(amazonS3).deleteObject(any(), any());

        //when //then
        assertThatThrownBy(() -> amazonS3StorageService.deleteAvatar(ID1)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("s3에서 객체를 삭제하는 로직이 모두 정상적으로 수행될 경우 avatarRepository.delete 실행")
    void deleteAvatar_success() throws Exception {
        //given
        User user = User.builder().build();
        Avatar avatar = Avatar.builder().id(ID1).user(user).build();
        BDDMockito.given(avatarRepository.findByUserId(any())).willReturn(Optional.of(avatar));

        //when
        amazonS3StorageService.deleteAvatar(ID1);

        // then
        BDDMockito.verify(avatarRepository, Mockito.times(1)).deleteById(ID1);
    }
}
