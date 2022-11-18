package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.CategoryName;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private CategoryName name;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Article> articleList = new ArrayList<>();

    public Category(CategoryName name) {
        this.name = name;
    }
}
