package org.zyzz;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        ReadFIie.getFlavor("flavor14");
        String s = "flavor15";
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher m = pattern.matcher(s);
        if (m.find()) {
            System.out.println(m.group(1));
        }
    }
}
