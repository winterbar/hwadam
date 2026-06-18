package com.miles.beauminity.service.review_board;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.ReviewMapper;
import com.miles.beauminity.vo.MasterBoardFileVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    // 메퍼단에 작업을 요청하기 위한 변수 선언
    private final ReviewMapper reviewMapper; 
    
    @Override
    @Transactional(rollbackFor = Exception.class) // 여러 데이블의 데이터 정보 무결성을 위해 트랜잭션 어노테이션 필수 부여
    public void registerReviewPost(ReviewBoardVO vo) {  // 책임: 후기 게시판 등록 서비스 처리
        // 1 단계. 공용 게시판 테이블에 데이터 삽입
        // 이 메서드가 실행되고 나면, MyBatyis의 설정에 의해서
        // DB가 자동으로 생성해준 board_id(PK) 값이 vo 객체의 boardId 필드에 자동으로 채워진다...
        reviewMapper.insertMasterBoard(vo);

        

        // 2 단계. 후기 전용 테이블(review_board)에 데이터 삽입
        // 1단계를 거치면서 vo.getBoardId()로 자동 주입된 글 번호를 꺼낼 수 있게 된다..
        // 이 번호를 매퍼가 인식하여 review_board의 외래키(board_id)로 활용해 인서트한다...
        reviewMapper.insertReviewBoard(vo);

        // 3 단계. 다중 첨부파일 업로드 및 파일 데이블(board_file)에 데이터 삽입
        // 사용자가 화면에서 파일 항목을 등록했을 때만 작동하도록 코드 작성
        if (vo.getReviewFiles() != null && !vo.getReviewFiles().isEmpty()) {
            
            // 1. 물리 파일이 저장될 컴퓨터(서버) 내부의 경로 지정
            String uploadDir = "C:/upload/review/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 만약 컴퓨터에 해당 폴더가 없다면 자동으로 생성
            }

            // 2. 사용자가 올린 파일의 개수만큼 루프(반복문)를 돌린다.
            for (MultipartFile file : vo.getReviewFiles()) {
                // 선택창만 있고 실제 첨부된 파일이 없은 빈칸은 건너뛴다.
                if (file.isEmpty()) continue;

                // 클라이언트가 보낸 원래 파일명, 타입, 용량 획득
                String originalName = file.getOriginalFilename();
                String fileType = file.getContentType();
                long fileSize = file.getSize();
                
                // 3. 파일명 중복 방지를 위한 UUID 고유 식별자 결합 (예: UUID_공용.png)
                String ext = originalName.substring(originalName.lastIndexOf("."));
                String savedName = UUID.randomUUID().toString() + ext;

                try {
                    // 4. 실제로 컴퓨터 하드디스크 경로에 물리 파일을 영구 저장한다.
                    file.transferTo(new File(uploadDir + savedName));

                    // 5. 공용 파일 VO(MasterBoardFileVO)를 사용하여 자바 규격을 맞춘다.
                    MasterBoardFileVO fileVO = new MasterBoardFileVO();

                    // 종합 바구니(vo)에서 확보해 둔 부모 글번호(boardId)를 공용 파일 객체에 매핑합니다.
                    fileVO.setBoardId(vo.getBoardId());
                    fileVO.setOriginalName(originalName);
                    fileVO.setSavedName(savedName);
                    fileVO.setFilePath(uploadDir);
                    fileVO.setFileType(fileType);
                    fileVO.setFileSize((int) fileSize);

                    // 6. 세팅이 완료된 공용 파일 객체를 매퍼로 넘겨 board_file 테이블에 최종 저장한다.
                    reviewMapper.insertBoardFile(fileVO);

                } catch (IOException e) {
                    // 파일 저장 중 하드디스크 용량 부족이나 경로 에러 발생 시
                    // 의도적으로 강제 예외를 발생시켜 상단의 @Transactional이 작동(전체 롤백)하도록 유도
                    throw new RuntimeException("물리 파일 저장 오류로 인해 게시글 등록을 전면 취소합니다.", e);
                }
            }
        } 
    }

    @Override
    public List<ReviewBoardVO> getReviewBoardList() {

            return reviewMapper.selectReviewBoardList(); 
    }

}
