package com.miles.beauminity.controller.qna_board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.miles.beauminity.service.qna_board.QnaService;
import com.miles.beauminity.vo.MasterBoardVO;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@AllArgsConstructor
public class QnaController {

    // 서비스를 받아옵니다.
    private QnaService qnaService;

    // 질문 게시판 이동
    // 여기에서 목록을 받아와야할 거 같은데...
    // 파라미터에 값 받아오는 느낌인가 으음 
    @GetMapping("/board/qna")
    public String getQnaList(@ModelAttribute List<MasterBoardVO> qnaBoardList) {
        
        qnaBoardList=qnaService.getTypeBoard();
        // 일단 

        return "qna_board/qna";
    }

    // 질문 게시글 작성
    @GetMapping("/board/qna/write")
    public String getMethodName() {
        return "qna_board/write";
    }

    // 질문 게시글을 포스트
    @PostMapping("/board/qna")
    public String postMethodName(@ModelAttribute MasterBoardVO masterBoardVO) {
        
        masterBoardVO.setBoardType("qna");
        masterBoardVO.setUsername("cjw0212");

        // vo에 모든 값이 잘 들어갔나? 테스트 해봅니다.
        System.out.println(masterBoardVO.toString());

        // 모든 값이 잘 들어가는 것을 확인했기 때문에 이제 서비스의 메서드로 넘기겠습니다.
        qnaService.insertBoard(masterBoardVO);
        
        return "redirect:/board/qna";
    }
    
    
}
