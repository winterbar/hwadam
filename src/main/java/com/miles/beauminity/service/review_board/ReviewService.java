package com.miles.beauminity.service.review_board;

import java.util.List;

import com.miles.beauminity.vo.review.ReviewBoardVO;

public interface ReviewService { // 역할: 후기 게시판에 대한 서비스 요청 처리

    // 후기 게시글 등록 명세
    boolean registerReviewPost(ReviewBoardVO reviewBoardVO);
    
    // 후기 게시판 목록 조회 명세
    List<ReviewBoardVO> getReviewBoardList();

    // 후기 게시판 목록에서 게시글 상세조회 명세
    ReviewBoardVO getReviewBoardDetail(Long boardId);

    // 후기 게시판 게시글의 수정 요청 명세
    void updateReviewBoard(ReviewBoardVO reviewForm);


        
}

    


