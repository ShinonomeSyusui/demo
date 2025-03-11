package com.example.demo.domain.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.dto.TaskStatusUpdateRequest;
import com.example.demo.domain.repository.MainPageRepository;
import com.example.demo.domain.service.MainPageService;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskRestController {

    @Autowired
    private MainPageService mainPageService;

    @Autowired
    private MainPageRepository repository;

    @Autowired
    private TaskApiValidator validator;

    @Autowired
    private TaskApiSecurityCheck securityCheck;

    private TaskStatusUpdateRequest request;

    // @GetMapping("/list")
    // public List<TaskDto> getTaskList() {
    // Integer userId = mainPageService.getLoggedInUserId();
    // return mainPageService.getPagedTasks(userId, 1, 10);
    // }

    /**
     * タスクを取得するメソッド
     *
     * @param page  ページ番号 (デフォルトは1)
     * @param year  年 (オプション)
     * @param month 月 (オプション)
     * @return APIレスポンスを含むResponseEntity
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getTasks(@RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        // ページパラメータのバリデーション
        validator.validatePageParams(page, 10);

        // 年と月が指定されている場合、日付パラメータのバリデーション
        if (year != null && month != null) {
            validator.validateDateParams(year, month);
        }

        Map<String, Object> response = new HashMap<>();
        int pageSize = 10;

        // 管理者かどうかをチェック
        if (mainPageService.isAdmin()) {
            // 全ユーザーのタスクを取得
            List<TaskDto> tasks = mainPageService.getAllUsersTasks(page, pageSize);
            int totalCount = repository.getAllUserTasksCount();
            response.put("tasks", tasks);
            response.put("total", totalCount);
            response.put("isAdmin", true);
        } else {
            // ログイン中のユーザーIDを取得し、そのユーザーのタスクを取得
            Integer userId = mainPageService.getLoggedInUserId();
            List<TaskDto> tasks = mainPageService.getTasksByMonth(userId, year, month, page, pageSize);
            int totalCount = repository.getCountAllByMonth(userId, year, month);
            response.put("tasks", tasks);
            response.put("total", totalCount);
            response.put("isAdmin", false);
        }

        // 成功レスポンスを返す
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * タスクのステータスを更新するメソッド
     *
     * @param id     タスクID
     * @param status 新しいステータス
     * @return APIレスポンスを含むResponseEntity
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateTaskStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {

        // タスクIDの検証を行う
        validator.validateTaskId(id);

        // ステータスの検証を行う
        validator.validateTaskStatus(status);

        // ユーザーアクセスのセキュリティチェックを実施
        securityCheck.validateUseAccess(id);

        // タスクのステータスを更新
        mainPageService.statusSelect(status, id);

        // ステータス更新成功のレスポンスを返す
        return ResponseEntity.ok(ApiResponse.success("ステータスを更新しました"));
    }

    /**
     * タスクを完了するメソッド
     *
     * @param id タスクID
     * @return APIレスポンスを含むResponseEntity
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse> completeYask(
            @PathVariable Integer id) {

        // タスクIDのバリデーション
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("タスクIDが無効です");
        }

        // タスクを完了としてマーク
        mainPageService.completed(id);

        // 成功レスポンスを返す
        return ResponseEntity.ok(ApiResponse.success("タスクを完了しました"));
    }

    /**
     * タスクを削除するメソッド
     *
     * @param id タスクID
     * @return APIレスポンスを含むResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable Integer id) {

        // タスクIDの検証を行う
        validator.validateTaskId(id);

        // ユーザーアクセスのセキュリティチェックを実施
        securityCheck.validateUseAccess(id);

        // タスクを削除する
        mainPageService.deleteTask(id);

        // タスク削除成功のレスポンスを返す
        return ResponseEntity.ok(ApiResponse.success("タスクを削除しました"));
    }

    /**
     * ユーザー名でタスクを検索するメソッド
     *
     * @param username ユーザー名
     * @param year     年
     * @param month    月
     * @param page     ページ番号
     * @return APIレスポンスを含むResponseEntity
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchTasksByUsername(
            @RequestParam String username,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "1") int page) {

        // ユーザー名のバリデーションを行う
        validator.validateUsername(username);
        // ページパラメータのバリデーション
        validator.validatePageParams(page, 10);
        // 日付パラメータのバリデーション
        validator.validateDateParams(year, month);

        // ユーザー名がnullまたは空白の場合、例外をスロー
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ユーザー名は必須です");
        }

        int pageSize = 10;
        // ユーザー名、年、月、ページ番号に基づいてタスクを取得
        List<TaskDto> tasks = mainPageService.getTasksByUsername(username, year,
                month, page, pageSize);
        // ユーザー名、年、月に基づくタスクの総数を取得
        int totalCount = repository.getCountByUsername(username, year, month);

        // レスポンス用のマップを作成
        Map<String, Object> response = new HashMap<>();
        response.put("tasks", tasks); // タスクのリストをマップに追加
        response.put("total", totalCount); // タスクの総数をマップに追加

        // 成功レスポンスを返す
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 複数のタスクステータスを一括で更新するエンドポイント
     * 
     * @param requests タスクステータス更新リクエストのリスト
     * @return 更新成功メッセージを含むレスポンスエンティティ
     */
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse> updateBatchTaskStatus(
            @RequestBody List<TaskStatusUpdateRequest> requests) {

        // 一括操作のバリデーション
        validator.validateBatchRequests(requests);

        // レート制限チェック
        securityCheck.checkRateLimit(mainPageService.getLoggedInUserId());

        // タスクIDのリストを抽出
        List<Integer> taskIds = requests.stream()
                .map(TaskStatusUpdateRequest::getTaskId)
                .collect(Collectors.toList());

        // 一括アクセス権限チェック
        securityCheck.validateBatchAccess(taskIds);

        // 各リクエストの処理
        for (TaskStatusUpdateRequest request : requests) {
            validator.validateTaskStatus(request.getStatus());
            mainPageService.statusSelect(request.getStatus(), request.getTaskId());
        }

        // 成功メッセージを含むレスポンスを返す
        return ResponseEntity.ok(ApiResponse.success("複数タスクのステータスを更新しました"));
    }

    /**
     * 複数のタスクを一括で削除するエンドポイント
     * 
     * @param taskIds 削除対象のタスクIDのリスト
     * @return 削除成功メッセージを含むレスポンスエンティティ
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse> deleteBatchTasks(
            @RequestBody List<Integer> taskIds) {

        // 各タスクIDについて繰り返し処理を行う
        for (Integer taskId : taskIds) {
            // タスクIDの妥当性を検証
            validator.validateTaskId(taskId);

            // ユーザーアクセス権限を検証
            securityCheck.validateUseAccess(taskId);

            // メインページサービスを使用してタスクを削除
            mainPageService.deleteTask(taskId);
        }

        // 成功メッセージを含むレスポンスを返す
        return ResponseEntity.ok(ApiResponse.success("複数タスクを削除しました"));
    }

}
