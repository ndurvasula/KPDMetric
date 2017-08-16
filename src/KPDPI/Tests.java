package KPDPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.special.Gamma;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.generator.SaidmanPoolGenerator;

public class Tests {
	static final int TRAJECTORIES = 1; //trajectories per simulation
	static final int SAMPLES = 5; //number of samples to get the lipschitz bound
	static final int PAIRS = 500; //number of start pairs
	static final int ALTS = 10;  //number of start altruists
	static final double SUPERSAMPLES = 15;
	static int TESTS = 10; //
	static int INC = 10;
	static final double ALPHA = .997;

	public static void main(String[] args) {
		SaidmanPoolGenerator s = new SaidmanPoolGenerator(new Random());
		VertexPair subject = s.generatePair(0);
		double[][] data = new Simulation(subject,TRAJECTORIES,PAIRS,ALTS).run();
		
	}

}
