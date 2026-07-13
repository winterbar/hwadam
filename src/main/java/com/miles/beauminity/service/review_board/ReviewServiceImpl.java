package com.miles.beauminity.service.review_board;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.board.MasterBoardFileMapper;
import com.miles.beauminity.mapper.board.MasterBoardLikeMapper;
import com.miles.beauminity.mapper.board.MasterBoardMapper;
import com.miles.beauminity.mapper.login.MemberMapper;
import com.miles.beauminity.mapper.review_board.ReviewBoardMapper;
import com.miles.beauminity.mapper.review_board.ReviewBoardReplyMapper;
import com.miles.beauminity.util.MasterFileUtil;
import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardLikeVO;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.board.TypeOffsetVO;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;
import com.miles.beauminity.vo.review.ReviewReplyVO;
import com.miles.beauminity.vo.review.ReviewSearchVO;

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
    private final ReviewBoardReplyMapper reviewBoardReplyMapper; 
    private final MasterBoardLikeMapper masterBoardLikeMapper;
    private final MemberMapper memberMapper;
  
    // 역할: 후기 게시판 게시글 등록요청 서비스 처리
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
            int point = 15; // 기본 포인트 15점

            if (fileList != null && !fileList.isEmpty() && !fileList.get(0).isEmpty()) {

                // 💥 [추가 1] 용량 체크 및 이미지 확장자 검증 선행 처리
                long maxPostSize = 50 * 1024 * 1024; // 50MB를 바이트(Byte) 단위로 계산
                
                for (MultipartFile file : fileList) {
                    // ① 용량 검증 (50MB 초과 시 에러 발생시켜 롤백 유도)
                    if (file.getSize() > maxPostSize) {
                        throw new IllegalArgumentException("❌ 업로드 한도를 초과했습니다. 파일당 최대 50MB까지 가능합니다. (" + file.getOriginalFilename() + ")");
                    }
                    
                    // ② 이미지 파일 검증 (Content-Type이 image/ 로 시작하는지 체크)
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("❌ 이미지 파일만 업로드할 수 있습니다. (" + file.getOriginalFilename() + ")");
                    }
                }

                // 파일 업로드 및 공용 파일 매퍼 호출 로직 위치
                // 1. 물리 파일이 저장될 디스크 경로 지정
                String uploadPath = "C:/upload/review";

                // 2. 유틸리티 스펙(배열)에 맞춰 List를 MultipartFile[] 배열로 기계적 변환
                MultipartFile[] filesArray = fileList.toArray(new MultipartFile[0]);

                // 3. static 매서드 다이렉트 호출 -> 디스크 저장 후 파일 정보 VO 리스트 리턴
                List<MasterBoardFileVO> savedFileList = MasterFileUtil.saveFiles(filesArray, uploadPath);  // -> 여기서 saved_name + original_name 로직 수행

                // 4. 반환된 파일 리스트를 루프 돌며 board_file 테이블에 순차 insert
                for (MasterBoardFileVO fileVO : savedFileList) {

                    // 💥 [추가 2] 파일명 특수문자 정화 로직 (알파벳, 숫자, ., _, - 제외하고 모두 제거)
                    String originalName = fileVO.getOriginalName();
                    if (originalName != null) {
                        // 자바스크립트의 .replace(/[^a-zA-Z0-9._-]/g, "") 와 동일한 자바 정규식 작동
                        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "");
                        
                        // 만약 특수문자가 싹 지워져서 파일명이 빈 값이 되었을 경우를 대비한 최소한의 안전장치
                        if (safeName.trim().isEmpty() || safeName.startsWith(".")) {
                            safeName = "cleaned_image_" + System.currentTimeMillis() + "_" + originalName;
                        }
                        fileVO.setOriginalName(safeName);
                    }

                    // 마스터 게시글 등록 시 발급받은 고유 boardId를 파일 객체에 수동 매핑
                    fileVO.setBoardId(reviewBoardVO.getBoardId());

                    // ReviewServiceImpl 에 void insertBoardFile 호출
                    masterBoardFileMapper.insertFile(fileVO);

                   
                }
                // 파일이 있으면 30점
                point = 30;
            }

            MemberVO pointVO = new MemberVO();
            pointVO.setUsername(reviewBoardVO.getUserName()); 
            pointVO.setPoint(point);  
            memberMapper.updatePoint(pointVO);
            
            return true; 
            
        } catch (Exception e) {
            // SQL Syntax 에러나 제약조건 위배 등 모든 DB 문제는 기계적으로 이 catch 구문으로 잡혀서 디버깅 로그가 찍힘
            log.error("❌ [디버깅 에러 발생] 원인: {}", e.getMessage(), e);
            throw e; // 트랜잭션 자동 롤백 유도
        }

        

       
    }

    
    // 역할: 후기게시판 게시글 전체보기 요청 서비스 처리
    @Override
    public List<ReviewBoardVO> getReviewBoardList(String type, PageVO pageVO, ReviewSearchVO searchVO) {
        TypeOffsetVO typeOffsetVO = new TypeOffsetVO();
        typeOffsetVO.setType(type);
        typeOffsetVO.setOffset(pageVO.getOffset());
        typeOffsetVO.setSize(pageVO.getSize());

        List <ReviewBoardVO> reviewList = reviewBoardMapper.getReviewBoardListWithSearch(
            type,
            pageVO,
            searchVO
        );

        for(ReviewBoardVO r : reviewList) {
            r.setNickName(masterBoardMapper.getNicknameByBoardId(r.getBoardId()));
            r.setAgeGroup(calculateAgeGroup(r.getBirthday().toString()));
        } 

        return reviewList;
    }

    

    // 역할: 후기 게시글 수 조회 요청 서비스 처리
    @Override
    public int getTypeBoardCount(String type, PageVO pageVO, ReviewSearchVO searchVO) {
        return reviewBoardMapper.getTypeBoardCountWithSearch(type, searchVO);
    }

    // 역할: 후기게시판 게시글 상세보기 요청 서비스 처리
    @Override
    public ReviewBoardVO getReviewBoardDetail(Long boardId) {
        // 1. 기존의 게시글 본문 텍스트 데이터 조회
        ReviewBoardVO detail = reviewBoardMapper.selectReviewBoardDetail(boardId);

        if (detail != null) {
            // 2. 해당 게시글의 첨부파일 목록 조회 후 VO에 적재
            List<MasterBoardFileVO> fileList = masterBoardFileMapper.getBoardFileById(boardId);
            detail.setAttachedFiles(fileList);
        }
        return detail;
        
    }

    // 역할: 후기게시판 게시글 수정 요청 서비스 처리
    @Override
    @Transactional
    public void updateReviewBoard(ReviewBoardVO reviewForm) {// 등록 요청시 보낸 파라미터가 담긴 것이 reviewForm

        MasterBoardVO masterBoardVO = new MasterBoardVO();
        masterBoardVO.setBoardId(reviewForm.getBoardId());
        masterBoardVO.setTitle(reviewForm.getTitle());
        masterBoardVO.setContent(reviewForm.getContent());

        // 1. 게시글의 기본 정보(제목, 내용 등) 우선 업데이트
        masterBoardMapper.updateBoard(masterBoardVO);

        List<MultipartFile> newFiles = reviewForm.getReviewFiles(); 
        List<Long> oldFileIds = reviewForm.getExistingFileIds();  

        // 파일 가공 및 c드라이브 삭제/교체 로직 진행
        if (newFiles != null && oldFileIds != null) {

            String uploadPath = "C:/upload/review";

            for (int i = 0; i<newFiles.size(); i++) {
                MultipartFile newFile = newFiles.get(i);

                // i번째 파일 선택창에 새로운 사진이 업로드된 경우에만 스왑(교체) 진행
                if (newFile != null && !newFile.isEmpty()) {
                    Long targetFileId = oldFileIds.get(i);

                    //1. DB에서 기존 파일의 정보 조회
                    List<MasterBoardFileVO> boardFileList = masterBoardFileMapper.getBoardFileById(reviewForm.getBoardId());

                    MasterBoardFileVO oldFileInfo = null;
                    // 2. 가져온 파일 목록 중에서 '현재 파일 선택창(i번째)'에 해당하는 실제 file_id 데이터 매칭하기
                    if (boardFileList != null) {
                        for (MasterBoardFileVO fileVO : boardFileList) {
                            //boardFileList 안의 file_id가 화면에서 넘어온 기존 file_id와 일치하는지 비교
                            if (fileVO.getFileId().equals(targetFileId)) {
                                oldFileInfo = fileVO;
                                break; 
                            }
                        }
                    }

                    // 3. 매칭되는 기존 파일 정보가 존재할 때만 교체(삭제 및 업데이트) 작업 진행
                    if (oldFileInfo != null) {
                        // 3-1 C드라이브 실제 경로에서 기존 물리 파일 삭제
                        // uploadPath("C:/upload/review") 뒤에 슬래시(/)를 붙여 경로를 완성한다.
                        File oldPhysicalFile = new File(uploadPath + "/" + oldFileInfo.getSavedName());
                        if (oldPhysicalFile.exists()) {
                            oldPhysicalFile.delete();
                        }
                        
                        // 3-2 새로 업로드된 신규 파일 정보 가공 (UUID 생성)
                        String originalName = newFile.getOriginalFilename();
                        String savedName = java.util.UUID.randomUUID().toString() + "_" + originalName;
                        
                        try {
                            // C드라이브에 새 물리 파일 저장
                            File saveFile =new File(uploadPath + "/" + savedName);
                            newFile.transferTo(saveFile);

                            // 3-3. 기존 파일 정보 객체(oldFileInfo)에 신규 파일 데이터 세팅
                            oldFileInfo.setOriginalName(originalName);
                            oldFileInfo.setSavedName(savedName);
                            oldFileInfo.setFileSize((int) newFile.getSize()); // int 타입 변환
                            // 3-4. 매퍼를 호출하여 DB의 board_file 테이블 정보 UPDATE 수행
                            reviewBoardMapper.updateReviewBoardFile(oldFileInfo);
                        } catch (IOException e) {
                            // 파일 쓰기 중 오류 발생 시 트랜잭션 롤백을 유도하기 위해 예외 던지기
                            throw new RuntimeException("물리 파일 저장 중 오류 발생. file_id:" + targetFileId, e);
                        }
                    } 
                    
                }
            }
        }

    }

    // 역할: 후기 게시판 작성글 삭제 요청
    @Override
    @Transactional
    public void delectReviewBoard(Long BoardId) {
        // 1. master_board 에 deleted 컬럼 0으로 업데이트
        masterBoardMapper.deleteBoard(BoardId);

        // 2. board_file 테이블에서 해당 board_id로 등록된 파일 목록 조회
        List<MasterBoardFileVO> fileList = masterBoardFileMapper.getBoardFileById(BoardId);

        //첨부된 파일이 존재할 경우에만 이동 로직 수행
        if (fileList != null && !fileList.isEmpty()) {
            
            // 격리할 디렉토리 경로 정의
            String deletedDirPath = "C:/deleted/review";
            File deletedDir = new File(deletedDirPath);

            // C:/deleted/review/ 폴더가 없을 경우 안전하게 자동 생성
            if (!deletedDir.exists()) {
                deletedDir.mkdirs();
            }

            // 3. 반복문을 돌며 파일 물리 이동 및 DB 경로 업데이트 수행
            for (MasterBoardFileVO fileVO : fileList) {

                // 기존 파일의 실제 전체 경로 객체 생성 (예: C:/upload/review/uuid_파일명.png)
                File srcFilePath = new File(fileVO.getFilePath() + "/" + fileVO.getSavedName());

                // System.out.println("==================================================");
                // System.out.println("검사 중인 파일 ID: " + fileVO.getFileId());
                // System.out.println("자바가 가리키는 원본 경로: " + fileVO.getFilePath());
                // System.out.println("실제 하드디스크에 파일이 존재합니까?: " + srcFilePath.exists());

                // 파일이 실제로 디스크에 존재하는지 검증 후 진행
                if (srcFilePath.exists()) {
                    // 새 격리 폴더 경로 문자열 생성 (C:/deleted/review_board)
                    String newFilePath = deletedDirPath + "/" + fileVO.getSavedName();
                    File targetFile = new File(newFilePath);

                    // 실제 C드라이브 내의 파일을 격리 폴더로 이동 (성공 시 true 반환)
                    boolean isMoved = srcFilePath.renameTo(targetFile);

                    // System.out.println("이동 타겟 경로:" + newFilePath);
                    // System.out.println("물리적 파일 이동 결과(ismoved):" + isMoved);

                    if (isMoved) {
                        // 파일 이동 성공 시, VO 객체의 파일 경로를 새 격리 경로로 변경
                        fileVO.setFilePath(newFilePath);

                        // DB 테이블(board_file)의 file_path 컬럼 업데이트 쿼리 실행
                        reviewBoardMapper.updateReviewBoardFile(fileVO);
                    }else {
                        // System.out.println("파일 이동 실패 (renameTO가 false를 반환함");
                    }
                }
            }

        }    
    }

    //역할: 게시글 조회수 상승
    @Override
    public void viewUp (Long boardId) {

        masterBoardMapper.viewUp(boardId);
    }
   
    //역할: 게시글 댓글 등록
    @Override
    public void registerReply(ReviewReplyVO replyVO) {
        
        reviewBoardReplyMapper.saveReply(replyVO);

           // 1. 게시글 작성자 조회 (MasterBoardMapper 사용)
        String boardWriter = masterBoardMapper.getUsernameByBoardId(replyVO.getBoardId());
        
        // 2. 부모 댓글 작성자 조회 (대댓글일 경우)
        String parentReplyWriter = null;
        if (replyVO.getParentsReplyId() != null && replyVO.getParentsReplyId() != 0) {
            parentReplyWriter = reviewBoardReplyMapper.getParentReplyWriter(replyVO.getParentsReplyId());
        }

        // 3. 포인트 지급 로직 (게시글 주인도 아니고, 부모 댓글 주인도 아닐 때)
        boolean isNotBoardWriter = !boardWriter.equals(replyVO.getUserName());
        boolean isNotParentWriter = (parentReplyWriter == null || !parentReplyWriter.equals(replyVO.getUserName()));

        if (isNotBoardWriter && isNotParentWriter) {
            MemberVO pointVO = new MemberVO();
            pointVO.setUsername(replyVO.getUserName()); // 댓글 작성자
            pointVO.setPoint(10); // 10포인트 지급
            memberMapper.updatePoint(pointVO);
        }
    }

    //역할: 게시글 댓글 조회
    @Override
    public List<ReviewReplyVO> getReplyList(Long boardId) {
    
        return reviewBoardReplyMapper.getReplyList(boardId);
    }

    //역할: 게시글 댓글수 상승
    @Override
    public void replyUp(Long boardId) {

        masterBoardMapper.replyUp(boardId);
    }

    //역할: 게시글 댓글수 하락
    @Override
    public void replyDown(Long boardId) {

        masterBoardMapper.replyDown(boardId);
    }

    //역할: 게시글 댓글 삭제(소프트 삭제)
    @Override
    public void removeReply(Long replyId) {

        reviewBoardReplyMapper.removeReply(replyId);
    }


    @Override
    public boolean isLikeON(Long boardId, String username) {
        // 팀원 맵퍼의 리턴 타입이 VO 객체였으므로, null이 아니면 좋아요가 눌린 상태(true)로 판단
        MasterBoardLikeVO vo = new MasterBoardLikeVO();
        vo.setBoardId(boardId);
        vo.setUsername(username);
        
        List<MasterBoardLikeVO> resultList = masterBoardLikeMapper.isLikeON(vo);
        
        // 데이터가 DB에 있고, boardId값이 제대로 넘어왔을 때만 true를 반환
        return resultList != null && !resultList.isEmpty();
    }

    @Override
    public void insertLike(MasterBoardLikeVO vo) {
        masterBoardLikeMapper.insertLike(vo);
    }

    @Override
    public void deleteLike(MasterBoardLikeVO vo) {
        masterBoardLikeMapper.deleteLike(vo);
    }

    @Override
    public int getLikeCount(Long boardId) {
        return masterBoardLikeMapper.getLikeCount(boardId);
    }


    public List<ReviewBoardVO> getTopReviewList() {
        return reviewBoardMapper.getTopReviewList();
    }

    public String calculateAgeGroup(String birthday) {
        if (birthday == null || birthday.length() < 4) {
        return "나이 미상";
        }
        try {
            int birthYear = Integer.parseInt(birthday.substring(0, 4));
            
            // 💥 핵심: 직접 2026을 적지 않고 시스템 시간을 가져옵니다.
            int currentYear = LocalDate.now().getYear(); 
            
            int age = currentYear - birthYear + 1;
            int ageGroup = (age / 10) * 10;
            
            return ageGroup + "대";
        } catch (Exception e) {
            return "나이 미상";
        }
    }
}
