package com.example.demo.domain.restcontroller;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyTaskRequest {
    private String title;
    private String startTime;
    private String endTime;
    private Integer priority;
    private String status;
    private String description;
    private LocalDate date;

    // バリデーション用メソッド
    public void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("開始時間と終了時間は必須です");
        }
        if (priority == null || priority < 1 || priority > 5) {
            throw new IllegalArgumentException("優先度は1から5の間で指定してください");
        }
    }
}
