package com.miles.beauminity.vo.admin;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminMemberVO {
    private String username;
<<<<<<< HEAD
    private String gradeId = "lvl1";
=======
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
    private String password;
    private String nickname;
    private String name;
    private String birthday;
    private String gender;
    private String email;
    private String phone;
    private String skinType;
    private String personalColor;
    private int point;
    private LocalDateTime signedAt;
    private String role;
    private String status;
}
