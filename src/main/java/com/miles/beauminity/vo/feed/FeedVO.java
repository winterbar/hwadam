package com.miles.beauminity.vo.feed;

import java.time.LocalDateTime;
import java.util.List;

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
    private String tagName;
    private String infoLink;
    private List<String> feedTagList;
    private List<String> feedFileList;
    private List<String> feedReplyList;
    private String skinType;
    private String personalColor;
    private String nickname;
    private boolean liked;

}
