package com.miles.beauminity.controller.qna_board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class QnaController {

    // 질문 게시글 작성
    @GetMapping("/board/qna/write")
    public String getMethodName() {
        return "qna_board/write";
    }

    // 질문 게시글을 포스트
    @PostMapping("/board/qna")
    public String postMethodName(@RequestParam String title, @RequestParam String content) {
        System.out.println(title);
        System.out.println(content);
        
        return "redirect:/board/qna";
    }
    
    
}
