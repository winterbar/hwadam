package com.miles.beauminity.service.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miles.beauminity.mapper.admin.AdminMemberMapper;
import com.miles.beauminity.vo.admin.AdminMemberAnalysisVO;
import com.miles.beauminity.vo.admin.AdminMemberBatchVO;
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
    public long countAnalysisMember(AdminMemberAnalysisVO adminMemberAnalysisVO) {
        return adminMemberMapper.countAnalysisMember(adminMemberAnalysisVO);
    }

    // 회원 조회 필터링
    @Override
    public List<AdminMemberVO> searchMembers(AdminPageVO adminPageVO) {
        // 검색어가 null일 경우 DB에서 검색하지 않음
        if (adminPageVO.getKeyword() == null) {
            return null;
        }
        // 정렬 기준 칼럼이 null이 아닐 경우
        if (adminPageVO.getSort() != null) {
            switch (adminPageVO.getSort()) {
                case "1":
                    adminPageVO.setSort("username");
                    break;
                case "2":
                    adminPageVO.setSort("name");
                    break;
                case "3":
                    adminPageVO.setSort("nickname");
                    break;
                case "4":
                    adminPageVO.setSort("point");
                    break;
                case "5":
                    adminPageVO.setSort("signed_at");
                    break;
            }
        }
        // searchType이 all일 경우 전체 목록을 검색
        // searchType이 있고 검색어가 있을 경우 조건에 따라 검색 (단, 키워드가 빈칸이면 검색X)
        List<AdminMemberVO> members = adminMemberMapper.selectMembers(adminPageVO);
        convertText(members);
        return members;
    }
    
    // 회원 분석 필터링
    @Override
    public List<AdminMemberVO> analysisMembers(AdminMemberAnalysisVO adminMemberAnalysisVO, AdminPageVO adminPageVO) {
        // 정렬 기준 칼럼이 null이 아닐 경우
        if (adminPageVO.getSort() != null) {
            switch (adminPageVO.getSort()) {
                case "1":
                    adminPageVO.setSort("username");
                    break;
                case "2":
                    adminPageVO.setSort("name");
                    break;
                case "3":
                    adminPageVO.setSort("nickname");
                    break;
                case "4":
                    adminPageVO.setSort("point");
                    break;
                case "5":
                    adminPageVO.setSort("signed_at");
                    break;
            }
        }
        List<AdminMemberVO> members = adminMemberMapper.selectAnalysisMembers(adminMemberAnalysisVO, adminPageVO);
        convertText(members);
        return members;
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
