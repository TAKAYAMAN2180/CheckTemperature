import javafx.scene.control.Alert;
import util.CmdManager;
import util.DataOutput;
import util.analyze.DataAnalyzer;
import util.analyze.format.TemperatureFile;
import util.loader.DirectoryLoader;
import util.loader.FileLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    private final Date startDate;
    private final Date endDate;
    private final Path directoryPathForLoad;
    private final Path directoryPathForSave;

    Main(Date startDate, Date endDate, Path directoryPathForLoad, Path directoryPathForSave) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.directoryPathForLoad = directoryPathForLoad;
        this.directoryPathForSave = directoryPathForSave;
    }

    public void run() {
        ArrayList<TemperatureFile> temperatureFileArrayList;

        temperatureFileArrayList = new DirectoryLoader(this.directoryPathForLoad).analyze();

        boolean hasExcelFile = false;

        //返された値の中にExcelファイルがあるかどうかの確認
        for (TemperatureFile temperatureFile : temperatureFileArrayList) {
            if (temperatureFile.isExcelFile) {
                hasExcelFile = true;
                break;
            }
        }

        if (!hasExcelFile) {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setHeaderText(null);
            dialog.setContentText("指定されたディレクトリにはExcelファイルが存在しません");
            dialog.showAndWait();

            return;
        }

        //fileLoaderにかける
        for (int i = 0; i < temperatureFileArrayList.size(); i++) {
            TemperatureFile getElement = temperatureFileArrayList.get(i);
            if (getElement.isExcelFile) {
                temperatureFileArrayList.set(i, FileLoader.loader(getElement));
            }
        }

        //DataAnalyzerにかける
        DataAnalyzer dataAnalyzer = new DataAnalyzer(this.startDate, this.endDate);
        for (int i = 0; i < temperatureFileArrayList.size(); i++) {
            TemperatureFile getElement = temperatureFileArrayList.get(i);
            if (getElement.canProcess) {
                temperatureFileArrayList.set(i, dataAnalyzer.analyze(getElement));
            }
        }

        //〇・△・×の判別
        for (TemperatureFile temperatureFile : temperatureFileArrayList) {
            temperatureFile.judgeResultStatus();
        }

        DataOutput.process(this.directoryPathForSave, temperatureFileArrayList);


        CmdManager.runCmd("cp -r " + directoryPathForLoad + " " + directoryPathForSave);

        CmdManager.runCmd("mkdir " + directoryPathForSave + "\\〇");
        CmdManager.runCmd("mkdir " + directoryPathForSave + "\\△");
        CmdManager.runCmd("mkdir " + directoryPathForSave + "\\×");

        for (TemperatureFile temperatureFile : temperatureFileArrayList) {
            switch (temperatureFile.resultStatus) {
                case PERFECT ->
                        CmdManager.runCmd("move " + temperatureFile.filePath + " " + directoryPathForSave + "\\〇");
                case BAD ->
                        CmdManager.runCmd("move " + temperatureFile.filePath + " " + directoryPathForSave + "\\△");
                case IMPOSSIBLE ->
                        CmdManager.runCmd("move " + temperatureFile.filePath + " " + directoryPathForSave + "\\×");
            }
        }

        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setHeaderText(null);
        dialog.setContentText("処理が完了しました");
        dialog.showAndWait();

    }


}
