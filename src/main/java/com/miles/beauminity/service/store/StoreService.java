package com.miles.beauminity.service.store;

import java.util.List;

import com.miles.beauminity.vo.store.StoreVO;


public interface StoreService {
 List<StoreVO> getStoreList();
 void getNaverProducts(String keyword);
 List<StoreVO> getStoreListCategory2(String category2);
    
}
