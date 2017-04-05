package hadoop;

import vrp.VRP;

public class Cache {
	String fileName;
	Integer clientNum;
	Integer capacity;
	double[][] distance;  //距离矩阵
    double[] clientDemandArr;    //顾客需求
    /************vrp**********/
    /***********vrptw***********/
    double[] serviceTime;   //服务时间
    double[][] time;     //车辆起止时间
    double[][] savedQnuantity;    //节约量
    public void refresh(){
    	this.fileName = VRP.fileName;
    	this.clientNum = VRP.clientNum;
    	this.capacity = VRP.capacity;
    	this.distance = VRP.distance;
    	this.clientDemandArr = VRP.clientDemandArr;
    	this.serviceTime = VRP.serviceTime;
    	this.time = VRP.time;
    	this.savedQnuantity  = VRP.savedQnuantity;
    }
}
