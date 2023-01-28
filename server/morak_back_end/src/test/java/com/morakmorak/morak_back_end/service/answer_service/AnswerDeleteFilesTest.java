package com.morakmorak.morak_back_end.service.answer_service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnswerDeleteFilesTest {
    /*
     *테스트 대상: S3를 실행시키지 않고 파일 삭제 여부만 테스트
     * 테스트 환경: deltetfile 시 실제로 파일이 지워지는가?
     * todo: S3 인터페이스 제작 후 인테퍼이스를 구현하는 서비스 제작,
     */
    @Mock
    AmazonS3 amazonS3;
    @InjectMocks
    AnswerService answerService;
    /*
    파일이 저장되려면 url과 기타등등이 생겼어야 하고
    이를 테스트하려면 삭제 시 url과 기타등등이 지워져 있어야 한다
    그리고 verify로 실행되었는지 확인만 하면 된다
    */
    @Test
    @DisplayName("답변글 삭제 시 파일이 지워진다")
    void deleteFilesOnAnswer_suc() {
        //답변글을 지웠을 때 요건을 전부 충족한다면
        //S3객체를 지우는 액션이 무조건 실행된다
        BDDMockito.verify(amazonS3.deleteObjects(request), Mockito.times(1));
    }
}
