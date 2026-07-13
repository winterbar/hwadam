package com.miles.beauminity.mapper.store;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.store.StoreVO;

@Mapper
public interface StoreMapper {
    // 상품 한 개 저장
    int insertStore(StoreVO storeVO);

    // 전체 상품 조회
    List<StoreVO> selectStoreList();
    
} 