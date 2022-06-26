package top.iceclean.chatspace.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author : Ice'Clean
 * @date : 2021-05-24
 */
public class DateUtils {

    private static String timeFormatter = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormatter);
    public static final int SECOND = 1;
    public static final int MINUTE = 2;
    public static final int HOUR = 3;
    public static final int MONTH = 4;
    public static final int YEAR = 5;

    /** 具类私有化  */
    private DateUtils() { }

    /**
     * 设置获取的时间格式（默认 yyyy-MM-dd HH:mm:ss）
     * @param timeFormatter 时间格式
     */
    public static void setTimeFormatter(String timeFormatter) {
        DateUtils.timeFormatter = timeFormatter;
        formatter = DateTimeFormatter.ofPattern(timeFormatter);
    }

    /**
     * 获取当前时间
     * @return 当前时间的字符串格式
     */
    public static String getTime() {
        return formatter.format(LocalDateTime.now());
    }

    public static String getTimeCompact() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
    }

    public static String getHourMinute() {
        return DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now());
    }

    public static String getDate() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
    }

    public static String getYear() {
        return DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now());
    }

    public static String getMonth() {
        return DateTimeFormatter.ofPattern("MM").format(LocalDateTime.now());
    }

    public static String getDay() {
        return DateTimeFormatter.ofPattern("dd").format(LocalDateTime.now());
    }

    /** 将 Date 格式化为字符串 */
    public static String parseDate(Date date) {
        return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * 将时间字符串重新解析为时间数组
     * @param time 时间字符串
     * @return 时间数组
     */
    public static int[] reparseTime(String time) {
        int[] reparse = new int[6];
        String[] dateTime = time.split(" ");
        String[] date = dateTime[0].split("-");
        String[] sTime = dateTime[1].split(":");
        reparse[0] = Integer.parseInt(date[0]);
        reparse[1] = Integer.parseInt(date[1]);
        reparse[2] = Integer.parseInt(date[2]);
        reparse[3] = Integer.parseInt(sTime[0]);
        reparse[4] = Integer.parseInt(sTime[1]);
        reparse[5] = Integer.parseInt(sTime[2]);
        return reparse;
    }
    /**
     * 对字符串时间作加减法
     * @param time 符合格式的字符串时间
     * @param type 进行加法的单位
     * @param num 需要执行的数量（正数为作减法，负数为作加法）
     * @return 加法运算后的时间
     */
    public static String addOrSub(String time, int type, int num) {
        // 解析时间，并作对应的加减法
        LocalDateTime parse = LocalDateTime.parse(time, formatter);
        switch (type) {
            case SECOND: parse = parse.plusSeconds(num); break;
            case MINUTE: parse = parse.plusMinutes(num); break;
            case HOUR: parse = parse.plusHours(num); break;
            case MONTH: parse = parse.plusMonths(num); break;
            case YEAR: parse = parse.plusYears(num); break;
            default:
        }
        return formatter.format(parse);
    }

    /**
     * 将时间字符串，解析成 cron 表达式
     * 要求时间格式必须为 yyyy-MM-dd HH:mm:ss
     * @param time 指定格式的时间字符串
     * @return 解析后的 cron 表达式字符串
     */
    public static String parseToCron(String time) {
        // cron 表达式结果
        String cron = null;

        try {
            // 拆分出日期和时间
            String[] dateTime = time.split(" ");
            String[] dates = dateTime[0].split("-");
            String[] times = dateTime[1].split(":");

            // 拼接 cron 表达式
            cron = times[2] + " " + times[1] + " " + times[0] + " " + dates[2] + " " + dates[1] + " ?";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cron;
    }

    /**
     * 计算所给时间与当前时间的间隔（格式要一样）
     *
     * @param time 当前格式的字符串时间
     * @return 时间间隔，单位：秒
     */
    public static long timeInterval(String time) {
        Instant provideTime = parseToInstant(time);
        Instant nowTime = parseToInstant(getTime());
        // 计算时间间隔，单位为秒
        return ChronoUnit.SECONDS.between(provideTime, nowTime);
    }

    /**
     * 将给定的时间字符串按照当前格式转化成 Instant 对象
     *
     * @param time 时间字符串
     * @return 时间对象
     */
    public static Instant parseToInstant(String time) {
        return LocalDateTime.parse(time, formatter).atZone(ZoneId.systemDefault()).toInstant().plusMillis(TimeUnit.HOURS.toMillis(8));
    }

    /**
     * 在基准时间上，根据type，对年月日时分秒进行增加或减少
     * @param type  年/月/日/时/分/秒
     * @param change    增或减多少
     * @param time  基准时间
     * @return  结果
     */
    public static Date changTime(String type, Integer change, Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        switch (type){
            case "YEAR":{
                calendar.add(Calendar.YEAR, change);
                break;
            }
            case "MONTH":{
                calendar.add(Calendar.MONTH, change);
                break;
            }
            case "DAY":{
                calendar.add(Calendar.DAY_OF_MONTH, change);
                break;
            }
            case "HOUR":{
                calendar.add(Calendar.HOUR, change);
                break;
            }
            case "MINUTE":{
                calendar.add(Calendar.MINUTE, change);
                break;
            }
            case "SECOND":{
                calendar.add(Calendar.SECOND, change);
                break;
            }
            default:
        }
        return  calendar.getTime();
    }
}
