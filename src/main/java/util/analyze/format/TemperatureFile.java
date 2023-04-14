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
    }

    public void init() {
        writeErrorMsg("");
    }


    public void writeErrorMsg(String errorMsg) {
        if (!isExcelFile) {
            System.err.println("このメソッドはExcelファイルにしか適用できません。");
            return;
        }
        Workbook wb = null;
        try (InputStream fis = new FileInputStream(this.filePath.toString())) {
            wb = WorkbookFactory.create(fis);
        } catch (FileNotFoundException fileNotFoundException) {
            //fileNotFoundException.printStackTrace();
            hasError = true;
            canProcess = false;
            isExcelFile = false;
            Status temp = Status.FILE_NAME_ERROR;
            temp.addMsg("ファイル名を変えて再読み込みしてください。");
            statusArrayList.add(temp);
            return;
        } catch (IOException ioException) {
            //ioException.printStackTrace();
            hasError = true;
            canProcess = false;
            isExcelFile = false;
            statusArrayList.add(Status.LOAD_ERROR);
            return;
        } catch (Exception exception) {

        }
        Sheet sheet = null;
        try {
            sheet = wb.getSheetAt(0);
        } catch (NullPointerException nullPointerException) {
            System.out.println("エラーが発生したファイル名→" + filePath);
        }
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
