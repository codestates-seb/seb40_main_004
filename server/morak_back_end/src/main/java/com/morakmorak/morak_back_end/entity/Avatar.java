package com.morakmorak.morak_back_end.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Avatar extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id")
    private Long id;

    private String originalFilename;

    private Integer fileSize;

    private String localPath;

    private String remotePath;

    @OneToOne(mappedBy = "avatar", cascade = CascadeType.PERSIST)
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
}
