package com.example.demo.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.form.SignupForm;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.service.SignupValidationService;

@Controller
public class SignupValidationController {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    SignupValidationService service;

    @Autowired
    UserRepository repository;

    /**
     * 登録内容の確認ページへの遷移処理
     * 
     * @param model
     * @param form
     * @return
     */
    @PostMapping("/signupValidation")
    public String showMessage(Model model, @ModelAttribute SignupForm form) {

        // String password = form.getPassword();
        // password = encoder.encode(password);
        // form.setPassword(password);

        // System.out.println(form + "ここやで");
        // model.addAttribute("form", form);

        // 日付のパターンを指定
        // model.addAttribute("birth", service.dateConversion(form.getBirth()));

        // 性別のパラメータを値からKeyに変換
        // model.addAttribute("gender", service.getKeys(form.getGender()));
        return "signupValidation";
    }

    /**
     * 新規ユーザーの登録処理
     * 
     * @param roleName
     * @param form
     * @return
     */
    @PostMapping("/signup/signupValidation")
    public String registerUser(@RequestParam("roleName") String roleName, SignupForm form) {
        // ユーザー登録処理
        repository.registerUser(form.getName(), form.getEmail(), form.getPassword(), roleName);
        return "redirect:/login";
    }

}
