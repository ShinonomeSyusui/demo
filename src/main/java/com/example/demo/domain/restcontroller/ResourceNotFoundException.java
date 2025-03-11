package com.example.demo.domain.restcontroller;

public class ResourceNotFoundException extends RuntimeException {

    /**
     * リソースが見つからない場合にスローされる例外クラス
     * 
     * @param message エラーメッセージを指定するためのパラメータ
     */
    public ResourceNotFoundException(String message) {
        // 親クラスのコンストラクタにエラーメッセージを渡す
        super(message);
    }

    /**
     * リソースが見つからない場合にスローされる例外クラスのコンストラクタ
     * 
     * @param resourceName リソースの名前を指定するためのパラメータ
     * @param fieldName    検索対象となるフィールドの名前を指定するためのパラメータ
     * @param fieldValue   フィールドの値を指定するためのパラメータ
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // 親クラスのコンストラクタにフォーマットされたエラーメッセージを渡す
        super(String.format("%s が %s : '%s' で見つかりません", resourceName, fieldName, fieldValue));
    }

}
