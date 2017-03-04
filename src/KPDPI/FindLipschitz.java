package KPDPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.special.Gamma;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.generator.SaidmanPoolGenerator;

public class FindLipschitz {
	static final int TRAJECTORIES = 10; //trajectories per simulation
	static final int SAMPLES = 10; //number of samples to get the lipschitz bound
	static final double BOUNDQ = 2; //Assumption for lipschitz condition c1 < K < bound*c1 for quality
	static final double BOUNDT = 2;
	static final double ERRQ = 10; //The maximum error in LKDPI that we allow
	static final double ERRT = 20;
	static final double SUPERSAMPLES = 10;
	static final int TESTS = 3000; //
	static final double ALPHA = 0.997;

	public static void main(String[] args) {
		double DIST = Tools.epsilon(TESTS,ALPHA);
		System.out.println(DIST);
		ArrayList<Double> lkdpis = new ArrayList<Double>();
		ArrayList<Double> times = new ArrayList<Double>();
		for(int l = 0; l < SUPERSAMPLES; l++){
			System.out.println("SUPERSAMPLES: "+l);
			SaidmanPoolGenerator s = new SaidmanPoolGenerator(new Random());
			VertexPair subject = s.generatePair(0);
			double[][] data = new Simulation(subject,TRAJECTORIES).run();
			double[] e = {Tools.eUtility(data, 0), Tools.eUtility(data, 1)};
			ArrayList<Double> lkdpi = new ArrayList<Double>();
			ArrayList<Double> dl = new ArrayList<Double>();
			ArrayList<Double> time = new ArrayList<Double>();
			for(int i = 0; i < SAMPLES; i++){
				VertexPair si = new SaidmanPoolGenerator(new Random()).generatePair(1);
				while(Tools.dist(si, subject) > DIST || Tools.dist(si, subject) == 0){
					System.out.println("NOPE "+Tools.dist(si, subject));
					double patientCPRA = s.generatePraIncompatibility(subject.isWifePatient());
					double eGFR = s.draweGFR();
					double bmi = s.drawBMI();
					double pweight = s.drawWeight();
					double dweight = s.drawWeight();
					int age = s.drawPatientAge();
					int sbp = s.drawSBP();
					si = new VertexPair(1, subject.getBloodTypePatient(), subject.getBloodTypeDonor(), subject.isWifePatient(), patientCPRA, subject.isCompatible(), eGFR, bmi, pweight, dweight, subject.isAfricanAmerican(), subject.isCigaretteUser(), subject.isPatientMale(), subject.isDonorMale(), subject.isRelated(), age, sbp, subject.getDonorHLA_B(), subject.getDonorHLA_DR(), subject.getPatientHLA_B(), subject.getPatientHLA_DR());
					//System.out.println(Tools.dist(si, subject));
				}
				System.out.println("Got a pair");
				double[][] di = new Simulation(si, TRAJECTORIES).run();
				double[] ei = new double[] {Tools.eUtility(di, 0), Tools.eUtility(di, 1)};
				double dst = Tools.dist(subject,si);
				dl.add(dst);
				lkdpi.add(Math.abs(e[0]-ei[0])/dst);
				time.add(Math.abs(e[1]-ei[1])/dst);
			}
			Collections.sort(lkdpi);
			Collections.sort(time);
			double lipschitzQuality = 0;
			int qi = 0;
			double lipschitzTime = 0;
			int ti = 0;
			for(int i = 0; i < SAMPLES; i++){
				if(lkdpi.get(i)*BOUNDQ >= lkdpi.get(lkdpi.size()-1)){
					lipschitzQuality = lkdpi.get(i)*BOUNDQ;
					qi = i;
					break;
				}
			}
			for(int i = 0; i < SAMPLES; i++){
				if(time.get(i)*BOUNDT >= time.get(time.size()-1)){
					lipschitzTime = time.get(i)*BOUNDT;
					ti = i;
					break;
				}
			}
			lkdpis.add(lipschitzQuality*DIST);
			times.add(lipschitzTime*DIST);
		
		}
		System.out.println(lkdpis.toString());
		System.out.println(times.toString());
		double lkdpiv = Collections.max(lkdpis);
		System.out.println(Collections.min(lkdpis) + " <= LKDPI ERROR <= "+lkdpiv);
		double timev = Collections.max(times);
		System.out.println(Collections.min(times) + " <= TIME ERROR <= "+timev);
	}

}
