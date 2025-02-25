package com.example.demo.domain.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.domain.dto.TaskDto;

@Configuration
public class JavaConfig {

    // private final DataSource dataSource;

    // public JavaConfig(DataSource dataSource) {
    // this.dataSource = dataSource;
    // }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public TaskDto dto() {
        return new TaskDto();
    }
}
