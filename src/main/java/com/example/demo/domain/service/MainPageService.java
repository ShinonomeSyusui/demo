package com.example.demo.domain.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.repository.MainPageRepository;
import com.example.demo.domain.restcontroller.ResourceNotFoundException;
import com.example.demo.domain.restcontroller.TaskPriorityUpdateRequest;

@Service
public class MainPageService {
    @Autowired
    MainPageRepository repository;

    @Autowired
    TaskDto dto;

    @Autowired
    JdbcTemplate jdbcTemplate;

    TaskPriorityUpdateRequest priorityUpdateRequest;

    /**
     * 管理者(admin)用のログイン処理
     * 
     * @return
     */
    public boolean isAdmin() {
        // 認証情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 認証情報がnullでないこと、かつ権限に"ROLE_ADMIN"が含まれていることを確認
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * 管理者(admin)権限でログインした際のページネイションの処理
     * 
     * @param page
     * @param pageSize
     * @return
     */
    public List<TaskDto> getAllUsersTasks(int page, int pageSize) {
        // ページングのオフセットを計算
        int offset = (page - 1) * pageSize;

        // オフセットとページサイズを使用して、リポジトリからユーザーのタスクを取得
        return repository.getAllUsersTasks(offset, pageSize);
    }

    /**
     * Authenticationからログインユーザーのメールアドレスを取得する処理
     * 
     * @return
     */
    public String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            // 認証されたユーザーのメールアドレスを取得
            return ((User) authentication.getPrincipal()).getUsername();
        }
        return null; // 認証情報が存在しない場合
    }

    /**
     * AuthenticationからログインユーザーのIDを取得する処理
     * 
     * @return
     */
    public Integer getLoggedInUserId() {
        // ログイン中のユーザーIDを取得するメソッド
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 認証情報が存在し、かつ認証情報の主体がUserクラスのインスタンスである場合
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            // Userクラスのインスタンスを取得
            User userDetails = (User) authentication.getPrincipal();

            // ユーザーのメールアドレスを取得
            String email = userDetails.getUsername();

            // メールアドレスを使ってリポジトリからユーザーIDを取得して返す
            return repository.getUserIdByEmail(email);
        }

        // ログインしていない場合はnullを返す
        return null;
    }

    /**
     * 更新のドロップダウンからの処理
     * 
     * @param selectNum
     * @param id
     * @return
     */
    public int statusSelect(Integer selectNum, Integer id) {
        return repository.statusSelect(selectNum, id);
    }

    /**
     * 完了ボタンの処理
     * 
     * @param id
     * @return
     */
    public int completed(Integer id) {
        return repository.completed(id);
    }

    /**
     * 削除ボタンの処理
     * 
     * @param id
     * @return
     */
    public int deleteTask(Integer id) {
        return repository.delete(id);
    }

    /**
     * このメソッドは、特定のユーザーのタスクをページ単位で取得します。
     * 
     * @param userId   タスクを取得するユーザーのID
     * @param page     取得するページ番号（1から始まる）
     * @param pageSize ページごとのタスク数
     * @return 指定されたページ番号とページサイズに基づいてページネーションされたタスクを表すTaskDtoオブジェクトのリスト
     */
    public List<TaskDto> getPagedTasks(Integer userId, int page, int pageSize) {
        // オフセットを計算します
        int offset = (page - 1) * pageSize;

        // リポジトリから指定されたユーザーの指定されたページとページサイズに基づいたタスクを取得します
        return repository.getPagedTasks(userId, offset, pageSize);
    }

    /**
     * 指定されたユーザーID、年、月、ページ番号、ページサイズに基づいて月のタスクを取得する
     *
     * @param userId   ユーザーID
     * @param year     年
     * @param month    月
     * @param page     ページ番号
     * @param pageSize ページサイズ
     * @return 月のタスクリスト
     */
    public List<TaskDto> getTasksByMonth(Integer userId, int year, int month, int page, int pageSize) {
        // ページ番号からオフセットを計算する
        int offset = (page - 1) * pageSize;

        // 指定されたユーザーID、年、月、オフセット、ページサイズに基づいてリポジトリからタスクを取得して返す
        return repository.getTaskByMonth(userId, year, month, offset, pageSize);
    }

    /**
     * ユーザー名、年、月、ページ、ページサイズを使用してタスクを取得するメソッド
     *
     * @param usersName ユーザー名
     * @param year      年
     * @param month     月
     * @param page      ページ番号
     * @param pageSize  ページサイズ
     * @return タスクのリスト
     */
    public List<TaskDto> getTasksByUsername(String usersName, int year, int month, int page, int pageSize) {
        // オフセットを計算
        int offset = (page - 1) * pageSize;

        // リポジトリからユーザー名、年、月、オフセット、ページサイズを使用してタスクを取得
        return repository.getTasksByUserName(usersName, year, month, offset, pageSize);
    }

    /**
     * タスクの優先度を更新するメソッド
     * 
     * @param taskId   タスクのID
     * @param priority 更新する優先度
     */
    public void updateTaskPriority(Integer taskId, Integer priority) {
        System.out.println("SQL実行前の確認 - taskId: " + taskId + ", priority: " + priority);
        // SQLクエリ: 優先度を更新し、削除フラグが立っていないタスクを対象とする
        String sql = "UPDATE tasks SET priority = ? WHERE id = ? AND del_flg = '0'";

        // SQLクエリを実行し、更新された行数を取得
        int updatedRows = jdbcTemplate.update(sql, priority, taskId);

        // 更新された行が0の場合は、指定したタスクが見つからなかったとして例外をスロー
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        sql = "UPDATE tasks SET priority = ? WHERE id = ? AND del_flg = '0'";
        jdbcTemplate.update(sql, priority, taskId);

        System.out.println("SQL実行完了");
    }

    /**
     * 複数のタスクの優先度を一括で更新するメソッド
     * 
     * @param requests 更新リクエストのリスト（各リクエストにはタスクIDと新しい優先度が含まれる）
     */
    public void updateBatchPriorities(List<TaskPriorityUpdateRequest> requests) {
        // SQLクエリ: 優先度を更新し、削除フラグが立っていないタスクを対象とする
        String sql = "UPDATE tasks SET priority = ? WHERE id = ? AND del_flg = '0'";

        // 各リクエストに対してSQLクエリを実行
        for (TaskPriorityUpdateRequest request : requests) {
            jdbcTemplate.update(sql, request.getPriority(), request.getTaskId());
        }
    }

    public List<TaskDto> getDailyTasks(Integer userId, Date date) {
        String sql = """
                SELECT t.*, u.username
                FROM tasks t
                JOIN users u ON t.user_id = u.id
                WHERE t.user_id = ?
                AND DATE(t.due_date) = ?
                AND t.del_flg = '0'
                ORDER BY t.start_time
                """;

        return jdbcTemplate.query(sql,
                new Object[] { userId, date },
                (rs, rowNum) -> {
                    TaskDto task = new TaskDto();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setStatus(rs.getString("status"));
                    task.setDueDate(rs.getTimestamp("due_date"));
                    task.setStartTime(rs.getTime("start_time"));
                    task.setEndTime(rs.getTime("end_time"));
                    task.setPriority(rs.getInt("priority"));
                    task.setUsername(rs.getString("username"));
                    return task;
                });
    }

    public void updateTaskTime(Integer taskId, String startTime, String endTime) {
        String sql = "UPDATE tasks SET start_time = ?, end_time = ? WHERE id = ? AND del_flg = '0'";
        int updatedRows = jdbcTemplate.update(sql, startTime, endTime, taskId);

        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }
    }

    public void createDailyTask(TaskDto taskDto) {
        String sql = """
                    INSERT INTO tasks (
                        user_id, title, description, status,
                        due_date, start_time, end_time, priority,
                        created_at, updated_at, del_flg
                    ) VALUES (
                        ?, ?, ?, ?,
                        CURRENT_DATE, ?, ?, ?,
                        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0'
                    )
                """;

        jdbcTemplate.update(sql,
                taskDto.getUserId(),
                taskDto.getTitle(),
                taskDto.getDescription(),
                taskDto.getStatus(),
                taskDto.getStartTime(),
                taskDto.getEndTime(),
                taskDto.getPriority());
    }

}
