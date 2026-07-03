package com.miles.beauminity.controller.admin;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miles.beauminity.service.admin.AdminMemberService;
import com.miles.beauminity.vo.admin.AdminMemberVO;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class AdminMemberController {
    
    private final AdminMemberService adminMemberService;

    // 신규 회원 등록 요청 (관리자)
    @PostMapping("/admin/members/register")
    public String registerMember(@ModelAttribute AdminMemberVO adminMemberVO) {
        adminMemberService.registerMember(adminMemberVO);
        return "redirect:/admin/members?page=1&keyword=";
    }

    // 회원 정보 요청 (관리자)
    @GetMapping("/admin/members/{username}")
    @ResponseBody
    public AdminMemberVO getMemberInfo(@PathVariable("username") String username) {
        return adminMemberService.getMemberDetails(username);
    }
    
    // 회원 수정 요청 (관리자)
    @PostMapping("/admin/members/modify")
    public String getMethodName(@ModelAttribute AdminMemberVO adminMemberVO) {
        adminMemberService.modifyMemberInfo(adminMemberVO);
        return "redirect:/admin/members?page=1&keyword=";
    }
    
}
