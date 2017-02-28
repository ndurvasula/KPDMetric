package edu.cmu.cs.dickerson.kpd.structure;

import edu.cmu.cs.dickerson.kpd.structure.real.UNOSPair;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class VertexPair extends Vertex {
	
	// Blood types for the patient and donor in the pair
	private BloodType bloodTypePatient;
	private BloodType bloodTypeDonor;
	
	// Patient's calculated probability of positive crossmatch, scaled [0,1]
	private final double patientCPRA;
	
	//estimated glomerular filtration rate, in mL/min/1.73 m^2
	private final double eGFR;
		
	private final double patientBMI;
	
	private final double patientWeight;
	
	private final double donorWeight;
	
	// Patient is wife of the donor (affects HLA)
	private final boolean isWifePatient;
	
	private final boolean isAfricanAmerican;
	
	private final boolean isCigaretteUser;
	
	private final boolean isPatientMale;
	
	private final boolean isDonorMale;
	
	//Is the donor a blood relative of the patient?
	private final boolean isRelated;

	// Is the donor compatible with the patient
	private final boolean isCompatible;
	
	private final int patientAge;
	
	//Systolic blood pressure
	private final int patientSBP;
	
	//HLA Donor
	private final int[] donorHLA_B;
	private final int[] donorHLA_DR;
	
	//HLA Patient
	private final int[] patientHLA_B;
	private final int[] patientHLA_DR;
	
	public Object[] features;
	
	public VertexPair(int ID, BloodType bloodTypePatient, BloodType bloodTypeDonor, boolean isWifePatient, double patientCPRA, boolean isCompatible, double eGFR, double patientBMI, double patientWeight, double donorWeight, boolean isAfricanAmerican, boolean isCigaretteUser, boolean isPatientMale, boolean isDonorMale, boolean isRelated, int patientAge, int patientSBP, int[] donorHLA_B, int[] donorHLA_DR, int[] patientHLA_B, int[] patientHLA_DR) {
		super(ID);
		this.bloodTypePatient = bloodTypePatient;
		this.bloodTypeDonor = bloodTypeDonor;
		this.isWifePatient = isWifePatient;
		this.patientCPRA = patientCPRA;
		this.isCompatible = isCompatible;
		this.eGFR = eGFR;
		this.patientBMI = patientBMI;
		this.patientWeight = patientWeight;
		this.donorWeight = donorWeight;
		this.isAfricanAmerican = isAfricanAmerican;
		this.isCigaretteUser = isCigaretteUser;
		this.isPatientMale = isPatientMale;
		this.isDonorMale = isDonorMale;
		this.isRelated = isRelated;
		this.patientAge = patientAge;
		this.patientSBP = patientSBP;
		this.donorHLA_B = donorHLA_B;
		this.donorHLA_DR = donorHLA_DR;
		this.patientHLA_B = patientHLA_B;
		this.patientHLA_DR = patientHLA_DR;
		features = new Object[] {bloodTypePatient, bloodTypeDonor, isWifePatient, patientCPRA, isCompatible, eGFR, patientBMI, patientWeight, donorWeight, isAfricanAmerican, isCigaretteUser, isPatientMale, isDonorMale, patientAge, patientSBP, donorHLA_B, donorHLA_DR, patientHLA_B, patientHLA_DR};
	}
	
	public int getBMismatches(){
		int m = 0;
		for(int i=0;i<patientHLA_B.length;i++){
			if(donorHLA_B[i] != patientHLA_B[i])
				m++;
		}
		return m;
	}
	
	public int getBMismatches(VertexAltruist a){
		int m = 0;
		for(int i=0;i<patientHLA_B.length;i++){
			if(a.getHLA_B()[i] != patientHLA_B[i])
				m++;
		}
		return m;
	}
	
	public int getDRMismatches(){
		int m = 0;
		for(int i=0;i<patientHLA_DR.length;i++){
			if(donorHLA_DR[i] != patientHLA_DR[i])
				m++;
		}
		return m;
	}
	
	public int getDRMismatches(VertexAltruist a){
		int m = 0;
		for(int i=0;i<patientHLA_DR.length;i++){
			if(a.getHLA_DR()[i] != patientHLA_DR[i])
				m++;
		}
		return m;
	}
	
	public double getDRWR(){
		return donorWeight/patientWeight;
	}
	
	public double getDRWR(VertexAltruist a){
		return a.getWeight()/patientWeight;
	}
	
	public double geteGFR() {
		return eGFR;
	}

	public double getPatientBMI() {
		return patientBMI;
	}

	public double getPatientWeight() {
		return patientWeight;
	}

	public double getDonorWeight() {
		return donorWeight;
	}

	public boolean isAfricanAmerican() {
		return isAfricanAmerican;
	}

	public boolean isCigaretteUser() {
		return isCigaretteUser;
	}

	public boolean isPatientMale() {
		return isPatientMale;
	}

	public boolean isDonorMale() {
		return isDonorMale;
	}

	public boolean isRelated() {
		return isRelated;
	}

	public int getPatientAge() {
		return patientAge;
	}

	public int getPatientSBP() {
		return patientSBP;
	}

	public int[] getDonorHLA_B() {
		return donorHLA_B;
	}

	public int[] getDonorHLA_DR() {
		return donorHLA_DR;
	}

	public int[] getPatientHLA_B() {
		return patientHLA_B;
	}

	public int[] getPatientHLA_DR() {
		return patientHLA_DR;
	}

	@Override
	public boolean isAltruist() {
		return false;
	}

	public BloodType getBloodTypePatient() {
		return bloodTypePatient;
	}

	public BloodType getBloodTypeDonor() {
		return bloodTypeDonor;
	}

	public double getPatientCPRA() {
		return patientCPRA;
	}

	public boolean isWifePatient() {
		return isWifePatient;
	}

	public boolean isCompatible() {
		return isCompatible;
	}

	public void setBloodTypePatient(BloodType bloodTypePatient) {
		this.bloodTypePatient = bloodTypePatient;
	}

	public void setBloodTypeDonor(BloodType bloodTypeDonor) {
		this.bloodTypeDonor = bloodTypeDonor;
	}
	
	
}
