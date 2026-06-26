package com.miles.beauminity.mapper.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.SearchVO;
import com.miles.beauminity.vo.board.TypeOffsetVO;

// 게시글용 매퍼

@Mapper
public interface MasterBoardMapper {
    // CREATE - 게시글 추가
    public void insertBoard(MasterBoardVO masterBoardVO);

    // READ1 - 게시글 전체조회(페이징 미적용 매퍼 구현완료)
    public List<MasterBoardVO> getTypeBoard(TypeOffsetVO typeOffsetVO);

    // READ2 - 게시글 상세조회
    public MasterBoardVO getOneBoard(Long id);

    // UPDATE1 - 게시글 삭제
    public void deleteBoard(Long id);

    // UPDATE2 - 게시글 조회수 증가
    public void viewUp(Long id);

    // UPDATE3 - 게시글 수정
    public void updateBoard(MasterBoardVO masterBoardVO);

    // SELECT1 - 게시판 별 회원이 등록한 게시글 수 조회
    public List<Map<String, Object>> countBoard(String username);

    // SELECT2 - 게시판별 게시글 수 조회
    int getTypeBoardCount(String type);

    // SELECT3 - 닉네임 검색
    public String getNicknameByBoardId(Long id);

    // SELECT4 - 게시글 검색
    public List<MasterBoardVO> getSearchBoard(SearchVO searchVO);

    // SELECT5 - 게시글 검색:: 제목
    public List<MasterBoardVO> getSearchBoardByTitle(SearchVO searchVO);

    // SELECT6 - 게시글 검색:: 내용
    public List<MasterBoardVO> getSearchBoardByContent(SearchVO searchVO);

    // SELECT7 - 게시글 작성자 찾기
    public String getUsernameByBoardId (Long id);

    public int getCountSearchBoardByTitle(SearchVO searchVO);

}

