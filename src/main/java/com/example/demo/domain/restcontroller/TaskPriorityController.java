package com.example.demo.domain.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.service.MainPageService;

@RestController
@RequestMapping("/api/v1/tasks/priority")
public class TaskPriorityController {

    @Autowired
    private MainPageService mainPageService;

    @Autowired
    private TaskApiValidator validator;

    @Autowired
    private TaskApiSecurityCheck securityCheck;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePriority(
            @PathVariable Integer id,
            @RequestParam Integer priority) {
        // 優先度更新の実装
        // バリデーション
        validator.validateTaskId(id);
        validator.validatePriority(priority); // このメソッドは後で実装します

        // セキュリティチェック
        securityCheck.validateUseAccess(id);

        // 優先度更新処理
        mainPageService.updateTaskPriority(id, priority); // このメソッドは後で実装します

        return ResponseEntity.ok(ApiResponse.success("優先度を更新しました"));
    }

    @PutMapping("/batch")
    public ResponseEntity<ApiResponse> updateBatchPriorities(
            @RequestBody List<TaskPriorityUpdateRequest> requests) {

        // バリデーション
        validator.validateBatchRequests(requests);

        for (TaskPriorityUpdateRequest request : requests) {
            validator.validateTaskId(request.getTaskId());
            validator.validatePriority(request.getPriority());
            securityCheck.validateUseAccess(request.getTaskId());

            mainPageService.updateTaskPriority(request.getTaskId(), request.getPriority());
        }

        return ResponseEntity.ok(ApiResponse.success("複数タスクの優先度を更新しました"));
    }
}
