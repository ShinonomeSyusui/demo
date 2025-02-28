package com.example.demo.domain.restcontroller;

import org.springframework.stereotype.Component;

@Component
public class TaskApiValidator {

    public void validatePageParams(int page, int pageSize) {
        if (page < 1) {
            throw new IllegalArgumentException("ページは番号は1以上である必要があります");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("ページサイズは1から100の間である必要があります");
        }
    }

    public void validateDateParams(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("付きはから12の間である必要があります");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("年は2000から2100の間である必要があります");
        }
    }
}
