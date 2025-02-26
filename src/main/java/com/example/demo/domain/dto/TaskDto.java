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

    /**
     * 優先度に応じて表示用の文字列を返すメソッド
     * 
     * @return 表示用の文字列
     */
    public String getPriorityDisplay() {
        if (this.priority == 1)
            return "至急！"; // 優先度が1の場合は"至急！"を返す
        if (this.priority == 2)
            return "高"; // 優先度が2の場合は"高"を返す
        if (this.priority == 3)
            return "中"; // 優先度が3の場合は"中"を返す
        if (this.priority == 4)
            return "低"; // 優先度が4の場合は"低"を返す
        return "未設定"; // 上記の条件に当てはまらない場合は"未設定"を返す
    }

    /**
     * 優先度に応じて適切なテキスト色を返すメソッド
     * 
     * @return テキスト色を表す文字列
     */
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

    /**
     * ステータスに応じて適切なバッジの色を返すメソッド
     * 
     * @return バッジの色を表す文字列
     */
    public String textBadgeColors() {
        if (this.status.equals("完了"))
            return "bg-secondary text-decoration-line-through"; // 完了状態の場合は灰色で取り消し線をつける
        if (this.status.equals("確認中"))
            return "btn-bg-orange"; // 確認中の場合はオレンジ色の背景
        if (this.status.equals("進行中"))
            return "bg-info"; // 進行中の場合は青色の背景
        if (this.status.equals("中止") || this.status.equals("キャンセル"))
            return "btn-bg-cokered text-decoration-line-through"; // 中止またはキャンセルの場合は赤色の背景で取り消し線をつける
        if (this.status.equals("未着手"))
            return "bg-light text-dark"; // 未着手の場合は薄い灰色の背景に黒色のテキスト
        return "bg-secondary"; // 上記条件に当てはまらない場合は灰色の背景を返す
    }

    public String getPriorityColorClass() {
        return "priority-" + (this.priority != null ? this.priority : 5);
    }
}
