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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.service.login.MemberService;
import com.miles.beauminity.vo.login.FeedbackVO;
import com.miles.beauminity.vo.login.MemberVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 정보 수정 요청
    @PostMapping("/mypage/edit/info")
    public String editMemberInfo(@ModelAttribute MemberVO memberVO,
            RedirectAttributes rttr) {
        boolean isUpdated = memberService.updateMember(memberVO);
        if(isUpdated) {
            rttr.addFlashAttribute("msg", "회원정보가 성공적으로 수정되었습니다.");
        }
        return "redirect:/mypage";
    }
    
    // 비밀번호 변경 요청
    @PostMapping("/mypage/edit/pw")
    public String editPassword(@ModelAttribute MemberVO memberVO,
            @AuthenticationPrincipal CustomUserDetails loginMember,
            RedirectAttributes rttr) {
        memberVO.setUsername(loginMember.getUsername());
        boolean isUpdated = memberService.updatePassword(memberVO);
        if(isUpdated) {
            rttr.addFlashAttribute("msg", "비밀번호가 성공적으로 변경되었습니다.");
        }
        return "redirect:/mypage";
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

    // 회원탈퇴 요청
    @PostMapping("/mypage/withdraw")
    public String withdrawMember(@ModelAttribute FeedbackVO feedbackVO,
                                @RequestParam("password") String password,
                                @AuthenticationPrincipal CustomUserDetails loginMember,
                                RedirectAttributes rttr,
                                HttpServletRequest request) throws ServletException{
        
        // 여기에 비밀번호 유효성 검사랑 피드백 테이블 튜플 삽입
        // 그리고 각 테이블 deleted 업데이트, username을 #deleted로 바꾸기
        boolean isChecked = memberService.findPassword(loginMember.getUsername(), password);
        if(isChecked) {
            memberService.withdraw(loginMember.getUsername(),feedbackVO);
            request.logout();            
            return "mypage/withdraw_complete";
        }else {
            rttr.addFlashAttribute("isChecked",false);
            return "redirect:/mypage/withdraw";
        }
        
    }
    
    // 아이디 중복 체크 요청
    @GetMapping("/chkid-dup/{username}")
    @ResponseBody
    public boolean checkIdDuplicate(@PathVariable("username") String username) {
        boolean isDuplicate = memberService.findMember(username);
        return isDuplicate;
    }

    // 비밀번호 확인 요청
    @PostMapping("/mypage/chkpw")
    public String checkPassword(@RequestParam("password") String password,
            @RequestParam(value = "path") String path,
            @AuthenticationPrincipal CustomUserDetails loginMember,
            RedirectAttributes rttr) {
    
        // 비밀번호 일치 여부 확인
        boolean isChecked = memberService.findPassword(loginMember.getUsername(), password);
        if(isChecked) {
            return "redirect:" + path;
        } else {
            rttr.addAttribute("error", true);
            rttr.addAttribute("path", path);
            return "redirect:/mypage";
        }
    }
}