package com.example.demo.domain.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

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

        // モデルに属性を追加
        model.addAttribute("calendar", calendar);
        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);
        model.addAttribute("prevYear", prevMonth.getYear());
        model.addAttribute("prevMonth", prevMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());
        model.addAttribute("weekDays", weekDays);

        return "calendar";
    }

}
