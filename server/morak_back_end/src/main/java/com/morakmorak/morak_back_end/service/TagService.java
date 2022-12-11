package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.entity.Tag;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import com.morakmorak.morak_back_end.exception.ErrorCode;
import com.morakmorak.morak_back_end.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findVerifiedTagByTagName(TagName tagName) {
        return tagRepository.findTagByName(tagName)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.TAG_NOT_FOUND));
    }
}
