package hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperStep1  extends Mapper<Object, Text, Text, IntWritable>{

	@Override
	protected void map(Object key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		System.out.println("key->"+key);
	}

	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("=====================MapperStep1.setup=========================");
	}

}
