package com.example.demo.domain.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TaskApiExceptionHandler {

    /**
     * セキュリティ例外を処理するメソッド
     * 
     * @param e 処理するセキュリティ例外のインスタンス
     * @return HTTPステータス403 Forbiddenとエラーメッセージを含むレスポンスエンティティ
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException e) {
        // エラーレスポンスを格納するマップを作成
        Map<String, String> response = new HashMap<>();
        // 例外メッセージをマップに追加
        response.put("error", e.getMessage());
        // HTTPステータス403 Forbiddenでレスポンスエンティティを返す
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 一般的な例外を処理するメソッド
     * 
     * @param e 処理する例外のインスタンス
     * @return HTTPステータス500 Internal Server Errorとエラーメッセージを含むレスポンスエンティティ
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        // エラーレスポンスを格納するマップを作成
        Map<String, String> response = new HashMap<>();
        // 汎用的なエラーメッセージをマップに追加
        response.put("error", "予期せぬエラーが発生しました");
        // HTTPステータス500 Internal Server Errorでレスポンスエンティティを返す
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * IllegalArgumentExceptionを処理するメソッド
     * 
     * @param e 処理するIllegalArgumentExceptionのインスタンス
     * @return HTTPステータス400 Bad Requestと例外メッセージを含むレスポンスエンティティ
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // エラーレスポンスを格納するマップを作成
        Map<String, String> response = new HashMap<>();
        // 例外から取得したメッセージをマップに追加
        response.put("error", e.getMessage());
        // HTTPステータス400 Bad Requestでレスポンスエンティティを返す
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * IllegalStateExceptionを処理するメソッド
     * 
     * @param e 処理するIllegalStateExceptionのインスタンス
     * @return HTTPステータス409 Conflictと例外メッセージを含むレスポンスエンティティ
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        // エラーレスポンスを格納するマップを作成
        Map<String, String> response = new HashMap<>();
        // 例外から取得したメッセージをマップに追加（タスクの状態が不正であることを示す）
        response.put("error", "タスクの状態が不正です: " + e.getMessage());
        // HTTPステータス409 Conflictでレスポンスエンティティを返す
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * ResourceNotFoundExceptionを処理するメソッド
     * 
     * @param e 処理するResourceNotFoundExceptionのインスタンス
     * @return HTTPステータス404 Not Foundと例外メッセージを含むレスポンスエンティティ
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        // エラーレスポンスを格納するマップを作成
        Map<String, String> response = new HashMap<>();
        // 例外から取得したメッセージをマップに追加（リソースが見つからないことを示す）
        response.put("error", "リソースが見つかりません: " + e.getMessage());
        // HTTPステータス404 Not Foundでレスポンスエンティティを返す
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
