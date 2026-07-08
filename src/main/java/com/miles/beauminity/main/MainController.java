package com.miles.beauminity.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.miles.beauminity.service.feed.FeedService;
import com.miles.beauminity.service.qna_board.QnaService;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

// 각 게시판 컨트롤러에서 이동하는 게 나을 거 같습니다. 각자 화면 찾아가주세요...ㅣ

@Controller
@RequiredArgsConstructor 
public class MainController {
    private final FeedService feedService;
    private final QnaService qnaService;
    
    // 메인화면 이동
    @GetMapping("/")
    public String getMainHome(Model model) {
        // 최근 피드 가져오기
        List<FeedVO> feedList = feedService.getFeedList();
        // 저장되어있는 전체 태그 리스트 및 인기 태그 가져오기
        List<String> tagList = feedService.getTagNameList();
        model.addAttribute("tagList", tagList);
        model.addAttribute("feedList", feedList);

        //인기 태그 가져오기
        List<FeedVO> topTagList = feedService.getTopTagList();
        model.addAttribute("topTagList",topTagList);
        //꿀팁 게시판 가져오기
        List<QnaBoardCompleteVO> tipList = qnaService.getTipList();
        model.addAttribute("tipList",tipList);
        return "main/index";
    }

}
