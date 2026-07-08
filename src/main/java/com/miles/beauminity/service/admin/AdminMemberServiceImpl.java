package com.miles.beauminity.service.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miles.beauminity.mapper.admin.AdminMemberMapper;
import com.miles.beauminity.vo.admin.AdminMemberAnalysisVO;
import com.miles.beauminity.vo.admin.AdminMemberBatchVO;
import com.miles.beauminity.vo.admin.AdminMemberConditionVO;
import com.miles.beauminity.vo.admin.AdminMemberStatsVO;
import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberServiceImpl implements AdminMemberService {
    
    private final AdminMemberMapper adminMemberMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원 조회 필터링 결과 회원 수 (페이징에 사용)
    @Override
    public long countMembers(AdminPageVO adminPageVO) {
        return adminMemberMapper.countMembers(adminPageVO);
    }

    // 회원 통계 필터링 결과 회원 수 (페이징에 사용)
    @Override
    public long countAnalysisMember(AdminMemberConditionVO adminMemberConditionVO) {
        return adminMemberMapper.countAnalysisMember(adminMemberConditionVO);
    }

    // 회원 조회 필터링
    @Override
    public List<AdminMemberVO> searchMembers(AdminPageVO adminPageVO) {
        // 검색어가 null일 경우 DB에서 검색하지 않음
        System.out.println(adminPageVO.toString());
        if (adminPageVO.getKeyword() == null) {
            return null;
        }
        // 만약 정렬 기준이 일치하지 않으면 정렬을 수행하지 않음
        if ((adminPageVO.getSort() != null) && 
            (!"username".equals(adminPageVO.getSort()) &&
             !"name".equals(adminPageVO.getSort()) &&
             !"nickname".equals(adminPageVO.getSort()) &&
             !"point".equals(adminPageVO.getSort()) &&
             !"signedAt".equals(adminPageVO.getSort()))
        ) {
            return null;
        }
        // searchType이 all일 경우 전체 목록을 검색
        // searchType이 있고 검색어가 있을 경우 조건에 따라 검색 (단, 키워드가 빈칸이면 검색X)
        List<AdminMemberVO> members = adminMemberMapper.selectMembers(adminPageVO);
        convertText(members);
        return members;
    }
    
    // 회원 분석 필터링
    @Override
    public List<AdminMemberVO> analysisMembers(AdminMemberConditionVO adminMemberConditionVO, AdminPageVO adminPageVO) {
        List<AdminMemberVO> members = adminMemberMapper.selectAnalysisMembers(adminMemberConditionVO, adminPageVO);
        convertText(members);
        return members;
    }

    // 회원 분석 필터링 결과 통계
    @Override
    public AdminMemberStatsVO statsMembers(AdminMemberConditionVO adminMemberConditionVO) {
        List<AdminMemberVO> members = adminMemberMapper.selectAnalysisAllMember(adminMemberConditionVO);
        
        Map<String, Long> genderStats = members.stream()
            .filter(m -> m.getGender() != null)
            .collect(Collectors.groupingBy(AdminMemberVO::getGender, Collectors.counting()));
        Map<String, Long> roleStats = members.stream()
            .filter(m -> m.getRole() != null)
            .collect(Collectors.groupingBy(AdminMemberVO::getRole, Collectors.counting()));
        Map<String, Long> statusStats = members.stream()
            .filter(m -> m.getStatus() != null)
            .collect(Collectors.groupingBy(AdminMemberVO::getStatus, Collectors.counting()));
        Map<String, Long> skinTypeStats = members.stream()
            .filter(m -> m.getSkinType() != null)
            .collect(Collectors.groupingBy(AdminMemberVO::getSkinType, Collectors.counting()));
        Map<String, Long> personalColorStats = members.stream()
            .filter(m -> m.getPersonalColor() != null)
            .collect(Collectors.groupingBy(AdminMemberVO::getPersonalColor, Collectors.counting()));
        
        return AdminMemberStatsVO.of(genderStats, roleStats, statusStats, skinTypeStats, personalColorStats);
    }

    // 화면에 출력하기 위한 텍스트 변경
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

    // 회원 등록
    @Override
    public void registerMember(AdminMemberVO adminMemberVO) {
        // 비밀번호 암호화
        if(adminMemberVO.getPassword() != null) {
            adminMemberVO.setPassword(passwordEncoder.encode(adminMemberVO.getPassword()));
        }
        adminMemberMapper.insertMember(adminMemberVO);
    }

    // 회원 정보 상세보기
    @Override
    public AdminMemberVO getMemberDetails(String username) {
        return adminMemberMapper.getMemberInfo(username);
    }
    
    // 회원 정보 변경
    @Override
    public void modifyMemberInfo(AdminMemberVO adminMemberVO) {
        adminMemberMapper.updateMemberInfo(adminMemberVO);
    }

    // 회원 정보 일괄 변경
    // 모든 회원들의 정보를 변경해야 반영하도록 트랜잭션 처리
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyMembersInfo(AdminMemberBatchVO adminMemberBatchVO) {
        // 포인트 변경 요청인지 확인
        // 만약 포인트 변경 요청이라면 변경할 값을 정수형으로 변경
        if("point".equals(adminMemberBatchVO.getModifyType())) {
            adminMemberBatchVO.setPoint(Integer.parseInt(adminMemberBatchVO.getValue()));
        }
        adminMemberMapper.updateMembersInfo(adminMemberBatchVO);
    }
    
}
