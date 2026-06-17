package com.miles.beauminity.vo.feed;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FeedVO {
    private Long feedId;
    private String username;
    private String feedContent;
    private int likeCnt;
    private int replyCnt;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private boolean deleted;
    private String savedName;
    private String filePath;

}
