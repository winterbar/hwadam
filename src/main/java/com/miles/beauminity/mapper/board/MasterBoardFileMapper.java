package com.miles.beauminity.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.board.MasterBoardFileVO;

@Mapper
public interface MasterBoardFileMapper {

    // CREATE1 - 파일 추가
    public void insertFile(MasterBoardFileVO masterBoardFileVO);

    // READ1 - 특정 게시글의 파일 전체조회
    public List<MasterBoardFileVO> getBoardFileById(Long id);


}
