package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.ArticleDto;
import com.morakmorak.morak_back_end.dto.TagDto;
import com.morakmorak.morak_back_end.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    default List<TagDto.SimpleTag> requestTagWithIdAndNameToTagDto(ArticleDto.RequestUploadArticle requestTagWithIdAndNames) {
        List<TagDto.SimpleTag> tagWithIdAndName = requestTagWithIdAndNames.getTags().stream()
                .map(request -> TagDto.SimpleTag.builder()
                        .tagId(request.getTagId())
                        .name(request.getName())
                        .build()
        ).collect(Collectors.toList());

        return tagWithIdAndName;
    }
    default List<TagDto.SimpleTag> requestTagWithIdAndNameToTagDto(ArticleDto.RequestUpdateArticle requestTagWithIdAndNames) {
        List<TagDto.SimpleTag> tagWithIdAndName = requestTagWithIdAndNames.getTags().stream()
                .map(request -> TagDto.SimpleTag.builder()
                        .tagId(request.getTagId())
                        .name(request.getName())
                        .build()
                ).collect(Collectors.toList());

        return tagWithIdAndName;
    }

    @Mapping(source = "tag.id", target = "tagId")
    TagDto.SimpleTag tagEntityToTagDto(Tag tag);


}
