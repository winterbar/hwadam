package com.miles.beauminity.vo.feed;

import lombok.Data;

@Data
public class FeedFileVO {
    private Long fileId;
    private Long feedId;
    private String originalName;
    private String savedName;
    private String filePath;
    private String fileType;
    private int fileSize;
}
