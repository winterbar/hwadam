package com.miles.beauminity.vo.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MasterBoardFileVO {
    private Long fileId;
    private Long boardId;
    private String originalName;
    private String savedName;
    private String filePath;
    private String fileType;
    private int fileSize;
}
