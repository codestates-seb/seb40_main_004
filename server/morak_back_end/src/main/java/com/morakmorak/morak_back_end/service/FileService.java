package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.FileDto;
import com.morakmorak.morak_back_end.entity.File;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    public List<File> createFileListFrom(List<FileDto.RequestFileWithId> fileIdList) {
        if (ObjectUtils.isEmpty(fileIdList)) {return Collections.emptyList();}
        return fileIdList.stream().map(request ->
                        findVerifiedFileById(request.getFileId()))
                .collect(Collectors.toList());
    }

    public File findVerifiedFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.FILE_NOT_FOUND));
    }
}
