package hadoop;

import java.io.IOException;

import localsearch.BaseStretegy;
import localsearch.DefaultStretegy;
import localsearch._10Relocate$Stretegy;
import localsearch._10RelocateStretegy;
import localsearch._2Opt$Stretegy;
import localsearch._2OptStretegy;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import util.GsonUtil;
import util.HDFSUtil;
import util.MatrixUtil;
import vrp.VRP;
import acs.Ant;

import com.google.gson.Gson;

import enums.DataPathEnum;

public class MapperStep1 extends
		Mapper<Object, Text, IntWritable, AntTempEntity> {
	// private static Cache cache;
	private static PheromoneData pheromoneData;

	@Override
	protected void map(Object key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//System.out.println("=====================MapperStep1.map============begin=============");
		// get the cache data(parameter and input)
		try {
			String val = String.valueOf(value);
			Ant ant = GsonUtil.gson.fromJson(val, Ant.class);
			ant.traceRoad(pheromoneData.getPheromone());
			//System.out.println("第" + ant.getId() + "只蚂蚁总路径长度---before"+ ant.getLength());
			DefaultStretegy.improveSolution(ant);
			//System.out.println("第" + ant.getId() + "只蚂蚁总路径长度---after"+ ant.getLength());
			String antTmp = GsonUtil.gson.toJson(ant);
			context.write(new IntWritable(1), new AntTempEntity(new Text(antTmp),
					new DoubleWritable(ant.getLength())));
			//System.out.println("=====================MapperStep1.map============end=============");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		// get the cache data
		System.out.println("=====================MapperStep1.setup=========================");
		// Path[]
		// path=DistributedCache.getLocalCacheFiles(context.getConfiguration());
		//String str;
		try {
			// str = HDFSUtil.getCacheStr(path[0]);
			// cache = GsonUtil.gson.fromJson(str, Cache.class); //cache got
			// get pheromone data from HDFS
			String str1 = HDFSUtil.readFile(DataPathEnum.PheromoneData
					.toString());
			pheromoneData = GsonUtil.gson.fromJson(str1, PheromoneData.class);
			// System.out.println("pheromoneData-->"+pheromoneData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
