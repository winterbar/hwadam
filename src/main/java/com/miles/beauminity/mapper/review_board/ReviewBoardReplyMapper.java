package com.miles.beauminity.mapper.review_board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.review.ReviewReplyVO;

@Mapper
public interface ReviewBoardReplyMapper {

    // 리뷰 게시글 등록
    void saveReply(ReviewReplyVO replyVO);

    // 리뷰 게시글 댓글 가져오기
    List<ReviewReplyVO> getReplyList(Long boardId);
}
