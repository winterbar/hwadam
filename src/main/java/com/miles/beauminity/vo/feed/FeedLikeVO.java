package com.miles.beauminity.vo.feed;

import lombok.Data;

@Data
public class FeedLikeVO {
    private long likeId;
    private long feedId;
    private String username;
}
