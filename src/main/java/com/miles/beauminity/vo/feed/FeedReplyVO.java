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
    private boolean deleted;
    private String nickname;
    private int replyCnt;
}
