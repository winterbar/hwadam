package com.miles.beauminity.controller.store;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class StoreController {
    @GetMapping("/store")
    public String getMethodName() {
        return "store/main";
    }
    
}
