package util.analyze.format;

public enum ResultStatus {
    PERFECT("〇"),
    BAD("△"),
    IMPOSSIBLE("×");

    public String statusStr;

    ResultStatus(String statusStr) {
        this.statusStr = statusStr;
    }

}
