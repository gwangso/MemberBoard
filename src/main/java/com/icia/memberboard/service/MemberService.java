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
import java.util.List;
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
            MemberFileEntity memberFileEntity = MemberFileEntity.toSaveEntity(originalFilename, storedFilename, savedEntity);
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

    @Transactional
    public void delete(Long id) throws IOException{
        MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
        if(memberDTO.getFileAttached() == 1){
            for(String storedFilename : memberDTO.getStoredFilename()){
                File savedFile = new File("D:\\memberBoard_img\\"+storedFilename);
                savedFile.delete();
            }
        }
        memberRepository.delete(memberEntity);
    }

    @Transactional
    public MemberDTO findById(Long id) {
        MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        return MemberDTO.toMemberDTO(memberEntity);
    }

    @Transactional
    public void update(MemberDTO memberDTO) throws IOException {
        // 기존 MemberEntity : memberEntity
        // 기존 MemberFileEntity : memberFileEntity
        // 새로운 데이터 : memberDTO
        // 새로운 데이터의 파일 : memberProfile

        // 1. 새로운 Entity에 파일이 있는지 확인
        MultipartFile memberProfile = memberDTO.getMemberProfile();

        // 2. 새로운 Entity에 파일 이 없으면 기존파일 정보 덮어씌우기
        // 이 경우 MemberFileEntity는 변경 필요 없음
        MemberEntity memberEntity = memberRepository.findById(memberDTO.getId()).orElseThrow(() -> new NoSuchElementException());
        if(memberProfile == null){
            memberEntity = memberEntity.toUpdateEntity(memberDTO);
            memberRepository.save(memberEntity);
        }else{
            // 3. 새로운 파일 정의
            String originalFilename = memberProfile.getOriginalFilename();
            String storedFilename = System.currentTimeMillis() + "_" + originalFilename;

            // 4. 새로운 파일 저장
            String savePath = "D:\\memberBoard_img\\" + storedFilename;
            memberProfile.transferTo(new File(savePath));

            // 5. 기존에는 파일이 있는지 확인
            if (memberEntity.getMemberFileEntityList().get(0) != null){
                // 6. 파일이 있다면 기존 파일 삭제
                for(String savedFilename : MemberDTO.toMemberDTO(memberEntity).getStoredFilename()){
                    File savedFile = new File("D:\\memberBoard_img\\"+savedFilename);
                    savedFile.delete();
                }
                // 7. 멤버파일엔티티 가져오기
                List<MemberFileEntity> memberFileEntityList = memberEntity.getMemberFileEntityList();
                for(MemberFileEntity memberFileEntity : memberFileEntityList){
                    // 8. 멤버파일엔티티 고치고 업데이트하기
                    memberFileEntity = memberFileEntity.toUpdateEntity(originalFilename,storedFilename);
                    memberFileRepository.save(memberFileEntity);
                }
            }else{
                // 8. 파일이 없다면 새롭게 저장하기
                MemberFileEntity memberFileEntity = MemberFileEntity.toSaveEntity(originalFilename, storedFilename, memberEntity);
                memberFileRepository.save(memberFileEntity);
            }
            // 9. 멤버엔티티 수정
            memberEntity = memberEntity.toUpdateEntityWithFile(memberDTO);
            memberRepository.save(memberEntity);
        }

    }
}
