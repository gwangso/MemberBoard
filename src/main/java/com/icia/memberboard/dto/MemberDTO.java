package com.icia.memberboard.dto;

import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.entity.MemberFileEntity;
import com.icia.memberboard.util.UtilClass;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String memberMobile;
    private String memberBirth;
    private String createdAt;
    private int fileAttached;
    private MultipartFile memberProfile;
    private List<String> originalFilename = new ArrayList<>();
    private List<String> storedFilename = new ArrayList<>();

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberBirth(memberEntity.getMemberBirth());
        memberDTO.setMemberMobile(memberEntity.getMemberMobile());
        memberDTO.setCreatedAt(UtilClass.dateTimeFormat(memberEntity.getCreatedAt()));
        if (memberEntity.getFileAttached() == 0){
            memberDTO.setFileAttached(0);
        }else{
            memberDTO.setFileAttached(1);
            for (MemberFileEntity memberFileEntity : memberEntity.getMemberFileEntityList()){
                memberDTO.getOriginalFilename().add(memberFileEntity.getOriginalFilename());
                memberDTO.getStoredFilename().add(memberFileEntity.getStoredFilename());
            }
        }
        return memberDTO;
    }
}
