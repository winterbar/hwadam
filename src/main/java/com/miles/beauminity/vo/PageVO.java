package com.miles.beauminity.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageVO {
    private int page=1; //클라이언트가 요청한 페이지
    private int size=10; // 가져올 글의 수
    private int totalCount; // 게시판의 전체글의 수

    private int offset; // 가져올 글의 시작위치 
    private int totalPage; //전체페이지 수
    private int pageBlock=3;

    // 그룹의 시작과 끝 변수

    private int startPage;
    private int endPage;
    
    private boolean prev;
    private boolean next;

    public int getOffset(){
        offset= (page-1)*size;
        return offset;
    }

    public void pageInfo(int totalCount){
        this.totalCount=totalCount;
        this.totalPage=(int)Math.ceil((double)totalCount/size);
        this.endPage=(int)Math.ceil((double)page/pageBlock)* pageBlock;
        this.startPage=endPage - pageBlock+1;

        // 현재 125건
        // 그러면 페이지 총 13개
        // 이것을 블록으로 표시
        // 

        if(endPage > totalPage){
            endPage=totalPage;
        }
        this.prev=startPage>1;
        this.next=endPage<totalPage;
    }

}
