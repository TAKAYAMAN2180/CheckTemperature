package util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import util.analyze.format.Status;
import util.analyze.format.TemperatureFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataOutput {

    public static void main(String[] args) {
        process(Paths.get("C:\\Users\\takay\\Desktop\\新しいフォルダー"), null);
    }

    public static void process(Path path, ArrayList<TemperatureFile> temperatureFiles) {
        Workbook wb = null;

        try (InputStream fis = DataOutput.class.getClassLoader().getResourceAsStream("result_template.xlsx")) {
            if (fis != null) {
                wb = WorkbookFactory.create(fis);
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        Sheet sheet = null;
        if (wb != null) {
            sheet = wb.getSheetAt(0);
        }

        int rowCount = 1;
        for (TemperatureFile temperatureFile : temperatureFiles) {
            String content = showStatus(temperatureFile);
            if (temperatureFile.isExcelFile) {
                temperatureFile.writeErrorMsg(content);
            }
            Row row = null;
            if (sheet != null) {
                row = sheet.createRow(rowCount);
            }
            if (row != null) {
                row.createCell(0).setCellValue(temperatureFile.resultStatus.statusStr);
                row.createCell(1).setCellValue(temperatureFile.studentName);
                row.createCell(2).setCellValue(temperatureFile.studentNum);
                row.createCell(3).setCellValue(temperatureFile.storeName);

                row.createCell(4).setCellValue(temperatureFile.filePath.getFileName().toString());
                row.createCell(5).setCellValue(temperatureFile.filePath.toString());

                row.createCell(6).setCellValue(content);

                rowCount++;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(path + "\\result.xlsx")) {
            if (wb != null) {
                wb.write(fos);
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    private static String showStatus(TemperatureFile temperatureFile) {
        StringBuilder returnValue = new StringBuilder();
        ArrayList<Status> statuses = temperatureFile.statusArrayList;
        ArrayList<String> writtenStatus = new ArrayList<>();

        for (Status status : statuses) {
            if (!writtenStatus.contains(status.msg)) {
                returnValue.append(status.msg).append("/");
                writtenStatus.add(status.msg);
            }
        }
        return returnValue.toString();
    }
}