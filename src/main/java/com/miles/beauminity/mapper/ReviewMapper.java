package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.MasterBoardFileVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;

@Mapper
public interface ReviewMapper {
    // 1. master_board 테이블 등록 (ReviewBoardVO 사용)
    void insertMasterBoard(ReviewBoardVO vo);
    
    // 2. review_board 테이블 등록 (ReviewBoardVO 사용)
    void insertReviewBoard(ReviewBoardVO vo);

    // 3. board_file 테이블 등록 (MasterBoardFileVO fileVO);
    void insertBoardFile(MasterBoardFileVO fileVO);

    // mater_board와 review_board 테이블 조회 
    List<ReviewBoardVO> selectReviewBoardList();
    
}
