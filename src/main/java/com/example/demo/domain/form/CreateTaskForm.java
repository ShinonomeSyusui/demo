package com.example.demo.domain.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// @AllArgsConstructor
public class CreateTaskForm {

    @NotBlank
    private String title; // タイトル

    private String description; // 詳細

    @NotBlank(message = "進捗状況")
    private String status; // 進捗

    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    // @FutureOrPresent
    private String dueDate; // 期限日

    private Integer priority = 5; // 優先度

}
