package com.miles.beauminity.vo.admin;

import java.util.List;

import lombok.Data;

@Data
public class AdminMemberBatchVO {
    private List<String> usernames; // 변경할 회원들 아이디
    private String modifyType; // 일괄 변경할 작업
    private String value; // 변경할 값
    private int point; // 포인트 칼럼 전용 변경할 값
}
