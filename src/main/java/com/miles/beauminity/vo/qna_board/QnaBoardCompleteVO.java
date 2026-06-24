package com.miles.beauminity.vo.qna_board;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QnaBoardCompleteVO {
    private Long boardId;
    private String nickname;
    private String boardType;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private int viewCnt;
    private int replyCnt;
    private int likeCnt;
    private boolean deleted;
    private String category;
    
}
