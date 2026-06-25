package com.miles.beauminity.controller.infoshare_board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class InfoshareBoardController {

    // 정보공유 게시판 페이지 요청
    @GetMapping("/board/infoshare")
    public String getInfoshareList() {
        return "infoshare_board/list";
    }

    // 정보공유 게시판에 글쓰기 페이지 요청
    @GetMapping("/board/infoshare/write")
    public String getInfoshareWrite() {
        return "infoshare_board/write";
    }
    
    
}
