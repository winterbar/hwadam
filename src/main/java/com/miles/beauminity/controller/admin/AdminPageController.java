package com.miles.beauminity.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.miles.beauminity.service.admin.AdminMemberService;
import com.miles.beauminity.vo.admin.AdminMemberConditionVO;
import com.miles.beauminity.vo.admin.AdminMemberStatsVO;
import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminPageController {
    
    private final AdminMemberService adminMemberService;

    // 관리자 페이지 요청 (대시보드)
    @GetMapping("/admin")
    public String getAdminHomePage() {
        return "admin/dashboard";
    }
    
    // 회원 관리 페이지 요청
    @GetMapping("/admin/members")
    public String getAdminMembersPage(@ModelAttribute AdminPageVO adminPageVO,
            @ModelAttribute AdminMemberConditionVO adminMemberConditionVO,
            @RequestParam(required = false, defaultValue = "search") String tab, Model model) {
        
        List<AdminMemberVO> members; // 회원 정보
        AdminMemberStatsVO stats; // 회원 정보에 대한 통계

        // 다중 필터링 검색 (회원 통계)
        if (adminMemberConditionVO.hasSearchCondition()) { // 다중 필터링이 하나라도 걸려있는지 확인
            // 페이징 설정
            long count = adminMemberService.countAnalysisMember(adminMemberConditionVO);
            adminPageVO.pageInfo(count);
            // 정렬 설정
            if(adminPageVO.getSort() != null) { // 정렬 기준이 있을 때만 설정
                // 사용자가 asc, desc가 아닌 값을 임의로 url에 적어 넣을 경우를 방지
                if(!"asc".equals(adminPageVO.getDir()) && !"desc".equals(adminPageVO.getDir())) {
                    adminPageVO.setDir("desc");
                }
            }
            members = adminMemberService.analysisMembers(adminMemberConditionVO, adminPageVO);
            stats = adminMemberService.statsMembers(adminMemberConditionVO);
            model.addAttribute("stats", stats);
            // 단일 필터링 검색 (회원 조회)
        } else {
            // 페이징 설정
            long count = adminMemberService.countMembers(adminPageVO);
            adminPageVO.pageInfo(count);
            // 정렬 설정
            if(adminPageVO.getSort() != null) {
                // 정렬 기준이 비어있다면 null로 치환
                if(adminPageVO.getSort().trim().isEmpty()) {
                    adminPageVO.setSort(null);
                }
                // 사용자가 asc, desc가 아닌 값을 임의로 url에 적어 넣을 경우를 방지
                if(!"asc".equals(adminPageVO.getDir()) && !"desc".equals(adminPageVO.getDir())) {
                    adminPageVO.setDir("desc");
                }
            }
            members = adminMemberService.searchMembers(adminPageVO);
        }
        model.addAttribute("tab", tab);
        model.addAttribute("members", members);
        model.addAttribute("page", adminPageVO);
        adminMemberConditionVO.convertForCheckBox(); // 체크박스의 코드와 라벨, 체크여부 설정
        model.addAttribute("analysis", adminMemberConditionVO);
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
