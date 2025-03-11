package com.example.demo.domain.restcontroller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.domain.dto.TaskDto;

@Component
public class TaskApiValidator {

    /**
     * ページングパラメータを検証するメソッド
     *
     * @param page     ページ番号
     * @param pageSize ページサイズ
     */
    public void validatePageParams(int page, int pageSize) {
        // ページ番号が1未満の場合、例外をスロー
        if (page < 1) {
            throw new IllegalArgumentException("ページは番号は1以上である必要があります");
        }
        // ページサイズが1未満または100を超える場合、例外をスロー
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("ページサイズは1から100の間である必要があります");
        }
    }

    /**
     * 日付パラメータを検証するメソッド
     *
     * @param year  年
     * @param month 月
     */
    public void validateDateParams(int year, int month) {
        // 月が1未満または12を超える場合、例外をスロー
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月は1から12の間である必要があります");
        }
        // 年が2000未満または2100を超える場合、例外をスロー
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("年は2000から2100の間である必要があります");
        }
    }

    /**
     * タスクリクエストを検証するメソッド
     *
     * @param dto タスク情報を含むデータ転送オブジェクト
     */
    public void validateTaskRequest(TaskDto dto) {
        // タイトルがnullまたは空白の場合、例外をスロー
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須です");
        }
        // 期日がnullの場合、例外をスロー
        if (dto.getDueDate() == null) {
            throw new IllegalArgumentException("期日は必須です");
        }
    }

    /**
     * タスクのステータスを検証するメソッド
     *
     * @param status 検証対象のタスクステータス
     */
    public void validateTaskStatus(Integer status) {
        // ステータスがnullの場合、例外をスロー
        if (status == null) {
            throw new IllegalArgumentException("ステータスは必須です");
        }
        // ステータスが1から6の範囲外の場合、例外をスロー
        if (status < 1 || status > 6) {
            throw new IllegalArgumentException("ステータスは1から6の間である必要があります");
        }
    }

    /**
     * タスクIDを検証するメソッド
     *
     * @param taskId 検証対象のタスクID
     */
    public void validateTaskId(Integer taskId) {
        // タスクIDがnullまたは0以下の場合、例外をスロー
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("タスクIDが無効です");
        }
    }

    /**
     * ユーザー名を検証するメソッド
     *
     * @param username 検証対象のユーザー名
     */
    public void validateUsername(String username) {
        // ユーザー名がnullまたは空白の場合、例外をスロー
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ユーザー名は必須です");
        }
    }

    private static final int MAX_BATCH_SIZE = 50; // 一括操作の最大数

    // 一括操作のバリデーション
    /**
     * バッチリクエストを検証するメソッド
     *
     * @param requests 検証対象のリクエストリスト
     */
    public void validateBatchRequests(List<?> requests) {
        // リクエストがnullまたは空の場合、例外をスロー
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("リクエストが空です");
        }

        // リクエストのサイズが最大バッチサイズを超える場合、例外をスロー
        if (requests.size() > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("一度に処理できるタスクは" + MAX_BATCH_SIZE + "個までです");
        }
    }

    // 日付範囲の検証
    /**
     * 日付範囲を検証するメソッド
     *
     * @param startDate 検証対象の開始日
     * @param endDate   検証対象の終了日
     */
    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        // 開始日または終了日がnullの場合、例外をスロー
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("開始日と終了日は必須です");
        }

        // 開始日が終了日より後の場合、例外をスロー
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("開始日は終了日より前である必要があります");
        }

        // 日付範囲が1年を超える場合、例外をスロー
        if (ChronoUnit.DAYS.between(startDate, endDate) > 365) {
            throw new IllegalArgumentException("日付範囲は1年以内である必要があります");
        }
    }

    /**
     * 優先度を検証するメソッド
     * 
     * @param priority 優先度の値
     */
    public void validatePriority(Integer priority) {
        // 優先度がnullの場合、例外をスロー
        if (priority == null) {
            throw new IllegalArgumentException("優先度は必須です");
        }
        // 優先度が1から5の範囲外の場合、例外をスロー
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("優先度は1から5の間である必要があります");
        }
    }

}
