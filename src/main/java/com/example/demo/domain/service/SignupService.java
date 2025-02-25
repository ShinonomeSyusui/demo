package com.example.demo.domain.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.dto.UsersDto;
import com.example.demo.domain.repository.MainPageRepository;

@Service
public class SignupService {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    MainPageRepository repository;

    @Autowired
    MessageSource messageSource;

    /**
     * アカウント登録処理
     * 
     * @param dto
     * @return
     */
    public int signup(UsersDto dto) {

        String password = dto.getPassword();

        password = encoder.encode(password);
        dto.setPassword(password);

        return repository.insertUserDetails(dto);
    }

    /**
     * 今日の日付をString型で取得
     * 
     * @return toDay
     */
    public String todays() {
        // 戻り値の宣言
        String toDay = "";

        // 日付のパターンを指定
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // 今日の日付を取得
        Date today = new Date();

        // 戻り値の変数に取得した今日の日付を格納し返却
        toDay = fmt.format(today);
        return toDay;
    }

    /**
     * 性別のMapを作成
     * 
     * @return
     */
    public Map<String, Integer> genderMap() {
        // 戻り値の宣言
        Map<String, Integer> reMap = new LinkedHashMap<String, Integer>();

        // メッセージソースから格納する値を取得
        String male = messageSource.getMessage("male", null, Locale.JAPAN);
        String female = messageSource.getMessage("female", null, Locale.JAPAN);
        String other = messageSource.getMessage("other", null, Locale.JAPAN);

        // 戻り値のマップにそれぞれの値を格納
        reMap.put(male, 1);
        reMap.put(female, 2);
        reMap.put(other, 3);

        return reMap;
    }

    /**
     * 血液型のList作成
     * 
     * @return reBloods
     */
    public List<String> bloods() {

        // メッセージソースから格納する値を所得
        String typeA = messageSource.getMessage("typeA", null, Locale.JAPAN);
        String typeB = messageSource.getMessage("typeB", null, Locale.JAPAN);
        String typeO = messageSource.getMessage("typeO", null, Locale.JAPAN);
        String typeAB = messageSource.getMessage("typeAB", null, Locale.JAPAN);
        String unknown = messageSource.getMessage("unknown", null, Locale.JAPAN);

        // 戻り値の宣言とListの内容を格納
        List<String> reBloods = List.of(typeA, typeB, typeO, typeAB, unknown);
        return reBloods;
    }

    /**
     * 1980年1月1日を取得する処理
     * 
     * @return
     */
    public String getEighties() {

        // フォーマットパターンを指定
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        // グレゴリオ暦カレンダーを1980年1月1日で初期化する
        GregorianCalendar theEighties = new GregorianCalendar(1980, 0, 1);

        // グレゴリオ暦を、直接フォーマットし文字列に変換して戻り値に格納
        String reDays = String.valueOf(fmt.format(theEighties.getTime()));

        return reDays;
    }
}
