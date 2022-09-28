package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdManager {
    public static String runCmd(String cmdStr) {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", cmdStr);
        processBuilder.redirectErrorStream(false);
        Process process = null;
        String result = "";
        try {
            process = processBuilder.start();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), "Shift_JIS"))) {
            String line = null;
            while ((line = r.readLine()) != null) {
                result += line + "\n";
            }
            assert result != null;

        } catch (Exception exception) {
            System.err.println("コマンドの実行中に予期せぬエラーが発生しました。");
            exception.printStackTrace();
        }
        return result;
    }


}
