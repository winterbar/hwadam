package com.miles.beauminity.service.qna_board;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.board.TypeOffsetVO;

// 스프링에서 쓸 거니까 서비스 걸어줍니다.

public interface QnaService {

    void insertBoard(MasterBoardVO masterBoardVO, MultipartFile[] files, String category);

    List<MasterBoardVO> getTypeBoard(String type, PageVO pageVO);

    List<MasterBoardVO> getQnaBoardByCategory(String type, PageVO pageVO, String category);

    MasterBoardVO getOneBoard(Long id);

    List<MasterBoardFileVO> getBoardFileById(Long id);

    void deleteBoard(Long id);

    void viewUp(Long id);

    void updateBoard(MasterBoardVO masterBoardVO);

    int getTypeBoardCount(String type);

    int getQnaCountByCategory(String type, String category);
    
}
