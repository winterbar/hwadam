package com.miles.beauminity.controller.review_board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.miles.beauminity.service.review_board.ReviewService;
import com.miles.beauminity.vo.review.ReviewBoardVO;

import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor // 1. 서비스 주입을 위한 의존성 주입 어노테이션 추가
public class ReviewController { // 역할: 후기 게시판에 대한 사용자의 요청 처리. 사용자가 보는 웹페이지를 보여주기 위한 요청 처리
                   
    // 비즈니스 로직을 수행할 서비스 레이어 선언
    private final ReviewService reviewService;
    
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

    @PostMapping("/board/review/write")
    public String registerReview(@RequestBody ReviewBoardVO reviewBoardVO) {
        
        // 로그인 미구현 대안: master_board.member_id(FK) 제약 조건 통과용 임시 ID 강제 설정 
        // ** 주의 ** 실제 DB member 테이블에 username이 'testuser01' 인 회원이 있어야 함
        reviewBoardVO.setUserName("testuser01");

        // 실제로 3개의 테이블에 순차 저장 및 파일 물리 저장을 수행하는 서비스 매서드 호출
        reviewService.registerReviewPost(reviewBoardVO);

        
        return "redirect:/board/review";  // 등록 완료 후 후기 게시판 목록 페이지로 리다이렉트 처리
    }
    

}
