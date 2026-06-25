package com.miles.beauminity.vo.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MasterBoardLikeVO {
    private Long likeId;
    private Long boardId;
    private String username;
}
