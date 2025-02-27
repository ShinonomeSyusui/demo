package com.example.demo.domain.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.form.CreateTaskForm;
import com.example.demo.domain.service.CreateTaskService;
import com.example.demo.domain.service.SignupService;

@Controller
public class CreateTaskPageController {

    @Autowired
    SignupService service;

    @Autowired
    CreateTaskService createTaskService;

    @Autowired
    TaskDto dto;

    /**
     * タスクの新規登録フォームへの画面遷移処理
     * 
     * @param form
     * @param model
     * @return
     */
    @GetMapping("/createTaskPage")
    public String getCreateTaskPage(@ModelAttribute CreateTaskForm form, Model model) {
        // 今日の日付を取得する処理
        // String today = taskService.todayLocalDate();

        String today = createTaskService.createToday();
        form.setDueDate(today);
        // form.setDueDate(createTaskService.todayLocalDate());
        // System.out.println(form.getDueDate());
        // statusのMapを取得する処理
        Map<Integer, String> tasksMap = createTaskService.getStatusMap();
        Map<Integer, String> priorityDisplayMap = createTaskService.getPriorityDisplayMap();

        // model.addAttribute("today", today);
        model.addAttribute("taskMap", tasksMap);
        model.addAttribute("priorityDisplayMap", priorityDisplayMap);
        return "/createTaskPage";
    }

    /**
     * タスクの新規登録処理
     * 
     * @param form
     * @param model
     * @return
     */
    @PostMapping("/createTaskPage/creating")
    public String createTasks(Model model, @ModelAttribute @Validated CreateTaskForm form,
            BindingResult bindingResult) {

        // バリデーションエラーが無ければ登録処理に進む
        if (!bindingResult.hasErrors()) {

            // formの値をdtoに詰め替える
            TaskDto task = new TaskDto();

            task.setTitle(form.getTitle());
            task.setDescription(form.getDescription());
            task.setStatus(form.getStatus());
            task.setDueDate(createTaskService.dueDay(form.getDueDate()));
            task.setPriority(form.getPriority());

            System.out.println(task);

            // 新しいタスクを登録
            createTaskService.setNewCreateTask(task);

            return "redirect:/mainPage";
        } else {
            // エラーがあれば、フォームに戻る
            Map<Integer, String> tasksMap = createTaskService.getStatusMap();
            model.addAttribute("taskMap", tasksMap);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return getCreateTaskPage(form, model);
        }
    }

    /**
     * タスクの修正フォームへの画面遷移処理
     * 
     * @param model
     * @param id
     * @param form
     * @return
     */
    @GetMapping(path = "/createTaskPage/edit", params = "taskId")
    public String editTasks(Model model, @RequestParam(value = "taskId", required = false) Integer id,
            CreateTaskForm form) {
        // 修正するタスクを引数のIDを元に、DBから取得
        TaskDto dto = createTaskService.getEditTasks(id);

        // 取得したレコードをFormにセット
        form.setTitle(dto.getTitle());
        form.setDescription(dto.getDescription());
        form.setStatus(dto.getStatus());
        form.setDueDate(createTaskService.dateConversion(dto.getDueDate()));
        form.setPriority(dto.getPriority());

        // 進捗のMapを取得しModelへ格納
        Map<Integer, String> tasksMap = createTaskService.getStatusMap();
        Map<Integer, String> priorityDisplayMap = createTaskService.getPriorityDisplayMap();
        model.addAttribute("taskMap", tasksMap);
        model.addAttribute("priorityDisplayMap", priorityDisplayMap);
        model.addAttribute("taskId", dto.getId());

        return "/editTaskPage";
    }

    /**
     * タスクの更新処理
     * 
     * @param model
     * @param form
     * @return
     */
    @PostMapping(path = "/createTaskPage/editTasks", params = "taskId")
    public String createEditTasks(Model model, @RequestParam(value = "taskId") Integer id,
            @ModelAttribute CreateTaskForm form) {

        // Formの内容をDtoへ格納
        TaskDto dto = new TaskDto();
        dto.setId(id);
        dto.setTitle(form.getTitle());
        dto.setDescription(form.getDescription());
        dto.setDueDate(createTaskService.dueDay(form.getDueDate()));
        dto.setPriority(form.getPriority());

        // レコードの更新
        createTaskService.updateTask(dto);

        return "redirect:/mainPage";
    }

    /**
     * カレンダーからタスクを作成するエンドポイント
     */
    @PostMapping("/createTaskPage/createFromCalendar")
    @ResponseBody
    public ResponseEntity<?> createTaskFromCalendar(@RequestBody Map<String, String> taskData) {
        try {
            TaskDto task = new TaskDto();

            // 日付の文字列を Date オブジェクトに変換
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dueDate = dateFormat.parse(taskData.get("dueDate"));

            task.setTitle(taskData.get("title"));
            task.setDescription(taskData.get("description"));
            task.setStatus("1");
            task.setDueDate(dueDate); // 変換した Date オブジェクトを設定
            task.setPriority(Integer.parseInt(taskData.get("priority")));
            task.setUserId(1); // ユーザーID設定

            createTaskService.setNewCreateTask(task);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
