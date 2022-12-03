package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.RoleName;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.*;

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

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.PERSIST)
    private List<UserRole> userRoles = new ArrayList<>();

    public Role(RoleName rolename) {
        this.roleName = rolename;
    }
}
