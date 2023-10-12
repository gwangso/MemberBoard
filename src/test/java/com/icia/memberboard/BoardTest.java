package com.icia.memberboard;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.entity.BoardEntity;
import com.icia.memberboard.entity.MemberEntity;
import com.icia.memberboard.repository.BoardRepository;
import com.icia.memberboard.repository.MemberRepository;
import com.icia.memberboard.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private MemberRepository memberRepository;



    @Test
    @DisplayName("테스트 보드 데이터 저장")
    public void dataInsert(){
        IntStream.rangeClosed(1,19).forEach(i ->{
            try {
                BoardDTO boardDTO = newBoard(i);
                MemberEntity memberEntity = memberRepository.findByMemberEmail("test_email"+i).orElseThrow(() -> new NoSuchElementException());
                BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO, memberEntity);
                boardRepository.save(boardEntity);
            } catch (NoSuchElementException noSuchElementException) {
                throw new NoSuchElementException();
            }
        });
    }

    private BoardDTO newBoard(int i) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardWriter("writer"+i);
        boardDTO.setBoardTitle("title"+i);
        boardDTO.setBoardContents("contents"+i);
        return boardDTO;
    }


}
