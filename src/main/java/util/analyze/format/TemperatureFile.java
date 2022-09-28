package util.analyze.format;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class TemperatureFile {
    public final Path filePath;
    public boolean hasError = false;
    public boolean isExcelFile = true;
    public boolean canProcess = true;
    public ArrayList<Status> statusArrayList = new ArrayList<>();
    public TreeMap<Date, Float> temperatures = new TreeMap<>();

    public ResultStatus resultStatus;

    public String studentName;
    public String studentNum;
    public String storeName;


    public TemperatureFile(Path filePath) {
        this.filePath = filePath;
        init();
    }

    public void init() {
        writeErrorMsg("");
    }

/*
    public void setPersonalInfo() {
        if (!isExcelFile) {
            System.err.println("このメソッドはExcelファイルにしか適用できません。");
            return;
        }
        Workbook workbookToRead = null;
        try {
            workbookToRead = WorkbookFactory.create(new File(this.filePath.toString()));
        } catch (IOException e) {
            System.err.println("予期せぬエラーが発生しました。");
            e.printStackTrace();
        }
        Sheet sheet = workbookToRead.getSheetAt(0);
        Row row = sheet.getRow(2);

        Cell cellForName = row.getCell(1);
        Cell cellForStudentNum = row.getCell(2);

        //TODO:もう少し詳細な動きを考える
        this.studentName = cellForName.getStringCellValue();
        this.studentNum = String.valueOf((long) cellForStudentNum.getNumericCellValue());
    }
*/


    public void writeErrorMsg(String errorMsg) {
        if (!isExcelFile) {
            System.err.println("このメソッドはExcelファイルにしか適用できません。");
            return;
        }
        Workbook wb = null;
        try (InputStream fis = new FileInputStream(this.filePath.toString())) {
            wb = WorkbookFactory.create(fis);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.createRow(20);
        Cell cell = row.createCell(4);

        cell.setCellValue(errorMsg);

        try (FileOutputStream fos = new FileOutputStream(filePath.toString())) {
            wb.write(fos);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void judgeResultStatus() {
        if (!this.isExcelFile) {
            this.resultStatus = ResultStatus.IMPOSSIBLE;
        } else {
            if (this.hasError) {
                this.resultStatus = ResultStatus.BAD;
            } else {
                this.resultStatus = ResultStatus.PERFECT;
            }
        }
    }
}
