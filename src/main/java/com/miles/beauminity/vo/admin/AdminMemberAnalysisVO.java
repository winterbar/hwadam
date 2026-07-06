package com.miles.beauminity.vo.admin;

import java.util.List;

import lombok.Data;

@Data
public class AdminMemberAnalysisVO {
    private String signedAt;
    private List<Integer> birthday;
    private String gender;
    private String role;
    private String status;
    private List<String> skinType;
    private List<String> personalColor;
    private Integer minPoint;
    private Integer maxPoint;
}
