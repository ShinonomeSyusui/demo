package com.example.demo.domain.entity;

import java.sql.Date;

import lombok.Data;

@Data
public class UserList {

    private Integer id;
    private String name;
    private String address;
    private Integer age;
    private Date birth;
    private Integer gender;
    private String bloodType;
}