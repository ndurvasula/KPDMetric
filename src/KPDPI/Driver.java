package KPDPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.generator.SaidmanPoolGenerator;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;



public class Driver {
	
	
	//Probability that someone will fall into an epsilon ellipsoid
	static final double ALPHA = .997;
	
	static final double epsilon = .1;
	static final double[] c = {};
	static final int TRAJECTORIES = 200;

	public static void main(String[] args) {
		//int samples = SAMPLES(ALPHA, D, c);
		VertexPair p = new SaidmanPoolGenerator(new Random()).generatePair(0);
		Simulation sim = new Simulation(p,20);
		Simulation sim2 = new Simulation(p,TRAJECTORIES);
		double[][] ret = sim.run();
		double[][] ret2 = sim2.run();
		for(int i = 0; i < ret2.length; i++){
			System.out.println(Arrays.toString(ret2[i]));
		}
		System.out.println(Tools.eUtility(ret2,0)+" "+Tools.eUtility(ret2,1));
		System.out.println();
		for(int i = 0; i < ret.length; i++){
			System.out.println(Arrays.toString(ret[i]));
		}
		System.out.println(Tools.eUtility(ret,0)+" "+Tools.eUtility(ret,1));

	}

}
