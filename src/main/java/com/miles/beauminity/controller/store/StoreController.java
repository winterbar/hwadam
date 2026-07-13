package com.miles.beauminity.controller.store;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.miles.beauminity.service.store.StoreService;
import com.miles.beauminity.vo.store.StoreVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor

public class StoreController {

    private final StoreService storeService;

    @GetMapping("/store")
    public String storeAll(
            @RequestParam(required = false) String category2,
            Model model) {

        // 전체 상품 조회
        List<StoreVO> storeList = storeService.getStoreList();

        // 카테고리가 선택됐을 때만 필터 적용
        if (category2 != null && !category2.trim().isEmpty()) {

            final String selectedCategory = category2;

            storeList = storeList.stream()
                    .filter(product -> selectedCategory.equals(product.getCategory2()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("storeList", storeList);
        model.addAttribute("category2", category2);
        model.addAttribute("totalCount", storeList.size());

        return "store/main";
    }
}