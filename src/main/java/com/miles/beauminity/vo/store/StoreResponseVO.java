package com.miles.beauminity.vo.store;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 모르는 데이터는 무시하라는 설정
public class StoreResponseVO {
    private List<StoreVO> items; // 네이버가 주는 'items'를 여기에 담습니다.
}