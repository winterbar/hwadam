package com.miles.beauminity.vo.login;

import lombok.Data;

@Data
public class MyPageFileVO {
    private String username;
    private String originalName;
    private String savedName;
    private String filePath;
    private String fileType;
    private String fileSize;
}
