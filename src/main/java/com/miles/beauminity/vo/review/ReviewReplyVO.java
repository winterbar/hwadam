package com.miles.beauminity.vo.review;

import lombok.Data;

@Data
public class ReviewReplyVO {
    private Long replyId;
    private Long boardId;
    private Long parentsReplyId;
    private String replyContent;
    private String userName;
    private boolean deleted;

    private String nickName;
  
}
