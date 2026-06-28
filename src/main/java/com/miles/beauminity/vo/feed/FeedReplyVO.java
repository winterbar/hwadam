package com.miles.beauminity.vo.feed;

import lombok.Data;

@Data
public class FeedReplyVO {
    private Long replyId;
    private Long feedId;
    private String username;
    private Long parentsReplyId;
    private String replyContent;
    private int likeCnt;
    private Integer deleted;
    private String nickname;
    private int replyCnt;
    private String parentNickname;
    private String profileImage;
private String profilePath;
}
