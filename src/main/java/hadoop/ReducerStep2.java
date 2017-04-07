package hadoop;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import enums.DataPathEnum;
import updatestrategy.BaseUpdateStrategy;
import updatestrategy.UpdateStrategy4Case1;
import util.DataUtil;
import util.GsonUtil;
import util.HDFSUtil;
import acs.Ant;

public class ReducerStep2 extends
		Reducer<IntWritable, AntTempEntity, NullWritable, Text> {
	private static PheromoneData pheromoneData;
	private BaseUpdateStrategy baseUpdateStrategy;  //信息素更新策略
	protected void reduce(IntWritable key, Iterable<AntTempEntity> values,
			Context context) throws IOException, InterruptedException {
		System.out
				.println("==================ReducerStep2.reduce-begin===================");
		// System.out.println("key--->"+key);
		// System.out.println("values--->"+values);
		// find the best solution
		Ant bestAnt = null;
		Double bestLength = Double.MAX_VALUE;
		for (AntTempEntity val : values) {
			Ant ant = GsonUtil.gson
					.fromJson(val.getAnt().toString(), Ant.class);
			Double len = val.getCost().get();
			//System.out.println("len--->" + len);
			if (DataUtil.le(len, bestLength)) {
				bestLength = len;
				bestAnt = ant;
			}
		}
		bestAnt.updatePheromone();
		System.out.println("----------------------------------");
		System.out.println("context----------------------------------"+context);
		context.write(null, new Text(""));
		//context.write(null, new Text(GsonUtil.gson.toJson(bestAnt)));
		// System.out.println("bestAnt--->"+bestAnt);
		// System.out.println("bestLength--->"+bestLength);
		//更新信息素
        //baseUpdateStrategy.updateByAntRule2(pheromoneData.getPheromone(), bestAnt);
      //create pheromone file in HDFS
        //HDFSUtil.CreateFile(DataPathEnum.PheromoneData.toString(), GsonUtil.gson.toJson(pheromoneData));
        //System.out.println("==================ReducerStep2.reduce-end===================");
	}

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("==================ReducerStep2.setup===================");
		String str1;
		try {
			str1 = HDFSUtil.readFile(DataPathEnum.PheromoneData.toString());
			pheromoneData = GsonUtil.gson.fromJson(str1, PheromoneData.class);
			baseUpdateStrategy = new UpdateStrategy4Case1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
