package com.morakmorak.morak_back_end.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.Avatar;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.repository.FileRepository;
import com.morakmorak.morak_back_end.repository.user.AvatarRepository;
import com.morakmorak.morak_back_end.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.morakmorak.morak_back_end.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AmazonS3StorageService {
    private final AmazonS3 amazonS3;
    private final AvatarRepository avatarRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final Long EXP_TIME = 1000 * 60 * 2L;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public AvatarDto.ResponseS3Url saveAvatar(Long userId) {
        String filename = UUID.randomUUID() + ".png";
        String preSignedUrl = getPreSignedUrl(filename);
        String remotePath = preSignedUrl.split("\\?")[0];

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessLogicException(USER_NOT_FOUND));
        Avatar avatar = Avatar.builder()
                .remotePath(remotePath)
                .originalFilename(filename)
                .user(user)
                .build();

        if (user.getAvatar() != null) {
            deleteAvatar(userId);
        }

        user.setAvatar(avatar);
        avatarRepository.save(avatar);

        return AvatarDto.ResponseS3Url.builder()
                .avatarId(avatar.getId())
                .preSignedUrl(preSignedUrl)
                .build();
    }

    public FileDto.ResponseFileDto saveFile() {
        String filename = UUID.randomUUID() + ".png";
        String preSignedUrl = getPreSignedUrl(filename);
        String remotePath = preSignedUrl.split("\\?")[0];


        File file = fileRepository.save(new File(remotePath));

        return FileDto.ResponseFileDto.builder()
                .fileId(file.getId())
                .preSignedUrl(preSignedUrl)
                .build();
    }

    public void deleteAvatar(Long userId) {
        Avatar avatar = avatarRepository.findByUserId(userId).orElseThrow(() -> new BusinessLogicException(FILE_NOT_FOUND));
        try {
            amazonS3.deleteObject(bucketName, avatar.getOriginalFilename());
        } catch (AmazonServiceException e) {
            log.error("",e);
            throw new BusinessLogicException(CAN_NOT_ACCESS_S3);
        }
        avatar.getUser().deleteAvatar();
        avatarRepository.deleteById(avatar.getId());
    }
    private String getPreSignedUrl(String filename) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + EXP_TIME;
        expiration.setTime(expTimeMillis);

        return generatePreSignedUrlRequest(bucketName, expiration, filename);
    }

    private String generatePreSignedUrlRequest(String bucketName, Date expiration, String filename) {
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filename)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (SdkClientException e) {
            log.error("",e);
            throw new BusinessLogicException(CAN_NOT_ACCESS_S3);
        }
    }
    public void deleteFileOnAnswer(Long answerId) {
        List<File> fileList = fileRepository.findByAnswerId(answerId);
        if (ObjectUtils.isEmpty(fileList)) {
            return;
        }
        fileList.stream().forEach(file -> {
            try {
                amazonS3.deleteObject(bucketName, file.getOriginalFilename());
            } catch (AmazonServiceException e) {
                log.error("", e);
                throw new BusinessLogicException(CAN_NOT_ACCESS_S3);
            }
            file.detachFromAnswer();
            fileRepository.deleteById(file.getId());
        });
    }

    public void deleteFileOnArticle(Long articleId) {
        List<File> fileList = fileRepository.findByArticleId(articleId);
        if (ObjectUtils.isEmpty(fileList)) {
            return;
        }
        fileList.stream().forEach(file -> {
            try {
                amazonS3.deleteObject(bucketName, file.getOriginalFilename());
            } catch (AmazonServiceException e) {
                log.error("", e);
                throw new BusinessLogicException(CAN_NOT_ACCESS_S3);
            }
            file.detachFromArticle();
            fileRepository.deleteById(file.getId());
        });
    }
}
