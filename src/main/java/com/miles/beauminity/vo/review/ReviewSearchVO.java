package com.miles.beauminity.vo.review;

import lombok.Data;

@Data
public class ReviewSearchVO {
    private String category1;
    private String category2;
    private String sortType = "latest"; // 기본값 최신순 고정
}
