package com.miles.beauminity.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.miles.beauminity.vo.login.EmailAuthCodeVO;

// 이메일 주소 및 인증 번호, 인증번호 생성시간을 저장하는 임시 저장소
@Component
public class VerificationStorage {
    // 멀티 스레드 환경을 고려하여 ConcurrentHashMap을 사용
    // 내부적으로 데이터 분할 잠금이란 기술로 사용자1이 이메일 데이터를 장부에 적고 있을 때
    // 사용자2가 본인 이메일을 적으러 와도 서로 다른 칸을 건드리기에
    // 기다리지 않고 안전하고 빠르게 동시 처리가 가능해진다.
    private final Map<String, EmailAuthCodeVO> storage =
        new ConcurrentHashMap<>();

    // 이메일과 인증번호, 인증번호 생성시간을 저장
    public void putVerificationStorage(String email, String type,
                                        EmailAuthCodeVO emailAuthCodeVO) {
        emailAuthCodeVO.setType(type);
        storage.put(email, emailAuthCodeVO);
    }

    // 이메일에 대한 인증번호, 인증번호 생성시간을 가져옴
    public EmailAuthCodeVO getVerificationStorage(String email) {
        return storage.get(email);
    }

    // 이메일에 대한 인증번호, 인증번호 생성시간을 삭제
    public void removeVerificationStorage(String email) {
        storage.remove(email);
    }
}
