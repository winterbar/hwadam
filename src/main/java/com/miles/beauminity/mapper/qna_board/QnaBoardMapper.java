package com.miles.beauminity.mapper.qna_board;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

<<<<<<< HEAD
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.board.TypeOffsetVO;
=======
import com.miles.beauminity.vo.board.PageVO;
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;
import com.miles.beauminity.vo.qna_board.QnaBoardVO;

@Mapper
public interface QnaBoardMapper {

    // qna 테이블에 삽입
    public void insertQna(QnaBoardVO qnaBoardVO);

    // 카테고리별로 질문게시판 조회
    public List<QnaBoardCompleteVO> selectCommuByCategory(@Param("type") String type
                                                        , @Param("page") PageVO pageVO                                                    
                                                        , @Param("category") String category
                                                        , @Param("sort") String sort
                                                        , @Param("startDate") LocalDateTime startDate
                                                        , @Param("endDate") LocalDateTime endDate
                                                        , @Param("searchType") String searchType
                                                        , @Param("keyword") String keyword);

    // 카테고리별 게시글 수
<<<<<<< HEAD
    public int getQnaCountByCategory(TypeOffsetVO typeOffsetVO);
=======
    public int getQnaCountByCategory(@Param("type") String type                                                    
                                    , @Param("category") String category
                                    , @Param("startDate") LocalDateTime startDate
                                    , @Param("endDate") LocalDateTime endDate
                                    , @Param("searchType") String searchType
                                    , @Param("keyword") String keyword);
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e

    // 카테고리만 따로 조회
    public String getCategoryById(Long id);

    public void updateQna(QnaBoardVO qnaBoardVO);
    
    // 해당 사용자의 게시물 리스트 가져오기 
    public List<QnaBoardCompleteVO> getCommunityList(String username);
    
    //메인 화면에 넣을 꿀팁 커뮤니티 가져오기
    public List<QnaBoardCompleteVO> getTopTipList();

    public List<QnaBoardCompleteVO> getRecentqnaList();
<<<<<<< HEAD
=======

    // 멤버 정보 일부 가져오기 
    public MemberVO getMemberInfoFromMember(String nickname);
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
    
}
