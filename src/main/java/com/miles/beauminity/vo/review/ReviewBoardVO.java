package com.miles.beauminity.vo.review;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewBoardVO {
    private Long boardId;
    private String userName; // 회원의 아이디
    private String grade_id;
    private String nickName;  // 회원의 닉네임
    private String title;                       // 후기 등록 폼은 리뷰 제목
    private String content;                     // 후기 등록 폼은 리뷰 내용

    // 후기 전용 데이터 (review_board에 넣게 위한 변수들)
    private String productName;                 // 후기 등록 폼은 선택된 리뷰 대상
    private String productLink;                 // 선택된 제품 링크
    private String productImage;                // 선택된 제품 이미지
    private String category1;                   // 대분류
    private String category2;                   // 소분류

    // 다수의 파일 수신용
    private List<MultipartFile> reviewFiles;       // 이미지 첨부 파일
}
