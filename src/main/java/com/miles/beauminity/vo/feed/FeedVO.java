package com.miles.beauminity.vo.feed;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FeedVO {
    private Long feedId;
    private String memberId;
    private String feedContent;
    private int likeCnt;
    private int replyCnt;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private boolean deleted;

}
