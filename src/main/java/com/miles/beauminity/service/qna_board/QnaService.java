package com.miles.beauminity.service.qna_board;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.MasterBoardVO;

// 스프링에서 쓸 거니까 서비스 걸어줍니다.

public interface QnaService {

    void insertBoard(MasterBoardVO masterBoardVO, MultipartFile[] files);

    List<MasterBoardVO> getTypeBoard(String type);

    MasterBoardVO getOneBoard(Long id);

    void deleteBoard(Long id);

    void viewUp(Long id);

    void updateBoard(MasterBoardVO masterBoardVO);
    
}
