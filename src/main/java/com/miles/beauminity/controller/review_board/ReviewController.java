package com.miles.beauminity.controller.review_board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ReviewController { // 역할: 후기 게시글 기능에 대한 요청 처리. 사용자가 보는 웹페이지를 그리기 위한 요청 처리
                                
    
    // 후기 게시판 페이지 요청 처리
    @GetMapping("/board/review")
    public String reviewPage() {


        return "review_board/review";
    }
    
    // 후기 게시판 리뷰 쓰기 요청 처리
    @GetMapping("/board/review/write")
    public String reviewWrite(Model model) {
        // 로그인 기능 구현 전까지 사용할 임시 작성자 이름을 모델에 추가
        model.addAttribute("loginUser", "테스트유저");

        return "review_board/write";
    }
    

}
