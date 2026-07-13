package com.miles.beauminity.mapper.review_board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.review.ReviewReplyVO;

@Mapper
public interface ReviewBoardReplyMapper {

    // 리뷰 게시글 등록
    public void saveReply(ReviewReplyVO replyVO);

    // 리뷰 게시글 댓글 가져오기
    public List<ReviewReplyVO> getReplyList(Long boardId);

    // 리뷰 게시글 삭제
    public void removeReply(Long boardId);

    // 리뷰 게시글의 부모댓글 작성자 아이디 가져오기
    public String getParentReplyWriter(Long parentsReplyId);
}
