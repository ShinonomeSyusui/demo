package com.example.demo.domain.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.domain.service.MainPageService;

@Component
public class TaskApiSecurityCheck {

    @Autowired
    private MainPageService mainPageService;

    public void validateUseAccess(Integer taskId) {
        Integer currentUserId = mainPageService.getLoggedInUserId();
        boolean isAdmin = mainPageService.isAdmin();

        if (!isAdmin && !hasAccessToTask(currentUserId, taskId)) {
            throw new SecurityException("このタスクへのアクセス権限がありません");
        }
    }

    private boolean hasAccessToTask(Integer userId, Integer taskId) {
        // タスク所有者のチェックロジックを実装
        return true; // 仮実装
    }
}
