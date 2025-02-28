package com.example.demo.domain.restcontroller;

import lombok.Data;

@Data
public class ApiResponse {

    private Object date;
    private boolean success;
    private String message;
    private int statusCode;

    public static ApiResponse success(Object data) {
        ApiResponse response = new ApiResponse();
        response.setDate(data);
        response.setSuccess(true);
        response.setStatusCode(200);
        return response;
    }

    public static ApiResponse error(String message, int statusCode) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setStatusCode(statusCode);
        return response;
    }
}
