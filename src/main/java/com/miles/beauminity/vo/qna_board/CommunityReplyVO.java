package com.miles.beauminity.vo.qna_board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommunityReplyVO {
    private Long replyId;
    private Long boardId;
    private Long parentsReplyId;
    private String username;
    private String replyContent;
    private boolean deleted;
    private String nickname;
}
