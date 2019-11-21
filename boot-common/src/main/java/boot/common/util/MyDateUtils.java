package boot.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Description 日期工具类
 * @Date 2019/11/21 13:44
 * @Author huangyangyang
 */
public class MyDateUtils {
    private MyDateUtils() {
    }

    /**
     * 日期转字符串
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String strDate = localDateTime.format(formatter);
        return strDate;
    }


}
