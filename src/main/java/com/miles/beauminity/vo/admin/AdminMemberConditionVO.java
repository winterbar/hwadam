package com.miles.beauminity.vo.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class AdminMemberConditionVO {
    private String signedAt = null;
    private List<Integer> birthdays = new ArrayList<>();
    private String gender = null;
    private String role = null;
    private String status = null;
    private List<String> skinTypes = new ArrayList<>();
    private List<String> personalColors = new ArrayList<>();
    private Integer minPoint = null;
    private Integer maxPoint = null;

    // 체크박스 출력용
    private List<CheckBox> ageList;
    private List<CheckBox> skinTypeList;
    private List<CheckBox> personalColorList;

    // 다중 필터링 (회원 통계)에서 체크박스 출력용 내부 클래스
    @Data
    public static class CheckBox {
        private String code;
        private String label;
        private boolean checked;

        public CheckBox(String code, String label, List<?> selected) {
            this.code = code;
            this.label = label;
            this.checked = selected.stream().map(String::valueOf).anyMatch(code::equals);
        }

    }

    public void convertForCheckBox() {
        // 연령대
        this.ageList = List.of(
            new AdminMemberConditionVO.CheckBox("10", "10대", birthdays),
            new AdminMemberConditionVO.CheckBox("20", "20대", birthdays),
            new AdminMemberConditionVO.CheckBox("30", "30대", birthdays),
            new AdminMemberConditionVO.CheckBox("40", "40대", birthdays),
            new AdminMemberConditionVO.CheckBox("50", "50대", birthdays),
            new AdminMemberConditionVO.CheckBox("60", "60대▲", birthdays)
        );

        // 피부 타입
        this.skinTypeList = List.of(
            new AdminMemberConditionVO.CheckBox("normal", "중성", skinTypes),
            new AdminMemberConditionVO.CheckBox("dry", "건성", skinTypes),
            new AdminMemberConditionVO.CheckBox("oily", "지성", skinTypes),
            new AdminMemberConditionVO.CheckBox("sensitive", "민감성", skinTypes),
            new AdminMemberConditionVO.CheckBox("combination", "복합성", skinTypes),
            new AdminMemberConditionVO.CheckBox("dehydrated", "수부지", skinTypes)
        );
        // 퍼스널 컬러
        this.personalColorList = List.of(
            new AdminMemberConditionVO.CheckBox("spring", "봄웜", personalColors),
            new AdminMemberConditionVO.CheckBox("fail", "가을웜", personalColors),
            new AdminMemberConditionVO.CheckBox("summer", "여름쿨", personalColors),
            new AdminMemberConditionVO.CheckBox("winter", "겨울쿨", personalColors)
        );
    }

    // 회원 통계에서 다중 필터링을 걸었는지 확인
    // 초기화 해뒀기 때문에 List는 isEmpty()를 통해 비어있는지 확인
    // String 타입은 스프링이 요청 파라미터가 비어있다면 "" (빈 문자열)을 바인딩하기 때문에
    // StringUtils.hasText()를 사용하여 빈 문자열 또한 false가 되도록 반영
    public boolean hasSearchCondition() {
        return (StringUtils.hasText(signedAt)) || (!birthdays.isEmpty())
            || (StringUtils.hasText(gender)) || (StringUtils.hasText(role))
            || (StringUtils.hasText(status))
            || (!skinTypes.isEmpty()) || (!personalColors.isEmpty())
            || (minPoint != null) || (maxPoint != null);
    }
}
