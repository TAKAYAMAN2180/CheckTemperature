package util.loader;

import util.CmdManager;
import util.analyze.format.Status;
import util.analyze.format.TemperatureFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

//ディレクトリを読み込んでその中のExcelファイルのパスをファイルの中身を解析するメソッド(FileLoader)にデータを渡す

public class DirectoryLoader {
    private Path path;

    public DirectoryLoader(Path path) {
        this.path = path;
    }

    public static void main(String[] args) {
        new DirectoryLoader(Paths.get("C:\\Users\\takay\\OneDrive - 学校法人立命館\\Welcome Festival\\Welcome Event Captain")).analyze();
    }

    public ArrayList<TemperatureFile> analyze() {
        ArrayList<TemperatureFile> temperatureFiles = new ArrayList<>();

        String getStr = CmdManager.runCmd("dir \"" + this.path.toString() + "\" /b /a-d");
        String[] files = getStr.split("\n");

        Arrays.stream(files)
                .forEach(s -> {
                    TemperatureFile temperatureFile = new TemperatureFile(Paths.get(this.path.toString() + "\\" + s));
                    if (!s.matches(".+.xlsx")) {
                        temperatureFile.hasError = true;
                        temperatureFile.isExcelFile = false;
                        temperatureFile.canProcess = false;

                        temperatureFile.statusArrayList.add(Status.FORMAT_ERROR);
                    }
                    temperatureFiles.add(temperatureFile);
                });
        return temperatureFiles;
    }
}
