package com.miles.beauminity.service.qna_board;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardLikeVO;
import com.miles.beauminity.vo.board.MasterBoardReplyVO;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.qna_board.CommunityReplyVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;

// 스프링에서 쓸 거니까 서비스 걸어줍니다.

public interface QnaService {

    void insertBoard(MasterBoardVO masterBoardVO, MultipartFile[] files, String category);

    List<QnaBoardCompleteVO> getTypeBoard(String type, PageVO pageVO);

    List<QnaBoardCompleteVO> getQnaBoardByCategory(String type, PageVO pageVO, String category, String sort, LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword);

    MasterBoardVO getOneBoard(Long id);

    List<MasterBoardFileVO> getBoardFileById(Long id);

    void deleteBoard(Long id);

    void viewUp(Long id);

    void updateBoard(MasterBoardVO masterBoardVO, MultipartFile[] files, String category);

    int getTypeBoardCount(String type);

    int getQnaCountByCategory(String type, String category, LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword);

    String getNicknameByBoardId(Long id);

    void insertLike(MasterBoardLikeVO masterBoardLikeVO);

    boolean isLikeON(Long id, String username);

    void deleteLike(MasterBoardLikeVO masterBoardLikeVO);

    int getLikeCount(Long id);

    List<QnaBoardCompleteVO> getSearchBoard(String type, String str, PageVO pageVO);

    int getCountSearchBoardByTitle(String type, String str);

    String getUsernameByBoardId (Long id);

    void deleteFilesForUpdate (Long id);

    MemberVO getMemberInfo(String username);

    void insertReply(MasterBoardReplyVO masterBoardReplyVO);

    List<CommunityReplyVO> getReplyList(Long id);

    int getReplyCountByBoardId(Long id);

    void updateReply(MasterBoardReplyVO masterBoardReplyVO);

    void deleteReply(Long id);

    public List<QnaBoardCompleteVO> getTopTipList();

    public List<QnaBoardCompleteVO> getRecentqnaList();

    String getCategoryById(Long id);

    MemberVO getMemberInfoFromMember(String nickname);
    
}
