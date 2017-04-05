package launch;

import java.io.IOException;
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

import enums.DataEnum;
import acs.ACO;
/**
 * Created by ab792 on 2017/3/6.
 */
public class LaunchDriver {
   /* public static void main(String[] args) throws Exception {
        org.apache.log4j.LogManager.resetConfiguration();
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
        String fileName = "benchmark/solomon/C102.vrp";
        ACO aco = new ACO();
        aco.init(fileName);
        aco.run();
    }*/
	
	public static void main(String[] args) throws Exception {
		org.apache.log4j.LogManager.resetConfiguration();
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
		Configuration conf = new Configuration();
		ACO aco = new ACO();
		aco.init(DataEnum.DATA_INPUT.toString());
		//aco.run();
    }
}
