package com.icia.memberboard.service;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.BoardFileEntity;
import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.repository.BoardFileRepository;
import com.icia.memberboard.repository.BoardRepository;
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
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Page<BoardDTO> findAll(int page, String query, int type) {
        page = page - 1;
        int pageLimit = 5;

        Page<BoardEntity> boardEntities = null;

        if(query.equals("")){
            boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }else{
            if(type==1){
                boardEntities = boardRepository.findByBoardTitleContaining(query, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
            }else if (type==2){
                boardEntities = boardRepository.findByBoardWriterContaining(query, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
            }
        }
        Page<BoardDTO> boardDTOPage =
                boardEntities.map(boardEntity ->
                        BoardDTO.builder()
                                .id(boardEntity.getId())
                                .boardTitle(boardEntity.getBoardTitle())
                                .boardWriter(boardEntity.getBoardWriter())
                                .boardHits(boardEntity.getBoardHits())
                                .createdAt(UtilClass.dateTimeFormat(boardEntity.getCreatedAt()))
                                .writerEmail(boardEntity.getMemberEntity().getMemberEmail())
                                .build()
                );
        return boardDTOPage;
    }

    public void save(BoardDTO boardDTO, String memberEmail) throws IOException {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail).orElseThrow(() -> new NoSuchElementException());
        if(boardDTO.getBoardFile().get(0).isEmpty()){
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO, memberEntity);
            boardRepository.save(boardEntity);
        }else{
            List<MultipartFile> boardFileList = boardDTO.getBoardFile();
            BoardEntity boardEntity = BoardEntity.toSaveEntityWithFile(boardDTO, memberEntity);
            boardRepository.save(boardEntity);
            for(MultipartFile boardFile : boardFileList){
                String originalFilename = boardFile.getOriginalFilename();
                String storedFilename = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "D:\\memberBoard_img\\"+storedFilename;
                boardFile.transferTo(new File(savePath));
                BoardFileEntity boardFileENtity = BoardFileEntity.toSaveFile(originalFilename, storedFilename, boardEntity);
                boardFileRepository.save(boardFileENtity);
            }
        }
    }

    @Transactional
    public BoardDTO findById(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        BoardDTO boardDTO = BoardDTO.toDTO(boardEntity);
        return boardDTO;
    }

    @Transactional // jpql로 작성한 메서드 호출할 때
    public void increaseHits(Long id) {
        boardRepository.increaseHits(id);
    }

    @Transactional
    public void delete(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(()-> new NoSuchElementException());
        if(boardEntity.getFileAttached() == 1){
            for(BoardFileEntity boardFileEntity: boardEntity.getBoardFileEntityList()){
                String storedFilename = boardFileEntity.getStoredFilename();
                File savedFile = new File("D:\\memberBoard_img\\"+storedFilename);
                savedFile.delete();
            }
        }
        boardRepository.delete(boardEntity);
    }

    @Transactional
    public void update(BoardDTO boardDTO, List<String> deleteFileList) throws IOException {
        // 1. boardEntity 가져오기
        BoardEntity boardEntity = boardRepository.findById(boardDTO.getId()).orElseThrow(() -> new NoSuchElementException());
        // 2. 삭제할 파일이 있으면, 없으면 그냥 넘어감
        if (deleteFileList.size()!=0){
            // 2-1. 기존 boardFileEntityList 가져오기
            // 2-2. 저장된 파일 삭제하기
            for(String savedFilename : deleteFileList){
                File savedFile = new File("D:\\memberBoard_img\\"+savedFilename);
                savedFile.delete();
                System.out.println("삭제완료");
                // 2-3. 삭제할 boardFileEntity 찾기
                for(BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()){
                    // 2-4. boardFileEntity의 storedFilename과 savedFilename이 같을 경우 Entity삭제
                    if(boardFileEntity.getStoredFilename().equals(savedFilename)){
                        boardFileRepository.delete(boardFileEntity);
                    }
                }
            }
            System.out.println("파일삭제 되나?");
        }
        // 3. boardEntity 업데이트
        // 4. 파일이 없다면
        if(boardDTO.getBoardFile().get(0).isEmpty()){
            System.out.println("추가 파일이 없음");

            // 4-1. boardEntity를 업데이트
            boardEntity = BoardEntity.toUpdateEntity(boardDTO, boardEntity);
            // 4-2. boareRepository를 통해 table update
            boardRepository.save(boardEntity);

            System.out.println("파일 없을 때 엔티티 수정되나?");
        // 5. 파일이 있다면
        }else {
            System.out.println("추가 파일 있음");

            // 5-1. 파일이 있는 메서드로 boardEntity
            boardEntity = BoardEntity.toUpdateEntityWithFile(boardDTO, boardEntity);
            // 5-2. boareRepository를 통해 table update
            boardRepository.save(boardEntity);

            System.out.println("파일 있을때 엔티티 수정되나");

            // 5-3. 새로 저장할 boardFile 지정
            for(MultipartFile boardFile : boardDTO.getBoardFile()){
                // 5-4. originalFilename, storedFilename 설정
                String originalFilename = boardFile.getOriginalFilename();
                String storedFilename = System.currentTimeMillis() + "_" + originalFilename;
                // 5-5. 파일저장
                String savePath = "D:\\memberBoard_img\\"+storedFilename;
                boardFile.transferTo(new File(savePath));
                // 5-6. boardFileEntity 생성
                BoardFileEntity boardFileENtity = BoardFileEntity.toSaveFile(originalFilename, storedFilename, boardEntity);
                // 5-7 boardFile Entity 저장
                boardFileRepository.save(boardFileENtity);

                System.out.println("파일저장되나?");
            }

        }
    }
}