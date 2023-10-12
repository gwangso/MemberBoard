package com.icia.memberboard.controller;

import com.icia.memberboard.dto.CommentDTO;
import com.icia.memberboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    private ResponseEntity save(@RequestBody CommentDTO commentDTO){
        Long boardId = commentDTO.getBoardId();
        commentService.save(commentDTO);
        List<CommentDTO> resultDTOList = commentService.findAllByBoardId(boardId);
        return new ResponseEntity<>(resultDTOList,HttpStatus.OK);
    }
}
