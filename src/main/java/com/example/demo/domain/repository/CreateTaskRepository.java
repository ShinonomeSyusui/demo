package com.example.demo.domain.repository;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.dto.TaskDto;

@Repository
public class CreateTaskRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    TaskDto dto;

    /**
     * 新しいタスクの登録処理
     * 
     * @param title
     * @param description
     * @param status
     * @param dueDate
     * @return
     */
    // public int createTask(String title, String description, String status,
    // LocalDateTime dueDate) {
    // String sql = "INSERT INTO tasks "
    // + "(title, description, status, du_date) "
    // + "VALUES (?,?,?,?)";

    // return jdbcTemplate.update(sql, title, description, status, dueDate);
    // }

    /**
     * 新しいタスクの登録処理
     * 
     * @param dto
     * @return
     */
    public int createTask(TaskDto dto) {
        String sql = "INSERT INTO tasks "
                + " (user_id, title, description, status, due_date, priority) "
                + "VALUES (?,?,?,?,?,?)";

        Integer status = Integer.parseInt(dto.getStatus());

        return jdbcTemplate.update(sql, dto.getUserId(), dto.getTitle(), dto.getDescription(), status,
                dto.getDueDate(), dto.getPriority());
    }

    /**
     * タスクIDから1件分のタスクレコードを所得する処理
     * 
     * @param id
     * @return
     */
    public TaskDto getEditTasks(Integer id) {
        String sql = "SELECT "
                + "id, "
                + "title, "
                + "description, "
                + "CASE "
                + " WHEN status = 1 THEN '未着手' "
                + " WHEN status = 2 THEN '確認中' "
                + " WHEN status = 3 THEN '進行中' "
                + " WHEN status = 4 THEN '完了' "
                + " WHEN status = 5 THEN '中止' "
                + " WHEN status = 6 THEN 'キャンセル' "
                + "ELSE '対象なし' "
                + "END AS status, "
                + "due_date, "
                + "priority "
                + "FROM "
                + "tasks "
                + "WHERE "
                + "id = ? ";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
        TaskDto dto = new TaskDto();
        dto.setId(id);
        dto.setTitle((String) result.get("title"));
        dto.setDescription((String) result.get("description"));
        dto.setDueDate((Date) result.get("due_date"));
        dto.setStatus((String) result.get("status"));
        dto.setPriority((Integer) result.get("priority"));

        return dto;
    }

    /**
     * タスクの修正処理
     * 
     * @param dto
     * @return
     */
    public int editTsks(TaskDto dto) {
        String sql = "UPDATE "
                + "tasks "
                + "SET "
                + "title = ?, "
                + "description = ?, "
                + "due_date = ?, "
                + "priority = ? "
                + "WHERE id = ?";

        return jdbcTemplate.update(sql, dto.getTitle(), dto.getDescription(), dto.getDueDate(),
                dto.getPriority(), dto.getId());
    }

    /**
     * タスクの日付を更新するメソッド
     * 
     * @param taskId  更新するタスクのID
     * @param dueDate 新しい期日
     * @return 更新された行数
     */
    public int updateTaskDate(Integer taskId, Date dueDate) {
        // SQLクエリ: タスクの期日を更新する
        String sql = "UPDATE tasks SET due_date = ? WHERE id = ?";

        // jdbcTemplateを使用してSQLクエリを実行し、影響を受けた行数を返す
        return jdbcTemplate.update(sql, dueDate, taskId);
    }

}
