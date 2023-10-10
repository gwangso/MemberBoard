package com.icia.memberboard.service;

import com.icia.memberboard.dto.MemberDTO;
import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.entity.MemberFileEntity;
import com.icia.memberboard.repository.MemberFileRepository;
import com.icia.memberboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberFileRepository memberFileRepository;

    public void save(MemberDTO memberDTO) throws IOException {
        if(memberDTO.getMemberProfile() == null){
            MemberEntity memberEntity = MemberEntity.toSaveEntity(memberDTO);
            memberRepository.save(memberEntity);
        }else {
            MemberEntity memberEntity = MemberEntity.toSaveEntityWithFile(memberDTO);
            MemberEntity savedEntity = memberRepository.save(memberEntity);
            MultipartFile memberProfile = memberDTO.getMemberProfile();
            String originalFilename = memberProfile.getOriginalFilename();
            String storedFilename = System.currentTimeMillis() + "_" + originalFilename;
            String savePath = "D:\\memberBoard_img\\" + storedFilename;
            memberProfile.transferTo(new File(savePath));
            MemberFileEntity memberFileEntity = MemberFileEntity.toMemberFileEntity(originalFilename, storedFilename, savedEntity);
            memberFileRepository.save(memberFileEntity);
        }
    }

    public boolean duplicate(String memberEmail) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);
        if (memberEntity==null){
            return true;
        }else{
            return false;
        }
    }
}
