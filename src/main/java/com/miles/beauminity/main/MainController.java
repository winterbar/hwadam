package com.miles.beauminity.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainController {
    
    // 메인화면 이동
    @GetMapping("/")
    public String getMainHome() {
        return "main/index";
    }

    // 후기 게시판 이동
    @GetMapping("/board/review")
    public String getReviewList() {
        return "review_board/review";
    }

    // 질문 게시판 이동
    @GetMapping("/board/qna")
    public String getQnaList() {
        return "qna_board/qna";
    }

    // 정보공유 게시판 이동
    @GetMapping("/board/info")
    public String getInfoShareList() {
        return "infoshare_board/info";
    }

    // 피드 게시판 이동
    @GetMapping("/feed")
    public String getFeedList() {
        return "feed/list";
    }

    // 
    
    
}
