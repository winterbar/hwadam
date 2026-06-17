package com.miles.beauminity.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.miles.beauminity.vo.MasterBoardVO;

// 각 게시판 컨트롤러에서 이동하는 게 나을 거 같습니다. 각자 화면 찾아가주세요...ㅣ

@Controller
public class MainController {
    
    // 메인화면 이동
    @GetMapping("/")
    public String getMainHome() {
        return "main/index";
    }
    
    
}
