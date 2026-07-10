package com.miles.beauminity.controller.store;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class StoreController {
    @GetMapping("/store")
    public String getMethodName() {
        
        return "store/main";
    }
    
}
