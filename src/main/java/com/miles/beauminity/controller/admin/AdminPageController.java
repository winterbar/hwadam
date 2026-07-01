package com.miles.beauminity.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminPageController {
    
    // 관리자 페이지 요청 (대시보드)
    @GetMapping("/admin")
    public String getAdminHomePage() {
        return "admin/dashboard";
    }
    
    // 회원 관리 페이지 요청
    @GetMapping("/admin/members")
    public String getAdminMembersPage() {
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
