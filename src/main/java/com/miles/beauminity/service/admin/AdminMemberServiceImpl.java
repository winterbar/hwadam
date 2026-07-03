package com.miles.beauminity.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.miles.beauminity.mapper.admin.AdminMemberMapper;
import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {
    
    private final AdminMemberMapper adminMemberMapper;

    @Override
    public long countMembers(AdminPageVO adminPageVO) {
        return adminMemberMapper.countMembers(adminPageVO);
    }

    @Override
    public List<AdminMemberVO> searchMembers(AdminPageVO adminPageVO) {
        // 검색어가 null일 경우 DB에서 검색하지 않음
        if (adminPageVO.getKeyword() == null) {
            return null;
        }
        // searchType이 all일 경우 전체 목록을 검색
        // searchType이 있고 검색어가 있을 경우 조건에 따라 검색 (단, 키워드가 빈칸이면 검색X)
        List<AdminMemberVO> members = adminMemberMapper.selectMembers(adminPageVO);
        convertText(members);
        return members;
    }

    private void convertText(List<AdminMemberVO> members) {
        for(AdminMemberVO member : members) {
            if(member.getRole().equals("USER")) {
                member.setRole("일반");
            } else {
                member.setRole("관리자");
            }

            if(member.getStatus().equals("normal")) {
                member.setStatus("정상");
            } else if(member.getStatus().equals("blocked")) {
                member.setStatus("정지");
            } else if(member.getStatus().equals("deleted")) {
                member.setStatus("탈퇴");
            }
        }
    }

    @Override
    public void registerMember(AdminMemberVO adminMemberVO) {
        adminMemberMapper.insertMember(adminMemberVO);
    }

    @Override
    public AdminMemberVO getMemberDetails(String username) {
        return adminMemberMapper.getMemberInfo(username);
    }

    @Override
    public void modifyMemberInfo(AdminMemberVO adminMemberVO) {
        adminMemberMapper.updateMemberInfo(adminMemberVO);
    }
    
}
