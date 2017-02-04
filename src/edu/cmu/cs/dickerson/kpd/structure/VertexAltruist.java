package edu.cmu.cs.dickerson.kpd.structure;

import edu.cmu.cs.dickerson.kpd.structure.real.UNOSPair;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class VertexAltruist extends Vertex {

	// Altruist's blood type
	private final BloodType bloodTypeDonor;
	
	//Used in calculating the patient donor weight ratio
	private final double weight;
	
	private final boolean isMale;
	
	//HLA B and DR
	private final int[] HLA_B;
	private final int[] HLA_DR;

	
	/**
	 * Constructor for SIMULATED data (Saidman, Heterogeneous, etc)
	 * @param ID
	 * @param bloodTypeDonor
	 */
	public VertexAltruist(int ID, BloodType bloodTypeDonor, double weight, boolean isMale, int[] HLA_B, int[] HLA_DR) {
		super(ID);
		this.bloodTypeDonor = bloodTypeDonor;
		this.weight = weight;
		this.isMale = isMale;
		this.HLA_B = HLA_B;
		this.HLA_DR = HLA_DR;
		
	}
	
	@Override
	public boolean isAltruist() {
		return true;
	}

	public BloodType getBloodTypeDonor() {
		return bloodTypeDonor;
	}
	
	public double getWeight() {
		return weight;
	}

	public boolean isMale() {
		return isMale;
	}

	public int[] getHLA_B() {
		return HLA_B;
	}

	public int[] getHLA_DR() {
		return HLA_DR;
	}
}
