package com.morakmorak.morak_back_end.controller;


import com.morakmorak.morak_back_end.dto.AvatarDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.dto.UserDto;
import com.morakmorak.morak_back_end.security.resolver.RequestUser;
import com.morakmorak.morak_back_end.service.AmazonS3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AmazonS3Controller {
    private final AmazonS3StorageService s3StorageService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/profiles/avatars")
    public AvatarDto.ResponseS3Url getS3AvatarUrl(@RequestUser UserDto.UserInfo token) {
        return s3StorageService.saveAvatar(token.getId());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/files")
    public FileDto.ResponseFileDto getS3FileUrl() {
        return s3StorageService.saveFile();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/profiles/avatars")
    public void deleteAvatar(@RequestUser UserDto.UserInfo token) {
        s3StorageService.deleteAvatar(token.getId());
    }
}
