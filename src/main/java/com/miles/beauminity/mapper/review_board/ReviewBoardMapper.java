package com.miles.beauminity.mapper.review_board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;

@Mapper
public interface ReviewBoardMapper {
    // 1. master_board 테이블 등록 (ReviewBoardVO 사용)
    void insertMasterBoard(ReviewBoardVO vo);
    
    // 2. review_board 테이블 등록 (ReviewBoardVO 사용) (파일 등록은 MasterBoardFileMapper에서)
    void insertReviewBoard(ReviewBoardVO vo);

    
    // member, master_board, review_board 테이블 join해서 조회 (후기 게시글 전체보기)
    List<ReviewBoardVO> selectReviewBoardList();

    // member, master_board, review_board 테이블 join해서 조회 (후기 게시글 상세보기)
    ReviewBoardVO selectReviewBoardDetail(Long boardId);
    
    // 특정 게시글의 첨부파일 파일명 목록만 가져오는 메서드
    List<MasterBoardFileVO> selectFilesByBoardId(Long boardId);

    // 파일 정보 수정 메서드 [추후 공용 Mapper에 update 생기면 갈이끼울 예정]
    void updateReviewBoardFile(MasterBoardFileVO fileVO);


}
