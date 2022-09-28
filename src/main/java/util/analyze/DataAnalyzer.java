package util.analyze;

import util.analyze.format.Status;
import util.analyze.format.TemperatureFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//データの処理(日数と体温の確認、データと日付の中身にのみ触れる)
public class DataAnalyzer {
    private Date startDate;
    private Date endDate;

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        TreeMap<Date, Float> temp = new TreeMap<>();

        double[] temperatures = {36.6, 36.6, 37.6, 36.6, 36.6, 36.6, 36.6, 36.6, 37.6, 36.6, 31.6, 36.6, 36.6, 36.6};
        Calendar calendar = Calendar.getInstance();
        Date tempStartDate;
        try {
            calendar.setTime(simpleDateFormat.parse("05/26"));
            tempStartDate = calendar.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        for (double value : temperatures) {
            temp.put(calendar.getTime(), (float) value);
            calendar.add(Calendar.DATE, 1);
        }
        calendar.add(Calendar.DATE, -2);
        Date tempEndDate = calendar.getTime();


    }


    //TODO:ぜんぶ重複していないかの確認
    //TODO:データペースから過去データ&メンバーの体温のGET(どうやって実装する？)

    public DataAnalyzer(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TemperatureFile analyze(TemperatureFile getTemperatureFile) {
        TreeMap<Date, Float> treeMap = getTemperatureFile.temperatures;
        ArrayList<Float> temperatureDataSet = new ArrayList<>();

        for (Float temp : treeMap.values()) {
            //発熱の確認
            if (temp >= 37.5) {
                getTemperatureFile.hasError = true;
                getTemperatureFile.statusArrayList.add(Status.FEVER);
            } else if (temp <= 35) {
                //低体温の確認
                getTemperatureFile.hasError = true;
                getTemperatureFile.statusArrayList.add(Status.HYPOTHERMIA);
            }
            temperatureDataSet.add(temp);
        }


        //全部同じ体温でないかの確認→異常な記録になる
        if (temperatureDataSet.size() > 1) {
            boolean isDeprecated = true;
            for (int i = 1; i < temperatureDataSet.size(); i++) {
                if (!Objects.equals(temperatureDataSet.get(0), temperatureDataSet.get(i))) {
                    isDeprecated = false;
                    break;
                }
            }
            if (isDeprecated) {
                getTemperatureFile.hasError = true;
                getTemperatureFile.statusArrayList.add(Status.CURIOUS);
            }
        }


        //日付が連続しているか、日付が指定された日付分保存先されているかどうか
        boolean isCorrect = true;
        Date tempDate = null;
        List<Date> dates = new ArrayList<>(treeMap.keySet());
        for (int i = 0; i < dates.size(); i++) {
            if (i == 0 && dates.get(i).compareTo(this.startDate) != 0) {
                isCorrect = false;
                break;
            } else if (i == dates.size() - 1 && dates.get(i).compareTo(this.endDate) != 0) {
                isCorrect = false;
                break;
            }
            if (i >= 1) {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(dates.get(i));

                dateCalendar.add(Calendar.DATE, -1);

                if (dateCalendar.getTime().compareTo(dates.get(i - 1)) != 0) {
                    isCorrect = false;
                }
            }
        }
        if (!isCorrect) {
            getTemperatureFile.statusArrayList.add(Status.DATE_DIFFERENT);
            getTemperatureFile.hasError = true;
        }
        return getTemperatureFile;
    }


}
