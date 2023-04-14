package util.loader;

import util.CmdManager;
import util.analyze.format.Status;
import util.analyze.format.TemperatureFile;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

//ディレクトリを読み込んでその中のExcelファイルのパスをファイルの中身を解析するメソッド(FileLoader)にデータを渡す
//TODO:旧字体が使われるとエラーになる現象を解決

public class DirectoryLoader {
    private Path path;

    public static void main(String[] args) {
        String s = "test.xlsx";
        System.out.println(s.matches(".+\\.xlsx"));
    }

    public DirectoryLoader(Path path) {
        this.path = path;
    }

    public ArrayList<TemperatureFile> analyze() {
        ArrayList<TemperatureFile> temperatureFiles = new ArrayList<>();

        String getStr = CmdManager.runCmd("dir \"" + this.path.toString() + "\" /b /a-d");
        String[] files = getStr.split("\n");

        Arrays.stream(files)
                .forEach(s -> {
                    TemperatureFile temperatureFile = null;
                    String getPath="";
                    try {
                        getPath = this.path.toString() + "\\" + s;
                        temperatureFile = new TemperatureFile(Paths.get(getPath));
                    } catch (InvalidPathException invalidPathException) {

                    }
                    System.out.println("ファイル名→" + s);
                    if (!s.matches(".+\\.xlsx")) {
                        temperatureFile.hasError = true;
                        temperatureFile.isExcelFile = false;
                        temperatureFile.canProcess = false;

                        temperatureFile.statusArrayList.add(Status.EXTENSION_ERROR);
                    }
                    try {
                        temperatureFile.init();
                    } catch (NullPointerException nullPointerException) {
                        temperatureFile = new TemperatureFile(Paths.get("can not read"));
                        temperatureFile.hasError = true;
                        temperatureFile.isExcelFile = false;
                        temperatureFile.canProcess = false;

                        temperatureFile.statusArrayList.add(Status.EXTENSION_ERROR);
                    }
                    temperatureFiles.add(temperatureFile);
                });
        return temperatureFiles;
    }
}
