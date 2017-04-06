package localsearch;

import acs.Ant;
import vrp.Solution;
import vrp.Truck;

public abstract class DefaultStretegy implements BaseStretegy {
	private static BaseStretegy stretegy;  //局部搜索策略

	public static void improveSolution(Ant ant) throws Exception{
		//System.out.println("DefaultStretegy.improveSolution");
		setStretegy(new _2OptStretegy());
        for (int k = 0; k < 5; k++) {
            stretegy.updateSolution(ant.getSolution());
        }
        //System.out.println("2opt优化后-------------------------------->" + ant.getLength());
        setStretegy(new _10RelocateStretegy());
        for (int m = 0; m < 3; m++) {
            stretegy.updateSolution(ant.getSolution());
        }
        //System.out.println("10relocate优化后-------------------------------->" + ant.getLength());
        setStretegy(new _2Opt$Stretegy());
        for (int k = 0; k < 5; k++) {
            stretegy.updateSolution(ant.getSolution());
        }
        //System.out.println("2opt*优化后------------------------->" + ant.getLength());
        setStretegy(new _10Relocate$Stretegy());
        for (int k = 0; k < 5; k++) {
            stretegy.updateSolution(ant.getSolution());
        }
        
	} 
	public static void setStretegy(BaseStretegy stretegy) {
		DefaultStretegy.stretegy = stretegy;
    }
}
