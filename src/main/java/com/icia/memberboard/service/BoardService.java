package com.icia.memberboard.service;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.repository.BoardFileRepository;
import com.icia.memberboard.repository.BoardRepository;
import com.icia.memberboard.util.UtilClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public Page<BoardDTO> findAll(int page, String query, String type) {
        page = page - 1;
        int pageLimit = 5;

        Page<BoardEntity> boardEntities = null;

        if(query.equals("")){
            boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }else{
            if(type.equals("boardTitle")){
                boardEntities = boardRepository.findByBoardTitleContaining(query, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
            }else if(type.equals("boarWriter")){
                boardEntities = boardRepository.findByBoardWriterContaining(query, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
            }
        }

        Page<BoardDTO> boardList = boardEntities.map(boardEntity ->
                BoardDTO.builder()
                        .id(boardEntity.getId())
                        .boardTitle(boardEntity.getBoardTitle())
                        .boardWriter(boardEntity.getBoardWriter())
                        .boardHits(boardEntity.getBoardHits())
                        .createdAt(UtilClass.dateTimeFormat(boardEntity.getCreatedAt()))
                        .build());
        return boardList;
    }
}
