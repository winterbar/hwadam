package com.miles.beauminity.service.review_board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.board.MasterBoardFileMapper;
import com.miles.beauminity.mapper.board.MasterBoardMapper;
import com.miles.beauminity.mapper.login.MemberMapper;
import com.miles.beauminity.mapper.review_board.ReviewBoardMapper;
import com.miles.beauminity.util.MasterFileUploadUtil;
import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    // 메퍼단에 작업을 요청하기 위한 변수 선언
    private final ReviewBoardMapper reviewBoardMapper; 
    private final MasterBoardMapper masterBoardMapper;
    private final MasterBoardFileMapper masterBoardFileMapper;
    
  
    // 역할: 후기 게시판 등록요청 서비스 처리
    @Override
    @Transactional(rollbackFor = Exception.class) // 여러 데이블의 데이터 정보 무결성을 위해 트랜잭션 어노테이션 필수 부여
    public boolean registerReviewPost(ReviewBoardVO reviewBoardVO) {
        try {
            // 1. 공용 마스터 VO 변환 및 데이터 세팅
            MasterBoardVO masterVO = new MasterBoardVO();
            masterVO.setUsername(reviewBoardVO.getUserName());
            masterVO.setBoardType("review");
            masterVO.setTitle(reviewBoardVO.getTitle());
            masterVO.setContent(reviewBoardVO.getContent());

            log.info("DB로 출발하는 username 값: [{}]",masterVO.getUsername());
            
            // 2. masterBoard 테이블에 데이터 입력
            masterBoardMapper.insertBoard(masterVO); 
            
            // MyBatis useGeneratedKeys 덕분에 void 메서드여도 masterVO 객체 내부에는 boardId가 자동으로 탑재됨
            reviewBoardVO.setBoardId(masterVO.getBoardId());
            
            // 3. reviewBoard 테이블에 데이터 입력
            reviewBoardMapper.insertReviewBoard(reviewBoardVO);
            
            // 4. 첨부 파일 처리 구역
            List<MultipartFile> fileList = reviewBoardVO.getReviewFiles();

            if (fileList != null && !fileList.isEmpty() && !fileList.get(0).isEmpty()) {
                // 파일 업로드 및 공용 파일 매퍼 호출 로직 위치
                // 1. 물리 파일이 저장될 디스크 경로 지정
                String uploadPath = "C:/upload/review";

                // 2. 유틸리티 스펙(배열)에 맞춰 List를 MultipartFile[] 배열로 기계적 변환
                MultipartFile[] filesArray = fileList.toArray(new MultipartFile[0]);

                // 3. static 매서드 다이렉트 호출 -> 디스크 저장 후 파일 정보 VO 리스트 리턴
                List<MasterBoardFileVO> savedFileList = MasterFileUploadUtil.saveFiles(filesArray, uploadPath);

                // 4. 반환된 파일 리스트를 루프 돌며 board_file 테이블에 순차 insert
                for (MasterBoardFileVO fileVO : savedFileList) {
                    // 마스터 게시글 등록 시 발급받은 고유 boardId를 파일 객체에 수동 매핑
                    fileVO.setBoardId(reviewBoardVO.getBoardId());

                    // ReviewServiceImpl 에 void insertBoardFile 호출
                    masterBoardFileMapper.insertFile(fileVO);
                }
            }
            
            return true; 
            
        } catch (Exception e) {
            // SQL Syntax 에러나 제약조건 위배 등 모든 DB 문제는 기계적으로 이 catch 구문으로 잡혀서 디버깅 로그가 찍힘
            log.error("❌ [디버깅 에러 발생] 원인: {}", e.getMessage(), e);
            throw e; // 트랜잭션 자동 롤백 유도
        }
    }

    
    // 역할: 후기게시판 게시글 전체보기 요청 서비스 처리
    @Override
    public List<ReviewBoardVO> getReviewBoardList() {

            return reviewBoardMapper.selectReviewBoardList(); 
    }

    // 역할: 후기게시판 게시글 상세보기 요청 서비스 처리
    @Override
    public ReviewBoardVO getReviewBoardDetail(Long boardId) {
        // 1. 기존의 게시글 본문 텍스트 데이터 조회
        ReviewBoardVO detail = reviewBoardMapper.selectReviewBoardDetail(boardId);

        if (detail != null) {
            // 2. 해당 게시글의 첨부파일 목록 조회 후 VO에 적재
            List<MasterBoardFileVO> fileList = reviewBoardMapper.selectFilesByBoardId(boardId);
            detail.setAttachedFiles(fileList);
        }
        return detail;
        
    }

   
}
