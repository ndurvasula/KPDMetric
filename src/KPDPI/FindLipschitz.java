package KPDPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.generator.SaidmanPoolGenerator;

public class FindLipschitz {
	static final int TRAJECTORIES = 10; //trajectories per simulation
	static final int SAMPLES = 100; //number of samples to get the lipschitz bound
	static final double BOUNDQ = 2; //Assumption for lipschitz condition c1 < K < bound*c1 for quality
	static final double BOUNDT = 2;
	static final double ERRQ = 10; //The maximum error in LKDPI that we allow
	static final double ERRT = 20;

	public static void main(String[] args) {
		SaidmanPoolGenerator s = new SaidmanPoolGenerator(new Random());
		VertexPair subject = s.generatePair(0);
		double[][] data = new Simulation(subject,TRAJECTORIES).run();
		double[] e = {Tools.eUtility(data, 0), Tools.eUtility(data, 1)};
		ArrayList<Double> lkdpi = new ArrayList<Double>();
		ArrayList<Double> time = new ArrayList<Double>();
		for(int i = 0; i < SAMPLES; i++){
			VertexPair si = new SaidmanPoolGenerator(new Random()).generatePair(1);
			double[][] di = new Simulation(si, TRAJECTORIES).run();
			double[] ei = new double[] {Tools.eUtility(di, 0), Tools.eUtility(di, 1)};
			lkdpi.add(Math.abs(e[0]-ei[0])/Tools.dist(subject,si));
			time.add(Math.abs(e[1]-ei[1])/Tools.dist(subject,si));
		}
		Collections.sort(lkdpi);
		Collections.sort(time);
		double lipschitzQuality = 0;
		double lipschitzTime = 0;
		for(int i = 0; i < SAMPLES; i++){
			if(lkdpi.get(i)*BOUNDQ >= lkdpi.get(lkdpi.size()-1)){
				lipschitzQuality = lkdpi.get(i);
				break;
			}
		}
		for(int i = 0; i < SAMPLES; i++){
			if(time.get(i)*BOUNDT >= time.get(time.size()-1)){
				lipschitzTime = time.get(i);
				break;
			}
		}
		System.out.println(ERRQ/lipschitzQuality);
		System.out.println(ERRT/lipschitzTime);
		double epsilon = Math.min(ERRQ/lipschitzQuality, ERRT/lipschitzTime);
		System.out.println("EPSILON = "+epsilon);
		double[] c = Tools.D.clone();
		for(int i = 0; i < c.length; i++){
			c[i] = c[i] * epsilon;
		}
		System.out.println(Tools.Samples(.997, c));
		
		
		
	}

}
