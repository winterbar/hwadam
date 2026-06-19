package com.miles.beauminity.controller.login;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.service.login.MemberService;
import com.miles.beauminity.vo.login.MemberVO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 페이지 요청
    @GetMapping("/register")
    public String getRegisterPage() {
        return "login/register";
    }

    // 아이디/비밀번호 찾기 페이지 요청
    @GetMapping("/find")
    public String getFindPage() {
        return "login/find";
    }

    // 마이페이지 요청
    @GetMapping("/mypage")
    public String getMyPage(@AuthenticationPrincipal CustomUserDetails loginMember, Model model) {
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("memberInfo",
                            memberService.getMemberInfo(loginMember.getUsername()));
        System.out.println(memberService.getMemberInfo(loginMember.getUsername()).toString());
        return "login/mypage";
    }

    // 회원정보 수정 요청 (프로필 사진)
    @PostMapping("/mypage/edit/photo")
    public String editProfilePhoto() {
        
        return "redirect:/mypage";
    }
    

    // 회원가입 요청
    @PostMapping("/register")
    public String registerMember(@ModelAttribute MemberVO memberVO, Model model) {
        memberService.registerMember(memberVO);
        model.addAttribute("username", memberVO.getUsername());
        model.addAttribute("name", memberVO.getName());
        return "login/register-success";
    }
    
    // 아이디 중복 체크 요청
    @GetMapping("/chkid-dup/{username}")
    @ResponseBody
    public boolean checkIdDuplicate(@PathVariable("username") String username) {
        boolean isDuplicate = memberService.findMember(username);
        return isDuplicate;
    }

}