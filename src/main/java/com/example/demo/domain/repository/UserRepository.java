package com.example.demo.domain.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * ユーザー登録処理
     * 
     * @param username
     * @param email
     * @param password
     * @return
     */
    public void saveUser(String username, String email, String password, String userRolls) {

        String sql = "INSERT INTO users(username, email, password, create_at, update_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        jdbcTemplate.update(sql, username, email, password);
    }

    /**
     * ユーザーの新規登録処理
     * 
     * @param name
     * @param email
     * @param rawPassword
     * @param roleName
     */
    @Transactional
    public void registerUser(String username, String email, String rawPassword, String roleName) {
        // パスワードのハッシュ化
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(rawPassword);

        // ユーザー情報をデータベースに登録
        String insertUserSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertUserSql, username, email, password);

        // 挿入したユーザーのIDを取得
        String getUserIdSql = "SELECT id FROM users WHERE email = ? ";
        Integer userId = jdbcTemplate.queryForObject(getUserIdSql, Integer.class, email);

        // ロールIDを取得
        String getRoleIdSql = "SELECT id FROM authoritis WHERE authority = ? ";
        Integer roleId = jdbcTemplate.queryForObject(getRoleIdSql, Integer.class, roleName);

        // ユーザーとロールのリレーションを'user_role'テーブルに挿入
        String insertUserRoleSql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
        jdbcTemplate.update(insertUserRoleSql, userId, roleId);
    }

    /**
     * パスワード変更処理
     * 
     * @param email
     * @param rawPassword
     */
    @Transactional
    public void updateUserPassword(String email, String rawPassword) {
        // パスワードのハッシュ化
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(rawPassword);

        System.out.println(email + "←これがユーザーのメールアドレスです。");
        String getIdSql = "SELECT id FROM users WHERE email = ? ";
        Integer userId = jdbcTemplate.queryForObject(getIdSql, Integer.class, email);
        System.out.println(userId + "←ここにユーザーのID");
        // パスワード更新処理のSQL文
        String updateSql = "UPDATE users SET password = ? WHERE id = ?";
        // パスワード更新
        jdbcTemplate.update(updateSql, password, userId);
    }
}
