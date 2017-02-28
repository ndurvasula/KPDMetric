package KPDPI;

import java.util.ArrayList;

import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class Tools {
	public static double eUtility(double[][] data, int l){
		ArrayList<Integer> freq = new ArrayList<Integer>();
		ArrayList<Double> value = new ArrayList<Double>();
		double ret = 0;
		for(int i = 0; i < data.length; i++){
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
	
	//adapted from http://introcs.cs.princeton.edu/java/91float/Gamma.java.html
	static double gamma(double x) {
	    double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
	    double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
	                     + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
	                     +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
	    return tmp + Math.exp(Math.log(ser * Math.sqrt(2 * Math.PI)));
	 }
	
	//from my paper, assuming that we have a uniform distribution (this is also a bound that I proved)
	public static int Samples(double alpha, double[] D, double[] c){
		double cprod = 1;
		double dprod = 1;
		for(int i = 0; i < D.length; i++){
			cprod *= c[i];
			dprod *= D[i];
		}

		return (int)Math.ceil(Math.log(1-alpha)/Math.log(1-(Math.pow(Math.PI,D.length/2)*cprod)/(gamma(D.length/2+1)*dprod)));
	}
	
	public double dist(VertexPair a, VertexPair b, double[] D){
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
			else{
				sum += Math.pow(((Double)ai-(Double)bi)/D[i], 2);
			}
		}
		return Math.sqrt(sum);
	}
}
