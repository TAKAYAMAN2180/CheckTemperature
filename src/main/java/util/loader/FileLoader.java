package util.loader;
//TODO:0バイトのファイルがあったときも適切に処理する
import org.apache.poi.ss.usermodel.*;
import util.analyze.format.Status;
import util.analyze.format.TemperatureFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class FileLoader {

    public static void main(String[] args) {
        TemperatureFile temperatureFile = new TemperatureFile(Paths.get("C:\\Users\\takay\\IdeaProjects\\Check_Temperture\\src\\main\\resources\\sample1.xlsx"));

        temperatureFile = loader(temperatureFile);

    }


    public static TemperatureFile loader(TemperatureFile temperatureFile) {
        int dateNum = 14;

        TreeMap<Date, Float> temperatureTreeMap = new TreeMap<>();

        Workbook workbookToRead = null;
        try {
            workbookToRead = WorkbookFactory.create(new File(temperatureFile.filePath.toString()));
        } catch (IOException e) {
            temperatureFile.hasError = true;
            temperatureFile.canProcess = false;
            temperatureFile.isExcelFile = false;

            temperatureFile.statusArrayList.add(Status.LOAD_ERROR);
            return temperatureFile;
        }

        Sheet sheetToRead = workbookToRead.getSheetAt(0);

        //データフォーマットの確認
        Row rowToCheck = sheetToRead.getRow(1);


        boolean isFormatted = true;

        try {
            if (!rowToCheck.getCell(5).getStringCellValue().equals("日付")) {
                isFormatted = false;
            } else if (!rowToCheck.getCell(6).getStringCellValue().equals("体温")) {
                isFormatted = false;
            } else if (!rowToCheck.getCell(7).getStringCellValue().equals("詳細もしくはその他体調不良等")) {
                isFormatted = false;
            }
        } catch (NullPointerException|IllegalStateException exception) {
            isFormatted = false;
        }

        if (!isFormatted) {
            temperatureFile.hasError = true;
            temperatureFile.canProcess = false;
            temperatureFile.isExcelFile = false;

            temperatureFile.statusArrayList.add(Status.FORMAT_ERROR);

            return temperatureFile;
        }

        //個人情報の読み込み
        rowToCheck = sheetToRead.getRow(2);

        //名前の確認
        try {
            String getName = rowToCheck.getCell(1).getStringCellValue();
            if (getName.equals("") || getName.matches("( |　)+")) {
                temperatureFile.hasError = true;

                Status status = Status.NO_INFORMATION;
                status.addMsg("名前が入力されていません");

                temperatureFile.statusArrayList.add(status);
            } else {
                temperatureFile.studentName = getName;
            }
        } catch (IllegalStateException illegalStateException) {
            Status status = Status.UNFORMATTED_RECORD_DATA;
            status.addMsg("名前の入力が不適切です");
        }

        //学籍番号の確認
        try {
            String getNum = String.valueOf((long) rowToCheck.getCell(2).getNumericCellValue());
            if (getNum.equals("") || getNum.matches("( |　)+|0")) {
                temperatureFile.hasError = true;

                Status status = Status.NO_INFORMATION;
                status.addMsg("学籍番号が入力されていません");

                temperatureFile.statusArrayList.add(status);
            } else {
                temperatureFile.studentNum = getNum;
            }
        } catch (IllegalStateException illegalStateException) {
            Status status = Status.UNFORMATTED_RECORD_DATA;
            status.addMsg("学籍番号の値が不適切です");
        }

        //店舗名の確認
        try {
            String getStoreName = rowToCheck.getCell(3).getStringCellValue();
            if (getStoreName.equals("") || getStoreName.matches("( |　)+")) {
                temperatureFile.hasError = true;

                Status status = Status.NO_INFORMATION;
                status.addMsg("店舗名が入力されていません");

                temperatureFile.statusArrayList.add(status);
            } else {
                temperatureFile.storeName = getStoreName;
            }
        } catch (IllegalStateException illegalStateException) {
            Status status = Status.UNFORMATTED_RECORD_DATA;
            status.addMsg("店舗名の入力が不適切です");
        }

        ArrayList<Date> temperatureErrorDates = new ArrayList<>();
        ArrayList<Date> temperatureStringValueDates = new ArrayList<>();

        for (int currentRow = 2; currentRow <= dateNum + 2; currentRow++) {
            //体温の中に不足があるときにtrueになる
            boolean hasDeficiencyInTemperatureDate = false;
            boolean hasErrorInDateData = false;

            boolean hasStringInDateDate = false;

            Row row = sheetToRead.getRow(currentRow);

            Date getDate = null;
            float getTemperature = 0;
            boolean hasDetail = true;

            Cell cellForDate = null;
            Cell cellForTemperature = null;
            Cell cellForDetail = null;

            if (!hasErrorInDateData) {
                try {
                    cellForDate = row.getCell(5);
                    getDate = cellForDate.getDateCellValue();
                } catch (NullPointerException nullPointerException) {
                    //日付の入力なし
                    hasErrorInDateData = true;
                } catch (IllegalStateException illegalStateException) {
                    //不正な入力の値
                    hasStringInDateDate = true;
                } catch (Exception exception) {
                    System.err.println("日付の読み込み中に予期せぬエラーが発生しました");
                    exception.printStackTrace();
                    temperatureFile.hasError = true;
                    temperatureFile.canProcess = false;
                    temperatureFile.isExcelFile = false;

                    temperatureFile.statusArrayList.add(Status.UNEXPECTED);
                    return temperatureFile;
                }
            }

            if (!hasDeficiencyInTemperatureDate) {
                try {
                    cellForTemperature = row.getCell(6);
                    getTemperature = (float) cellForTemperature.getNumericCellValue();
                    if (getTemperature == 0) {
                        throw new NullPointerException();
                    }
                } catch (NullPointerException nullPointerException) {
                    //体温記録の不足のあり
                    hasDeficiencyInTemperatureDate = true;
                    temperatureErrorDates.add(getDate);
                } catch (IllegalStateException exception) {
                    hasStringInDateDate = true;
                    temperatureStringValueDates.add(getDate);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    temperatureFile.hasError = true;
                    temperatureFile.canProcess = false;
                    temperatureFile.isExcelFile = false;

                    temperatureFile.statusArrayList.add(Status.UNEXPECTED);
                    return temperatureFile;
                }
            }

            String getDetailMsg = "";
            try {
                cellForDetail = row.getCell(7);
                getDetailMsg = cellForDetail.getStringCellValue();
                if (getDetailMsg.equals("") || getDetailMsg.matches("( |　)*")) {
                    hasDetail = false;
                }
            } catch (NullPointerException nullPointerException) {
                hasDetail = false;
            } catch (IllegalStateException illegalStateException) {
                try {
                    getDetailMsg = String.valueOf(cellForDetail.getNumericCellValue());
                    if (getDetailMsg.equals("0") || getDetailMsg.equals("")) {
                        hasDetail = false;
                    }
                } catch (IllegalStateException illegalStateException1) {
                    illegalStateException.printStackTrace();
                    temperatureFile.hasError = true;
                    temperatureFile.canProcess = false;
                    temperatureFile.isExcelFile = false;

                    temperatureFile.statusArrayList.add(Status.UNEXPECTED);

                    return temperatureFile;
                } catch (NullPointerException Sy) {
                    hasDetail = false;
                }
            }

            if (hasDetail) {
                temperatureFile.hasError = true;
                temperatureFile.statusArrayList.add(Status.HAVE_DETAILS);
            }

            //【TODO】他の２つのエラーの反応はまだ未定義

            if (hasDeficiencyInTemperatureDate) {
                temperatureFile.hasError = true;
                temperatureFile.canProcess = false;

                temperatureFile.statusArrayList.add(Status.LACK_OF_TEMPERATURE);
            }
            if (hasStringInDateDate) {
                temperatureFile.hasError = true;
                temperatureFile.canProcess = false;

                temperatureFile.statusArrayList.add(Status.UNFORMATTED_RECORD_DATA);
            }

            //何のエラーもないとき
            if (!(hasDeficiencyInTemperatureDate || hasErrorInDateData)) {
                try {

                    temperatureTreeMap.put(getDate, getTemperature);
                } catch (Exception exception) {
                    temperatureFile.hasError = true;
                    temperatureFile.canProcess = false;

                    temperatureFile.statusArrayList.add(Status.UNEXPECTED);
                }
            }
        }
        temperatureFile.temperatures = temperatureTreeMap;
        return temperatureFile;
    }
}