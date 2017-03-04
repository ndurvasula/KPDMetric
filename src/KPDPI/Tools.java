package KPDPI;

import java.util.ArrayList;
import org.apache.commons.math3.special.Gamma;

import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class Tools {
	static public final double[] D = {1,1,1,1,1,125,50,300,300,1,1,1,1,100,200,1,1,1,1};
	public static double epsilon(int samples, double alpha){
		return 2*Math.exp(Gamma.logGamma(19.0/2.0+1.0))*(1-Math.pow(Math.E,(Math.log(1-alpha)/samples)))/Math.pow(Math.PI,(19/2));
	}
	public static double eUtility(double[][] data, int l){
		ArrayList<Integer> freq = new ArrayList<Integer>();
		ArrayList<Double> value = new ArrayList<Double>();
		double ret = 0;
		for(int i = 0; i < data.length; i++){
			if(data[i][l] == Integer.MAX_VALUE)
				continue;
			if(!value.contains(data[i][l])){
				value.add(data[i][l]);
				freq.add(1);
			}
			else{
				int ind = value.indexOf(data[i][l]);
				freq.set(ind, freq.get(ind) + 1);
			}
		}
		for(int i = 0; i < freq.size(); i++){
			ret += ((double)freq.get(i))/data.length*value.get(i);
		}
		return ret;
	}
	
	
	public static double dist(VertexPair a, VertexPair b){
		double sum = 0;
		for(int i = 0; i < D.length; i++){
			Object ai = a.features[i];
			Object bi = b.features[i];
			if(ai instanceof Boolean || ai instanceof BloodType){
				if(!ai.equals(bi))
					sum ++;
			}
			else if(ai instanceof  int[]){
				for(int j = 0; j < 2; j++){
					if(((int[])(ai))[j] != ((int[])bi)[j]){
						sum += .5;
					}
				}
			}
			else if(ai instanceof Integer){
				sum += Math.pow(((Integer)ai-(Integer)bi)/D[i], 2);
			}
			else{
				sum += Math.pow(((Double)ai-(Double)bi)/D[i], 2);
			}
		}
		return Math.sqrt(sum);
	}
}
