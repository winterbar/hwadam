package com.miles.beauminity.mapper.qna_board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.TypeOffsetVO;
import com.miles.beauminity.vo.qna_board.QnaBoardVO;

@Mapper
public interface QnaBoardMapper {

    // qna 테이블에 삽입
    public void insertQna(QnaBoardVO qnaBoardVO);

    // 카테고리별로 질문게시판 조회
    public List<MasterBoardVO> selectQnaByCategory(TypeOffsetVO typeOffsetVO);

    // 카테고리별 게시글 수
    public int getQnaCountByCategory(TypeOffsetVO typeOffsetVO);

    // 카테고리만 따로 조회
    public String getCategoryById(Long id);
}
