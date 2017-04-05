package hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
public class ReducerStep2 extends Reducer<Text,IntWritable,Text,IntWritable>{

	protected void reduce(Text arg0, Iterable<IntWritable> arg1,
			org.apache.hadoop.mapreduce.Reducer.Context arg2)
			throws IOException, InterruptedException {
		System.out.println("==================ReducerStep2.reduce===================");
	}

	@Override
	protected void setup(org.apache.hadoop.mapreduce.Reducer.Context context)
			throws IOException, InterruptedException {
		System.out.println("=========================ReducerStep2.setup===============");
	}

}
