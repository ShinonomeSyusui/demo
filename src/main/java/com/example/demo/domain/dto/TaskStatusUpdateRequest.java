package com.example.demo.domain.dto;

import lombok.Data;

@Data
public class TaskStatusUpdateRequest {

    private Integer taskId;
    private Integer status;
}
