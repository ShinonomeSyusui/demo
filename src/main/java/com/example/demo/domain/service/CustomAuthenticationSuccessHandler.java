package com.example.demo.domain.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // ログインユーザーのメールアドレスを取得
        String email = authentication.getName(); // デフォルトではusernameが取得される
        HttpSession session = request.getSession();

        // セッションにメールアドレスを保存
        session.setAttribute("email", email);

        // デフォルトのログイン成功時の遷移先へリダイレクト
        response.sendRedirect("/mainPage"); // メイン画面のURL
    }

}
