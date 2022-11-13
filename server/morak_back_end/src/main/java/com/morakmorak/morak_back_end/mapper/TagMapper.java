package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    default List<TagDto.RequestTagWithIdAndName> requestTagWithIdAndNameToTagDto(ArticleDto.RequestUploadArticle requestTagWithIdAndNames) {
        List<TagDto.RequestTagWithIdAndName> tagWithIdAndName = requestTagWithIdAndNames.getTags().stream()
                .map(request -> TagDto.RequestTagWithIdAndName.builder()
                        .tagId(request.getTagId())
                        .tagName(request.getTagName())
                        .build()
        ).collect(Collectors.toList());

        return tagWithIdAndName;
    }
    default List<TagDto.RequestTagWithIdAndName> requestTagWithIdAndNameToTagDto(ArticleDto.RequestUpdateArticle requestTagWithIdAndNames) {
        List<TagDto.RequestTagWithIdAndName> tagWithIdAndName = requestTagWithIdAndNames.getTags().stream()
                .map(request -> TagDto.RequestTagWithIdAndName.builder()
                        .tagId(request.getTagId())
                        .tagName(request.getTagName())
                        .build()
                ).collect(Collectors.toList());

        return tagWithIdAndName;
    }


}
