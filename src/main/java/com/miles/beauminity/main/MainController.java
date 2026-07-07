package com.miles.beauminity.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.miles.beauminity.service.feed.FeedService;
import com.miles.beauminity.vo.feed.FeedVO;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

// 각 게시판 컨트롤러에서 이동하는 게 나을 거 같습니다. 각자 화면 찾아가주세요...ㅣ

@Controller
@RequiredArgsConstructor 
public class MainController {
    private final FeedService feedService;
    
    // 메인화면 이동
    @GetMapping("/")
    public String getMainHome(Model model) {
        List<FeedVO> mainFeed = feedService.getMainFeed();
        model.addAttribute("mainFeed",mainFeed);
        return "main/index";
    }

}
