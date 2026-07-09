package com.miles.beauminity.service.login;

import com.miles.beauminity.storage.VerificationStorage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.miles.beauminity.vo.login.EmailAuthCodeVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final VerificationStorage verificationStorage;
    private final JavaMailSender javaMailSender; // 메일 발송 객체

    // 6자리 난수 인증번호 생성
    private String createCode() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(1000000));
    }

    // 인증 이메일 방송 및 발송된 인증번호 반환
    @Override
    public void sendVerificationEmail(String email) throws MessagingException {
        String verificationCode = createCode(); // 인증번호 6자리 생성

        // 발송할 빈 메일 상자 생성
        MimeMessage message = javaMailSender.createMimeMessage();
        // 메일 내용물을 채우기 쉽게 헬퍼를 사용. true는 html 형식 지원, utf-8은 한글 인코딩
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email); // 메일을 받을 수신자의 이메일 주소 설정
        helper.setSubject("[화담] 이메일 인증번호 안내"); // 메일 제목 설정

        // 메일 본문 내용 설정
        String content = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                <meta charset="UTF-8">
                <title>화담 이메일 인증</title>
                </head>

                <body style="margin:0;padding:40px 0;background:#f5f5f5;font-family:Pretendard,Arial,sans-serif;">

                <table align="center" cellpadding="0" cellspacing="0" width="600"
                    style="background:#ffffff;border-radius:12px;padding:20px;border:1px solid #eeeeee;">

                    <!-- Logo -->
                    <tr>
                        <td align="center" style="padding-bottom:20px;">
                            <img src="cid:logoImage"
                                alt="HWADAM"
                                style="width:190px;">
                        </td>
                    </tr>

                    <tr>
                        <td align="center"
                            style="font-size:28px;
                                font-weight:700;
                                color:#222;
                                padding-bottom:12px;">
                            이메일 인증번호 안내
                        </td>
                    </tr>

                    <tr>
                        <td align="center"
                            style="font-size:15px;
                                color:#666;
                                line-height:1.8;
                                padding-bottom:30px;">

                            화담 아이디/비밀번호를 찾기 위해서는 인증이 필요합니다.<br>
                            이메일 인증을 위해 아래의 인증번호를 복사해주세요.<br>
                            해당 인증번호는 발송된 시점으로부터 <b>5분간</b> 유효합니다.
                        </td>
                    </tr>

                    <tr>
                        <td align="center">

                            <div style="
                                display:inline-block;
                                background:#FFF5F8;
                                border:2px solid #FFA1C1;
                                border-radius:10px;
                                padding:18px 45px;
                                font-size:34px;
                                font-weight:700;
                                color:#FFA1C1;
                                letter-spacing:8px;
                            ">
                                %s
                            </div>

                        </td>
                    </tr>

                    <tr>
                        <td align="center"
                            style="
                            padding-top:35px;
                            font-size:14px;
                            color:#888;
                            line-height:1.8;">

                            인증번호를 요청하지 않으셨다면 이 메일은 무시하셔도 됩니다.<br>
                            누군가가 귀하의 이메일 주소를 잘못 입력한 걸수도 있습니다.<br>
                            감사합니다.
                        </td>
                    </tr>

                    <tr>
                        <td style="padding:15px 0;">
                            <hr style="border:none;border-top:1px solid #eeeeee;">
                        </td>
                    </tr>

                    <tr>
                        <td align="center"
                            style="
                            font-size:13px;
                            color:#aaaaaa;
                            line-height:1.8;">

                            본 메일은 발신전용 메일로 본 메일로 회신하실 경우 답변이 되지 않습니다.<br>
                            서비스 이용 안내메일로 수신동의 여부와 관계없이 발송되었습니다.
                        </td>
                    </tr>

                </table>

                </body>
                </html>
                """.formatted(verificationCode);

        // 본문 내용을 실제 메일 내용으로 적용
        // 2번째 인자를 true로 설정해야 html 태그가 실제 디자인으로 렌더링되어 발송됨
        helper.setText(content, true);

        // 메일 내용 자체에 로고 이미지를 포함시켜 출력될 수 있도록 설정
        helper.addInline("logoImage",
            new ClassPathResource("static/images/common/title.png"));
        
        // 설정 완료된 메일을 실제로 발송
        javaMailSender.send(message);

        // 이메일, 인증번호, 인증번호 시간을 임시 저장소에 저장
        EmailAuthCodeVO emailAuthCodeVO = new EmailAuthCodeVO();
        emailAuthCodeVO.setCode(verificationCode);
        verificationStorage.putVerificationStorage(email, emailAuthCodeVO);
    }


    @Override
    public boolean verifyCode(String email, String userInputCode) {
        // 저장된 이메일 인증번호 및 인증번호 생성시간
        EmailAuthCodeVO emailAuthCodeVO = verificationStorage.getVerificationStorage(email);
        
        // 인증번호를 제대로 입력했는지 확인
        if(userInputCode == null) {
            return false;
        }
        // 인증번호가 만료되었는지 확인
        if (emailAuthCodeVO.isExpired()) {
            verificationStorage.removeVerificationStorage(email); // 만료되었다면 스토리지에서 삭제
            return false;
        }
        // 인증번호가 일치하는지 확인
        if (emailAuthCodeVO.getCode().equals(userInputCode)) {
            verificationStorage.removeVerificationStorage(email); // 인증 성공 시 삭제
            return true;
        }
        
        return false;
    }

    
    
}
