package util.analyze.format;//TODO:エラーメッセージをメンバに持たせる

public enum Status {
    //↓これは１箇所でしか与えないこと
    PERFECT("問題なし"),
    FEVER("37.5℃以上の発熱あり"),
    HYPOTHERMIA("35.0℃以下の低体温あり"),
    HAVE_DETAILS("詳細の記入あり"),
    LACK_OF_TEMPERATURE("体温記録の未記入"),
    LACK_OF_DATE("日数の不足"),
    UNFORMATTED_RECORD_DATA("入力値のフォーマットエラー"),

    FILE_NAME_ERROR("ファイル名のエラー"),

    CURIOUS("異常な記録"),
    NO_RECORD("学生証番号の登録なし"),
    FORMAT_ERROR("Excelデータのフォーマットエラー"),
    EXTENSION_ERROR("データ拡張子の誤り"),
    LOAD_ERROR("読込不可"),
    NO_INFORMATION("個人情報の登録なし"),
    DATE_DIFFERENT("記録日程の誤り"),
    UNEXPECTED("予期せぬエラー");

    public String msg;

    /*
     * PERFECT
     * →完璧で問題なし
     *
     * FEVER
     * →発熱(37.5℃以上)あり
     *
     * HYPOTHERMIA
     * →低体温(35.0℃以下)あり
     *
     * HAVE_DETAILS
     * →詳細の欄への記入あり
     *
     * LACK
     * →未記入あり
     *
     * CURIOUS
     * →「ずっと体温が変化していない」、、「他のメンバーの体温と被っている」
     *  「今までに提出したものと全く同じ体温が提出されている」
     *
     * UNEXPECTED
     * →予期せぬエラー
     *
     * NO_RECORD
     * →当該学籍番号のデータベースへの登録なし
     *
     * FORMAT_ERROR
     * →Excelファイルだけどフォーマットが違う
     *
     * EXTENSION_ERROR
     * →Excelファイルの拡張子じゃない
     *
     * LOAD_ERROR
     * →Excelファイルだけど開けない
     *
     * NO_INFORMATION
     * →学籍番号の入力なし
     *
     * NO_DATE
     * →日付の入力なし
     *
     * DATE_DIFFERENT
     * →日付が違う
     *
     *　UNFORMATTED_RECORD_DATA
     * →("入力値のフォーマットエラー")、漢字で入力されている、数字で入力されていないなど
     *
     * */


    Status(String msg) {
        this.msg = msg;
    }

    public void addMsg(String msg) {
        this.msg = this.msg + "(" + msg + ")";
    }
}
