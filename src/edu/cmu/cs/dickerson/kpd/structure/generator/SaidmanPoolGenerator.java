package edu.cmu.cs.dickerson.kpd.structure.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import edu.cmu.cs.dickerson.kpd.structure.Edge;
import edu.cmu.cs.dickerson.kpd.structure.Pool;
import edu.cmu.cs.dickerson.kpd.structure.Vertex;
import edu.cmu.cs.dickerson.kpd.structure.VertexAltruist;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

/**
 * Compatibility graph generator based on the following paper:
 * <i>Increasing the Opportunity of Live Kidney Donation by Matching for Two and Three Way Exchanges.</i>
 * S. L. Saidman, Alvin Roth, Tayfun Sonmez, Utku Unver, Frank Delmonico.
 * <b>Transplantation</b>.  Volume 81, Number 5, March 15, 2006.
 * 
 * This is known colloquially as the "Saidman Generator".
 * 
 * @author John P. Dickerson
 *
 */
public class SaidmanPoolGenerator extends PoolGenerator {

	// Numbers taken from Saidman et al.'s 2006 paper "Increasing
	// the Opportunity of Live Kidney Donation..."

	protected double Pr_SPOUSAL_DONOR = 0.4897;

	/*protected double Pr_LOW_PRA = 0.7019;
	protected double Pr_MED_PRA = 0.2;

	protected double Pr_LOW_PRA_INCOMPATIBILITY = 0.05;
	protected double Pr_MED_PRA_INCOMPATIBILITY = 0.45;
	protected double Pr_HIGH_PRA_INCOMPATIBILITY = 0.90;*/
	
	protected double Pr_LOW_PRA = 0.216;  // 0.007 + 0.001 + 0.016 + 0.192 = 0.216
	protected double Pr_MED_PRA = 0.16;   // 0.018 + 0.001 + 0.025 + 0.116 = 0.16
	// Pr_HIGH_PRA = 1.0 - 0.216 - 0.16 = 0.624
	
	protected double Pr_LOW_PRA_INCOMPATIBILITY = 0.50;
	protected double Pr_MED_PRA_INCOMPATIBILITY = 0.80;
	protected double Pr_HIGH_PRA_INCOMPATIBILITY = 0.98;

	protected double Pr_SPOUSAL_PRA_COMPATIBILITY = 0.75;
	
	//Probabilities/values for features required by LKDPI


	protected double Pr_isAfricanAmerican = 0.131;
	protected double Pr_isCigaretteUser = .24;
	
	//Doubles/Integers

	//Systolic blood pressure (LOW is healthy)
	protected double Pr_SBP_HIGH = .071;
	protected double Pr_SBP_MED = .396;
	protected double Pr_SBP_LOW = .533;
	
	HashMap<String, ArrayList<String>> fmap = new HashMap<String, ArrayList<String>>();
	
	// Current unused vertex ID for optimization graphs
	public int currentVertexID;

	public SaidmanPoolGenerator(Random random) {
		super(random);
		this.currentVertexID = 1;
	}

	public String drawFromFile(String featureName) throws FileNotFoundException{
		ArrayList<String> data = fmap.get(featureName);
		if(data == null){
			Scanner sc = new Scanner(new File("distributions/"+featureName+".txt"));
			data = new ArrayList<String>();
			while(sc.hasNext()){
				String a = sc.next();
				if(!a.equals("UNK"))
					data.add(a);
			}
			sc.close();
			fmap.put(featureName, data);
		}
		return data.get(random.nextInt(data.size()));
	}
	
	/**
	 * Draws a random patient's blood type using UNOS distribution
	 * @return BloodType.{O,A,B,AB}
	 * @throws FileNotFoundException 
	 */
	public BloodType drawPatientBloodType() throws FileNotFoundException {
		String BT = drawFromFile("ABO");
		if(BT.contains("O"))
			return BloodType.O;
		else if(BT.contains("A"))
			return BloodType.A;
		else if(BT.contains("B"))
			return BloodType.B;
		else if(BT.contains("AB"))
			return BloodType.AB;
		else{
			System.out.println(BT);
			return null;
		}
		
	}

	/**
	 * Draws a random donor's blood type from the US distribution 
	 * @return BloodType.{O,A,B,AB}
	 * @throws FileNotFoundException 
	 */
	public BloodType drawDonorBloodType() throws FileNotFoundException {
		String BT = drawFromFile("ABO_DON");
		if(BT.contains("O"))
			return BloodType.O;
		else if(BT.contains("A"))
			return BloodType.A;
		else if(BT.contains("B"))
			return BloodType.B;
		else if(BT.contains("AB"))
			return BloodType.AB;
		else{
			System.out.println(BT);
			return null;
		}
		
	}
	//.24 for cigarette, .11 for african american, GFR random between 78 to 136, SBP < 120 .533 120-139 .396 >=140 .71
	public double draweGFR() {
		double r = random.nextInt(59)+78;
		return r/10;
	}
	
	public double drawBMI() throws FileNotFoundException {
		String BMI = drawFromFile("BMI_DON_CALC");
		return Double.parseDouble(BMI);
	}
	
	public double drawDonorWeight() throws FileNotFoundException {
		String DW = drawFromFile("WGT_KG_DON_CALC");
		return Double.parseDouble(DW);
	}
	public double drawPatientWeight() throws NumberFormatException, FileNotFoundException {
		return Double.parseDouble(drawFromFile("WGT_KG_CALC"));
	}
	public int drawDonorAge() throws FileNotFoundException {
		String age = drawFromFile("AGE_DON");
		return (int)Double.parseDouble(age);
	}
	
	public int drawSBP() {
		double r = random.nextDouble();

		if (r <= Pr_SBP_LOW) { 
			return random.nextInt(11)+110; 
			}
		if (r <= Pr_SBP_LOW + Pr_SBP_MED) { 
			return random.nextInt(20)+120; 
			}
		if (r <= Pr_SBP_LOW + Pr_SBP_MED + Pr_SBP_HIGH) { 
			return random.nextInt(11)+140; 
			}
		return 0;
	}
	

	/**
	 * Draws a random spousal relationship between donor and patient
	 * @return true if willing donor is patient's spouse, false otherwise
	 */
	public boolean isDonorSpouse() {
		return random.nextDouble() <= Pr_SPOUSAL_DONOR;
	}

	/**
	 * Random roll to see if a patient and donor are crossmatch compatible
	 * @param pr_PraIncompatibility probability of a PRA-based incompatibility
	 * @return true is simulated positive crossmatch, false otherwise
	 */
	public boolean isPositiveCrossmatch(double pr_PraIncompatibility) {
		return random.nextDouble() <= pr_PraIncompatibility;
	}
	
	public boolean isAfricanAmerican(){
		return random.nextDouble() <= Pr_isAfricanAmerican;
	}
	
	public boolean isCigaretteUser(){
		return random.nextDouble() <= Pr_isCigaretteUser;
	}
	
	public boolean isDonorMale() throws FileNotFoundException{
		return drawFromFile("GENDER_DON").equals("M");
	}
	
	public boolean isPatientMale() throws FileNotFoundException{
		return drawFromFile("GENDER").equals("M");
	}
	
	
	public boolean isRelated(){
		return false;
	}

	/**
	 * Randomly generates CPRA (Calculated Panel Reactive Antibody) for a
	 * patient-donor pair, using the Saidman method.  If the patient is the
	 * donor's wife, then CPRA is increased.
	 * @param isWifePatient is the patent the wife of the donor?
	 * @return scaled CPRA double value between 0 and 1.0
	 */
	public double generatePraIncompatibility(boolean isWifePatient) {
		double pr_PraIncompatiblity;

		double r = random.nextDouble();
		if (r <= Pr_LOW_PRA) {
			pr_PraIncompatiblity = Pr_LOW_PRA_INCOMPATIBILITY;
		} else if (r <= Pr_LOW_PRA + Pr_MED_PRA) {
			pr_PraIncompatiblity = Pr_MED_PRA_INCOMPATIBILITY;
		} else {
			pr_PraIncompatiblity = Pr_HIGH_PRA_INCOMPATIBILITY;
		}

		if (!isWifePatient) { 
			return pr_PraIncompatiblity; 
		} else {
			return 1.0 - Pr_SPOUSAL_PRA_COMPATIBILITY*(1.0 - pr_PraIncompatiblity);
		}
	}	
	
	public int[] drawHLA_B() throws NumberFormatException, FileNotFoundException{
		int[] res = new int[2];
		res[0] = (int)Double.parseDouble(drawFromFile("B1"));
		res[1] = (int)Double.parseDouble(drawFromFile("B2"));
		return res;
	}
	
	public int[] drawHLA_DR() throws NumberFormatException, FileNotFoundException{
		int[] res = new int[2];
		res[0] = (int)Double.parseDouble(drawFromFile("DR1"));
		res[1] = (int)Double.parseDouble(drawFromFile("DR2"));
		return res;
	}
	public int[] drawDonorHLA_B() throws NumberFormatException, FileNotFoundException{
		int[] res = new int[2];
		res[0] = (int)Double.parseDouble(drawFromFile("DB1"));
		res[1] = (int)Double.parseDouble(drawFromFile("DB2"));
		return res;
	}
	
	public int[] drawDonorHLA_DR() throws NumberFormatException, FileNotFoundException{
		int[] res = new int[2];
		res[0] = (int)Double.parseDouble(drawFromFile("DDR1"));
		res[1] = (int)Double.parseDouble(drawFromFile("DDR2"));
		return res;
	}


	/**
	 * Randomly rolls a patient-donor pair (possibly compatible or incompatible)
	 * @param ID unique identifier for the vertex
	 * @return a patient-donor pair KPDVertexPair
	 * @throws FileNotFoundException 
	 */
	public VertexPair generatePair(int ID) throws FileNotFoundException {

		// Draw blood types for patient and donor, along with spousal details and probability of PositiveXM
		BloodType bloodTypePatient = drawPatientBloodType();
		BloodType bloodTypeDonor = drawDonorBloodType();
		double eGFR = draweGFR();
		double bmi = drawBMI();
		double pweight = drawPatientWeight();
		double dweight = drawDonorWeight();
		boolean iaa = isAfricanAmerican();
		boolean icu = isCigaretteUser();
		boolean idm = isDonorMale();
		boolean ipm = isPatientMale();
		boolean isWifePatient = !ipm && isDonorSpouse();
		double patientCPRA = generatePraIncompatibility(isWifePatient);
		boolean ir = isRelated();
		int age = drawDonorAge();
		int sbp = drawSBP();
		int[] pHLA_B = drawHLA_B();
		int[] dHLA_B = drawHLA_B();
		int[] pHLA_DR = drawHLA_DR();
		int[] dHLA_DR = drawHLA_DR();
		

		// Can this donor donate to his or her patient?
		boolean compatible = bloodTypeDonor.canGiveTo(bloodTypePatient)    // Donor must be blood type compatible with patient
				&& !isPositiveCrossmatch(patientCPRA);   // Crossmatch must be negative

		return new VertexPair(ID, bloodTypePatient, bloodTypeDonor, isWifePatient, patientCPRA, compatible, eGFR, bmi, pweight, dweight, iaa, icu, ipm, idm, ir, age, sbp, dHLA_B, dHLA_DR, pHLA_B, pHLA_DR);
	}
	
	

	/**
	 * Random rolls an altruistic donor (donor with no attached patient)
	 * @param ID unique identifier for the vertex
	 * @return altruistic donor vertex KPDVertexAltruist
	 * @throws FileNotFoundException 
	 * @throws NumberFormatException 
	 */
	public VertexAltruist generateAltruist(int ID) throws NumberFormatException, FileNotFoundException {

		// Draw blood type for the altruist
		BloodType bloodTypeAltruist = drawDonorBloodType();

		return new VertexAltruist(ID, bloodTypeAltruist, drawDonorWeight(), isDonorMale(), drawDonorHLA_B(), drawDonorHLA_DR(), drawDonorAge(), isAfricanAmerican(), drawSBP(), isCigaretteUser(), drawBMI(), draweGFR());
	}


	public boolean isCompatible(VertexPair donor, VertexPair patient) { 
		boolean compatible = donor.getBloodTypeDonor().canGiveTo(patient.getBloodTypePatient())    // Donor must be blood type compatible with patient
				&& !isPositiveCrossmatch(patient.getPatientCPRA());   // Crossmatch must be negative
		return compatible;
	}
	
	public boolean isCompatible(VertexAltruist alt, VertexPair patient) { 
		boolean compatible = alt.getBloodTypeDonor().canGiveTo(patient.getBloodTypePatient())    // Donor must be blood type compatible with patient
				&& !isPositiveCrossmatch(patient.getPatientCPRA());   // Crossmatch must be negative
		return compatible;
	}
	
	
	
	@Override
	public Pool generate(int numPairs, int numAltruists) {

		assert(numPairs > 0);
		assert(numAltruists >= 0);

		// Keep track of the three types of vertices we can generate: 
		// altruist-no_donor, patient-compatible_donor, patient-incompatible_donor
		List<VertexPair> incompatiblePairs = new ArrayList<VertexPair>();
		List<VertexPair> compatiblePairs = new ArrayList<VertexPair>();
		List<VertexAltruist> altruists = new ArrayList<VertexAltruist>();

		// Generate enough incompatible and compatible patient-donor pair vertices
		while(incompatiblePairs.size() < numPairs) {

			VertexPair v = null;
			try {
				v = generatePair(currentVertexID++);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(v.isCompatible()) {
				compatiblePairs.add(v);  // we don't do anything with these
				currentVertexID--;       // throw away compatible pair; reuse the ID
			} else {
				incompatiblePairs.add(v);
			}
		}

		// Generate altruistic donor vertices
		while(altruists.size() < numAltruists) {
			VertexAltruist altruist = null;
			try {
				altruist = generateAltruist(currentVertexID++);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			altruists.add(altruist);
		}

		
		

		// Only add the incompatible pairs to the pool
		Pool pool = new Pool(Edge.class);
		for(VertexPair pair : incompatiblePairs) {
			pool.addPair(pair);	
		}


		// Add altruists to the pool
		for(VertexAltruist altruist : altruists) {
			pool.addAltruist(altruist);
		}


		// Add edges between compatible donors and other patients
		for(VertexPair donorPair : incompatiblePairs) {
			for(VertexPair patientPair : incompatiblePairs) {

				if(donorPair.equals(patientPair)) { continue; }

				if(isCompatible(donorPair, patientPair)) {
					Edge e = pool.addEdge(donorPair, patientPair);
					pool.setEdgeWeight(e, 1.0);
				}
			}
		}




		for(VertexAltruist alt : altruists) {
			for(VertexPair patientPair : incompatiblePairs) {

				// Add edges from a donor to a compatible patient elsewhere
				if(isCompatible(alt, patientPair)) {
					Edge e = pool.addEdge(alt, patientPair);
					pool.setEdgeWeight(e, 1.0);
				}
				
				// Add dummy edges from a non-altruist donor to each of the altruists
				Edge dummy = pool.addEdge(patientPair, alt);
				pool.setEdgeWeight(dummy, 0.0);
			}
		}

		return pool;
	}

	
	@Override
	public Set<Vertex> addVerticesToPool(Pool pool, int numPairs, int numAltruists) {
		
		// Generate new vertices
		Pool more = this.generate(numPairs, numAltruists);
		
		// Add edges from/to the new vertices
		for(VertexPair v : more.getPairs()) { pool.addPair(v); }
		for(VertexPair vN : more.getPairs()) {
			for(VertexPair vO : pool.getPairs()) {
				if(vN.equals(vO)) { continue; }  // Don't add self-edges
				
				// Donate from new vertex to other vertex
				if(isCompatible(vN, vO) && !pool.containsEdge(vN, vO)) {
					pool.setEdgeWeight(pool.addEdge(vN, vO), 1.0);
				}
				// Donate from other vertex to new vertex
				if(isCompatible(vO, vN)&& !pool.containsEdge(vO, vN)) {
					pool.setEdgeWeight(pool.addEdge(vO, vN), 1.0);
				}
			}
			
			// Adds edges from old altruists to new vertices
			for(VertexAltruist altO : pool.getAltruists()) {
				if(isCompatible(altO, vN)) {
					pool.setEdgeWeight(pool.addEdge(altO, vN), 1.0);
				}
				// Add dummy edges from a non-altruist donor to each of the altruists
				pool.setEdgeWeight(pool.addEdge(vN, altO), 0.0);
			}
		}
		
		
		// Add edges from/to the new altruists from all (old+new) vertices
		for(VertexAltruist a : more.getAltruists()) { pool.addAltruist(a); }
		for(VertexAltruist altN : more.getAltruists()) {
			// No edges between altruists
			for(VertexPair v : pool.getPairs()) {
				if(isCompatible(altN, v)) {
					pool.setEdgeWeight(pool.addEdge(altN, v), 1.0);
				}
				
				// Add dummy edges from a non-altruist donor to each of the altruists
				pool.setEdgeWeight(pool.addEdge(v, altN), 0.0);
			}
		}
		
		// Return only the new vertices that were generated
		return more.vertexSet();
	}

}
