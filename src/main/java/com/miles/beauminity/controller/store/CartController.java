package com.miles.beauminity.controller.store;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CartController {
    
    @GetMapping("/cart")
    public String getMyCart() {
        return "store/cart";
    }
    
}
