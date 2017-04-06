package hadoop;

import java.io.IOException;

import localsearch.DefaultStretegy;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import util.GsonUtil;
import util.HDFSUtil;
import acs.Ant;

import com.google.gson.Gson;

import enums.DataPathEnum;

public class MapperStep1  extends Mapper<Object, Text, Text, IntWritable>{
	private static Cache cache;
	private static PheromoneData pheromoneData;
	@Override
	protected void map(Object key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		System.out.println("=====================MapperStep1.map=========================");
		//get the cache data(parameter and input)
		String val = String.valueOf(value);
		Ant ant = GsonUtil.gson.fromJson(val, Ant.class);
		//get the pheromone
		//System.out.print("key------->"+key);
		//System.out.print("val------->"+ant.getId());
		//traceRoad
		ant.traceRoad(pheromoneData.getPheromone());
		try {
			DefaultStretegy.improveSolution(ant);
			System.out.println("第" + ant.getId() + "只蚂蚁总路径长度" + ant.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		// get the cache data
		System.out.println("=====================MapperStep1.setup=========================");
		Path[] path=DistributedCache.getLocalCacheFiles(context.getConfiguration());  
		String str;
		try {
			str = HDFSUtil.getCacheStr(path[0]);
			cache = GsonUtil.gson.fromJson(str, Cache.class);	//cache got
			String str1 = HDFSUtil.readFile(DataPathEnum.PheromoneData.toString());
			pheromoneData = GsonUtil.gson.fromJson(str1, PheromoneData.class);
			//System.out.println("pheromoneData-->"+pheromoneData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
