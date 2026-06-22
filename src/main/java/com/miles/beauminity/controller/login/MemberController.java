package com.miles.beauminity.controller.login;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.service.login.MemberService;
import com.miles.beauminity.vo.login.MemberVO;

import lombok.RequiredArgsConstructor;

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
            memberService.getMemberInfo(loginMember.getUsername())
        );
        return "mypage/info";
    }

    @GetMapping("/mypage/edit")
    public String getMyPageEdit(@AuthenticationPrincipal CustomUserDetails loginMember, Model model) {
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("memberInfo",
            memberService.getMemberInfo(loginMember.getUsername())
        );
        return "mypage/edit";
    }

    // 프로필 사진 수정 요청
    @PostMapping("/mypage/edit/photo")
    public String editProfilePhoto(@AuthenticationPrincipal CustomUserDetails loginMember,
                                @RequestParam("profile") MultipartFile file) {
        if(file == null || file.getSize() <= 0) {
            return "redirect:/mypage";
        }
        memberService.updateMemberProfile(loginMember, file);
        return "redirect:/mypage";
    }

    // 프로필 사진 초기화 요청
    @GetMapping("/mypage/reset/photo")
    public String resetProfilePhoto(@AuthenticationPrincipal CustomUserDetails loginMember) {
        memberService.resetMemberProfile(loginMember.getUsername());
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