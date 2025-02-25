package com.example.demo.domain.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.repository.CreateTaskRepository;

@Service
public class CreateTaskService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    MainPageService mainPageService;

    @Autowired
    CreateTaskRepository repository;

    /**
     * statusのMapを作成する処理
     * 
     * @return
     */
    public Map<Integer, String> getStatusMap() {
        // 戻り値を宣言
        Map<Integer, String> reMap = new HashMap<>();

        // メッセージプロパティから項目名を取得しMapに格納する
        String not_started = messageSource.getMessage("not_started", null, Locale.JAPAN);
        String in_confirmation = messageSource.getMessage("in_confirmation", null, Locale.JAPAN);
        String in_progress = messageSource.getMessage("in_progress", null, Locale.JAPAN);
        // String end = messageSource.getMessage("end", null, Locale.JAPAN);
        // String suspension = messageSource.getMessage("suspension", null,
        // Locale.JAPAN);

        reMap.put(1, not_started);
        reMap.put(2, in_confirmation);
        reMap.put(3, in_progress);
        // reMap.put(4, end);
        // reMap.put(5, suspension);

        return reMap;
    }

    /**
     * 優先度に応じた表示用のマップを取得するメソッド
     * 
     * @return 優先度とそれに対応する表示文字列を保持するマップ
     */
    public Map<Integer, String> getPriorityDisplayMap() {
        // 戻り値となるマップの定義
        Map<Integer, String> priorityMap = new HashMap<>();
        priorityMap.put(1, "至急！");
        priorityMap.put(2, "高");
        priorityMap.put(3, "中");
        priorityMap.put(4, "低");
        priorityMap.put(5, "未設定");

        return priorityMap;
    }

    // public Map<Integer, String> getPriorityDisplayMap() {
    // return Map.ofEntries(
    // Map.entry(1, "至急！"),
    // Map.entry(2, "高"),
    // Map.entry(3, "中"),
    // Map.entry(4, "低"),
    // Map.entry(5, "未設定"));
    // }

    /**
     * 今日の日付をLocalDate型をString型で取得
     * 
     * @return
     */
    public String todayLocalDate() {
        // 今日の日付をLocalDate型で取得
        LocalDateTime localDate = LocalDateTime.now();
        // String型のパターンを指定する
        String pattern = "yyyy-MM-dd HH:mm";
        // フォーマットする
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // 戻り値の変数に格納
        String today = localDate.format(formatter);

        return today;
    }

    /**
     * 日付をString型からLocalDateTime型へ変換する処理
     * 
     * @param dueDate
     * @return
     */
    public LocalDateTime createDay(String dueDate) {
        // 戻り値を宣言
        LocalDateTime createDayLocalDateTime = null;

        try {
            createDayLocalDateTime = LocalDateTime.parse(dueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (Exception e) {
            System.out.println(e);
        }
        return createDayLocalDateTime;
    }

    /**
     * 受け取った日付をDate型に変換する処理
     * 
     * @param dueDate
     * @return
     */
    public Date dueDay(String dueDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date creDate = format.parse(dueDate);
            return creDate;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * 新しいタスクの登録処理
     * 
     * @param title
     * @param description
     * @param status
     * @param dueDate
     * @return
     */
    public int setNewCreateTask(TaskDto dto) {

        // ユーザーIDをログイン情報から取得
        Integer userId = mainPageService.getLoggedInUserId();
        // ユーザーIDをdtoにセット
        dto.setUserId(userId);

        return repository.createTask(dto);
    }

    /**
     * 今日の日付を取得しString型に変換する処理
     * 
     * @return
     */
    public String createToday() {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createToday = dateFormat.format(today);
        return createToday;
    }

    public String dateConversion(Date days) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String day = fmt.format(days);
            return day;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * 修正対象のレコード取得処理
     * 
     * @param id
     * @return
     */
    public TaskDto getEditTasks(Integer id) {
        TaskDto dto = repository.getEditTasks(id);
        return dto;
    }

    /**
     * 修正対象のレコードの修正処理
     * 
     * @param dto
     * @return
     */
    public int updateTask(TaskDto dto) {
        return repository.editTsks(dto);
    }
}
