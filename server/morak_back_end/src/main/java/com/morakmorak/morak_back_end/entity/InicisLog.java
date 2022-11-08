package com.morakmorak.morak_back_end.entity;

import com.morakmorak.morak_back_end.entity.enums.PilType;
import com.morakmorak.morak_back_end.entity.enums.PStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InicisLog extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inicis_log_id")
    private Long id;

    private PilType pilType;

    private String PTid;

    private String PMid;

    private String pAuthDt;

    private PStatus pStatus;

    private String pOid;

    private String pFnNm;

    private String pAmt;

    private String pAuthNo;

    private String pReMsg1;
}
