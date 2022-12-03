package com.morakmorak.morak_back_end.repository;


import com.morakmorak.morak_back_end.entity.Tag;
import com.morakmorak.morak_back_end.entity.enums.TagName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {

    Optional<Tag> findTagByName(TagName name);
}
