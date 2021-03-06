package launch;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import enums.DataPathEnum;
import acs.ACO;
/**
 * Created by ab792 on 2017/3/6.
 */
public class LaunchDriver {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		org.apache.log4j.LogManager.resetConfiguration();
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
        //PrintStream print=new PrintStream("logs.mylog.txt");  //写好输出位置文件；  
        //System.setOut(print);  
        //System.out.println("LaunchDriver.main ===========begin");
		ACO aco = new ACO();
		//aco.init(DataPathEnum.DATA_INPUT.toString());
		String filePath = "benchmark/solomon/C101.vrp";
		aco.init(filePath);
		aco.run();
		//System.out.println("LaunchDriver.main ===========end");
		long end = System.currentTimeMillis();
		System.out.println("during time-->"+(end-start));
    }
}
