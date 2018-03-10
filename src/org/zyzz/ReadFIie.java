package org.zyzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFIie {
    public static void main(String[] args) {
        ArrayList<int[]> dataList = loadDataFromFile("C:\\Users\\zyzz\\Desktop\\练习数据\\初赛文档\\用例示例\\TrainData.txt");
        System.out.println();
    }
    //读取训练文件
    public static ArrayList<int[]> loadDataFromFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        String date = null;
        int day = 0;
        int flag = 0;
        ArrayList<int[]> dataList = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] tempData = tempString.split("\t");
                if (flag == 0) {
                    dataList.add(new int[16]);
                    date = (tempData[2].split(" "))[0];
                    flag++;
                }
                if (tempData[2].contains(date)) {
                    ((dataList.get(day))[getFlavor(tempData[1])])++;
                }
                else {
                    date = (tempData[2].split(" "))[0];
                    day++;
                    dataList.add(new int[16]);
                    (dataList.get(day))[getFlavor(tempData[1])]++;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            return dataList;
        }
    }


    //读取虚拟机flavor号
    public static int getFlavor(String s) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher m = pattern.matcher(s);
        if (m.find()) {
            if (Integer.parseInt(m.group(1))>15)
                return 0;
            else
                return Integer.parseInt(m.group(1));
        }
        else
            return 0;
    }
}
