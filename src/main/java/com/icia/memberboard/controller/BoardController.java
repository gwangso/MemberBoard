package com.icia.memberboard.controller;

import com.icia.memberboard.dto.BoardDTO;
import com.icia.memberboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/save")
    private String save(){
        return "boardPages/boardSave";
    }

    @GetMapping()
    private String list(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(value = "query", required = false, defaultValue = "") String query,
                        @RequestParam(value = "type", required = false, defaultValue = "") String type,
                        Model model){
        Page<BoardDTO> boardDTOPage = boardService.findAll(page,query,type);
        return "boardPages/boardList";
    }
}
