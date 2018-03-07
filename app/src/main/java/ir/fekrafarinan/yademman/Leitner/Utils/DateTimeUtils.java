package ir.fekrafarinan.yademman.Leitner.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy hh:mm:ss";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.US);

    public static Date stringToDate(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static String dateToString(Date date){
        return dateFormat.format(date);
    }

    public static String dateToDbString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.format(date);
    }

    public static Date dbStringToDate(String dataString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.parse(dataString);
    }

    public static long secondsDifference(Date startDate, Date endDate){
        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    public static long secondsDifference(String startDate, String endDate) throws ParseException {
        return secondsDifference(stringToDate(startDate), stringToDate(endDate));
    }

    public static long minutesDifference(Date startDate, Date endDate){
        return secondsDifference(startDate, endDate) / 60;
    }

    public static long minutesDifference(String startDate, String endDate) throws ParseException {
        return minutesDifference(stringToDate(startDate), stringToDate(endDate));
    }

    public static long hoursDifference(Date startDate, Date endDate){
        return minutesDifference(startDate, endDate) / 60;
    }

    public static long hoursDifference(String startDate, String endDate) throws ParseException {
        return hoursDifference(stringToDate(startDate), stringToDate(endDate));
    }

    public static long daysDifference(Date startDate, Date endDate){
        return hoursDifference(startDate, endDate) / 24;
    }

    public static long daysDifference(String startDate, String endDate) throws ParseException {
        return daysDifference(stringToDate(startDate), stringToDate(endDate));
    }

    public static Date getNow(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getNowWithCustomTime(String time){
        String now = dateToString(getNow());
        now = now.split(" ")[0] + " " + time;
        return now;
    }

}
