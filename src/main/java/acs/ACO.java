package acs;

import hadoop.AntTempEntity;
import hadoop.MapperStep1;
import hadoop.PheromoneData;
import hadoop.ReducerStep2;

import java.io.Serializable;

import updatestrategy.BaseUpdateStrategy;
import updatestrategy.UpdateStrategy4Case1;
import updatestrategy.UpdateStrategy4Case2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.google.gson.Gson;

import demo.WordCount.IntSumReducer;
import demo.WordCount.TokenizerMapper;
import enums.DataPathEnum;
import static util.LogUtil.logger;
import static vrp.VRP.*;
import util.DataUtil;
import util.GsonUtil;
import util.HDFSUtil;
import util.LogUtil;
import util.MatrixUtil;
import util.StringUtil;
import parameter.Parameter;
import vrp.Solution;
import vrp.VRP;

import java.io.IOException;

import launch.LaunchDriver;
import localsearch.*;

/**
 * Created by ab792 on 2016/12/30.
 */
public class ACO implements Serializable {
    private static final long serialVersionUID = -2542322072011298603L;
    private Ant[] ants; //蚂蚁
    private Integer antNum; //蚂蚁数量
    private Integer ITER_NUM;   //迭代数
    private double[][] pheromone;   //信息素矩阵
    private double bestLen; //最佳长度
    private Solution bestSolution;  //最佳解
    private Ant bestAnt;    //最佳路径的蚂蚁
    private BaseUpdateStrategy baseUpdateStrategy;  //信息素更新策略
    private BaseStretegy stretegy;  //局部搜索策略
    private Solution pre3Solution = null;
    private Solution preNSolution = null;
    private static PheromoneData pheromoneData;
    int FINISHCounter;

    public ACO() {
        this.antNum = Parameter.ANT_NUM;
        ITER_NUM = Parameter.ITER_NUM;
        ants = new Ant[antNum];
        baseUpdateStrategy = new UpdateStrategy4Case1();
        FINISHCounter = 0;
    }


    public void init(String filePath) {
        if (StringUtil.isNotEmpty(filePath)) {
            try {
                //导入数据
                importDataFromSolomon(filePath);
                LogUtil.logger.info("fileName---" + fileName);
                //初始化信息素矩阵
                pheromone = new double[clientNum][clientNum];
                for (int i = 0; i < clientNum; i++) {
                    for (int j = 0; j < clientNum; j++) {
                        pheromone[i][j] = Parameter.PHEROMONE_INIT;
                    }
                }
                PheromoneData pheromoneData = new PheromoneData();
                pheromoneData.setPheromone(pheromone);
                //create pheromone file in HDFS
                HDFSUtil.CreateFile(DataPathEnum.PheromoneData.toString(), GsonUtil.gson.toJson(pheromoneData));
                bestLen = Double.MAX_VALUE;
                //初始化蚂蚁
                initAntCommunity();
            } catch (Exception e) {
                System.err.print("FILE_PATH invalid!");
                e.printStackTrace();
            }

        } else {
            System.err.print("FILE_PATH empty!");
        }
    }

    /**
     * 初始化蚂蚁
     * @throws IOException 
     */
    private void initAntCommunity() throws IOException {
    	StringBuilder sb = new StringBuilder();
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(i);
            ants[i].init();
            sb.append(GsonUtil.gson.toJson(ants[i])).append("\n");
        }
        HDFSUtil.CreateFile(DataPathEnum.ANT_COLONY_PATH.toString(), sb.toString());
        
    }

    /**
     * ACO的运行过程
     */
    public void run() throws Exception {
	    	int RHOCounter = 0;
	        FINISHCounter = 0;
	        //进行ITER_NUM次迭代
	        Configuration conf = new Configuration();
	        for (int i = 0; i < ITER_NUM; i++) {
		    	Job job = new Job(conf, "aco run"+i);
		    	job.setJarByClass(ACO.class);
		    	//take the data to hdfs distributed cache
		    	//Path cachePath = new Path(DataPathEnum.CACHE_PATH.toString());
		    	//DistributedCache.addCacheFile(cachePath.toUri(), job.getConfiguration());
		    	/*----------mapper-----------*/
		    	job.setMapOutputKeyClass(IntWritable.class);
		    	job.setMapOutputValueClass(AntTempEntity.class);
		        job.setMapperClass(MapperStep1.class);
				//job.setCombinerClass(ReducerStep2.class);  
		        /*----------mapper-----------*/
				job.setNumReduceTasks(1);  
		        job.setReducerClass(ReducerStep2.class);
		        job.setOutputKeyClass(NullWritable.class);
		        job.setOutputValueClass(Text.class);
		        FileInputFormat.addInputPath(job, new Path(DataPathEnum.ANT_COLONY_PATH.toString()));
		        FileOutputFormat.setOutputPath(job, new Path(DataPathEnum.DATA_OUTPUT.toString()));
		        if(!job.waitForCompletion(true)){  
		            System.exit(1); // run error then exit  
		        }  
		        //get pheromone in HDFS
		        String pheromoneStr = HDFSUtil.readFile(DataPathEnum.PheromoneData.toString());
				pheromoneData = GsonUtil.gson.fromJson(pheromoneStr, PheromoneData.class);
				//MatrixUtil.printMatrix(pheromoneData.getPheromone());
				//get bestAnt in HDFS
				String bestANtStr = HDFSUtil.readFile(DataPathEnum.DATA_OUTPUT_RESULT.toString());
				Ant result = GsonUtil.gson.fromJson(bestANtStr, Ant.class);
				//System.out.println("result===================>"+result.getLength());
				//delete output
				HDFSUtil.deleteDir(DataPathEnum.DATA_OUTPUT.toString());
				baseUpdateStrategy = new UpdateStrategy4Case1();
				updatePheromoneBySolution(result,pheromoneData.getPheromone());
	            //更新信息素
	            baseUpdateStrategy.updateByAntRule2(pheromoneData.getPheromone(), bestAnt);
	            //再次广播变量
	            //System.out.println("广播开始");
	          //create pheromone file in HDFS
	            HDFSUtil.CreateFile(DataPathEnum.PheromoneData.toString(), GsonUtil.gson.toJson(pheromoneData));
	            //System.out.println("广播结束");
	            ++RHOCounter;
	            ++FINISHCounter;
	            //初始化蚁群
	            initAntCommunity();
	            //如果三代以内，最优解的变化值在3之内，则更新RHO
	            if (RHOCounter > 3) {
	                RHOCounter = 0;
	                if (DataUtil.le(pre3Solution.calCost() - bestSolution.calCost(), 3.0)) {
	                    updateRHO();
	                }
	                pre3Solution = bestSolution;
	            }
	            if (FINISHCounter >= Parameter.N) {
	                LogUtil.logger.info("FINISHCounter--->" + Parameter.N);
	                break;
	            }
        }
	        //System.out.println("====================================end======================================");  
	    //打印最佳结果
	    printOptimal();
    }

    /**
     * 通过得出的解更新信息素
     *
     * @param ant
     */
    private void updatePheromoneBySolution(Ant ant,double[][] pheromone) {
    	if (bestSolution == null && bestAnt == null) {
            //logger.info("=========case1==========");
            bestAnt = ant;
            bestLen = bestAnt.getLength();
            bestSolution = bestAnt.getSolution();
            //更新最大最小信息素
            updateMaxMinPheromone();
            pre3Solution = bestSolution;
            preNSolution = bestSolution;
        }
        //1.若𝑅的用车数大于𝑅∗的 用车数, 则将𝑅中所有边上的信息素进行大量蒸发
        else if (ant.getSolution().getTruckNum() > bestSolution.getTruckNum()) {
            //logger.info("=========case2==========");
            setBaseUpdateStrategy(new UpdateStrategy4Case1());
            baseUpdateStrategy.updatePheBySolution(pheromone, ant.getSolution());
        }
        //2.若𝑅的用车数等 于𝑅∗的用车数, 但𝑅的距离/时间费用大于等于𝑅∗相 应的费用, 则将𝑅中所有边上的信息素进行少量蒸发
        else if (ant.getSolution().getTruckNum() == bestSolution.getTruckNum() && DataUtil.ge(ant.getLength(), bestLen)) {
            //logger.info("=========case3==========");
            setBaseUpdateStrategy(new UpdateStrategy4Case2());
            baseUpdateStrategy.updatePheBySolution(pheromone, ant.getSolution());
        } else {
            //logger.info("=========case4==========");
            bestAnt = ant;
            bestLen = bestAnt.getLength();
            bestSolution = bestAnt.getSolution();
            preNSolution = bestSolution;
            FINISHCounter = 0;
            //更新最大最小信息素
            updateMaxMinPheromone();
        }
    }

    private void updateRHO() {
        //System.out.println("ACO.updateRHO");
        Parameter.RHO *= 1.05;
        Parameter.RHO = DataUtil.ge(Parameter.RHO, 1.0) ? 0.99 : Parameter.RHO;
        //System.out.println("RHO--->" + Parameter.RHO);
    }

    /**
     * 更新最大最小信息素
     */
    private void updateMaxMinPheromone() {
        Parameter.PHEROMONE_MAX = calPheromoneMax(bestLen, clientNum);
        Parameter.PHEROMONE_MIN = calPheromoneMin(Parameter.PHEROMONE_MAX);
    }

    /**
     * 计算最小信息素
     * 𝜏min = 𝜏max/20
     *
     * @param pheromoneMax
     * @return
     */
    private Double calPheromoneMin(Double pheromoneMax) {
        return pheromoneMax / Parameter.pheSpan;
    }

    /**
     * 计算最大信息素
     * 𝜏max = 𝐶/(𝐿(𝑅∗)×𝑛×(1−𝜌))
     *
     * @param bestLen
     * @param clientNum
     * @return
     */
    private Double calPheromoneMax(double bestLen, Integer clientNum) {
        return Parameter.C / bestLen * (clientNum - 1) * (1 - Parameter.RHO);
    }


    /**
     * 打印最佳结果
     */
    private void printOptimal() {
    	System.out.println("printOptimal begin...");
        System.out.println("The optimal length is: " + bestLen);
        System.out.println("The optimal tour is: ");
        System.out.println(bestSolution);
        //System.out.println("The value of pheromone:");
        /*for (int i = 0; i < pheromone.length; i++) {
            for (int j = 0; j < pheromone[i].length; j++) {
                System.out.print(pheromone[i][j] + "\t");
            }
            System.out.print("\n");
        }*/
    }

    public void setBaseUpdateStrategy(BaseUpdateStrategy baseUpdateStrategy) {
        this.baseUpdateStrategy = baseUpdateStrategy;
    }

    public void setStretegy(BaseStretegy stretegy) {
        this.stretegy = stretegy;
    }
}