package com.morakmorak.morak_back_end.service.amazon_s3_service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.morakmorak.morak_back_end.entity.Answer;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.service.AmazonS3StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.morakmorak.morak_back_end.util.TestConstants.ID1;
import static com.morakmorak.morak_back_end.util.TestConstants.ID2;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class S3_Delete_AnswerFile {
    @Mock
    AmazonS3 amazonS3;

    @Mock
    FileRepository fileRepository;

    @Mock
    File file;

    @InjectMocks
    AmazonS3StorageService amazonS3StorageService;


    @Test
    @DisplayName("파일 삭제 과정에서 S3 예외 발생 시 CAN_NOT_ACCESS_S3 발생")
    void deleteFiles_OnAnswer_failed1() throws Exception {
        //given
        Answer answer = Answer.builder().id(ID1).build();
        File file1 = File.builder().id(ID1).answer(answer).build();
        File file2 = File.builder().id(ID2).answer(answer).build();

        BDDMockito.given(fileRepository.findByAnswerId(any())).willReturn(List.of(file1,file2));
        BDDMockito.willThrow(AmazonServiceException.class).given(amazonS3).deleteObject(any(), any());
        //when //then
        assertThatThrownBy(() -> amazonS3StorageService.deleteFileOnAnswer(ID1)).isInstanceOf(BusinessLogicException.class);
    }
    @Test
    @DisplayName("첨부된 file이 없는 경우 fileRepository.deleteById 실행되지 않는다.")
    void deleteFiles_OnAnswer_success_1() throws Exception {
        //given
        Answer answer = Answer.builder().build();

        BDDMockito.given(fileRepository.findByAnswerId(any())).willReturn(Collections.emptyList());

        //when
        amazonS3StorageService.deleteFileOnAnswer(ID1);

        // then
        BDDMockito.verify(fileRepository, Mockito.times(0)).deleteById(ID1);
        BDDMockito.verify(file, Mockito.times(0)).detachFromAnswer();

    }

    @Test
    @DisplayName("s3에서 객체를 삭제하는 로직이 모두 정상적으로 수행될 경우 fileRepository.deleteByAnswerId 실행된다")
    void deleteFiles_OnAnswer_success_2() {
        //given
        Answer answer = Answer.builder().build();
        File file1 = File.builder().id(ID1).answer(answer).build();
        File file2 = File.builder().id(ID2).answer(answer).build();

        BDDMockito.given(fileRepository.findByAnswerId(any())).willReturn(List.of(file1,file2));

        //when
        amazonS3StorageService.deleteFileOnAnswer(ID1);

        // then
        BDDMockito.verify(fileRepository, Mockito.times(1)).deleteById(ID1);
        BDDMockito.verify(fileRepository, Mockito.times(1)).deleteById(ID2);
    }
}
