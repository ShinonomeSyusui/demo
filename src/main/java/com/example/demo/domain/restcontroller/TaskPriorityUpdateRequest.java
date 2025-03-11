package com.example.demo.domain.restcontroller;

import lombok.Data;

@Data
public class TaskPriorityUpdateRequest {

    private Integer taskId;
    private Integer priority;
}
