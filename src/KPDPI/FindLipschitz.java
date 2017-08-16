package KPDPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.special.Gamma;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.generator.SaidmanPoolGenerator;

public class FindLipschitz {
	static final int TRAJECTORIES = 5; //trajectories per simulation
	static final int SAMPLES = 5; //number of samples to get the lipschitz bound
	static final int PAIRS = 500; //number of start pairs
	static final int ALTS = 10;  //number of start altruists
	static final double SUPERSAMPLES = 15;
	static int TESTS = 10; //
	static int INC = 10;
	static final double ALPHA = .997;

	public static void main(String[] args) {
		System.out.println(Tools.epsilon(10000, .997));
		
		ArrayList<Long> samps = new ArrayList<Long>();
		ArrayList<Double> maxlkdpis = new ArrayList<Double>();
		ArrayList<Double> minlkdpis = new ArrayList<Double>();
		ArrayList<Double> maxtimes = new ArrayList<Double>();
		ArrayList<Double> mintimes = new ArrayList<Double>();
		ArrayList[] lkd = new ArrayList[TESTS];
		ArrayList[] td = new ArrayList[TESTS];
		for(int i = 0; i < TESTS; i++){
			lkd[i] = new ArrayList<Double>();
			td[i] = new ArrayList<Double>();
		}
		for(int u = 1; u <= TESTS; u++){
			samps.add((long)Math.pow(10, u)*INC);
			double DIST = Tools.epsilon(samps.get(samps.size()-1),ALPHA);
			System.out.println(DIST);
			ArrayList<Double> lkdpis = new ArrayList<Double>();
			ArrayList<Double> times = new ArrayList<Double>();
			for(int l = 0; l < SUPERSAMPLES; l++){
				System.out.println("SUPERSAMPLES: "+l);
				SaidmanPoolGenerator s = new SaidmanPoolGenerator(new Random());
				VertexPair subject = s.generatePair(0);
				double[][] data = new Simulation(subject,TRAJECTORIES,PAIRS,ALTS).run();
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
					double[][] di = new Simulation(si, TRAJECTORIES, PAIRS, ALTS).run();
					double[] ei = new double[] {Tools.eUtility(di, 0), Tools.eUtility(di, 1)};
					double dst = Tools.dist(subject,si);
					dl.add(dst);
					lkdpi.add(Math.abs(e[0]-ei[0]));
					time.add(Math.abs(e[1]-ei[1]));
				}
				Collections.sort(lkdpi);
				Collections.sort(time);
	//			double lipschitzQuality = 0;
	//			int qi = 0;
	//			double lipschitzTime = 0;
	//			int ti = 0;
	//			for(int i = 0; i < SAMPLES; i++){
	//				if(lkdpi.get(i)*BOUNDQ >= lkdpi.get(lkdpi.size()-1)){
	//					lipschitzQuality = lkdpi.get(i)*BOUNDQ;
	//					qi = i;
	//					break;
	//				}
	//			}
	//			for(int i = 0; i < SAMPLES; i++){
	//				if(time.get(i)*BOUNDT >= time.get(time.size()-1)){
	//					lipschitzTime = time.get(i)*BOUNDT;
	//					ti = i;
	//					break;
	//				}
	//			}
				lkdpis.add(lkdpi.get(lkdpi.size()-1));
				times.add(time.get(time.size()-1));
			
			}
			lkd[u-1] = lkdpis;
			td[u-1] = times;
			double lkdpiv = Collections.max(lkdpis);
			double timev = Collections.max(times);
			maxlkdpis.add(lkdpiv);
			minlkdpis.add(Collections.min(lkdpis));
			maxtimes.add(timev);
			mintimes.add(Collections.min(times));
		}
		System.out.println("# OF SAMPLES: "+samps.toString());
		System.out.println("MIN LKDPI ERROR: "+minlkdpis.toString());
		System.out.println("MAX LKDPI ERROR: "+maxlkdpis.toString());
		System.out.println("MIN TIME ERROR: "+mintimes.toString());
		System.out.println("MAX TIME ERROR: "+maxtimes.toString());
		for(int i = 0; i < TESTS; i++){
			System.out.println("LKDPI "+samps.get(i)+" "+lkd[i].toString());
			System.out.println("TIMES "+samps.get(i)+" "+lkd[i].toString());
		}
	}

}
