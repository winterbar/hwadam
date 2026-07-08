package com.miles.beauminity.mapper.qna_board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.board.MasterBoardReplyVO;
import com.miles.beauminity.vo.qna_board.CommunityReplyVO;

@Mapper
public interface CommunityReplyMapper {
    public void insertReply(MasterBoardReplyVO masterBoardReplyVO);

    public List<CommunityReplyVO> getReplyList(Long id);

    public int getReplyCountByBoardId(Long id);

    public void updateCommunityReply(MasterBoardReplyVO masterBoardReplyVO);
    
    public void deleteReply(Long id);
}
