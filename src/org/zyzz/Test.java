package org.zyzz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws ParseException {
        String beginDate = "2015-01-01 00:00:00";
        String endDate = "2015-01-02 00:00:00";
        System.out.println(Main.calDaysBetween(beginDate,endDate));
    }
}
