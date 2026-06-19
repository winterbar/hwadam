package com.miles.beauminity.vo.login;

import lombok.Data;

@Data
public class MyPageVO {
    private MemberVO member;
    private String gradeName;
    private long reviewCnt;
    private long qnaCnt;
    private long infoshareCnt;
    private long feedCnt;
}
