package com.miles.beauminity.service.qna_board;

import java.util.List;

import com.miles.beauminity.vo.MasterBoardVO;

// 스프링에서 쓸 거니까 서비스 걸어줍니다.

public interface QnaService {

    void insertBoard(MasterBoardVO masterBoardVO);

    List<MasterBoardVO> getTypeBoard();
    
}
