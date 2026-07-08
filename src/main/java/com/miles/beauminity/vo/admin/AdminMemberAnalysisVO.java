package com.miles.beauminity.vo.admin;

import java.util.List;

import lombok.Data;

@Data
public class AdminMemberAnalysisVO {
    private String signedAt;
    private List<Integer> birthdays;
    private String gender;
    private String role;
    private String status;
    private List<String> skinTypes;
    private List<String> personalColors;
    private Integer minPoint;
    private Integer maxPoint;

    // 회원 통계에서 다중 필터링을 걸었는지 확인
    public boolean hasSearchCondition() {
        return (signedAt != null) || (birthdays != null)
            || (gender != null) || (role != null) | (status != null)
            || (skinTypes != null) || (personalColors != null)
            || (minPoint != null) || (maxPoint != null);
    }
}
