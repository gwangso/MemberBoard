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
                    .boardTitle(boardEntity.getBoardTitle())
                    .boardWriter(boardEntity.getBoardWriter())
                    .boardHits(boardEntity.getBoardHits())
                    .createdAt(UtilClass.dateTimeFormat(boardEntity.getCreatedAt()))
                    .build()
            );
        return boardDTOPage;
    }

    public void save(BoardDTO boardDTO, String memberEmail) throws IOException {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail).orElseThrow(() -> new NoSuchElementException());
        if(boardDTO.getBoardFile().isEmpty()){
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
}
