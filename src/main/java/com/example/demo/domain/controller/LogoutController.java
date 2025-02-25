package com.example.demo.domain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    /**
     * ログアウト画面への遷移処理
     * 
     * @return
     */
    @GetMapping("/logout")
    public String getLogoutPage() {
        return "logout";
    }

}
