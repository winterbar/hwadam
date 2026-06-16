package com.miles.beauminity.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MemberController {
    @GetMapping("/register")
    public String getRegisterPage() {
        return "login/register";
    }


    @GetMapping("/chkid-dup/{username}")
    @ResponseBody
    public boolean checkIdDuplicate(@PathVariable("username") String username) {
        boolean isDuplicate = false;
        return isDuplicate;
    }
    
    
}
