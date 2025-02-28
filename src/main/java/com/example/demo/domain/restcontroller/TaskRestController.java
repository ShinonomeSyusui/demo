package com.example.demo.domain.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.service.MainPageService;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskRestController {

    @Autowired
    private MainPageService mainPageService;

    // @Autowired
    // private MainPageRepository repository;

    // @Autowired
    // private TaskApiValidator validator;

    // @Autowired
    // private TaskApiSecurityCheck securityCheck;

    @GetMapping("/list")
    public List<TaskDto> getTaskList() {
        Integer userId = mainPageService.getLoggedInUserId();
        return mainPageService.getPagedTasks(userId, 1, 10);
    }

    // @GetMapping
    // public ResponseEntity<ApiResponse> getTasks(@RequestParam(defaultValue = "1")
    // int page,
    // @RequestParam(required = false) Integer year,
    // @RequestParam(required = false) Integer month) {

    // validator.validatePageParams(page, 10);
    // if (year != null && month != null) {
    // validator.validateDateParams(year, month);
    // }

    // Map<String, Object> response = new HashMap<>();
    // int pageSize = 10;

    // if (mainPageService.isAdmin()) {
    // List<TaskDto> tasks = mainPageService.getAllUsersTasks(page, pageSize);
    // int totalCount = repository.getAllUserTasksCount();
    // response.put("tasks", tasks);
    // response.put("total", totalCount);
    // response.put("isAdmin", true);
    // } else {
    // Integer userId = mainPageService.getLoggedInUserId();
    // List<TaskDto> tasks = mainPageService.getTasksByMonth(userId, year, month,
    // page, pageSize);
    // int totalCount = repository.getCountAllByMonth(userId, year, month);
    // response.put("tasks", tasks);
    // response.put("total", totalCount);
    // response.put("isAdmin", false);
    // }
    // return ResponseEntity.ok(ApiResponse.success(response));
    // }

    // @PutMapping("/{id}/status")
    // public ResponseEntity<ApiResponse> updateTaskStatus(
    // @PathVariable Integer id,
    // @RequestParam Integer status) {

    // securityCheck.validateUseAccess(id);

    // if (id == null || id <= 0) {
    // throw new IllegalArgumentException("タスクIDが無効です");
    // }

    // if (status == null || status < 1 || status > 6) {
    // throw new IllegalArgumentException("ステータスは1から6の間である必要があるます");
    // }

    // mainPageService.statusSelect(status, id);
    // return ResponseEntity.ok(ApiResponse.success("ステータスを更新しました"));
    // }

    // @PutMapping("/{id}/complete")
    // public ResponseEntity<ApiResponse> completeYask(
    // @PathVariable Integer id) {

    // if (id == null || id <= 0) {
    // throw new IllegalArgumentException("タスクIDが無効です");
    // }
    // mainPageService.completed(id);
    // return ResponseEntity.ok(ApiResponse.success("タスクを完了しました"));
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<ApiResponse> deleteTask(
    // @PathVariable Integer id) {

    // securityCheck.validateUseAccess(id);

    // if (id == null || id <= 0) {
    // throw new IllegalArgumentException("タスクIDが無効です");
    // }

    // mainPageService.deleteTask(id);
    // return ResponseEntity.ok(ApiResponse.success("タスクを削除しました"));
    // }

    // @GetMapping("/search")
    // public ResponseEntity<ApiResponse> searchTasksByUsername(
    // @RequestParam String username,
    // @RequestParam int year,
    // @RequestParam int month,
    // @RequestParam(defaultValue = "1") int page) {

    // validator.validatePageParams(page, 10);
    // validator.validateDateParams(year, month);

    // if (username == null || username.trim().isEmpty()) {
    // throw new IllegalArgumentException("ユーザー名は必須です");
    // }

    // int pageSize = 10;
    // List<TaskDto> tasks = mainPageService.getTasksByUsername(username, year,
    // month, page, pageSize);
    // int totalCount = repository.getCountByUsername(username, year, month);

    // Map<String, Object> response = new HashMap<>();
    // response.put("tasks", tasks);
    // response.put("total", totalCount);

    // return ResponseEntity.ok(ApiResponse.success(response));
    // }
}
