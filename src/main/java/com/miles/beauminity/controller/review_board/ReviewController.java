package com.miles.beauminity.controller.review_board;

import java.security.Principal;
import java.util.List;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.service.review_board.ReviewService;
import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;


import lombok.RequiredArgsConstructor;





@Controller
@RequiredArgsConstructor // 1. 서비스 주입을 위한 의존성 주입 어노테이션 추가
public class ReviewController { // 역할: 후기 게시판에 대한 사용자의 요청 처리. 사용자가 보는 웹페이지를 보여주기 위한 요청 처리
                   
    // 비즈니스 로직을 수행할 서비스 레이어 선언
    private final ReviewService reviewService;
    
    // 후기 게시판 페이지 요청 처리
    @GetMapping("/board/review")
    public String reviewPage(Model model) {
        // 1. 서비스 호출을 통해 전체 후기 리스트 데이터 확보
        List<ReviewBoardVO> reviewList = reviewService.getReviewBoardList();

        // 2. **타임리프 화면으로 데이터를 "reviewList"라는 열쇠(key)로 배송
        model.addAttribute("reviewList", reviewList);

        return "review_board/review";
    }

    // 후기 게시판 상세 페이지 요청 처리
    @GetMapping("/board/review/detail/{boardId}")
    public String reviewDetailPage(@PathVariable("boardId") Long boardId, Model model) {

        // 1. 서비스 호출을 통해 후기 상세 데이터 가져오기
        ReviewBoardVO detail = reviewService.getReviewBoardDetail(boardId);

        model.addAttribute("detail", detail);


        return "review_board/detail";
    }
    

    
    // 후기 게시판 리뷰 쓰기 요청 처리
    @GetMapping("/board/review/write")
    public String reviewWrite(@AuthenticationPrincipal CustomUserDetails loginMember, Model model) {
        // 로그인 기능 구현 전까지 사용할 임시 작성자 이름을 모델에 추가
        // model.addAttribute("loginUser", "테스트유저");

        
        // 1. 로그인 상태일 때 세션 ID 추출 및 모델 주입
        String nickName = loginMember.getNickname();
        model.addAttribute("loginUser",nickName);

    

        return "review_board/write";
    }

    // 후기 게시판 리뷰 작성글 등록 요청 처리
    @PostMapping("/board/review/write")
    public String registerReview(@ModelAttribute ReviewBoardVO reviewBoardVO, Principal principal) {
        
        // 로그인 미구현 대안: master_board.member_id(FK) 제약 조건 통과용 임시 ID 강제 설정 
        // ** 주의 ** 실제 DB member 테이블에 username이 'testuser01' 인 회원이 있어야 함
        // reviewBoardVO.setUserName("testuser01");

        String userName = principal.getName();

        reviewBoardVO.setUserName(userName);

        // 실제로 3개의 테이블에 순차 저장 및 파일 물리 저장을 수행하는 서비스 매서드 호출
        reviewService.registerReviewPost(reviewBoardVO);

        
        return "redirect:/board/review";  // 등록 완료 후 후기 게시판 목록 페이지로 리다이렉트 처리
    }
    
    // 후기 게시판 리뷰 작성글 수정 페이지 요청 처리
    @GetMapping("/board/review/edit/{boardId}")
    public String editReviewDetailPage(@PathVariable ("boardId") Long boardId,Principal principal, Model model) {

        // 서비스 호출을 통해 후기 상세 데이터 가져오기
        ReviewBoardVO detail = reviewService.getReviewBoardDetail(boardId);

        // 비로그인 상태이거나 로그안한 Id와 게시글 작성자 Id가 다를 경우 예외 발생 처리
        if (principal == null || !principal.getName().equals(detail.getUserName())) {
            
            return "redirect:/board/review/detail/" + boardId + "?error=unauthorized";
        }

        List<MasterBoardFileVO> files = detail.getAttachedFiles();

        // 검증 통과한 사용자(게시글 작성한 본인)일 때만 수정 폼 화면으로 이동시키기
        model.addAttribute("fileList", files);
        model.addAttribute("reviewForm",detail);
    
        

        return "review_board/edit"; // 수정 화면 HTML 파일명
    }
    
    // 후기 게시판 리뷰 작성글 수정 요청 처리
    @PostMapping("/board/review/edit/{id}")
    public String reviewEditUpdate(
            @PathVariable("id") Long id,
            @ModelAttribute("reviewForm") ReviewBoardVO reviewForm) {

        System.out.println("컨트롤러 진입 성공");
        System.out.println("수정 대상 게시글 ID (path): " + id);
        System.out.println("VO에서 꺼낸 boardId: " + reviewForm.getBoardId());

        // 서비스 레이어로 파일 배열 정보와 게시글 수정 정보 토스
        reviewService.updateReviewBoard(reviewForm);

        return "redirect:/board/review/detail/" + id;
    }

    // 후기 게시판 리뷰 작성글 삭제 요청 처리
    @PostMapping("/board/review/delete/{boardId}")
    public String deleteReview(@PathVariable("boardId") Long boardId, Principal principal ) {
        
        // 1. 서비스 호출을 통해 후기 상세 데이터 가져오기 (작성자 ID 확인용)
        ReviewBoardVO detail = reviewService.getReviewBoardDetail(boardId);

        // 2. 비로그인 상태이거나 로그인한 ID와 게시글 작성자 Id가 다를 경우 권한 에러 처리
        if (principal == null || !principal.getName().equals(detail.getUserName())) {
            // 비동기 fetch 통신이므로 redirect가 아닌 403 Forbidden 상태코드를 보냅니다.
            return "redirect:/error/403";
        }
        // 3. 검증 통과한 사용자(게시글 작성한 본인)일 때만 실제 삭제 서비스 호출
        reviewService.delectReviewBoard(boardId); // 실제 DB에서 글을 지우는 서비스

        return "redirect:/board/review";
    }
    
    

}
