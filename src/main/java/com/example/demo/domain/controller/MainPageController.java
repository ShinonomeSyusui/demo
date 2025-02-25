package com.example.demo.domain.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.repository.MainPageRepository;
import com.example.demo.domain.service.MainPageService;

@Controller
public class MainPageController {

    @Autowired
    MainPageRepository repository;

    @Autowired
    MainPageService service;

    /**
     * ログイン成功時の画面遷移処理
     * 
     * @param model
     * @param page
     * @param year
     * @param month
     * @param usersName
     * @return
     */
    @GetMapping("/mainPage")
    public String showMainPage(Model model, @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String usersName,
            @RequestParam(required = false) String showAll) {
        // 1ページあたりの表示数を設定
        int pageSize = 10;

        int countAll;

        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        if (service.isAdmin()) {
            List<TaskDto> tasks;
            if ("true".equals(showAll)) {
                tasks = service.getAllUsersTasks(page, pageSize);
                countAll = repository.getAllUserTasksCount();

                model.addAttribute("task", tasks);
                model.addAttribute("isAdmin", true);
            } else if (usersName != null && !usersName.isEmpty()) {
                // ユーザー名で検索
                tasks = service.getTasksByUsername(usersName, year, month,
                        page, pageSize);
                countAll = repository.getCountByUsername(usersName, year, month);
                model.addAttribute("task", tasks);
                model.addAttribute("isAdmin", true);
            } else {

                // 管理者の場合の処理
                tasks = service.getAllUsersTasks(page, pageSize);
                countAll = repository.getAllUserTasksCount(); // 全タスク

                // モデルにタスクと管理者フラグを追加
                model.addAttribute("task", tasks);
                model.addAttribute("isAdmin", true);
            }
        } else {
            // 一般ユーザーの場合の処理
            Integer userId = service.getLoggedInUserId();
            String username = repository.getUserNameById(userId);
            List<TaskDto> tasks = service.getTasksByMonth(userId, year, month, page, pageSize);
            countAll = repository.getCountAllByMonth(userId, year, month);

            // モデルにユーザー名、タスク、管理者フラグを追加
            model.addAttribute("username", username);
            model.addAttribute("task", tasks);
            model.addAttribute("isAdmin", false);
        }

        // モデルに全体のタスク数、現在のページ、総ページ数を追加
        model.addAttribute("countAll", countAll);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) countAll / pageSize));
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("searchUsername", usersName);

        return "mainPage";
    }

    // public String showMainPage(Model model, @RequestParam(defaultValue = "1") int
    // page) {
    // // ログインユーザーIDを取得
    // Integer userId = service.getLoggedInUserId();

    // // ユーザー名をユーザーIDから取得
    // String username = repository.getUserNameById(userId);

    // // ページサイズの設定
    // int pageSize = 10;

    // // ユーザーの全タスク数を取得
    // int countAll = repository.getCountAll(userId);

    // // 総ページ数を計算
    // int totalPages = (int) Math.ceil((double) countAll / pageSize);

    // // ページングされたタスクリストを取得
    // List<TaskDto> tasks = service.getPagedTasks(userId, page, pageSize);

    // // モデルに属性を追加
    // model.addAttribute("task", tasks);
    // model.addAttribute("username", username);
    // model.addAttribute("countAll", countAll);
    // model.addAttribute("currentPage", page);
    // model.addAttribute("totalPages", totalPages);

    // return "mainPage";
    // }

    // @GetMapping("/mainPage")
    // public String showMainPage(Model model) {

    // // ログイン情報からユーザーのメールアドレスを取得
    // // String email = service.getLoggedInUserEmail();

    // // ログイン情報からユーザーのIDを取得
    // Integer userId = service.getLoggedInUserId();

    // // ユーザーIDからユーザー名を取得
    // String username = repository.getUserNameById(userId);

    // // 指定したユーザーの全タスクを取得する処理
    // List<TaskDto> task = repository.getAllTasks(userId);

    // int countAll = repository.getCountAll(userId);
    // // 取得したタスクの期限日の表示パターンを変更する処理
    // // for (TaskDto t : task) {
    // // String convertdeDate = dateConversion(t.getDueDate());
    // // t.setFormattedDueDate(convertdeDate);
    // // }

    // // 結果をModelに格納して画面遷移へ
    // model.addAttribute("task", task);
    // model.addAttribute("username", username);
    // model.addAttribute("countAll", countAll);
    // System.out.println("ここです｛" + countAll + "｝ここです");
    // // System.out.println("ここです→" + task + "←ここです。");
    // return "mainPage";
    // }

    /**
     * 完了、削除ボタンそれぞれの処理を行うメソッド
     *
     * @param actionNum アクション番号
     * @param id        タスクID
     * @param year      年
     * @param month     月
     * @param page      ページ
     * @param model     モデル
     * @return リダイレクト先URL
     */
    @PostMapping("/taskChange")
    public String delete(@RequestParam("action") int actionNum,
            @RequestParam("taskId") int id,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @ModelAttribute Model model) {

        // アクション番号に応じて処理を分岐
        switch (actionNum) {
            case 2:
                service.completed(id);
                break;

            case 3:
                service.deleteTask(id);
                break;

            default:
                break;
        }

        // 年と月が指定されている場合は、指定された年月とページにリダイレクト
        if (year != null && month != null) {
            return "redirect:/mainPage?year=" + year + "&month=" + month + "&page=" + page;
        }

        // 年と月が指定されていない場合は、現在のページにリダイレクト
        return "redirect:/mainPage" + year + "&month=" + month + "&page=" + page;

    }

    /**
     * 更新のドロップダウンリンクからのそれぞれの処理を行うメソッド
     *
     * @param selectNum 選択番号
     * @param id        タスクID
     * @param year      年
     * @param month     月
     * @param page      ページ
     * @return リダイレクト先URL
     */
    @GetMapping(path = "/statusSelect/{selectNum}", params = "taskId")
    public String progress(@PathVariable("selectNum") Integer selectNum,
            @RequestParam(value = "taskId", required = false) Integer id,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        // 選択番号とタスクIDを使ってサービスの状態選択メソッドを呼び出す
        service.statusSelect(selectNum, id);

        // 年と月が指定されている場合は、指定された年月とページにリダイレクト
        if (year != null && month != null) {
            return "redirect:/mainPage?year=" + year + "&month=" + month + "&page=" + page;
        }

        // 年と月が指定されていない場合は、現在のページにリダイレクト
        return "redirect:/mainPage" + year + "&month=" + month + "&page=" + page;
    }

    /**
     * 取得した日付を変換して返す処理
     * 
     * @param day
     * @return
     */
    // private String dateConversion(LocalDateTime day) {
    // // 出力フォーマット指定
    // DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日
    // HH時mm分");
    // return day.format(outputFormatter);
    // }
}
