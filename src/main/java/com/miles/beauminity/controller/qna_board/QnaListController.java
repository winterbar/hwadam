package com.miles.beauminity.controller.qna_board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miles.beauminity.service.qna_board.QnaService;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/board")
public class QnaListController {
    private QnaService qnaService;

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterBoard(@RequestParam(
        value = "category", defaultValue = "all") String category, @ModelAttribute PageVO pageVO){
        Map<String, Object> result = new HashMap<>();
        // 카테고리에 맞는 리스트를 DB에서 조회해서 반환

        String type="qna";

        int count = qnaService.getQnaCountByCategory(type, category); 
        pageVO.pageInfo(count);

        List<MasterBoardVO> filteredList = qnaService.getQnaBoardByCategory(type, pageVO, category);
        
        result.put("list", filteredList);
        result.put("pageInfo", pageVO);

        return ResponseEntity.ok(result);
    }
}
