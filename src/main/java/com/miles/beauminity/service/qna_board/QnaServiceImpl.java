package com.miles.beauminity.service.qna_board;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.board.MasterBoardFileMapper;
import com.miles.beauminity.mapper.board.MasterBoardLikeMapper;
import com.miles.beauminity.mapper.board.MasterBoardMapper;
import com.miles.beauminity.mapper.login.MemberMapper;
import com.miles.beauminity.mapper.qna_board.CommunityReplyMapper;
import com.miles.beauminity.mapper.qna_board.QnaBoardMapper;
import com.miles.beauminity.util.MasterFileUtil;
import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardLikeVO;
import com.miles.beauminity.vo.board.MasterBoardReplyVO;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.board.SearchVO;
import com.miles.beauminity.vo.board.TypeOffsetVO;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.qna_board.CommunityReplyVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;
import com.miles.beauminity.vo.qna_board.QnaBoardVO;

import lombok.AllArgsConstructor;

// 질문게시판 구현체입니다.
// 서비스는 구현체에 다는 거다...

@Service
@AllArgsConstructor
public class QnaServiceImpl implements QnaService {

    // 매퍼를 객체로 불러와줍니다.
    private MasterBoardMapper masterBoardMapper;
    private MasterBoardFileMapper masterBoardFileMapper;
    private MasterBoardLikeMapper masterBoardLikeMapper;
    private QnaBoardMapper qnaBoardMapper;
    private MemberMapper memberMapper;
    private CommunityReplyMapper communityReplyMapper;

    // 게시글 등록
    @Override
    public void insertBoard(MasterBoardVO masterBoardVO, MultipartFile[] files, String category) {

        // 일단 게시글부터 게시.
        System.out.println("전:" + masterBoardVO.getBoardId());
        masterBoardMapper.insertBoard(masterBoardVO);
        System.out.println("후:" + masterBoardVO.getBoardId());

        // 번호가 잘 들어가는 걸 확인했으니 이제 파일 정보랑 글 번호를 합쳐서 DB에 올려놓아야한다.
        // VO가 필요하겠다. vo 만들러 가자
        // 오늘은 게시만 하고 집 가겠다 수정은 내일 와서 해야지..

        Long boardId = masterBoardVO.getBoardId();
        String uploadPath = "c:/uploads/qna";
        List<MasterBoardFileVO> fileList = MasterFileUtil.saveFiles(files, uploadPath);
        for (MasterBoardFileVO f : fileList) {
            f.setBoardId(boardId);

            // 매퍼 적용
            masterBoardFileMapper.insertFile(f);
        }

        // Qna 정보를 저장
        QnaBoardVO qnaBoardVO = new QnaBoardVO();
        qnaBoardVO.setBoardId(boardId);

        qnaBoardVO.setCategory(category);

        qnaBoardMapper.insertQna(qnaBoardVO);

    }

    // 게시글 전체조회
    @Override
    public List<QnaBoardCompleteVO> getTypeBoard(String type, PageVO pageVO) {

        System.out.println(type);

        TypeOffsetVO typeOffsetVO = new TypeOffsetVO();
        typeOffsetVO.setType(type);
        typeOffsetVO.setOffset(pageVO.getOffset());
        typeOffsetVO.setSize(pageVO.getSize());

        List<MasterBoardVO> qnaList = masterBoardMapper.getTypeBoard(typeOffsetVO);

        List<QnaBoardCompleteVO> finalList = new ArrayList<>();
        for (MasterBoardVO q : qnaList) {
            QnaBoardCompleteVO nQ = new QnaBoardCompleteVO();
            nQ.setBoardId(q.getBoardId());
            nQ.setNickname(masterBoardMapper.getNicknameByBoardId(q.getBoardId()));
            nQ.setTitle(q.getTitle());
            nQ.setCreatedAt(q.getCreatedAt());
            nQ.setViewCnt(q.getViewCnt());
            nQ.setReplyCnt(getReplyCountByBoardId(q.getBoardId()));
            nQ.setCategory(qnaBoardMapper.getCategoryById(q.getBoardId()));
            finalList.add(nQ);
        }

        return finalList;
    }

    // 게시글 카테고리별 전체조회
    @Override
    public List<QnaBoardCompleteVO> getQnaBoardByCategory(String type, PageVO pageVO, String category, String sort
                                                        , LocalDateTime startDate, LocalDateTime endDate
                                                        , String searchType, String keyword) {
        
        return qnaBoardMapper.selectCommuByCategory(type, pageVO, category
                                                    , sort, startDate, endDate
                                                    , searchType, keyword);
    }

    // 게시글 상세조회
    @Override
    public MasterBoardVO getOneBoard(Long id) {

        System.out.println(masterBoardMapper.getOneBoard(id).toString());

        return masterBoardMapper.getOneBoard(id);
    }

    // 게시글 상세조회 시 파일 조회
    @Override
    public List<MasterBoardFileVO> getBoardFileById(Long id) {
        // 파일의 경로를 확인하기 위해 일단 파일의 리스트를 불러온다.
        List<MasterBoardFileVO> flist =masterBoardFileMapper.getBoardFileById(id);
        List<MasterBoardFileVO> existFList= new ArrayList<>();
        
        // 파일을 순차탐색하면서 파일의 저장명이 지정 경로에 있는지 확인한다. 있으면 최종리스트에 푸시
        for(MasterBoardFileVO f : flist){
            String fileName = f.getSavedName();
            System.out.println("현재 파일 이름: "+fileName);
            String filePath = "c:/uploads/qna/"+fileName;
            System.out.println("파일 경로: "+filePath);
            File file = new File(filePath);

            if(file.exists() && file.isFile()){
                System.out.println("파일 있음");
                existFList.add(f);
            } else{
                System.out.println("파일 없음");
            }
        }

        // 최종리스트를 반환한다.
        return existFList;
    }

    // 게시글 삭제
    @Override
    public void deleteBoard(Long id) {

        List<MasterBoardFileVO> fileList = masterBoardFileMapper.getBoardFileById(id);
        String prevPath = "c:/uploads";
        String nextPath = "c:/deleted/qna";

        masterBoardMapper.deleteBoard(id);

        // 파일 삭제
        for (MasterBoardFileVO f : fileList) {
            // 삭제 이전에 삭제할 파일 복사
            MasterFileUtil.copyFiles(prevPath, f.getSavedName(), nextPath);
            // 이전 경로에 있던 파일은 삭제
            MasterFileUtil.deleteFiles(prevPath, f.getSavedName());
        }

    }

    // 조회수 증가
    @Override
    public void viewUp(Long id) {
        masterBoardMapper.viewUp(id);
    }

    // 게시글 수정
    @Override
    public void updateBoard(MasterBoardVO masterBoardVO, MultipartFile[] files, String category) {

        masterBoardMapper.updateBoard(masterBoardVO);

        Long boardId = masterBoardVO.getBoardId();
        String uploadPath = "c:/uploads/qna";
        List<MasterBoardFileVO> fileList = MasterFileUtil.saveFiles(files, uploadPath);
        for (MasterBoardFileVO f : fileList) {
            f.setBoardId(boardId);

            // 매퍼 적용
            masterBoardFileMapper.insertFile(f);
        }

        // Qna정보 저장
        QnaBoardVO qnaBoardVO = new QnaBoardVO();
        qnaBoardVO.setBoardId(boardId);
        qnaBoardVO.setCategory(category);

        qnaBoardMapper.updateQna(qnaBoardVO);

    }

    // 게시글 수 조회
    @Override
    public int getTypeBoardCount(String type) {
        return masterBoardMapper.getTypeBoardCount(type);
    }

    // 카테고리별 게시글 수 조회
    @Override
    public int getQnaCountByCategory(String type, String category) {

        System.out.println("카테고리: " + category);

        TypeOffsetVO typeOffsetVO = new TypeOffsetVO();

        typeOffsetVO.setType(type);
        if (category.equals("전체보기")) {
            return masterBoardMapper.getTypeBoardCount(type);
        } else {
            typeOffsetVO.setCategory(category);
            System.out.println("현재상태: " + typeOffsetVO.toString());
            return qnaBoardMapper.getQnaCountByCategory(typeOffsetVO);
        }

    }

    @Override
    public String getNicknameByBoardId(Long id) {
        return masterBoardMapper.getNicknameByBoardId(id);
    }

    // 좋아요를 집어넣는다.
    @Override
    public void insertLike(MasterBoardLikeVO masterBoardLikeVO) {
        masterBoardLikeMapper.insertLike(masterBoardLikeVO);
    }

    @Override
    public boolean isLikeON(Long id, String username) {

        MasterBoardLikeVO masterBoardLikeVO = new MasterBoardLikeVO();
        masterBoardLikeVO.setBoardId(id);
        masterBoardLikeVO.setUsername(username);

        if (masterBoardLikeMapper.isLikeON(masterBoardLikeVO).isEmpty())
            return false;
        else
            return true;

    }

    @Override
    public void deleteLike(MasterBoardLikeVO masterBoardLikeVO) {
        masterBoardLikeMapper.deleteLike(masterBoardLikeVO);
    }

    @Override
    public int getLikeCount(Long id) {
        return masterBoardLikeMapper.getLikeCount(id);
    }

    @Override
    public List<QnaBoardCompleteVO> getSearchBoard(String type, String str, PageVO pageVO) {

        System.out.println(type);

        SearchVO searchVO = new SearchVO();
        searchVO.setType(type);
        searchVO.setOffset(pageVO.getOffset());
        searchVO.setSize(pageVO.getSize());
        searchVO.setStr(str);

        List<MasterBoardVO> qnaList = masterBoardMapper.getSearchBoard(searchVO);

        List<QnaBoardCompleteVO> finalList = new ArrayList<>();
        for (MasterBoardVO q : qnaList) {
            QnaBoardCompleteVO nQ = new QnaBoardCompleteVO();
            nQ.setBoardId(q.getBoardId());
            nQ.setNickname(masterBoardMapper.getNicknameByBoardId(q.getBoardId()));
            nQ.setTitle(q.getTitle());
            nQ.setCreatedAt(q.getCreatedAt());
            nQ.setViewCnt(q.getViewCnt());
            nQ.setReplyCnt(q.getReplyCnt());
            nQ.setCategory(qnaBoardMapper.getCategoryById(q.getBoardId()));

            System.out.println("검색된 게시글 정보: " + nQ.toString());

            finalList.add(nQ);
        }

        return finalList;
    }

    @Override
    public String getUsernameByBoardId(Long id) {
        return masterBoardMapper.getUsernameByBoardId(id);
    }

    @Override
    public int getCountSearchBoardByTitle(String type, String str) {

        SearchVO searchVO = new SearchVO();
        searchVO.setType(type);
        searchVO.setStr(str);

        return masterBoardMapper.getCountSearchBoardByTitle(searchVO);
    }

    @Override
    public void deleteFilesForUpdate(Long id) {

        MasterBoardFileVO getFile = masterBoardFileMapper.getFileById(id);

        String prevPath = "c:/uploads";
        String nextPath = "c:/deleted/qna";

        // 삭제 이전에 삭제할 파일 복사
        MasterFileUtil.copyFiles(prevPath, getFile.getSavedName(), nextPath);
            // 이전 경로에 있던 파일은 삭제
        MasterFileUtil.deleteFiles(prevPath, getFile.getSavedName());
    }

    @Override
    public MemberVO getMemberInfo(String username) {
        return memberMapper.findLoginId(username);
    }

    @Override
    public List<CommunityReplyVO> getReplyList(Long id) {
        List<CommunityReplyVO> reList = communityReplyMapper.getReplyList(id);
        List<CommunityReplyVO> finList = new ArrayList<>();

        for(CommunityReplyVO c : reList){
            String nickname=memberMapper.findLoginId(c.getUsername()).getNickname();
            c.setNickname(nickname);
            finList.add(c);
        }

        return finList;

    }

    @Override
    public void insertReply(MasterBoardReplyVO masterBoardReplyVO) {
        communityReplyMapper.insertReply(masterBoardReplyVO);
    }

    @Override
    public int getReplyCountByBoardId(Long id) {

        return communityReplyMapper.getReplyCountByBoardId(id);
    }

    @Override
    public void updateReply(MasterBoardReplyVO masterBoardReplyVO) {
        communityReplyMapper.updateCommunityReply(masterBoardReplyVO);
    }

    @Override
    public void deleteReply(Long id) {
        communityReplyMapper.deleteReply(id);
    }
    public List<QnaBoardCompleteVO> getTipList() {
        return qnaBoardMapper.getTipList();
    }


}
