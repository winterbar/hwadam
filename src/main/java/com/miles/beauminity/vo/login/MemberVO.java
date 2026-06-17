package com.miles.beauminity.vo.login;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MemberVO {
    private String username;
    private String grade_id = "lvl1";
    private String password;
    private String nickname;
    private String name;
    private String brithday;
    private String gender;
    private String email;
    private String phone;
    private String skinType;
    private String personalColor;
    private int point;
    private LocalDateTime signedDate;
}
