package org.zyzz;

import com.filetool.util.FileUtil;

import javax.print.attribute.standard.PrinterLocation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException {
        List<int[]> resultList = new ArrayList<>();
        resultList = predictAll("2015-02-20 00:00:00","2015-02-27 00:00:00");
        System.out.printf("%10s","\t\t\t\t");
        System.out.printf("%-10s","sum\t");
        for (int i=1;i<resultList.get(0).length;i++) {
            System.out.printf("%10s","day"+i+"\t");
        }
        System.out.println();
        int i=0;
        for (int[] day : resultList) {
            System.out.printf("%10s","flavor"+(++i)+"\t");
            for (int flavor : day) {
                System.out.printf("%10s",flavor+"\t\t");
            }
            System.out.println();
        }
    }

    public static List<int[]> predictAll(String beginDate, String endDate) throws ParseException {
        int days = calDaysBetween(beginDate, endDate);
        int[] result;
        List<int[]> resultList = new ArrayList<>();
        for (int i=1;i<16;i++) {
            result = predict(i, days);
            resultList.add(result);
        }
        return resultList;
    }


    public static int[] predict(int flavor, int days) {
        int key = 20;
        List<double[]> dataList = new ArrayList<>();
        dataList = ReadFIie.loadDataFromStringArray(FileUtil.read("/home/tanjie/java/IdeaProjects/HWRuanTiao/src/data/TrainData.txt", null));
        double alpha = 0.0001;
        int iteration = 200000;
        double[][] trainData = ReadFIie.getFlavorArrayFromDataList(flavor, key, dataList);
        LinearRegression m = new LinearRegression(trainData, alpha, iteration);
        m.trainTheta();
        double[] history = new double[key + 1];
        double[] temp = new double[key + 1];
        double[] result_double = new double[days+1];
        int[] result_int = new int[days+1];
        System.arraycopy(trainData[trainData.length - 1], 2, history, 1, key);
        history[0] = 1.0;
        for (int i = 1; i < days+1; i++) {
            for (int j = 0; j < key + 1; j++) {
                result_double[i] = result_double[i] + m.getTheta()[j] * history[j];
            }
            if (result_double[i] < 0) {
                result_int[i] = 0;
            } else result_int[i] = (int) Math.round(result_double[i]);
            temp = history;
            System.arraycopy(temp, 2, history, 1, key - 1);
            history[key] = result_double[i];
        }
        for (int r : result_int) {
            result_int[0]=result_int[0]+r;
        }
        return result_int;
    }

    public static int calDaysBetween(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        try {
            calendar1.setTime(sdf.parse(date1));
            calendar2.setTime(sdf.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar2.get(Calendar.DAY_OF_YEAR) - calendar1.get(Calendar.DAY_OF_YEAR);
    }
}

class LinearRegression {
    private double [][] trainData;//训练数据，一行一个数据，每一行最后一个数据为 y
    private int row;//训练数据  行数
    private int column;//训练数据 列数
    private double [] theta;//参数theta

    public double[][] getTrainData() {
        return trainData;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public double[] getTheta() {
        return theta;
    }

    private double alpha;//训练步长
    private int iteration;//迭代次数

    public LinearRegression(String fileName){
        int rowoffile=getRowNumber(fileName);//获取输入训练数据文本的   行数
        int columnoffile = getColumnNumber(fileName);//获取输入训练数据文本的   列数

        trainData = new double[rowoffile][columnoffile+1];//这里需要注意，为什么要+1，因为为了使得公式整齐，我们加了一个特征x0，x0恒等于1
        this.row=rowoffile;
        this.column=columnoffile+1;

        this.alpha = 0.001;//步长默认为0.001
        this.iteration=100000;//迭代次数默认为 100000

        theta = new double [column-1];//h(x)=theta0 * x0 + theta1* x1 + theta2 * x2 + .......
        initialize_theta();

        loadTrainDataFromFile(fileName,rowoffile,columnoffile);
    }

    public LinearRegression(String fileName,double alpha,int iteration){
        int rowoffile=getRowNumber(fileName);//获取输入训练数据文本的   行数
        int columnoffile = getColumnNumber(fileName);//获取输入训练数据文本的   列数

        trainData = new double[rowoffile][columnoffile+1];//这里需要注意，为什么要+1，因为为了使得公式整齐，我们加了一个特征x0，x0恒等于1
        this.row=rowoffile;
        this.column=columnoffile+1;

        this.alpha = alpha;
        this.iteration=iteration;

        theta = new double [column-1];//h(x)=theta0 * x0 + theta1* x1 + theta2 * x2 + .......
        initialize_theta();

        loadTrainDataFromFile(fileName,rowoffile,columnoffile);
    }

    public LinearRegression(double[][] trainData, double alpha, int iteration) {
        int rowoffile = trainData.length;
        int columnoffile = trainData[0].length;
        this.trainData = trainData;
        this.row = rowoffile;
        this.column = columnoffile;
        this.alpha = alpha;
        this.iteration = iteration;
        theta = new double[column - 1];
        initialize_theta();
    }
    private int getRowNumber(String fileName)
    {
        int count =0;
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ( reader.readLine() != null)
                count++;
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
        }
        return count;
    }

    private int getColumnNumber(String fileName)
    {
        int count =0;
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = reader.readLine();
            if(tempString!=null)
                count = tempString.split(" ").length;
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
        }
        return count;
    }

    private void initialize_theta()//将theta各个参数全部初始化为1.0
    {
        for(int i=0;i<theta.length;i++)
            theta[i]=0.5;
    }

    public void trainTheta()
    {
        int iteration = this.iteration;
        while( (iteration--)>0 )
        {
            //对每个theta i 求 偏导数
            double [] partial_derivative = compute_partial_derivative();//偏导数
            //更新每个theta
            for(int i =0; i< theta.length;i++)
                theta[i]-= alpha * partial_derivative[i];
        }
    }

    private double [] compute_partial_derivative()
    {
        double [] partial_derivative = new double[theta.length];
        for(int j =0;j<theta.length;j++)//遍历，对每个theta求偏导数
        {
            partial_derivative[j]= compute_partial_derivative_for_theta(j);//对 theta i 求 偏导
        }
        return partial_derivative;
    }
    private double compute_partial_derivative_for_theta(int j)
    {
        double sum=0.0;
        for(int i=0;i<row;i++)//遍历 每一行数据
        {
            sum+=h_theta_x_i_minus_y_i_times_x_j_i(i,j);
        }
        return sum/row;
    }
    private double h_theta_x_i_minus_y_i_times_x_j_i(int i,int j)
    {
        double[] oneRow = getRow(i);//取一行数据，前面是feature，最后一个是y
        double result = 0.0;

        for(int k=0;k< (oneRow.length-1);k++)
            result+=theta[k]*oneRow[k];
            result-=oneRow[oneRow.length-1];
            result*=oneRow[j];
            return result;
    }
    private double [] getRow(int i)//从训练数据中取出第i行，i=0，1，2，。。。，（row-1）
    {
        return trainData[i];
    }


    private void loadTrainDataFromFile(String fileName,int row, int column)
    {
        for(int i=0;i< row;i++)//trainData的第一列全部置为1.0（feature x0）
            trainData[i][0]=1.0;

        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int counter = 0;
            while ( (counter<row) && (tempString = reader.readLine()) != null) {
                String [] tempData = tempString.split(" ");
                for(int i=0;i<column;i++)
                    trainData[counter][i+1]=Double.parseDouble(tempData[i]);
                counter++;
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
        }
    }

    public void printTrainData()
    {
        System.out.println("Train Data:\n");
        for(int i=0;i<column-1;i++)
            System.out.printf("%10s","x"+i+" ");
        System.out.printf("%10s","y"+" \n");
        for(int i=0;i<row;i++)
        {
            for(int j=0;j<column;j++)
            {
                System.out.printf("%10s",trainData[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printTheta()
    {
        for(double a:theta)
            System.out.print(a+" ");
    }
}