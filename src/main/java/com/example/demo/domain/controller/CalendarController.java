package com.example.demo.domain.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.repository.CreateTaskRepository;
import com.example.demo.domain.repository.MainPageRepository;
import com.example.demo.domain.service.MainPageService;

@Controller
public class CalendarController {

    @Autowired
    MainPageService service;

    @Autowired
    MainPageRepository repository;

    @Autowired
    CreateTaskRepository createTaskRepository;

    /**
     * カレンダーを表示するメソッド
     * 
     * @param model モデル
     * @param year  年
     * @param month 月
     * @return カレンダー画面
     */
    @GetMapping("/calendar")
    public String showCalender(Model model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        // 現在の日付を取得
        LocalDate today = LocalDate.now();
        // パラメータがnullでない場合はその値を使用し、nullの場合は現在の年を設定
        year = (year != null) ? year : today.getYear();
        // パラメータがnullでない場合はその値を使用し、nullの場合は現在の月を設定
        month = (month != null) ? month : today.getMonthValue();

        // 指定された年月の最初の日を取得
        LocalDate firstDay = LocalDate.of(year, month, 1);

        // 最終日を取得
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        // カレンダーを保持するリストを作成
        List<List<LocalDate>> calendar = new ArrayList<>();
        // カレンダーの開始日を設定
        LocalDate date = firstDay.minusDays(firstDay.getDayOfWeek().getValue());

        // カレンダーを構築
        while (date.isBefore(lastDay) || date.getMonthValue() == month) {
            List<LocalDate> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                week.add(date);
                date = date.plusDays(1);
            }
            calendar.add(week);
        }

        // 日本語の曜日表示用
        String[] weekDays = { "日", "月", "火", "水", "木", "金", "土" };

        // 前月・翌月へのナビゲーション用データ
        LocalDate prevMonth = firstDay.minusMonths(1);
        LocalDate nextMonth = firstDay.plusMonths(1);

        // ログインユーザーの情報取得
        Integer userId = service.getLoggedInUserId();
        String username = repository.getUserNameById(userId);
        boolean isAdmin = service.isAdmin();

        List<TaskDto> monthTasks = repository.getTaskByMonth(userId, year, month, 0, Integer.MAX_VALUE);

        // モデルに属性を追加
        model.addAttribute("calendar", calendar);
        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);
        model.addAttribute("prevYear", prevMonth.getYear());
        model.addAttribute("prevMonth", prevMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("monthTasks", monthTasks);
        System.out.println(monthTasks + "ここだよ");

        return "calendar";
    }

    /**
     * タスクを編集するエンドポイント
     * 
     * @param payload クライアントから送られたデータ
     * @return 編集結果を含むレスポンス
     */
    @PostMapping("/editTask")
    @ResponseBody
    public Map<String, Object> editTask(@RequestBody Map<String, String> payload) {
        // タスクID、年、月、日を取得
        Integer taskId = Integer.parseInt(payload.get("taskId"));
        Integer year = Integer.parseInt(payload.get("year"));
        Integer month = Integer.parseInt(payload.get("month"));
        Integer day = Integer.parseInt(payload.get("day"));

        // 新しい期日の日付を作成
        LocalDateTime newDueDate = LocalDateTime.of(year, month, day, 0, 0);

        // 既存のタスク情報を取得
        TaskDto taskDto = createTaskRepository.getEditTasks(taskId);

        // タスクに新しい期日をセット
        taskDto.setDueDate(Date.from(newDueDate.atZone(ZoneId.systemDefault()).toInstant()));

        // タスクを更新
        createTaskRepository.editTsks(taskDto);

        // レスポンスを作成し、成功を示すフラグを設定
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }

}
