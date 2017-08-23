package KPDM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.SortedSet;

import edu.cmu.cs.dickerson.kpd.dynamic.arrivals.ExponentialArrivalDistribution;
import edu.cmu.cs.dickerson.kpd.solver.CycleFormulationCPLEXSolver;
import edu.cmu.cs.dickerson.kpd.solver.GreedyPackingSolver;
import edu.cmu.cs.dickerson.kpd.solver.approx.CyclesSampleChainsIPPacker;
import edu.cmu.cs.dickerson.kpd.solver.exception.SolverException;
import edu.cmu.cs.dickerson.kpd.solver.solution.Solution;
import edu.cmu.cs.dickerson.kpd.structure.Cycle;
import edu.cmu.cs.dickerson.kpd.structure.Edge;
import edu.cmu.cs.dickerson.kpd.structure.Pool;
import edu.cmu.cs.dickerson.kpd.structure.Vertex;
import edu.cmu.cs.dickerson.kpd.structure.VertexAltruist;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.alg.CycleGenerator;
import edu.cmu.cs.dickerson.kpd.structure.alg.CycleMembership;
import edu.cmu.cs.dickerson.kpd.structure.alg.FailureProbabilityUtil;
import edu.cmu.cs.dickerson.kpd.structure.generator.PoolGenerator;
import edu.cmu.cs.dickerson.kpd.structure.generator.SaidmanPoolGenerator;
import edu.cmu.cs.dickerson.kpd.structure.generator.SparseUNOSSaidmanPoolGenerator;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class Sim {
	// Probabilities generated based on a match frequency of 1 day
	static final int CHAIN_CAP = 4;
	static final int CYCLE_CAP = 3;
	static int EXPECTED_PAIRS = 15;
	static int EXPECTED_ALTRUISTS = 1;
	static final double DEATH = 0.000580725433182381168050643691;
	static final double PATIENCE = 0.02284;
	static final double RENEGE = .5;

	VertexPair subject;
	int pairs0;
	int alts0;

	public Sim(VertexPair s, int p, int a, int ep, int ea) {
		subject = s;
		pairs0 = p;
		alts0 = a;
		EXPECTED_PAIRS = ep;
		EXPECTED_ALTRUISTS = ea;
	}
	
	//Receive command line arguments from python BayesOpt client and run the simulation
	public static void main(String[] args){
		BloodType btp = null;
		BloodType btd = null;
		boolean iwp = false;
		double pcpra;
		boolean ic = false;
		double egfr = 0;
		double dbmi = 0;
		double pw;
		double dw = 0;
		boolean iaa = false;
		boolean icu = false;
		boolean ipm = false;
		boolean idm = false;
		boolean ir = false;
		double da = 0;
		double dsbp = 0;
		int[] dhlab = new int[2];
		int[] dhladr = new int[2];
		int[] phlab = new int[2];
		int[] phladr = new int[2];
		
		pw = Double.parseDouble(args[0]);
		
		pcpra = Double.parseDouble(args[1]);
		
		switch((int)Double.parseDouble(args[2])){
		case 0: btp = BloodType.O;
		break;
		case 1: btp = BloodType.A;
		break;
		case 2: btp = BloodType.B;
		break;
		case 3: btp = BloodType.AB;
		break;
		}
		
		switch((int)Double.parseDouble(args[3])){
		case 0: btd = BloodType.O;
		break;
		case 1: btd = BloodType.A;
		break;
		case 2: btd = BloodType.B;
		break;
		case 3: btd = BloodType.AB;
		break;
		}
		
		switch((int)Double.parseDouble(args[4])){
		case 0: iwp = false;
		break;
		case 1: iwp = true;
		break;
		}
		
		switch((int)Double.parseDouble(args[5])){
		case 0: ic = false;
		break;
		case 1: ic = true;
		break;
		}
		
		switch((int)Double.parseDouble(args[6])){
		case 0: ipm = false;
		break;
		case 1: ipm = true;
		break;
		}
		
		for(int i = 0; i < 2; i++){
			phlab[i] = (int)Double.parseDouble(args[7+i]);
			phladr[i] = (int)Double.parseDouble(args[9+i]);
		}
		int pstart = (int)Double.parseDouble(args[11]);
		int astart = (int)Double.parseDouble(args[12]);
		int ep = (int)Double.parseDouble(args[13]);
		int ea = (int)Double.parseDouble(args[14]);

		VertexPair s = new VertexPair(0, btp, btd, iwp, pcpra, ic, egfr, dbmi, pw, dw, iaa, icu, ipm, idm, ir, da, dsbp, dhlab, dhladr, phlab, phladr);
		
		Sim q = new Sim(s,pstart,astart,ep,ea);
		q.run();
		
	}
	
	public double LKDPI(VertexPair p){
		double ret = -11.30;
		if(p.getDonorAge() > 50)
			ret += 1.85*(p.getDonorAge()-50);
		ret -= 0.381*p.geteGFR();
		double bmi = p.getDonorBMI();
		if(p.isAfricanAmerican())
			bmi += 22.34;
		if(p.isCigaretteUser())
			bmi += 14.33;
		ret += 1.17*bmi;
		double sbp = p.getDonorSBP();
		if(p.isDonorMale() && p.isPatientMale())
			sbp -= 21.68;
		if(!p.getBloodTypeDonor().equals(p.getBloodTypePatient()))
			sbp += 27.30;
		if(!p.isRelated())
			sbp -= 10.61;
		ret += .44*sbp;
		ret += 8.57*p.getBMismatches();
		ret += 8.26*p.getDRMismatches();
		ret -= 50.87*Math.min(p.getDRWR(),0.9);
		return ret;
	}
	
	public double LKDPI(VertexPair p, VertexAltruist a){
		double ret = -11.30;
		if(a.getDonorAge() > 50)
			ret += 1.85*(a.getDonorAge()-50);
		ret -= 0.381*a.geteGFR();
		double bmi = a.getDonorBMI();
		if(a.isAfricanAmerican())
			bmi += 22.34;
		if(a.isCigaretteUser())
			bmi += 14.33;
		ret += 1.17*bmi;
		double sbp = a.getDonorSBP();
		if(a.isMale() && p.isPatientMale())
			sbp -= 21.68;
		if(!a.getBloodTypeDonor().equals(p.getBloodTypePatient()))
			sbp += 27.30;
		sbp -= 10.61;
		ret += .44*sbp;
		ret += 8.57*p.getBMismatches(a);
		ret += 8.26*p.getDRMismatches(a);
		ret -= 50.87*Math.min(p.getDRWR(a),0.9);
		return ret;
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		

		long rFailureSeed = System.currentTimeMillis(); // for experiments,
		// set seed
		// explicitly, e.g.
		// "12345L" and
		// record
		Random rFailure = new Random(rFailureSeed);
		long rEntranceSeed = System.currentTimeMillis() + 1L;
		Random rEntrance = new Random(rEntranceSeed);
		long rDepartureSeed = System.currentTimeMillis() + 2L;
		Random rDeparture = new Random(rDepartureSeed);

		PoolGenerator poolGen = new SaidmanPoolGenerator(rEntrance);
		ExponentialArrivalDistribution m = new ExponentialArrivalDistribution(1.0 / EXPECTED_PAIRS);
		ExponentialArrivalDistribution a = new ExponentialArrivalDistribution(1.0 / EXPECTED_ALTRUISTS);
		Pool pool = new Pool(Edge.class);
		ArrayList<Cycle> matches = new ArrayList<Cycle>();

		pool.addPair(subject);
		poolGen.addVerticesToPool(pool, pairs0, alts0);

		int totalSeen = 0;
		int totalMatched = 0;
		int totalFailedMatches = 0;
		int totalDeceased = 0;

		int t = 0;

		while (true) {
			if(t == 365){
				System.out.println("DNM");
				break;
			}
			boolean stop = false;
			// Add new vertices to the pool
			int pairs = m.draw().intValue();
			int alts = a.draw().intValue();
			//System.out.println("ITERATION: " + t + "\t" + pairs + " new pairs and " + alts + " new altruist(s)");
			if (pairs > 0) {
				totalSeen += poolGen.addVerticesToPool(pool, pairs, alts).size();
			}
			FailureProbabilityUtil.setFailureProbability(pool,
					FailureProbabilityUtil.ProbabilityDistribution.CONSTANT, rFailure);

			// Remove all pairs where the patient dies
			ArrayList<VertexPair> rm = new ArrayList<VertexPair>();
			for (VertexPair v : pool.getPairs()) {
				if (!v.equals(subject) && rDeparture.nextDouble() <= DEATH) {
					totalDeceased++;
					Iterator<Cycle> matchIterator = matches.iterator();
					while (matchIterator.hasNext()) {
						Cycle c = matchIterator.next();
						if (Cycle.getConstituentVertices(c, pool).contains(v)) {
							matchIterator.remove();
						}
					}
					rm.add(v);
				}
			}
			for (VertexPair v : rm) {
				pool.removeVertex(v);
			}
			// Remove all altruists that run out of patience
			Iterator<VertexAltruist> aiter = pool.getAltruists().iterator();
			ArrayList<VertexAltruist> toRemove = new ArrayList<VertexAltruist>();
			while (aiter.hasNext()) {
				VertexAltruist alt = aiter.next();
				if (rDeparture.nextDouble() <= PATIENCE) {
					toRemove.add(alt);
				}
			}
			pool.removeAllVertices(toRemove);

			// Remove edges in matchings
			Iterator<Cycle> iter = matches.iterator();
			while (iter.hasNext()) {
				Cycle ci = iter.next();
				boolean fail = false;
				for (Edge e : ci.getEdges()) {
					if (rFailure.nextDouble() <= e.getFailureProbability()) {
						iter.remove();
						totalFailedMatches++;
						fail = true;
						break;
					}
				}
				if (fail) {
					continue;
				}
				// All edges in the Cycle remain, so we have a match!
				else {
					// We matched a chain, now we have to make the last
					// donor a bridge donor with some probability
					if (Cycle.isAChain(ci, pool)) {
						ArrayList<VertexPair> trm = new ArrayList<VertexPair>();
						List<Edge> le = new ArrayList<Edge>();
						for(Edge e : ci.getEdges()){
							le.add(e);
						}
						Collections.reverse(le);
						le.remove(le.size()-1);
						for(Edge e : le){
							if(pool.getEdgeTarget(e).equals(subject)){
								VertexAltruist bridge;
								if(!pool.getEdgeSource(e).isAltruist()){
									VertexPair bridgep = (VertexPair) pool.getEdgeSource(e);
									bridge = new VertexAltruist(bridgep.getID(),
											bridgep.getBloodTypeDonor(), bridgep.getDonorWeight(), bridgep.isDonorMale(), bridgep.getDonorHLA_B(), bridgep.getDonorHLA_DR(), bridgep.getDonorAge(), bridgep.isAfricanAmerican(), bridgep.getDonorSBP(), bridgep.isCigaretteUser(), bridgep.getDonorBMI(), bridgep.geteGFR());
								}
								else{
									bridge = (VertexAltruist)pool.getEdgeSource(e);
								}
								System.out.println(bridge.getDonorAge()+" "+bridge.geteGFR()+" "+bridge.getDonorBMI()+" "+bridge.getDonorSBP()+" "+bridge.isAfricanAmerican()+" "+bridge.isCigaretteUser()+" "+bridge.isMale()+" "+bridge.getBloodTypeDonor().toString()+" "+bridge.getHLA_B()[0]+" "+bridge.getHLA_B()[1]+" "+bridge.getHLA_DR()[0]+" "+bridge.getHLA_DR()[1]+" "+bridge.getWeight()+" "+t);
								stop = true;
								break;
							}
							// The bridge donor reneged, we stop the chain here
							if (rDeparture.nextDouble() <= RENEGE) {
								trm.add((VertexPair)pool.getEdgeTarget(e));
								break;
							} 

							totalMatched++;
						}
						pool.removeAllVertices(trm);

					}
					else{
						// Remove all vertices in the match from the pool
						if(Cycle.getConstituentVertices(ci, pool).contains(subject)){
							stop = true;
							for(Edge e : ci.getEdges()){
								if(pool.getEdgeTarget(e).equals(subject)){
									VertexPair donorPair = (VertexPair)pool.getEdgeSource(e);
									VertexAltruist donor = new VertexAltruist(donorPair.getID(),
											donorPair.getBloodTypeDonor(), donorPair.getDonorWeight(), donorPair.isDonorMale(), donorPair.getDonorHLA_B(), donorPair.getDonorHLA_DR(), donorPair.getDonorAge(), donorPair.isAfricanAmerican(), donorPair.getDonorSBP(), donorPair.isCigaretteUser(), donorPair.getDonorBMI(), donorPair.geteGFR());
									System.out.println(donor.getDonorAge()+" "+donor.geteGFR()+" "+donor.getDonorBMI()+" "+donor.getDonorSBP()+" "+donor.isAfricanAmerican()+" "+donor.isCigaretteUser()+" "+donor.isMale()+" "+donor.getBloodTypeDonor().toString()+" "+donor.getHLA_B()[0]+" "+donor.getHLA_B()[1]+" "+donor.getHLA_DR()[0]+" "+donor.getHLA_DR()[1]+" "+donor.getWeight()+" "+t);
								}
							}
						}
						totalMatched += Cycle.getConstituentVertices(ci, pool).size();
						pool.removeAllVertices(Cycle.getConstituentVertices(ci, pool));
					}
					if(stop)
						break;
					// Remove this match from our current set of matchings
					iter.remove();
				}

			}

			// Match the vertex pairs in the pool
			CycleGenerator cg = new CycleGenerator(pool);
			//List<Cycle> cycles = cg.generateCyclesAndChains(CYCLE_CAP, 0, true);
			// CycleMembership membership = new CycleMembership(pool,
			// cycles);
			// CyclesSampleChainsIPPacker optIPS = new
			// CyclesSampleChainsIPPacker(pool, cycles, 100, CHAIN_CAP,
			// true);

			try {
				// Solution optSolIP = optIPS.solve();
				GreedyPackingSolver s = new GreedyPackingSolver(pool);
				List<Cycle> reducedCycles = (new CycleGenerator(pool)).generateCyclesAndChains(CYCLE_CAP, 0, true);
				Solution sol = s.solve(1, new CyclesSampleChainsIPPacker(pool, reducedCycles, 100, CHAIN_CAP, true),
						Double.MAX_VALUE);
				for (Cycle c : sol.getMatching()) {
					matches.add(c);
				}

			} catch (SolverException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			/*System.out.println("ITERATION: "+t);

				System.out.println(totalSeen + " vertices were seen");
				System.out.println(totalMatched + " vertices were matched");
				System.out.println(totalFailedMatches + " matches failed");
				System.out.println(totalDeceased + " patients died");*/

			long endTime = System.currentTimeMillis();

			//System.out.println(endTime - startTime);
			t++;
			if(stop)
				break;
			
		}
		

	}

}