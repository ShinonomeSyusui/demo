package com.example.demo.domain.restcontroller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.domain.service.MainPageService;

@Component
public class TaskApiSecurityCheck {

    @Autowired
    private MainPageService mainPageService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // public TaskApiSecurityCheck(JdbcTemplate jdbcTemplate) {
    // this.jdbcTemplate = jdbcTemplate;
    // }

    /**
     * ユーザーが特定のタスクへのアクセス権限を持っているかを検証するメソッド
     * 
     * @param taskId 検証するタスクのID
     */
    public void validateUseAccess(Integer taskId) {
        // 現在ログインしているユーザーのIDを取得
        Integer currentUserId = mainPageService.getLoggedInUserId();
        // 現在のユーザーが管理者であるかどうかを確認
        boolean isAdmin = mainPageService.isAdmin();

        // ユーザーが管理者でない場合、かつタスクへのアクセス権限を持たない場合は例外をスロー
        if (!isAdmin && !hasAccessToTask(currentUserId, taskId)) {
            throw new SecurityException("このタスクへのアクセス権限がありません");
        }
    }

    /**
     * ユーザーが特定のタスクにアクセスできるかを確認するメソッド
     * 
     * @param userId アクセスをチェックするユーザーのID
     * @param taskId アクセスをチェックするタスクのID
     * @return タスクへのアクセス権限がある場合はtrue、ない場合はfalse
     */
    private boolean hasAccessToTask(Integer userId, Integer taskId) {
        // タスクの所有者を確認するためのSQLクエリを定義
        String sql = "SELECT COUNT(*) FROM tasks WHERE id = ? AND user_id = ? AND del_flg = '0'";

        try {
            // SQLクエリを実行して、指定されたタスクIDとユーザーIDに一致するレコード数を取得
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, taskId, userId);

            // タスクが存在し、かつ指定されたユーザーが所有者である場合はtrueを返す
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            // 結果が見つからない場合の処理
            return false;
        } catch (Exception e) {
            // その他の例外をログに記録し、必要に応じて処理
            e.printStackTrace();
            return false;
        }

    }

    // 一括操作時の権限チェック
    /**
     * バッチアクセスを検証するメソッド
     *
     * @param taskIds 検証対象のタスクIDのリスト
     */
    public void validateBatchAccess(List<Integer> taskIds) {
        // 現在ログインしているユーザーのIDを取得
        Integer currentUserId = mainPageService.getLoggedInUserId();

        // 現在のユーザーが管理者かどうかを確認
        boolean isAdmin = mainPageService.isAdmin();

        // 管理者でない場合にのみ権限チェックを行う
        if (!isAdmin) {
            // 各タスクIDについてアクセス権限を確認
            for (Integer taskId : taskIds) {
                // タスクへのアクセス権限がない場合、例外をスロー
                if (!hasAccessToTask(currentUserId, taskId)) {
                    throw new SecurityException("タスクID: " + taskId + " へのアクセス権限がありません");
                }
            }
        }
    }

    // レート制限の実装
    /**
     * リクエスト数を管理するマップと1分あたりの最大リクエスト数を定義
     */
    private static final Map<Integer, Integer> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    /**
     * ユーザーごとのリクエスト制限をチェックするメソッド
     *
     * @param userId チェック対象のユーザーID
     */
    public void checkRateLimit(Integer userId) {
        // 現在のユーザーのリクエスト数を取得
        int currentRequests = requestCounts.getOrDefault(userId, 0);

        // リクエスト数が最大値を超えているかを確認
        if (currentRequests >= MAX_REQUESTS_PER_MINUTE) {
            // 制限を超えた場合は例外をスロー
            throw new SecurityException("リクエスト制限を超えました。しばらく待ってから再試行してください。");
        }

        // リクエスト数をインクリメントして更新
        requestCounts.put(userId, currentRequests + 1);
    }

}
