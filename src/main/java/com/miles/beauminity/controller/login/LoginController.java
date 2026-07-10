package com.miles.beauminity.controller.login;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miles.beauminity.service.login.EmailService;
import com.miles.beauminity.service.login.MemberService;
import com.miles.beauminity.storage.VerificationStorage;
import com.miles.beauminity.vo.login.EmailAuthCodeVO;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final EmailService emailService;
    private final MemberService memberService;
    
    // 로그인 페이지 요청
    @GetMapping("/login")
    public String getLoginPage() {
        return "login/login";
    }
    
    // 계정 찾기용 인증 메일 발송
    @PostMapping("/find/send-email")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendEmail(@RequestParam("email") String email) {
        
        // 가입된 이메일이 있는지 확인하고 없으면 인증번호 메일을 전송하지 않음
        // 단, 이메일 존재 여부는 클라이언트에게 보안상 전달하지 않음
        if(!memberService.findEmail(email)) {
            return ResponseEntity.ok(Map.of(
               "message", "성공적으로 인증번호가 발송되었습니다." 
            ));
        }
        // 가입된 이메일이 있다면 인증번호 메일을 전송
        try {
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok(Map.of(
                "message", "성공적으로 인증번호가 발송되었습니다."
            ));
        } catch(MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "인증번호 전송에 실패했습니다. 다시 시도해주세요."
            ));
        }
    }

    @PostMapping("/find/verify-email")
    public String verifyEmail(@RequestParam("email") String email,
                              @RequestParam("code") String userInputCode) {
        
        // 데이터 잘 들어오는 거 확인 완료
        boolean verified = emailService.verifyCode(email, userInputCode);

        if(verified) {
            System.out.println("성공");
            // 인증번호가 일치하면 아이디 정보 memberService로 가져오기
        } else {
            System.out.println("실패");
            // 인증번호가 불일치하면 어떻게 할지 생각하기
            // 1. 페이지를 새롭게 띄운다.
            // 2. 데이터만 보낸다.
        }
                
        return "login/login";
    }
    
    

}
