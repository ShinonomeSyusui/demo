package com.example.demo.domain.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupValidationService {

    @Autowired
    SignupService service;

    /**
     * 日付のフォーマットを変更する処理
     * 
     * @param inputDay
     * @return birthDay
     */
    public String dateConversion(String inputDay) {
        // 戻り値の宣言
        String birthDay = "";

        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            // 引数をDate型に変換
            Date inDay = fmt.parse(inputDay);

            // フォーマットパターンを変更
            fmt.applyPattern("yyyy年MM月dd日");

            // 文字列の変数に変更したパターンで日付を格納
            birthDay = fmt.format(inDay);
        } catch (Exception e) {

        }
        return birthDay;
    }

    /**
     * 性別のパラメータを値からKeyへ変換する処理
     * 
     * @param value
     * @return reKey
     */
    public String getKeys(Integer value) {
        // 戻り値の宣言
        String reKey = "";

        // 性別のMapを取得
        Map<String, Integer> map = service.genderMap();

        // 所得したMapを双方向マップに格納
        BidiMap<String, Integer> bidiMap = new DualHashBidiMap<String, Integer>(map);

        // 値からKeyを取得し返却
        reKey = bidiMap.getKey(value);
        return reKey;
    }
}
