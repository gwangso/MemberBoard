package com.icia.memberboard.service;

import com.icia.memberboard.dto.MemberDTO;
import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.entity.MemberFileEntity;
import com.icia.memberboard.repository.MemberFileRepository;
import com.icia.memberboard.repository.MemberRepository;
import com.icia.memberboard.util.UtilClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

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
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail).orElseThrow(() -> new NoSuchElementException());
        if (memberEntity==null){
            return true;
        }else{
            return false;
        }
    }

    public Page<MemberDTO> findAll(int page) {
        page = page-1;
        int pageLimit = 10;
        Page<MemberEntity> memberEntities = memberRepository.findAll(PageRequest.of(page,pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        Page<MemberDTO> memberList = memberEntities.map(memberEntity ->
            MemberDTO.builder()
                    .id(memberEntity.getId())
                    .memberEmail(memberEntity.getMemberEmail())
                    .memberName(memberEntity.getMemberName())
                    .createdAt(UtilClass.dateTimeFormat(memberEntity.getCreatedAt()))
                    .build());
        return memberList;
    }

    @Transactional
    public MemberDTO login(MemberDTO memberDTO) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberDTO.getMemberEmail()).orElseThrow(() -> new NoSuchElementException());
        MemberDTO result = MemberDTO.toMemberDTO(memberEntity);
        if(result.getMemberPassword().equals(memberDTO.getMemberPassword())){
            return result;
        }else {
            return null;
        }
    }

    @Transactional
    public MemberDTO findByEmail(String memberEmail) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail).orElseThrow(() -> new NoSuchElementException());
        return MemberDTO.toMemberDTO(memberEntity);
    }


    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    public MemberDTO findById(Long id) {
        MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        return MemberDTO.toMemberDTO(memberEntity);
    }
}
