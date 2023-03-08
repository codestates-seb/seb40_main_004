package com.morakmorak.morak_back_end.service.file_service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.morakmorak.morak_back_end.exception.ErrorCode.FILE_NOT_FOUND;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DeleteFileService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final FileRepository fileRepository;

    private final AmazonS3 amazonS3;
    void deleteFile(Long fileId) {
        try{
            File file = fileRepository.findById(fileId).orElseThrow(() -> new BusinessLogicException(FILE_NOT_FOUND));
            amazonS3.deleteObject(bucketName, file.getOriginalFilename());
        } catch (NullPointerException e) {
            throw new BusinessLogicException(FILE_NOT_FOUND);
        } catch (AmazonServiceException e) {
            log.error("", e);
            fileRepository.deleteById(fileId);
            return;
        }
    }

}
