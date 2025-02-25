package com.example.demo.domain.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupForm {

    @NotBlank
    private String name; // ユーザー名

    @Email
    @NotBlank
    private String email; // Eメール

    @NotBlank
    private String password; // パスワード
    // private String address; //住所
    // private String age; //年齢
    // private String birth; //生年月日
    // private Integer gender; //性別
    // private String bloodType; //血液型
}
