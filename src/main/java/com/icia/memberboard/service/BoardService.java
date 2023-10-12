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
                File savedFile = new File("D:\\memberBoard_img//"+storedFilename);
                savedFile.delete();
            }
        }
        boardRepository.delete(boardEntity);
    }
}