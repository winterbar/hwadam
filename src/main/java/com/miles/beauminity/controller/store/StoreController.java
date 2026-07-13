package com.miles.beauminity.controller.store;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miles.beauminity.service.store.StoreService;
import com.miles.beauminity.vo.store.StoreVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    // 스토어 메인 화면
    @GetMapping
    public String getStoreMain(Model model) {
        List<StoreVO> storeList = storeService.getStoreList();

        model.addAttribute("storeList", storeList);
        return "store/main";
    }

    // 스토어 전체 상품 화면
    @GetMapping("/all")
    public String storeAll(Model model) {

        List<StoreVO> storeList = storeService.getStoreList();

        model.addAttribute("storeList", storeList);


        return "store/store-all-page";
    }
}