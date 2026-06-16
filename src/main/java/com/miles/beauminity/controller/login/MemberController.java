package com.miles.beauminity.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MemberController {
    @GetMapping("/register")
    public String getRegisterPage() {
        return "login/register";
    }
    
}
