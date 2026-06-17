package com.miles.beauminity.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 게시판 공용 VO
public class MasterBoardVO {
    private Long boardId;
    private String username;
    private String boardType;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private int viewCount;
    private int replyCount;
    private int likeCount;
    private boolean deleted;
}
