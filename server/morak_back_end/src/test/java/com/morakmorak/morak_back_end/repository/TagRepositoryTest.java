package com.morakmorak.morak_back_end.repository;

import com.morakmorak.morak_back_end.entity.Tag;
import com.morakmorak.morak_back_end.entity.enums.TagName;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;


    @Test
    @DisplayName("태그아이디 검색시 정확하게 값을 가져올때")
    public void Tag_suc1() throws Exception{
        //given
        Tag build = Tag.builder().tagName(TagName.JAVA).build();
        tagRepository.save(build);
        //when
        Tag tag = tagRepository.findById(1L).orElseThrow(() -> new RuntimeException("dsf"));
        //then
        Assertions.assertThat(tag.getTagName()).isEqualTo(build.getTagName());
     }
}