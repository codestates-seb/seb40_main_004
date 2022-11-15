package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.FileDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface FileMapper {

    default List<FileDto.RequestFileWithId> RequestFileWithIdToFile(ArticleDto.RequestUploadArticle requestUploadArticle){
        List<FileDto.RequestFileWithId> filesWithOnlyId = requestUploadArticle.getFileId().stream()
                .map(file -> FileDto.RequestFileWithId.builder()
                        .fileId(file.getFileId())
                        .build())
                .collect(Collectors.toList());
        return filesWithOnlyId;
    }

    default List<FileDto.RequestFileWithId> RequestFileWithIdToFile(ArticleDto.RequestUpdateArticle requestUploadArticle) {
        List<FileDto.RequestFileWithId> filesWithOnlyId = requestUploadArticle.getFileId().stream()
                .map(file -> FileDto.RequestFileWithId.builder()
                        .fileId(file.getFileId())
                        .build())
                .collect(Collectors.toList());
        return filesWithOnlyId;
    }



}
