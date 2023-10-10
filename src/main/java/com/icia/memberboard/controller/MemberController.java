package com.icia.memberboard.controller;

import com.icia.memberboard.dto.MemberDTO;
import com.icia.memberboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/save")
    private String save(){
        return "memberPages/memberSave";
    }

    @PostMapping("/save")
    private String save(@ModelAttribute MemberDTO memberDTO) throws IOException {
        memberService.save(memberDTO);
        return "redirect:/member/login";
    }

    @PostMapping("/duplicate")
    private ResponseEntity duplicate(@RequestParam("memberEmail") String memberEmail){
        boolean result = memberService.duplicate(memberEmail);
        if(result){
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    private String findAll(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           Model model){
        Page<MemberDTO> memberDTOPage = memberService.findAll(page);
        model.addAttribute("memberList", memberDTOPage);

        int blockLimit = 3;
        int startPage = (((int) (Math.ceil((double) page / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < memberDTOPage.getTotalPages()) ? startPage + blockLimit - 1 : memberDTOPage.getTotalPages();
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("page", page);
        return "memberPages/memberList";
    }

    @GetMapping("/login")
    private String login(@RequestParam(value = "success", required = false, defaultValue = "") String success,
                         Model model){
        model.addAttribute("success", success);
        return "memberPages/login";
    }

    @PostMapping("/login")
    private String login(@ModelAttribute MemberDTO memberDTO,
                         HttpSession session){
        try{
            MemberDTO result = memberService.login(memberDTO);
            if (result != null){
                session.setAttribute("loginName", result.getMemberName());
                session.setAttribute("loginEmail", result.getMemberEmail());
                return "redirect:/member/main";
            }else {
                return "redirect:/member/login?success=error1";
            }
        }catch (NoSuchElementException noSuchElementException){
            return "redirect:/member/login?success=error1";
        }
    }

    @GetMapping("/main")
    private String main(){
        return "memberPages/main";
    }

    @GetMapping("/logout")
    private String main(HttpSession session){
        session.removeAttribute("loginName");
        session.removeAttribute("loginEmail");
        return "redirect:/";
    }

    @GetMapping("/detail")
    private String detail(HttpSession session,
                          Model model){
        String memberEmail = session.getAttribute("loginEmail").toString();
        MemberDTO memberDTO = memberService.findByEmail(memberEmail);
        model.addAttribute("member",memberDTO);
        System.out.println(memberDTO.getStoredFilename());
        return "memberPages/memberDetail";
    }
}
