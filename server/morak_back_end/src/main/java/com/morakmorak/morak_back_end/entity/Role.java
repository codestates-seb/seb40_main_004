package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.RoleName;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    private RoleName roleName;

    @Builder.Default
    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles = new ArrayList<>();
}
