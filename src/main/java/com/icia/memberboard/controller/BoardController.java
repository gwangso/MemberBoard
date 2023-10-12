package com.icia.memberboard.controller;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.dto.CommentDTO;
import com.icia.memberboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/save")
    private String save(){
        return "boardPages/boardSave";
    }

    @PostMapping("/save")
    private String save(@ModelAttribute BoardDTO boardDTO,
                        @RequestParam("memberEmail") String memberEmail) {
        try{
            boardService.save(boardDTO, memberEmail);
            return "redirect:/board";
        }catch (IOException ioException){
            return "redirect:/board/save";
        }
    }

    @GetMapping()
    private String list(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(value = "query", required = false, defaultValue = "") String query,
                        @RequestParam(value = "type", required = false, defaultValue = "1") int type,
                        Model model){
        Page<BoardDTO> boardDTOPage = boardService.findAll(page,query,type);
        int blockLimit = 3;
        int startPage = (((int) (Math.ceil((double) page / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardDTOPage.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOPage.getTotalPages();
        model.addAttribute("boardList", boardDTOPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("query", query);
        model.addAttribute("type",type);
        model.addAttribute("page",page);
        return "boardPages/boardList";
    }

    @GetMapping("/detail/{id}")
    private String detail(@PathVariable("id") Long id,
                          Model model){
        boardService.increaseHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        List<CommentDTO> commentDTOList = null;
        model.addAttribute("commentList", commentDTOList);
        return "boardPages/boardDetail";
    }
}

