package com.example.demo.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.form.SignupForm;
import com.example.demo.domain.repository.UserRepository;

@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    UserRepository repository;

    /**
     * ログイン画面への遷移
     * 
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    /**
     * パスワード再設定画面へ
     * 
     * @return パスワード再設定画面へ遷移
     */
    @GetMapping("/update/password")
    public String updatePasswordPage(@ModelAttribute SignupForm form) {
        return "update_password";
    }

    /**
     * パスワード再設定処理
     * 
     * @param form
     * @return ログイン画面へ遷移
     */
    @PostMapping("/update/password")
    public String postMethodName(SignupForm form) {
        System.out.println(form + "←ここにformの中身がある");
        repository.updateUserPassword(form.getEmail(), form.getPassword());
        return "redirect:/login";
    }

}
