package com.icia.memberboard;

import com.icia.memberboard.dto.MemberDTO;
import com.icia.memberboard.repository.MemberRepository;
import com.icia.memberboard.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;


}
