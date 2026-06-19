package com.miles.beauminity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.MasterBoardVO;

// 게시글용 매퍼

@Mapper
public interface MasterBoardMapper {
    // CREATE - 게시글 추가
    public void insertBoard(MasterBoardVO masterBoardVO);

    // READ1 - 게시글 전체조회(페이징 미적용 매퍼 구현완료)
    public List<MasterBoardVO> getTypeBoard(String type);

    // READ2 - 게시글 상세조회
    public MasterBoardVO getOneBoard(Long id);

    // UPDATE1 - 게시글 삭제
    public void deleteBoard(Long id);

    // UPDATE2 - 게시글 조회수 증가
    public void viewUp(Long id);

    // UPDATE3 - 게시글 수정
    public void updateBoard(MasterBoardVO masterBoardVO);

    // SELECT - 게시판 별 회원이 등록한 게시글 수 조회
    public List<Map<String, Object>> countBoard(String username);

}

