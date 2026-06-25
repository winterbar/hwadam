package com.miles.beauminity.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.board.MasterBoardLikeVO;

@Mapper
public interface MasterBoardLikeMapper {
    
    public void insertLike(MasterBoardLikeVO masterBoardLikeVO);

    public List<MasterBoardLikeVO> isLikeON(MasterBoardLikeVO masterBoardLikeVO); 

    public void deleteLike(MasterBoardLikeVO masterBoardLikeVO);

    public int getLikeCount(Long id);
}
