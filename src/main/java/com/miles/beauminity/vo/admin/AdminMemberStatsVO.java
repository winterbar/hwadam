package com.miles.beauminity.vo.admin;

import java.util.Map;

import lombok.Data;

@Data
public class AdminMemberStatsVO {
    private Map<String, Long> genderStats;
    private Map<String, Long> roleStats;
    private Map<String, Long> statusStats;
    private Map<String, Long> skinTypeStats;
    private Map<String, Long> personalColorStats;

    // 각 칼럼별 통계 집계
    public static AdminMemberStatsVO of(Map<String, Long> genders,
        Map<String, Long> roles, Map<String, Long> status,
        Map<String, Long> skinTypes, Map<String, Long> personalColors) {

        AdminMemberStatsVO result = new AdminMemberStatsVO();
        result.setGenderStats(genders);
        result.setRoleStats(roles);
        result.setStatusStats(status);
        result.setSkinTypeStats(skinTypes);
        result.setPersonalColorStats(personalColors);

        return result;
    }
}
