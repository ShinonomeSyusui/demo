package com.example.demo.domain.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("deprecation")
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // ユーザー名とパスワードを取得するSQL文
        // String sql = "SELECT email, password FROM users WHERE email = ?";

        String sql = "SELECT "
                + "u.email, "
                + "u.password, "
                + "a.authority "
                + "FROM users AS u "
                + "INNER JOIN "
                + "user_role AS ur "
                + "ON u.id = ur.user_id "
                + "INNER JOIN "
                + "authorites AS a "
                + "ON a.id = ur.role_id "
                + "WHERE email = ? ";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[] { email }, (rs, rowNum) -> {
                String fetchedEmail = rs.getString("email");
                String password = rs.getString("password");
                String userRole = rs.getString("authority");

                return User.withUsername(fetchedEmail)
                        .password(password)
                        .roles(userRole)
                        .build();
            });
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User not found with email:" + email);
        }

    }
    // UserDetails user = jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
    // User.withUsername(rs.getString("email"))
    // .password(rs.getString("password"))
    // .build(), email);

    // if (user == null) {
    // throw new UsernameNotFoundException("User not found:" + email);
    // }

    // // 権限情報を取得
    // String sqlAuth = "SELECT authority FROM authorities WHERE username = ?";
    // List<String> authList = jdbcTemplate.queryForList(sqlAuth, String.class,
    // email);

    // return User.withUserDetails(user)
    // .authorities(authList.toArray(new String[0]))
    // .build();
    // }
}
