package com.miles.beauminity.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {
    
    // 로그인 페이지 요청
    @GetMapping("/login")
    public String getLoginPage() {
        return "login/login";
    }

}
