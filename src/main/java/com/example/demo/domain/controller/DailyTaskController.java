package com.example.demo.domain.controller;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.restcontroller.ApiResponse;
import com.example.demo.domain.restcontroller.DailyTaskRequest;
import com.example.demo.domain.service.MainPageService;

@RestController
@RequestMapping("/api/v1/tasks/daily")
public class DailyTaskController {

    @Autowired
    private MainPageService mainPageService;

    @GetMapping
    public ResponseEntity<ApiResponse> getDailyTasks(
            @RequestParam String date) {

        Integer userId = mainPageService.getLoggedInUserId();
        List<TaskDto> tasks = mainPageService.getDailyTasks(userId, date);

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // HTTP PUTリクエストを受け付けるためのマッピングを設定します。パスにIDが含まれます。
    @PutMapping("/{id}/time")
    public ResponseEntity<ApiResponse> updateTaskTime(
            // パスからタスクのIDを取得します。
            @PathVariable Integer id,
            // リクエストパラメータから開始時間を取得します。
            @RequestParam String startTime,
            // リクエストパラメータから終了時間を取得します。
            @RequestParam String endTime) {

        // サービスクラスを使って、指定されたIDのタスクの時間を更新します。
        mainPageService.updateTaskTime(id, startTime, endTime);
        // 更新が成功したことを示すレスポンスを返します。
        return ResponseEntity.ok(ApiResponse.success("タスク時間を更新しました"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createDailyTask(@RequestBody DailyTaskRequest request) {
        Integer userId = mainPageService.getLoggedInUserId();

        TaskDto taskDto = new TaskDto();
        taskDto.setUserId(userId);
        taskDto.setTitle(request.getTitle());
        taskDto.setStartTime(Time.valueOf(request.getStartTime() + ":00"));
        taskDto.setEndTime(Time.valueOf(request.getEndTime() + ":00"));
        taskDto.setPriority(request.getPriority());
        taskDto.setStatus(request.getStatus());

        mainPageService.createDailyTask(taskDto);

        return ResponseEntity.ok(ApiResponse.success("タスクを作成しました"));
    }

}
