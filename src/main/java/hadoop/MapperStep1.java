package hadoop;

import java.io.IOException;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import util.HDFSUtil;

import com.google.gson.Gson;

import enums.DataPathEnum;

public class MapperStep1  extends Mapper<Object, Text, Text, IntWritable>{
	private static Cache cache;
	@Override
	protected void map(Object key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//get the cache data
		//System.out.println("key->"+key);
		
	}

	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("=====================MapperStep1.setup=========================");
		Path[] path=DistributedCache.getLocalCacheFiles(context.getConfiguration());  
		String str = HDFSUtil.getCacheStr(path[0], context.getConfiguration());
		Gson gson = new Gson();
		cache = gson.fromJson(str, Cache.class);
		System.out.println("cache->"+cache);
	}

}
