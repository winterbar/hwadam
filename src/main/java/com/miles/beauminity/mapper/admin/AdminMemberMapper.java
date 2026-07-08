package com.miles.beauminity.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.miles.beauminity.vo.admin.AdminMemberConditionVO;
import com.miles.beauminity.vo.admin.AdminMemberBatchVO;
import com.miles.beauminity.vo.admin.AdminMemberVO;
import com.miles.beauminity.vo.admin.AdminPageVO;

@Mapper
public interface AdminMemberMapper {
    public long countMembers(AdminPageVO AdminPageVO);
    public long countAnalysisMember(AdminMemberConditionVO adminMemberConditionVO);
    public List<AdminMemberVO> selectMembers(AdminPageVO AdminPageVO);
    public List<AdminMemberVO> selectAnalysisMembers(@Param("analysisVO") AdminMemberConditionVO adminMemberConditionVO,
                                                    @Param("pageVO") AdminPageVO adminPageVO);
    public List<AdminMemberVO> selectAnalysisAllMember(@Param("analysisVO") AdminMemberConditionVO adminMemberConditionVO);
    public void insertMember(AdminMemberVO adminMemberVO);
    public AdminMemberVO getMemberInfo(String username);
    public void updateMemberInfo(AdminMemberVO adminMemberVO);
    public void updateMembersInfo(AdminMemberBatchVO adminMemberBatchVO);
}
