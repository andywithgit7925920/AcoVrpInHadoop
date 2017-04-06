package vrp;

import util.ArrayUtil;
import util.HDFSUtil;
import util.LogUtil;
import util.MatrixUtil;

import com.google.gson.Gson;  

import enums.DataPathEnum;
import hadoop.Cache;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;

/**
 * Created by ab792 on 2017/1/18.
 */
public class VRP {
    /******待读取信息******/
    /***********vrp***********/
    public static String fileName;  //文件名
    public static Integer clientNum;    //顾客数量
    public static Integer capacity;     //货车容量
    public static double[][] distance;  //距离矩阵
    public static double[] clientDemandArr;    //顾客需求
    /************vrp**********/
    /***********vrptw***********/
    public static double[] serviceTime;   //服务时间
    public static double[][] time;     //车辆起止时间
    public static double[][] savedQnuantity;    //节约量
    /************vrptw**********/
    /******待读取信息******/

    /**
     * 读取对应的文件信息
     *
     * @param filePath
     * @throws IOException
     */
    public static void importDataFromAVRP(String filePath) throws IOException {
        Double[] x_Axis = null;
        Double[] y_Axis = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line;
        String strBuff;
        boolean flag4NodeCoordSection = false;
        boolean flag4DemandSection = false;
        while (!"EOF".equals((line = reader.readLine()).trim())) {
            if (line.startsWith("DIMENSION")) {
                clientNum = Integer.valueOf(line.substring(12));
                clientDemandArr = new double[clientNum];
                x_Axis = new Double[clientNum];
                y_Axis = new Double[clientNum];
            }
            if (line.startsWith("CAPACITY")) {
                capacity = Integer.valueOf(line.substring(11));
                //将值直接赋给truck
                Truck.capacity = capacity;
            }
            if (line.startsWith("NODE_COORD_SECTION") || line.startsWith("DEMAND_SECTION")) {
                flag4NodeCoordSection = !flag4NodeCoordSection;
            }
            if (line.startsWith("DEMAND_SECTION") || line.startsWith("DEPOT_SECTION")) {
                flag4DemandSection = !flag4DemandSection;
            }
            if (flag4NodeCoordSection) {
                for (int i = 0; i < clientNum; i++) {
                    strBuff = reader.readLine();
                    String[] strArr = strBuff.split(" ");
                    x_Axis[i] = Double.valueOf(strArr[2]);
                    y_Axis[i] = Double.valueOf(strArr[3]);
                }
            }
            if (flag4DemandSection) {
                for (int i = 0; i < clientNum; i++) {
                    strBuff = reader.readLine();
                    String[] strArr = strBuff.split(" ");
                    clientDemandArr[i] = Integer.valueOf(strArr[1]);
                }
            }
        }
        //计算距离矩阵
        distance = new double[clientNum][clientNum];
        for (int i = 0; i < clientNum; i++) {
            distance[i][i] = 0.0;
            for (int j = i + 1; j < clientNum; j++) {
                Double len = Math.sqrt((x_Axis[i] - x_Axis[j]) * (x_Axis[i] - x_Axis[j]) + (y_Axis[i] - y_Axis[j]) * (y_Axis[i] - y_Axis[j]));
                distance[i][j] = len;
                distance[j][i] = distance[i][j];
            }
        }
        //ArrayUtil.printArr(x_Axis);
        //ArrayUtil.printArr(y_Axis);
        ArrayUtil.printArr(clientDemandArr);
        MatrixUtil.printMatrix(distance);
    }

    public static void importDataFromSolomon(String filePath) throws URISyntaxException  {
        System.out.print("--importDataFromSolomon--");
        Configuration conf = new Configuration();
    	Double[] x_Axis = null;
        Double[] y_Axis = null;
        Path inputPath = new Path(filePath);
        FSDataInputStream FSin = null;
        InputStreamReader ireader = null;
        BufferedReader reader = null;
        try{
        	FileSystem fs = FileSystem.get(inputPath.toUri(),conf);
            if(!fs.exists(inputPath)){
            	LogUtil.logger.error("err file path!");
            	System.exit(1);
            }
            FSin = fs.open(inputPath);
            ireader = new InputStreamReader(FSin);
            reader = new BufferedReader(ireader);
            String line;
            boolean flag4InformationSection = false;
            while (!"EOF".equals((line = reader.readLine()).trim())) {
                if (line.startsWith("NAME")){
                    fileName=line.substring(7);
                }
                if (line.startsWith("DIMENSION")) {
                    clientNum = 1 + Integer.valueOf(line.substring(12));
                    clientDemandArr = new double[clientNum];
                    serviceTime = new double[clientNum];
                    x_Axis = new Double[clientNum];
                    y_Axis = new Double[clientNum];
                    time = new double[clientNum][3];
                    //System.out.println("clientNum--->" + clientNum);
                }
                if (line.startsWith("CAPACITY")) {
                    capacity = Integer.valueOf(line.substring(11));
                    //将值直接赋给truck
                    Truck.capacity = capacity;
                    //System.out.println("capacity--->" + capacity);
                }
                if (line.startsWith("INFORMATION")) {
                    flag4InformationSection = !flag4InformationSection;
                    reader.readLine();
                }
                if (flag4InformationSection) {
                    for (int i = 0; i < clientNum; i++) {
                        Scanner scanner = new Scanner(reader.readLine().trim());
                        for (int j = 0; j < 7; j++) {
                            double temp = scanner.nextDouble();
                            if (j == 1) {
                                x_Axis[i] = temp;
                            }
                            if (j == 2) {
                                y_Axis[i] = temp;
                            }
                            if (j == 3) {
                                clientDemandArr[i] = temp;
                            }
                            if (j == 4) {
                                time[i][0] = temp;
                            }
                            if (j == 5) {
                                time[i][1] = temp;
                                time[i][2] = time[i][1] - time[i][0];
                            }
                            if (j == 6) {
                                serviceTime[i] = temp;
                            }
                        }
                    }
                }
            }
            //为truck的最大服务时间赋值
            Truck.serviceTime = time[0][1];
            //计算距离矩阵
            distance = new double[clientNum][clientNum];
            for (int i = 0; i < clientNum; i++) {
                distance[i][i] = 0.0;
                for (int j = i + 1; j < clientNum; j++) {
                    Double len = Math.sqrt((x_Axis[i] - x_Axis[j]) * (x_Axis[i] - x_Axis[j]) + (y_Axis[i] - y_Axis[j]) * (y_Axis[i] - y_Axis[j]));
                    distance[i][j] = len;
                    distance[j][i] = distance[i][j];
                }
            }
            //计算节约量
            savedQnuantity = new double[clientNum][clientNum];
            for (int i = 0; i < clientNum; i++) {
                savedQnuantity[i][i] = distance[i][0] + distance[0][i] - distance[i][i];
                for (int j = i + 1; j < clientNum; j++) {
                    savedQnuantity[i][j] = distance[i][0] + distance[0][j] - distance[i][j];
                    savedQnuantity[j][i] = savedQnuantity[i][j];
                }
            }
            //开始打印数据
            //System.out.println("=========clientDemandArr===========");
            //ArrayUtil.printArr(clientDemandArr);
            //System.out.println("=========serviceTime===========");
            //ArrayUtil.printArr(serviceTime);
            //System.out.println("=========time===========");
            /*for (int i = 0; i < time.length; i++) {
                for (int j = 0; j < time[i].length; j++) {
                    System.out.print(time[i][j] + " ");
                }
                System.out.print("\n");
            }*/
            //System.out.println("=========distance===========");
            //MatrixUtil.printMatrix(distance);
            //System.out.println("=========savedQnuantity===========");
            //MatrixUtil.printMatrix(savedQnuantity);
            //System.out.println("读入数据完毕");
            System.out.println("add data to cache");
            //add data to cache
            Cache cache = new Cache();
            cache.refresh();
            //create caches file in HDFS
            Gson gson = new Gson();
            String str = gson.toJson(cache);
            HDFSUtil.CreateFile(DataPathEnum.CACHE_PATH.toString(), str);
            
            //JobConf jobConf = new JobConf(VRP.class);
            //DistributedCache.addCacheFile(new URI(DataPathEnum.CACHE_PATH.toString()), jobConf);
        }catch(IOException e){
        	e.printStackTrace();
        }finally{
        	try {
				reader.close();
				ireader.close();
				FSin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
    }
}
