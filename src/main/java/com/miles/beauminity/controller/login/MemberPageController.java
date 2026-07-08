package com.miles.beauminity.controller.login;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.miles.beauminity.security.CustomUserDetails;
import com.miles.beauminity.service.login.MemberService;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;
import com.miles.beauminity.vo.review.ReviewBoardVO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class MemberPageController {

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
        List<FeedVO> feedList = memberService.getFeedList(loginMember.getUsername());
        List<QnaBoardCompleteVO> communityList = memberService.getCommunityList(loginMember.getUsername());
        List<ReviewBoardVO> reviewList = memberService.getReviewList(loginMember.getUsername());
        model.addAttribute("feedList",feedList);
        model.addAttribute("communityList", communityList);
        model.addAttribute("reviewList", reviewList);
        return "mypage/info";
    }
    
    // 회원정보 수정 페이지 요청
    @GetMapping("/mypage/edit/info")
    public String getMyInfoEditPage(@AuthenticationPrincipal CustomUserDetails loginMember,
                                    Model model) {
        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("memberInfo",
            memberService.getMemberInfo(loginMember.getUsername())
        );
        return "mypage/edit";
    }

    // 비밀번호 변경 페이지 요청
    @GetMapping("/mypage/edit/pw")
    public String getPasswordEditPage() {
        return "mypage/editpw";
    }

    // 회원 탈퇴 페이지 요청
    @GetMapping("/mypage/withdraw")
    public String getWithdrawPage() {
        return "mypage/withdraw";
    }

    
    

}
