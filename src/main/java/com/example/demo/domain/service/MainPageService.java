package com.example.demo.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.example.demo.domain.dto.TaskDto;
import com.example.demo.domain.repository.MainPageRepository;

@Service
public class MainPageService {
    @Autowired
    MainPageRepository repository;

    @Autowired
    TaskDto dto;

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

}
