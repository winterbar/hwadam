package com.miles.beauminity.vo.login;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmailAuthCodeVO {
    private String code;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();

    // 생성된 인증번호가 만료되었는지 확인 (5분)
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(5));
    }
}
