package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.MasterBoardVO;

// 게시글용 매퍼

@Mapper
public interface MasterBoardMapper {
    // CREATE - 게시글 추가
    public void insertBoard (MasterBoardVO masterBoardVO);
    // READ1 - 게시글 전체조회(매퍼 테스트 중)
    public List<MasterBoardVO> getTypeBoard();
}

