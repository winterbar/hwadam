package com.miles.beauminity.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.miles.beauminity.service.admin.AdminMemberService;
import com.miles.beauminity.vo.admin.AdminMemberAnalysisVO;
import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequiredArgsConstructor
public class AdminPageController {
    
    private final AdminMemberService adminMemberService;

    // 관리자 페이지 요청 (대시보드)
    @GetMapping("/admin")
    public String getAdminHomePage() {
        return "admin/dashboard";
    }
    
    // 회원 관리 페이지 요청 (회원 조회)
    @GetMapping("/admin/members")
    public String getAdminMembersPage(@ModelAttribute AdminPageVO adminPageVO,
            @RequestParam(required = false, defaultValue = "search") String tab, Model model) {

        // 페이징
        long count = adminMemberService.countMembers(adminPageVO);
        adminPageVO.pageInfo(count);
        // 오름차순/내림차순 정렬 설정 (정렬 기준이 있을 때만 설정)
        if(adminPageVO.getSort() != null) {
            // 사용자가 asc, desc가 아닌 값을 임의로 url에 적어 넣을 경우를 방지
            if(!"asc".equals(adminPageVO.getDir()) && !"desc".equals(adminPageVO.getDir())) {
                adminPageVO.setDir("desc");
            }
        }
        // 회원 목록
        List<AdminMemberVO> members = adminMemberService.searchMembers(adminPageVO);
        model.addAttribute("tab", tab);
        model.addAttribute("members", members);
        model.addAttribute("page", adminPageVO);
        return "admin/members";
    }

    // 회원 관리 페이지 요청 (회원 통계)
    @PostMapping("/admin/members")
    public String getAdminMemberPage(@ModelAttribute AdminPageVO adminPageVO,
        @ModelAttribute AdminMemberAnalysisVO adminMemberAnalysisVO,
        @RequestParam(required = false, defaultValue = "analysis") String tab, Model model) {
        
        System.out.println(adminPageVO.toString());
        System.out.println(adminMemberAnalysisVO.toString());
        
        return "admin/members";
    }
    
    
    // 게시글 관리 페이지 요청
    @GetMapping("/admin/posts")
    public String getAdminPostsPage() {
        return "admin/posts";
    }

    // 피드 관리 페이지 요청
    @GetMapping("/admin/feeds")
    public String getAdminFeedsPage() {
        return "admin/feeds";
    }

    // 댓글 관리 페이지 요청
    @GetMapping("/admin/replies")
    public String getAdminRepliesPage() {
        return "admin/replies";
    }

    // 스토어 관리 페이지 요청
    @GetMapping("/admin/stores")
    public String getAdminStoresPage() {
        return "admin/stores";
    }

    // 관리 로그 페이지 요청
    @GetMapping("/admin/logs")
    public String getAdminLogsPage() {
        return "admin/logs";
    }
    
}
