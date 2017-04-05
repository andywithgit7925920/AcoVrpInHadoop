package hadoop;
/**
 * for each iteration,store the pheromone data
 * @author hadoop
 *
 */
public class PheromoneData {
	private double[][] pheromone;   //信息素矩阵

	public double[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(double[][] pheromone) {
		this.pheromone = pheromone;
	}
	
}
