package com.example.demo.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.form.SignupForm;
import com.example.demo.domain.service.SignupService;

@Controller
@RequestMapping("/")
public class SignupController {

    @Autowired
    SignupService service;

    /**
     * 最初のページへの遷移処理
     *
     * @param form
     * @param model
     * @return hello
     */
    @GetMapping("/signup")
    public String getMethodName(@ModelAttribute SignupForm form, Model model) {

        // 1980年1月1日の日付を取得し変数に格納
        // form.setBirth(service.getEighties());

        // 性別のMapを取得
        // Map<String, Integer> genderMap = service.genderMap();

        // 血液型のListを取得
        // List<String> bloods = service.bloods();

        // Modelにパラメータを格納
        // model.addAttribute("gender", genderMap);
        // model.addAttribute("bloods", bloods);
        return "signup";
    }

    /**
     * アカウント登録処理
     * 
     * @param form
     * @return
     */
    @PostMapping("/signup/user")
    public String signupuser(Model model, @ModelAttribute SignupForm form) {

        // UsersDto dto = new UsersDto();

        // dto.setName(form.getName());
        // dto.setEmail(form.getEmail());
        // dto.setPassword(form.getPassword());

        return "signupValidation";
    }
}
