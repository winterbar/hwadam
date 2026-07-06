package com.miles.beauminity.controller.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miles.beauminity.service.admin.AdminMemberService;
import com.miles.beauminity.vo.admin.AdminMemberBatchVO;
import com.miles.beauminity.vo.admin.AdminMemberVO;

import lombok.RequiredArgsConstructor;


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
    
    // 한 명의 회원 정보 수정 요청 (관리자)
    @PostMapping("/admin/members/modify")
    public String modifyMemberInfo(@ModelAttribute AdminMemberVO adminMemberVO) {
        adminMemberService.modifyMemberInfo(adminMemberVO);
        return "redirect:/admin/members?page=1&keyword=";
    }

    // 여러 명의 회원 정보 일괄 수정 요청 (관리자)
    // JSON 내용을 받기 위해서 @RequestBody 어노테이션 사용
    @PostMapping("/admin/members/modifies")
    public ResponseEntity<Map<String, Object>> modifyMembersInfo(@RequestBody AdminMemberBatchVO adminMemberBatchVO) {
        adminMemberService.modifyMembersInfo(adminMemberBatchVO);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
    
    
}
