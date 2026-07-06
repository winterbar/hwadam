package com.miles.beauminity.vo.admin;

import lombok.Data;

@Data
public class AdminPageVO {
    private int page = 1; // 현재 페이지
    private int size = 10; // 한 페이지 게시글 수
    private long totalCount; // 전체 게시글 수
    private int block = 10; // 화면에 보여줄 페이지 수
    private int startPage; // 시작 페이지
    private int endPage; // 끝 페이지

    private boolean prev; // 이전 페이지 여부
    private boolean next; // 다음 페이지 여부

    private int totalPage; // 전체 페이지 수
    private int offset; // limit 시작 위치

    private String keyword; // 검색어
    private String searchType; // 검색 타입
    private String sort; // 정렬 기준
    private String dir = "asc"; // 정렬 방향
    // 기본은 내림차순이라 desc지만, 컨트롤러에서 변경되어 이를 고려해 asc로 둠
    
    // limit 시작 위치 계산
    private int getOffset() {
        offset = (page - 1) * size;
        return offset;
    }

    // 페이징 계산
    public void pageInfo(long totalCount) {
        this.totalCount = totalCount; // 총 게시글 수
        this.totalPage = (int)Math.ceil((double) totalCount / size); // 전체 페이지 수
        this.endPage = (int)Math.ceil((double) page / block) * block; // 끝 페이지
        this.startPage = endPage - block + 1; // 시작 페이지
        // 계산된 마지막 페이지보다
        // 끝 페이지가 크면 끝페이지를 마지막 페이지로 수정
        if(endPage > totalPage) { 
            endPage = totalPage;
        }
        this.prev = startPage > 1;
        this.next = endPage < totalPage;
    }
}
