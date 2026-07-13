package com.miles.beauminity.vo.review;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.board.MasterBoardFileVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewBoardVO {
    private Long boardId;
    private String userName; // 회원의 아이디
    private String gradeId;
    private String nickName;  // 회원의 닉네임
    private String title;                       // 후기 등록 폼은 리뷰 제목
    private String content;                     // 후기 등록 폼은 리뷰 내용
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    private boolean deleted;
    private String category;

    private long viewCnt;
    private long replyCnt;

    // DB에서 JOIN으로 새로 조회해오는 출력용 필드
    private String memberProfilePath; // 작성자 프로필 이미지 경로 수신용
    private String savedName;         // 리뷰 메인에 띄울 C드라이브 저장 파일명 수신용 (사용자가 업로드한 파일경로 수신용)
    private String memberProfileSavedName; // 리뷰 메인에 띄울 C드라이브 저장 파일명 수신용 (사용자의 프로필 사진 파일경로 수신용)
    private String skinType;
    private String personalColor;
    private LocalDateTime birthday;

    private String ageGroup;


    // 후기 전용 데이터 (review_board에 넣게 위한 변수들)
    private String productName;                 // 후기 등록 폼은 선택된 리뷰 대상
    private String productLink;                 // 선택된 제품 링크
    private String productImage;                // 선택된 제품 이미지
    private String category1;                   // 대분류
    private String category2;                   // 소분류

    // 다수의 파일 수신용 (DB 입력용)
    private List<MultipartFile> reviewFiles;       // 이미지 첨부 파일  html name="reviewFiles"와 매칭

    // 수정 시 화면에서 넘어올 기존 파일 Id 목록 수신용     html name="existingFileIds"와 매칭
    private List<Long> existingFileIds;

    // 컨트롤러에 넘길 파일 목록 (화면 출력용)
    private List<MasterBoardFileVO> attachedFiles;
}
