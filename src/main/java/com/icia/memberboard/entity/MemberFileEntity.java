package com.icia.memberboard.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@Table(name = "member_file_table")
@Entity
public class MemberFileEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String originalFilename;

    @Column(length = 500)
    private String storedFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;


    public static MemberFileEntity toSaveEntity(String originalFilename, String storedFilename, MemberEntity savedEntity) {
        MemberFileEntity memberFileEntity = new MemberFileEntity();
        memberFileEntity.setOriginalFilename(originalFilename);
        memberFileEntity.setStoredFilename(storedFilename);
        memberFileEntity.setMemberEntity(savedEntity);
        return memberFileEntity;
    }

    public static MemberFileEntity toUpdateEntity(String originalFilename, String storedFilename, MemberFileEntity memberFileEntity) {
        memberFileEntity.setOriginalFilename(originalFilename);
        memberFileEntity.setStoredFilename(storedFilename);
        return memberFileEntity;
    }
}
