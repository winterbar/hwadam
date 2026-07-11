package com.miles.beauminity.controller.login;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miles.beauminity.service.login.EmailService;
import com.miles.beauminity.service.login.MemberService;
import com.miles.beauminity.vo.login.MemberVO;

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
    
    // 아이디 찾기용 인증 메일 발송
    @PostMapping("/find/id/send-email")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendEmailFindId(@RequestParam("email") String email) {
        
        // 입력된 이메일로 가입된 계정이 있는지 확인하고
        // 없으면 인증번호 메일을 전송하지 않음
        // 단, 이메일 존재 여부는 클라이언트에게 보안상 전달하지 않음
        if(!memberService.findEmail(email)) {
            return ResponseEntity.ok(Map.of(
               "message", "성공적으로 인증번호가 발송되었습니다." 
            ));
        }
        // 가입된 이메일이 있다면 인증번호 메일을 전송
        try {
            emailService.sendVerificationEmail(email, "id");
            return ResponseEntity.ok(Map.of(
                "message", "성공적으로 인증번호가 발송되었습니다."
            ));
        } catch(MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "인증번호 전송에 실패했습니다. 다시 시도해주세요."
            ));
        }
    }

    // 
    @PostMapping("/find/id/verify-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyEmailFindId(@RequestParam("email") String email,
                              @RequestParam("code") String userInputCode) {
        
        // 데이터 잘 들어오는 거 확인 완료
        boolean verified = emailService.verifyCode(email, "id", userInputCode);

        if(verified) {
            List<MemberVO> members = memberService.findSignedMembers(email);
            return ResponseEntity.ok(Map.of(
                "message", "인증한 이메일로 가입하신 아이디 목록입니다.",
                "members", members
            ));
        } else {
            return ResponseEntity.status(400).body(Map.of(
                "message", "인증번호가 일치하지 않습니다. 다시 시도해주세요."
            ));
        }
    }
    
    @PostMapping("/find/pw/send-email")
    public ResponseEntity<Map<String, String>> sendEmailFindPw(
        @RequestParam("username") String username, @RequestParam("email") String email) {

        // 입력된 아이디와 이메일로 가입된 계정이 있는지 확인하고
        // 없으면 인증번호 메일을 전송하지 않음
        // 단, 이메일 존재 여부는 클라이언트에게 보안상 전달하지 않음
        if(!memberService.findEmailAndId(username, email)) {
            return ResponseEntity.ok(Map.of(
               "message", "성공적으로 인증번호가 발송되었습니다." 
            ));
        }
        try {
            emailService.sendVerificationEmail(email, "pw");
            return ResponseEntity.ok(Map.of(
                "message", "성공적으로 인증번호가 발송되었습니다."
            ));
        } catch(MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "인증번호 전송에 실패했습니다. 다시 시도해주세요."
            ));
        }
    }

    @PostMapping("/find/pw/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmailFindPw(
        @RequestParam("username") String username, @RequestParam("email") String email,
                                @RequestParam("code") String userInputCode) {
        
        boolean verified = emailService.verifyCode(email, "pw", userInputCode);

        if(verified) {
            // 10자리의 무작위 비밀번호 생성
            String newPassword = emailService.createPassword();
            try {
                String isSent;
                emailService.sendNoticeEmail(email, newPassword);
                MemberVO member = new MemberVO();
                member.setUsername(username);
                member.setPassword(newPassword);
                if(memberService.updatePassword(member)) {
                    isSent = "success";
                } else {
                    isSent = "fail";
                }
                return ResponseEntity.ok().body(Map.of(
                    "message", "변경된 임시 비밀번호가 회원님의 메일로 전송되었습니다.",
                    "isSent", isSent
                ));
            } catch(MessagingException e) {
                return ResponseEntity.status(500).body(Map.of(
                    "message", "메일 전송에 실패했습니다. 다시 시도해주세요."
                ));
            }
        } else {
            return ResponseEntity.status(400).body(Map.of(
                "message", "인증번호가 일치하지 않습니다. 다시 시도해주세요."
            ));
        }
    }
    
    
    

}
