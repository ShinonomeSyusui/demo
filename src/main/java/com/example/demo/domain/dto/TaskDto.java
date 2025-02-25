package com.example.demo.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class TaskDto {

    private Integer id; // ID
    private Integer userId; // ユーザーID
    private String username; // ユーザー名
    private String title; // タイトル
    private String description; // 詳細
    private String status; // 進捗
    private Date dueDate; // 締切日
    private Integer priority; // 優先度
    private String formattedDueDate; // 文字列変換後の締切日
    private Integer year;
    private Integer month;

    /**
     * 進捗表示欄の進捗状況によって文字色を変える処理
     * 
     * @return それぞれの進捗に合わせた文字色を返す
     */
    public String textcolors() {
        if (this.status.equals("完了"))
            return "text-lime";
        if (this.status.equals("確認中"))
            return "text-orange";
        if (this.status.equals("進行中"))
            return "text-info";
        if (this.status.equals("中止") || this.status.equals("キャンセル"))
            return "text-cokered";
        return null;
    }

    /**
     * 進捗表示欄以外の文字色と文字装飾を変える処理
     * 
     * @return それぞれの進捗に合わせた文字色と文字装飾を返す
     */
    public String textDecorateColors() {
        if (this.status.equals("完了"))
            return "text-decorate-secondary";
        if (this.status.equals("確認中"))
            return "text-orange";
        if (this.status.equals("進行中"))
            return "text-info";
        if (this.status.equals("中止") || this.status.equals("キャンセル"))
            return "text-decorate-cokered";
        return null;
    }

    public String getPriorityDisplay() {
        if (this.priority == 1)
            return "至急！";
        if (this.priority == 2)
            return "高";
        if (this.priority == 3)
            return "中";
        if (this.priority == 4)
            return "低";
        return "未設定";
    }

    public String textColorPriorityDisplay() {
        if (this.priority == 1)
            return "text-cokered"; // 至急！
        if (this.priority == 2)
            return "text-cokered"; // 高
        if (this.priority == 3)
            return "text-orange"; // 中
        if (this.priority == 4)
            return "text-lime"; // 低
        return null; // 未設定
    }

    // public String textColorPriorityDisplay() {
    // if (this.priority.equals("緊急") || this.priority.equals("高"))
    // return "text-cakered";
    // if (this.priority.equals("中"))
    // return "text-orange";
    // if (this.priority.equals("低"))
    // return "text-lime";
    // if (this.priority.equals("未設定"))
    // return "text-info";
    // return null;
    // }

    // public String getPriorityDisplay() {
    // return switch (this.priority) {
    // case 1 -> "🚨 緊急";
    // case 2 -> "🔥 高";
    // case 3 -> "⌛ 中";
    // case 4 -> " 低";
    // case 5 -> "💤 未設定";
    // default -> "";
    // };
    // }

    public String getPriorityColorClass() {
        return "priority-" + (this.priority != null ? this.priority : 5);
    }
}
