package com.example.demo.domain.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.service.MainPageService;

@Controller
public class DailyViewController {

    @Autowired
    private MainPageService mainPageService;

    @GetMapping("/daily")
    public String showDailyView(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            Model model) {
        // ユーザー情報を設定
        Integer userId = mainPageService.getLoggedInUserId();
        String username = mainPageService.getLoggedInUserEmail();
        boolean isAdmin = mainPageService.isAdmin();

        // 日付が指定されていない場合は今日の日付を使用
        if (date == null) {
            date = new Date();
        }

        // 日付をフォーマット
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);

        // 今日のタスクを取得
        List<TaskDto> dailyTasks = mainPageService.getDailyTasks(userId, date);

        // モデルに必要な情報を追加
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("tasks", dailyTasks);
        model.addAttribute("currentDate", date);

        return "dailyView";
    }
}
