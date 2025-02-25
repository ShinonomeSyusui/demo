package com.example.demo.domain.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.dto.UsersDto;

@Repository
public class MainPageRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * このメソッドは、特定のユーザーのタスクをページネーションで取得します。
     * 
     * @param userId タスクを取得するユーザーのID
     * @param offset 取得する結果セットの開始インデックス
     * @param limit  取得するタスクの最大数
     * @return ページネーションされたタスクを表すTaskDtoオブジェクトのリスト
     */
    public List<TaskDto> getPagedTasks(Integer userId, int offset, int limit) {
        String sql = "SELECT "
                + "t.id, "
                + "t.title, "
                + "t.description, "
                + "CASE "
                + " WHEN t.status = 1 THEN '未着手' "
                + " WHEN t.status = 2 THEN '確認中' "
                + " WHEN t.status = 3 THEN '進行中' "
                + " WHEN t.status = 4 THEN '完了' "
                + " WHEN t.status = 5 THEN '中止' "
                + " WHEN t.status = 6 THEN 'キャンセル' "
                + "ELSE '対象なし' "
                + "END AS status, "
                + "t.due_date "
                + "FROM tasks AS t "
                + "INNER JOIN users AS u ON t.user_id = u.id "
                + "WHERE u.id = ? AND t.del_flg = '0' "
                + "ORDER BY t.due_date ASC "
                + "LIMIT ? OFFSET ?";

        // SQLクエリを実行し、BeanPropertyRowMapperを使用して結果をTaskDtoオブジェクトにマッピングします
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(TaskDto.class),
                userId, limit, offset);
    }

    /**
     * admin権限用の全ユーザーのタスクを習得する処理
     * 
     * @param offset
     * @param limit
     * @return
     */
    public List<TaskDto> getAllUsersTasks(int offset, int limit) {
        String sql = "SELECT "
                + "t.id, "
                + "t.title, "
                + "t.description, "
                + "u.username, "
                + "CASE "
                + " WHEN t.status = 1 THEN '未着手' "
                + " WHEN t.status = 2 THEN '確認中' "
                + " WHEN t.status = 3 THEN '進行中' "
                + " WHEN t.status = 4 THEN '完了' "
                + " WHEN t.status = 5 THEN '中止' "
                + " WHEN t.status = 6 THEN 'キャンセル' "
                + "ELSE '対象なし' "
                + "END AS status, "
                + "t.due_date "
                + "FROM tasks AS t "
                + "INNER JOIN users AS u ON t.user_id = u.id "
                + "WHERE t.del_flg = '0' "
                + "ORDER BY u.username, t.due_date ASC "
                + "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(TaskDto.class), limit, offset);
    }

    /**
     * 指定されたユーザーID、年、月、オフセット、リミットに基づいて月のタスクを取得する
     *
     * @param userId ユーザーID
     * @param year   年
     * @param month  月
     * @param offset オフセット
     * @param limit  リミット
     * @return 月のタスクリスト
     */
    public List<TaskDto> getTaskByMonth(Integer userId, int year, int month, int offset, int limit) {
        // SQLクエリを構築
        String sql = "SELECT "
                + "t.id, "
                + "t.title, "
                + "t.description, "
                + "CASE "
                + " WHEN t.status = 1 THEN '未着手' "
                + " WHEN t.status = 2 THEN '確認中' "
                + " WHEN t.status = 3 THEN '進行中' "
                + " WHEN t.status = 4 THEN '完了' "
                + " WHEN t.status = 5 THEN '中止' "
                + " WHEN t.status = 6 THEN 'キャンセル' "
                + "ELSE '対象なし' "
                + "END AS status, "
                + "t.due_date, "
                + "priority "
                + "FROM tasks AS t "
                + "INNER JOIN users AS u ON t.user_id = u.id "
                + "WHERE u.id = ? "
                + "AND YEAR(t.due_date) = ? "
                + "AND MONTH(t.due_date) = ? "
                + "AND t.del_flg = '0' "
                + "ORDER BY t.due_date ASC "
                + "LIMIT ? OFFSET ?";

        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、タスクリストを取得して返す
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(TaskDto.class),
                userId, year, month, limit, offset);
    }

    // 月別のタスクの総数を取得する処理
    /**
     * 指定されたユーザーID、年、月に基づいて月のタスク数を取得する
     *
     * @param userId ユーザーID
     * @param year   年
     * @param month  月
     * @return 月のタスク数
     */
    public Integer getCountAllByMonth(Integer userId, int year, int month) {
        // SQLクエリを構築
        String sql = "SELECT COUNT(*) FROM tasks t "
                + "WHERE t.user_id = ? "
                + "AND DATE_FORMAT(t.due_date, '%Y') = ? "
                + "AND DATE_FORMAT(t.due_date, '%m') = ? "
                + "AND t.del_flg = '0'";

        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、月のタスク数を取得して返す
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, String.valueOf(year),
                String.format("%02d", month));
    }

    // public List<TaskDto> getAllTasks(Integer userId) {

    // String sql = "SELECT "
    // + "t.id, "
    // + "t.title, "
    // + "t.description, "
    // + "CASE "
    // + " WHEN t.status = 1 THEN '未着手' "
    // + " WHEN t.status = 2 THEN '確認中' "
    // + " WHEN t.status = 3 THEN '進行中' "
    // + " WHEN t.status = 4 THEN '完了' "
    // + " WHEN t.status = 5 THEN '中止' "
    // + " WHEN t.status = 6 THEN 'キャンセル' "
    // + "ELSE '対象なし' "
    // + "END AS status, "
    // + "t.due_date "
    // + "FROM tasks AS t "
    // + "INNER JOIN users AS u ON t.user_id = u.id "
    // + "WHERE u.id = ? AND t.del_flg = '0' "
    // + "ORDER BY t.due_date ASC "
    // + "LIMIT 10";

    // RowMapper<TaskDto> rowMapper = (rs, rowNum) -> {
    // TaskDto task = new TaskDto();
    // task.setId(rs.getInt("id"));
    // task.setTitle(rs.getString("title"));
    // task.setDescription(rs.getString("description"));
    // task.setStatus(rs.getString("status"));
    // task.setDueDate(rs.getTimestamp("due_date"));
    // return task;
    // };
    // return jdbcTemplate.query(sql, rowMapper, userId);
    // }

    /**
     * ステータスを完了に変更する処理
     *
     * @param id タスクID
     * @return 更新された行数
     */
    public int completed(Integer id) {
        // SQLクエリを構築してタスクのステータスを完了に更新
        String sql = "UPDATE "
                + "tasks "
                + "SET status = '4' "
                + "WHERE "
                + " id = ? ";
        // パラメータを使用してJDBCテンプレートを介して更新を実行し、更新された行数を返す
        return jdbcTemplate.update(sql, id);
    }

    /**
     * ステータスを変更する処理
     *
     * @param selectNum 変更後のステータス番号
     * @param id        タスクID
     * @return 更新された行数
     */
    public int statusSelect(Integer selectNum, Integer id) {
        // SQLクエリを構築して指定したステータスにタスクのステータスを更新
        String sql = "UPDATE "
                + "tasks "
                + "SET status = ? "
                + "WHERE "
                + " id = ? ";
        // パラメータを使用してJDBCテンプレートを介して更新を実行し、更新された行数を返す
        return jdbcTemplate.update(sql, selectNum, id);
    }

    /**
     * 削除フラグを追加する処理
     *
     * @param id タスクID
     * @return 更新された行数
     */
    public int delete(Integer id) {
        // SQLクエリを構築して指定したタスクの削除フラグを設定
        String sql = "UPDATE "
                + "tasks "
                + "SET del_flg = '1' "
                + "WHERE "
                + " id = ? ";
        // パラメータを使用してJDBCテンプレートを介して更新を実行し、更新された行数を返す
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Eメールからユーザー名を取得する処理
     *
     * @param email ユーザーのEメールアドレス
     * @return ユーザー名
     */
    public String getUserNameByEmail(String email) {
        // SQLクエリを構築して指定したEメールに対応するユーザー名を取得
        String sql = "SELECT username FROM users WHERE email = ?";
        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、結果として取得したユーザー名を返す
        return jdbcTemplate.queryForObject(sql, String.class, email);
    }

    /**
     * EメールからユーザーIDを取得する処理
     *
     * @param email ユーザーのEメールアドレス
     * @return ユーザーID
     */
    @SuppressWarnings("null")
    public int getUserIdByEmail(String email) {
        // SQLクエリを構築して指定したEメールに対応するユーザーIDを取得
        String sql = "SELECT id FROM users WHERE email = ?";
        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、結果として取得したユーザーIDを返す
        return jdbcTemplate.queryForObject(sql, int.class, email);
    }

    /**
     * ユーザーIDからユーザー名を取得する処理
     *
     * @param userId ユーザーのID
     * @return ユーザー名
     */
    public String getUserNameById(Integer userId) {
        // SQLクエリを構築して指定したユーザーIDに対応するユーザー名を取得
        String sql = "SELECT username FROM users WHERE id = ?";
        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、結果として取得したユーザー名を返す
        return jdbcTemplate.queryForObject(sql, String.class, userId);
    }

    /**
     * アカウントを登録する処理
     *
     * @param dto ユーザーの詳細情報を含むDTOオブジェクト
     * @return 登録されたユーザーの数
     */
    public int insertUserDetails(UsersDto dto) {
        // SQLクエリを構築してユーザーの詳細情報をusersテーブルに挿入
        String sql = "INSERT INTO users "
                + " (username, email, password) "
                + "VALUES (?,?,?)";
        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、結果として登録されたユーザーの数を返す
        return jdbcTemplate.update(sql, dto.getName(), dto.getEmail(), dto.getPassword());
    }

    /**
     * 指定されたユーザーが所有するタスクの総数を取得する処理
     *
     * @param userId タスク数を取得したいユーザーのID
     * @return ユーザーが所有するタスクの総数
     */
    public Integer getCountAll(Integer userId) {
        // 指定されたユーザーIDに紐づくデータベース内のタスクの総数をカウントするSQLクエリ
        String sql = "SELECT COUNT(*) FROM tasks WHERE user_id = ? AND del_flg = '0' ";
        // パラメータを使用してJDBCテンプレートを介してクエリを実行し、結果としてユーザーが所有するタスクの総数を取得
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    /**
     * 全ユーザーの所有するタスクの総数を取得する処理
     *
     * @return 全ユーザーが所有するタスクの総数
     */
    public Integer getAllUserTasksCount() {
        // データベース内の全てのタスクの総数をカウントするSQLクエリ
        String sql = "SELECT COUNT(*) FROM tasks WHERE del_flg = '0' ";
        // JDBCテンプレートを介してクエリを実行し、結果として全ユーザーが所有するタスクの総数を取得
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * ページネーションを伴った1ユーザーのタスクを取得する処理
     *
     * @param userId ユーザーID
     * @param limit  取得するタスクの最大数
     * @param offSet オフセット値
     * @return 指定された条件に基づいて取得したタスクのリスト
     */
    public List<TaskDto> findTasks(Integer userId, Integer limit, Integer offSet) {

        // タスク情報を取得するためのSQLクエリ
        String sql = "SELECT "
                + "t.id, "
                + "t.title, "
                + "t.description, "
                + "CASE "
                + " WHEN t.status = 1 THEN '未着手' "
                + " WHEN t.status = 2 THEN '確認中' "
                + " WHEN t.status = 3 THEN '進行中' "
                + " WHEN t.status = 4 THEN '完了' "
                + " WHEN t.status = 5 THEN '中止' "
                + " WHEN t.status = 6 THEN 'キャンセル' "
                + "ELSE '対象なし' "
                + "END AS status, "
                + "t.due_date "
                + "FROM tasks AS t "
                + "INNER JOIN "
                + "users AS u "
                + "ON t.user_id = u.id "
                + "WHERE u.id = ? "
                + "AND t.del_flg = '0' "
                + "ORDER BY t.due_date ASC "
                + "LIMIT ? OFFSET ?";

        // JDBCテンプレートを使用してSQLクエリを実行し、指定された条件に基づいてタスクを取得する
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TaskDto.class), userId, limit, offSet);

    }

    /**
     * ユーザー名、年、月、オフセット、リミットを使用してタスクを取得するメソッド
     *
     * @param usersName ユーザー名
     * @param year      年
     * @param month     月
     * @param offset    オフセット
     * @param limit     リミット
     * @return タスクのリスト
     */
    public List<TaskDto> getTasksByUserName(String usersName, int year, int month, int offset, int limit) {
        String sql = "SELECT "
                + "t.id, "
                + "t.title, "
                + "t.description, "
                + "u.username, "
                + "CASE "
                + " WHEN t.status = 1 THEN '未着手' "
                + " WHEN t.status = 2 THEN '確認中' "
                + " WHEN t.status = 3 THEN '進行中' "
                + " WHEN t.status = 4 THEN '完了' "
                + " WHEN t.status = 5 THEN '中止' "
                + " WHEN t.status = 6 THEN 'キャンセル' "
                + "ELSE '対象なし' "
                + "END AS status, "
                + "t.due_date "
                + "FROM tasks AS t "
                + "INNER JOIN users AS u ON t.user_id = u.id "
                + "WHERE u.username LIKE CONCAT('%', ?, '%') "
                + "AND YEAR(t.due_date) = ? "
                + "AND MONTH(t.due_date) = ? "
                + "AND t.del_flg = '0' "
                + "ORDER BY t.due_date ASC "
                + "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TaskDto.class), usersName, year, month,
                limit, offset);
    }

    /**
     * ユーザー名、年、月を使用してタスクの数を取得するメソッド
     *
     * @param usersName ユーザー名
     * @param year      年
     * @param month     月
     * @return タスクの数
     */
    public Integer getCountByUsername(String usersName, int year, int month) {
        // SQLクエリを作成
        String sql = "SELECT COUNT(*) FROM tasks t "
                + "INNER JOIN users u ON t.user_id = u.id "
                + "WHERE u.username LIKE CONCAT('%', ?, '%') "
                + "AND YEAR(t.due_date) = ? "
                + "AND MONTH(t.due_date) = ? "
                + "AND t.del_flg = '0'";

        // SQLクエリを実行し、結果をInteger型で返す
        return jdbcTemplate.queryForObject(sql, Integer.class, usersName, year, month);
    }

}
