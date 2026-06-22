package com.miles.beauminity.vo.board;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
// 게시판 공용 VO
public class MasterBoardVO {
    private Long boardId;
    private String username;
    private String boardType;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private int viewCnt;
    private int replyCnt;
    private int likeCnt;
    private boolean deleted;
}
