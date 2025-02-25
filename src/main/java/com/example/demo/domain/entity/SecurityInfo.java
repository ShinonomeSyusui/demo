package com.example.demo.domain.entity;

import lombok.Data;

@Data
public class SecurityInfo {

    private Integer id;
    private Integer userId;
    private String email;
    private String password;
    private String bcryptPassword;
}
