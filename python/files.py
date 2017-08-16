#!/usr/bin/env python

# External libraries
import numpy as np
import sklearn

# Python standard libraries
import os.path
import time

# My modules
import converters


class paths:

    # Raw input files (STAR data from OPTN DVD), to be loaded
    ftransplant="KIDPAN_DATA.DAT"
    fwaitlist="Waiting List History/KIDPAN_WLHISTORY_DATA.DAT"
    ffollowup="Individual Follow-up Records/KIDNEY_FOLLOWUP_DATA.DAT"
    fdelim="\t"

    # Binary .npy output files, to be written
    fbinary_base = "binaries/"
    ftransplant_out = "kidney_transplant_out.npy"
    fwaitlist_out = "kidney_waitlist_out.npy"
    ffollowup_out = "kidney_followup_out.npy"

    def __init__(self):
        #uname = os.uname()
        uname = "a"
        if uname[0] == 'Linux':
            if os.path.isdir("/home/spook"):
                self.fraw_base="/home/spook/STAR/data/Kidney_ Pancreas_ Kidney-Pancreas/"
            else:
                self.fraw_base="/usr0/home/jpdicker/STAR/data/Kidney_ Pancreas_ Kidney-Pancreas/"
        elif uname[0] == 'Darwin':
            self.fraw_base="/Users/spook/STAR/data/Kidney_ Pancreas_ Kidney-Pancreas/"
        else:
            self.fraw_base="D:\\Documents\\DD\Data\\STAR\\Delimited Text File 201306\\Delimited Text File\Kidney_ Pancreas_ Kidney-Pancreas\\"

    _instance = None
    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super(paths, cls).__new__(cls, *args, **kwargs)
        return cls._instance

    def delim_raw(self):
        return self.fdelim 
    def waitlist_raw(self):
        return os.path.join(self.fraw_base, self.fwaitlist)
    def followup_raw(self):
        return os.path.join(self.fraw_base, self.ffollowup)
    def transplant_raw(self):
        return os.path.join(self.fraw_base, self.ftransplant)
    def waitlist_binary(self):
        return os.path.join(self.fbinary_base, self.fwaitlist_out)
    def followup_binary(self):
        return os.path.join(self.fbinary_base, self.ffollowup_out)
    def transplant_binary(self):
        return os.path.join(self.fbinary_base, self.ftransplant_out)



# Loads .npy binary of Kidney Waiting List History
def waitlist_data():
    return np.load( paths().waitlist_binary() )

# Loads .npy binary of Individual Kindey Followup
def followup_data():
    return np.load( paths().followup_binary() )

# Loads .npy binary of Kidney-Pancreas transplants
def transplant_data():
    return np.load( paths().transplant_binary() )



# Converts raw "Kidney Waiting List History" file to .npy binary
# *  Details of all modifications to waiting list record while registrations 
#    listed.  Includes changes to active/inactive status,  antigens, PRA, etc.
#    Will be multiple records per waiting list registration.
# File length: 17,280,189 rows
# File load time:  ~20 minutes
def convert_waitlist_data(fwaitlist, fdelim, fwaitlist_out):
    
    print "Loading data from waiting list file {0}".format(fwaitlist)
    start = time.clock()
    waitlist_data = np.genfromtxt( fname=fwaitlist,
                                   delimiter=fdelim,
                                   comments=None,
                                   dtype="f8,a1,f8,a1,f8,f8,a10,a12,f8,f8,f8,a1,f8,f8,f8,f8,f8,f8,f8,a1,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,a1,a1,a1,a1,a1,a1,a1,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8",
                                   names=["CHG_TY","UNOS_CAND_STAT_CD","PRELIM_XMATCH_REQ","INACT_REASON_CD","CPRA","CHG_DATE","CHG_TIME","WLREG_AUDIT_ID_CODE","CURRENT_PRA","PEAK_PRA","USE_WHICH_PRA","MAX_MISMATCH_A","MAX_MISMATCH_AB","MAX_MISMATCH_ABDR","MAX_MISMATCH_ADR","MAX_MISMATCH_B","MAX_MISMATCH_BDR","MAX_MISMATCH_DR","ACPT_ORGAN_PROC_OTHTEAM","ON_EXPAND_DONOR","MAX_EXPANDED_MISMATCH_A","MAX_EXPANDED_MISMATCH_AB","MAX_EXPANDED_MISMATCH_ABDR","MAX_EXPANDED_MISMATCH_ADR","MAX_EXPANDED_MISMATCH_B","MAX_EXPANDED_MISMATCH_BDR","MAX_EXPANDED_MISMATCH_DR","ON_IEXPAND_DONOR","MAX_IEXPANDED_MISMATCH_A","MAX_IEXPANDED_MISMATCH_AB","MAX_IEXPANDED_MISMATCH_ABDR","MAX_IEXPANDED_MISMATCH_ADR","MAX_IEXPANDED_MISMATCH_B","MAX_IEXPANDED_MISMATCH_BDR","MAX_IEXPANDED_MISMATCH_DR","DONCRIT_MIN_AGE","DONCRIT_MIN_AGE_IMPORT","DONCRIT_MAX_AGE","DONCRIT_MAX_AGE_IMPORT","DONCRIT_MIN_WGT","DONCRIT_MAX_WGT","DONCRIT_MAX_BMI","DONCRIT_ACPT_DCD","DONCRIT_ACPT_DCD_IMPORT","DONCRIT_ACPT_HIST_DIABETES","DONCRIT_ACPT_HIST_HYPERTENSION","DONCRIT_ACPT_HBCOREPOS","DONCRIT_ACPT_HCVPOS","DONCRIT_ACPT_HTLV_POS","DONCRIT_MAX_WARM_TIME","DONCRIT_MAX_COLD_TIME","DONCRIT_MAX_PCT_SCLER_LT10GL","DONCRIT_MAX_PCT_SCLER_LT10GL_IMP","DONCRIT_MAX_PCT_SCLER_10PLGL","DONCRIT_MAX_PCT_SCLER_10PLGL_IMP","DONCRIT_MAX_MILE","DONCRIT_PEAK_CREAT","DONCRIT_PEAK_CREAT_IMPORT","DONCRIT_FINAL_CREAT","DONCRIT_FINAL_CREAT_IMPORT","DONCRIT_MAX_PEAK_AMYLASE","DONCRIT_MAX_PEAK_LIPASE"]
                                   )
    end = time.clock()
    print "Loaded {0} rows in {1} seconds from waitlist data.".format(len(waitlist_data), end-start)

    print "Saving to {0}.".format(waitlist_out)
    np.save(ffollowup_out, waitlist_data)
    print "Saved as a binary."

    

# Converts "Kidney Waiting List History" file to .npy binary
# *  Follow-up information for kidney transplants.  Link by TRR_ID_CODE to original transplant record.  Follow-up collected at six months and then annually, so may be multiple records per each transplant event.  If patient received a kidney-pancreas transplant prior to 1/27/2003, their follow-up data prior to that date will be found in both the kidney and pancreas follow-up datasets, not the kidpan follow-up dataset.
# File length: 2,395,422 rows
# File load time:  ~6 minutes
def convert_followup_data(ffollowup, fdelim, ffollowup_out):
    
    print "Loading data from kidney followup file: {0}".format(ffollowup)
    start = time.clock()
    followup_data = np.genfromtxt( fname=ffollowup,
                                   delimiter=fdelim,
                                   comments=None,
                                   dtype="f8,a1,f8,a50,a1,f8,a1,f8,f8,f8,a1,a50,a1,a2,a10,f8,a50,f8,a50,f8,a1,f8,f8,f8,f8,f8,a3,f8,f8,a1,a50,a50,a1,a50,a1,f8,f8,f8,f8,f8,f8,a1,f8,a1,f8,a50,f8,a1,a1,a1,a1,a50,a1,f8,a1,a1,a1,a1,a1,a1,a1,f8,f8,f8,f8,a1,f8,f8,a1,a2,a2,f8,f8,f8,f8,f8,a15,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,a1,f8,a15",
                                   names=["CARE_PROVIDED_BY","PX_STAT","COD","COD_OSTXT","HOSP","NUM_TXREL_HOSP","PX_NCOMPLIANT","FUNC_STAT","WGT_KG","HGT_CM","PX_RESEARCH","PX_RESEARCH_OSTXT","IMMUNO_DISCONTINUE","PERM_STATE","PERM_ZIP","COD2","COD2_OSTXT","COD3","COD3_OSTXT","PHYSICAL_CAPACITY","WORK_INCOME","WORK_NO_STATUS","WORK_YES_STATUS","ACADEMIC_PRG","ACADEMIC_LEVEL","PRI_PAYMENT","PRI_PAYMENT_CTRY","ACUTE_REJ_EPI","BIOPSY_CONFIRMED","ANTI_VIRAL","ANTI_VIRAL1_OSTXT","ANTI_VIRAL2_OSTXT","POLYOMA","POLYOMA_VIRUS_OSTXT","THERAPIES","THERAPIES_TREATMENT","IMMUNO_MAINT_MED","BMI","COGNITIVE_DEV","MOTOR_DEV","MEASUREMENT_DT","BK_VIRUS","CREAT","DIABETES_DURING","DIAL_TY","GRF_FAIL_CAUSE_OSTXT","GRF_FAIL_CAUSE_TY","GRF_STAT","GRF_THROMB","INFECT","INSULIN_DEPENDENT","OTH_GRF_FAIL_CAUSE_OSTXT","PX_NON_COMPL","RECURRED_ORIG_DGN","RECUR_DISEASE","REJ_ACUTE","REJ_CHRONIC","URINE_PROTEIN","UROL_COMPL","AVN","CORART","FRAC_EXTREM","FRAC_EXTREM_NUM","FRAC_OTHER","FRAC_OTHER_NUM","FRAC_PASTYR","FRAC_SPINE","FRAC_SPINE_NUM","GROWTH_HORMONE","CMV_IGG","CMV_IGM","PT_CODE","RETXDATE_KI","GRF_FAIL_DATE","PX_STAT_DATE","RESUM_MAINT_DIAL_DATE","TRR_ID_CODE","ACYCLOVIR","CYTOGAM","GAMIMUNE","GAMMAGARD","GANCICLOVIR","VALGANCYCLOVIR","HBIG","FLUVACCINE","LAMIVUDINE","OTHER","YES_IMMUNOSUPPRESSIONREDUCTION","YES_CIDOFOVIR","YES_IVIG","YES_TYPEUNKNOWN","YES_OTHER_SPECIFY","TRT_REJ","TRT_REJ_NUM","TRR_FOL_ID_CODE"],
                                   usecols=("GRF_FAIL_CAUSE_TY", "GRF_STAT"),
                               
                                   )
    end = time.clock()
    print "Loaded {0} rows in {1} seconds from kidney followup data.".format(len(followup_data), end-start)

    print "Saving to {0}.".format(ffollowup_out)
    np.save(ffollowup_out, followup_data)
    print "Saved as a binary."





# Converts "Kidney Waitlist and Transplant" file to .npy binary
# *  
# File length: 727476 rows
# File load time: ~1 minutes
def convert_transplant_data(ftransplant, fdelim, ftransplant_out):
    
    print "Loading data from kidney transplant file: {0}".format(ftransplant)
    start = time.clock()
    transplant_data = np.genfromtxt( fname=ftransplant,
                                   delimiter=fdelim,
                                   comments=None,
                                   dtype="a5,a5,a2,a2,a2,f8,f8,f8,a1,a1,a50,f8,a2,f8,a3,f8,a1,f8,f8,a3,a1,a10,f8,f8,a1,f8,f8,f8,a1,f8,a1,a1,f8,a1,f8,f8,a1,f8,a1,f8,f8,a1,f8,a1,a1,a1,a1,a1,a1,f8,f8,f8,f8,a1,a1,f8,f8,a22,f8,f8,f8,a2,f8,a3,f8,f8,a1,f8,a1,a1,f8,f8,a1,a1,f8,a50,a1,f8,f8,f8,f8,f8,f8,f8,a1,a10,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,a8,f8,f8,f8,a1,f8,f8,f8,f8,a1,a2,a2,a1,a1,a1,a1,a2,a2,a2,a2,a2,a2,a2,a2,f8,f8,f8,f8,f8,a2,a2,a3,a3,a1,a2,f8,a2,a2,f8,a3,a50,a1,a1,a1,a1,a1,f8,a1,f8,f8,a1,a1,a2,a2,a50,a50,a50,f8,a1,a50,a1,a1,a1,a1,a1,a50,a50,a50,a50,f8,a1,f8,f8,f8,f8,a1,a1,a2,a1,f8,f8,a50,a1,a1,a1,a1,a1,a1,a1,a1,a1,f8,a1,f8,a1,a1,a1,f8,a1,a1,a1,a1,f8,f8,a1,a1,a1,a50,a1,a1,a1,f8,f8,f8,f8,f8,f8,a10,a10,a10,a10,a1,a1,f8,f8,a1,a1,f8,a50,f8,f8,a1,a50,f8,a50,a1,a1,a1,a1,a1,a1,a1,a1,a1,a1,f8,f8,f8,f8,a50,f8,a50,f8,a50,a1,f8,f8,a1,a1,a1,f8,a3,f8,f8,a50,f8,a50,f8,f8,f8,f8,f8,f8,f8,a1,f8,a3,f8,a1,f8,f8,a6,a6,a6,a6,a6,a6,a1,a10,f8,a10,a10,f8,a50,a1,a50,f8,a50,a1,a1,a1,a1,a1,a1,a1,a1,a1,a1,a10,f8,f8,f8,a10,f8,a50,f8,a50,f8,a50,a1,f8,a10,f8,a1,a1,a2,a2,a2,a2,a2,a2,a2,a3,f8,f8,f8,f8,a1,a10,a10,f8,f8,f8,f8,f8,a1,f8,a1,a1,a50,f8,f8,f8,f8,a1,a1,a1,a1,a1,a15,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,a50,f8,f8,f8,f8,f8,f8,f8,a1,a1,a10,a10,a1,a1,a1,a50,f8,f8,f8,f8,f8,f8,a3,a1,a1,a1,a1,a1,a1,a1,a1,a1,a3,a1,f8,f8,a1,a1,f8,a1,a1,a1,f8,a1,f8,a10,a10,a10,f8,a2,a2,a2,a1,f8,f8,f8,a40,a40,a40,a4,f8,a50,f8,f8,f8,a1,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,f8,a10,a10,a10,a10,a10,a10,a10,a1,a1,a1,a1,a1,a1,a1,a1,a1,f8,a50,a50,f8,f8,a1,a1",
                                   names=["PERM_ZIP","PERM_ZIP_TRR","PREV_TX","PREV_KI_TX","PREV_PA_TX","ACADEMIC_LEVEL_TRR","ACADEMIC_PRG_TRR","FUNC_STAT_TRR","HOSP_90_DAYS","MALIG_TRR","MALIG_OSTXT_TRR","MALIG_TY_TRR","PERM_STATE_TRR","PRI_PAYMENT_TRR_KI","PRI_PAYMENT_CTRY_TRR_KI","SECONDARY_PAY_TRR_KI","WORK_INCOME_TRR","WORK_NO_STATUS_TRR","WORK_YES_STATUS_TRR","ABO","GENDER","TX_DATE","ACUTE_REJ_EPI_KI","BIOPSY_CONFIRMED_KI","CREAT_DECL25","CREAT_TRR","FIN_FLOW_RATE_TX","FIN_RESIST_TX","FIRST_WK_DIAL","ORG_REC_ON","PRE_TX_BIOP","PRE_TX_TXFUS","PREV_PREG","PROD_URINE24","REC_ON_ICE","REC_ON_PUMP","RESUM_MAINT_DIAL","SERUM_CREAT","TUMOR_TX","TUMOR_TY","REGION","PROD_URINE24_OLD","MULTIORG_ID","TXHRT","TXINT","TXKID","TXLIV","TXLNG","TXPAN","ACADEMIC_LEVEL_TCR","ACADEMIC_PRG_TCR","BMI_TCR","PHYSICAL_CAPACITY_TCR","PREV_PI_TX_TCR_NEW","WORK_INCOME_TCR","WORK_NO_STATUS_TCR","WORK_YES_STATUS_TCR","YR_ENTRY_US_TCR","CITIZENSHIP","EDUCATION","FUNC_STAT_TCR","PERM_STATE","PRI_PAYMENT_TCR_KI","PRI_PAYMENT_CTRY_TCR_KI","SECONDARY_PAY_TCR_KI","ANGINA","CEREB_VASC","DIAL_TY_TCR","DRUGTRT_COPD","DRUGTRT_HYP","MOST_RCNT_CREAT","PEPTIC_ULCER","PERIP_VASC","MALIG_TCR_KI","PREV_MALIG_TY","PREV_MALIG_TY_OSTXT","PULM_EMBOL","PREV_PI_TX_TCR_OLD","RDA1","RDA2","RDB1","RDB2","RDDR1","RDDR2","DON_RETYP","RESUM_MAINT_DIAL_DT","DA1","DA2","DB1","DB2","DDR1","DDR2","RA1","RA2","RB1","RB2","RDR1","RDR2","AMIS","BMIS","DRMIS","HLAMIS","NPKID","NPPAN","PRAMR_CL1","PRAMR_CL2","PRAMR","PRAPK_CL1","PRAPK_CL2","PRAPK","SSDMF_DEATH_DATE","ANGINA_OLD","CURRENT_PRA","PEAK_PRA","USE_WHICH_PRA","END_CPRA","PT_CODE","HAPLO_TY_MATCH_DON","AGE_DON","DDAVP_DON","CMV_OLD_LIV_DON","CMV_DON","CMV_TEST_DON","EBV_TEST_DON","HBV_TEST_DON","HCV_TEST_DON","CMV_NUCLEIC_DON","CMV_IGG_DON","CMV_IGM_DON","EBV_DNA_DON","EBV_IGG_DON","EBV_IGM_DON","HBV_CORE_DON","HBV_SUR_ANTIGEN_DON","ETHCAT_DON","COD_CAD_DON","DEATH_CIRCUM_DON","DEATH_MECH_DON","CITIZENSHIP_DON","HEP_C_ANTI_DON","HCV_RNA_DON","ABO_DON","DON_TY","GENDER_DON","HOME_STATE_DON","WARM_ISCH_TM_DON","HCV_RIBA_DON","HCV_ANTIBODY_DON","LIV_DON_TY","CITIZEN_COUNTRY_DON","COD_OSTXT_DON","CONTROLLED_DON","CORE_COOL_DON","NON_HRT_DON","ANTICONV_DON","ANTIHYPE_DON","BLOOD_INF_DON","BLOOD_INF_CONF_DON","BUN_DON","CREAT_DON","DOBUT_DON_OLD","DOPAMINE_DON_OLD","HTLV1_OLD_DON","HTLV2_OLD_DON","OTH_DON_MED1_OSTXT_DON_OLD","OTH_DON_MED2_OSTXT_DON_OLD","OTH_DON_MED3_OSTXT_DON_OLD","OTHER_INF_DON","OTHER_INF_CONF_DON","OTHER_INF_OSTXT_DON","PRETREAT_MED_DON_OLD","PT_DIURETICS_DON","PT_STEROIDS_DON","PT_T3_DON","PT_T4_DON","PT_OTH2_OSTXT_DON","PT_OTH3_OSTXT_DON","PT_OTH4_OSTXT_DON","PT_OTH1_OSTXT_DON","PULM_INF_DON","PULM_INF_CONF_DON","SGOT_DON","SGPT_DON","TBILI_DON","URINE_INF_DON","URINE_INF_CONF_DON","VASODIL_DON","VDRL_DON","CLIN_INFECT_DON","HYPERTENS_DUR_DON","CANCER_FREE_INT_DON","CANCER_OTH_OSTXT_DON","CONTIN_ALCOHOL_OLD_DON","CONTIN_CIG_DON","CONTIN_IV_DRUG_OLD_DON","CONTIN_COCAINE_DON","CONTIN_OTH_DRUG_DON","DIET_DON","DIURETICS_DON","EXTRACRANIAL_CANCER_DON","HIST_ALCOHOL_OLD_DON","CANCER_SITE_DON","HIST_CIG_DON","DIABDUR_DON","HIST_COCAINE_DON","HIST_HYPERTENS_DON","HIST_IV_DRUG_OLD_DON","INSULIN_DEP_DON","INTRACRANIAL_CANCER_DON","OTHER_HYPERTENS_MED_DON","HIST_CANCER_DON","HIST_INSULIN_DEP_DON","INSULIN_DUR_DON","HIST_DIABETES_DON","HIST_OTH_DRUG_DON","SKIN_CANCER_DON","DIABETES_DON","LIV_DON_TY_OSTXT","HEPARIN_DON","ARGININE_DON","INSULIN_DON","HGT_CM_DON_CALC","WGT_KG_DON_CALC","BMI_DON_CALC","END_STAT_KI","ETHNICITY","ETHCAT","DIAL_DATE","RETXDATE_KI","INIT_DATE_KI","FAILDATE_KI","PUMP_KI","ABO_MAT","AGE","DISTANCE","DIAL_TCR","DIAL_TRR","DIAG_KI","DIAG_OSTXT_KI","COLD_ISCH_KI","WARM_ISCH_TM","GRF_STAT_KI","GRF_FAIL_CAUSE_OSTXT_KI","GRF_FAIL_CAUSE_TY_KI","OTH_GRF_FAIL_CAUSE_OSTXT_KI","REJ_ACUTE_KI","GRF_THROMB_KI","INFECT_KI","SURG_COMPL_KI","UROL_COMPL_KI","RECUR_DISEASE_KI","REJ_CHRONIC_KI","BK_VIRUS_KI","DWFG_KI","MRCREATG","PRVTXDIF_KI","GTIME_KI","GSTATUS_KI","COD_KI","COD_OSTXT_KI","COD2_KI","COD2_OSTXT_KI","COD3_KI","COD3_OSTXT_KI","PX_NON_COMPL_KI","DAYSWAIT_CHRON_KI","TX_PROCEDUR_TY_KI","TRTREJ1Y_KI","TRTREJ6M_KI","MULTIORG","PRI_PAYMENT_TRR_PA","PRI_PAYMENT_CTRY_TRR_PA","SECONDARY_PAY_TRR_PA","ART_RECON","ART_RECON_OSTXT","DUCT_MGMT","DUCT_MGMT_OSTXT","GRF_PLACEM","PRE_AVG_INSULIN_USED_TRR","PA_REVASC","ACUTE_REJ_EPI_PA","BIOPSY_CONFIRMED_PA","PA_PRESERV_TM","VASC_MGMT","VEN_EXT_GRF","PRI_PAYMENT_TCR_PA","PRI_PAYMENT_CTRY_TCR_PA","SECONDARY_PAY_TCR_PA","MALIG_TCR_PA","DIAB","AGE_DIAB","PK_DA1","PK_DA2","PK_DB1","PK_DB2","PK_DDR1","PK_DDR2","ENTERIC_DRAIN","ENTERIC_DRAIN_DT","END_STAT_PA","FAILDATE_PA","GRF_REMOV_DT_PA","DIAG_PA","DIAG_OSTXT_PA","GRF_STAT_PA","GRF_FAIL_CAUSE_OSTXT_PA","GRF_FAIL_CAUSE_TY_PA","OTH_GRF_FAIL_CAUSE_OSTXT_PA","GRF_VASC_THROMB_PA","INFECT_PA","BLEED_PA","ANAST_LK_PA","REJ_ACUTE_PA","REJ_CHRONIC_PA","BIOP_ISLET_PA","PANCREATIT_PA","REJ_HYPER_PA","GRF_REMOV_PA","RETXDATE_PA","PRVTXDIF_PA","GTIME_PA","GSTATUS_PA","MEDICATION_RES_DT_PA","COD_PA","COD_OSTXT_PA","COD2_PA","COD2_OSTXT_PA","COD3_PA","COD3_OSTXT_PA","PX_NON_COMPL_PA","DAYSWAIT_CHRON_PA","INIT_DATE_PA","TX_PROCEDUR_TY_PA","TRTREJ1Y_PA","TRTREJ6M_PA","ORGAN","CMV_IGG","CMV_IGM","EBV_SEROSTATUS","HBV_CORE","HBV_SUR_ANTIGEN","HCV_SEROSTATUS","TX_TYPE","HGT_CM_TCR","WGT_KG_TCR","MED_COND_TCR","MED_COND_TRR","PX_STAT","PX_STAT_DATE","PREV_KI_DATE","FUNC_STAT_TRF","PSTATUS","PTIME","SHARE_TY","LOS","PAYBACK","ECD_DONOR","AGE_GROUP","MALIG","MALIG_TY_OSTXT","MALIG_TY","HGT_CM_CALC","WGT_KG_CALC","BMI_CALC","REJ_BIOPSY","REJCNF_KI","REJTRT_KI","REJCNF_PA","REJTRT_PA","TRR_ID_CODE","ACYCLOVIR","CYTOGAM","GAMIMUNE","GAMMAGARD","GANCICLOVIR","VALGANCYCLOVIR","HBIG","FLUVACCINE","LAMIVUDINE","OTHER","OTH_LIFE_SUP","OTH_LIFE_SUP_OSTXT","INOTROPES","VENTILATOR","VAD_TAH","IABP","PGE","ECMO","TOT_SERUM_ALBUM","EXH_PERIT_ACCESS","EXH_VASC_ACCESS","ADMISSION_DATE","DISCHARGE_DATE","COMPL_ABSC","COMPL_ANASLK","COMPL_PANCREA","OTH_COMPL_OSTXT","SURG_INCIS","OPER_TECH","EDUCATION_DON","KI_CREAT_PREOP","KI_PROC_TY","PRI_PAYMENT_DON","PRI_PAYMENT_CTRY_DON","MEDICARE_DON","MEDICAID_DON","OTH_GOVT_DON","PRIV_INS_DON","HMO_PPO_DON","SELF_DON","DONATION_DON","FREE_DON","RECOV_OUT_US","RECOV_COUNTRY","PROTEIN_URINE","LIPASE","AMYLASE","INOTROP_AGENTS","CARDARREST_NEURO","RESUSCIT_DUR","INOTROP_SUPPORT_DON","TATTOOS","LT_KI_BIOPSY","LT_KI_GLOMERUL","RT_KI_BIOPSY","RT_KI_GLOMERUL","REFERRAL_DATE","RECOVERY_DATE","ADMIT_DATE_DON","DONOR_ID","HBSAB_DON","EBV_IGG_CAD_DON","EBV_IGM_CAD_DON","CDC_RISK_HIV_DON","INO_PROCURE_AGENT_1","INO_PROCURE_AGENT_2","INO_PROCURE_AGENT_3","INO_PROCURE_OSTXT_1","INO_PROCURE_OSTXT_2","INO_PROCURE_OSTXT_3","WL_ORG","COD_WL","COD_OSTXT_WL","NUM_PREV_TX","CREAT_CLEAR","GFR","ON_DIALYSIS","ON_EXPAND_DONOR","ON_IEXPAND_DONOR","A1","A2","B1","B2","DR1","DR2","INIT_CURRENT_PRA","INIT_PEAK_PRA","INIT_STAT","INIT_WGT_KG","INIT_HGT_CM","INIT_CPRA","REM_CD","DAYSWAIT_CHRON","END_STAT","INIT_AGE","ACTIVATE_DATE","CREAT_CLEAR_DATE","DEATH_DATE","DIALYSIS_DATE","END_DATE","GFR_DATE","INIT_DATE","WLHR","WLHL","WLIN","WLKI","WLKP","WLLI","WLLU","WLPA","WLPI","WL_ID_CODE","DGN_OSTXT_TCR","DGN2_OSTXT_TCR","DGN_TCR","DGN2_TCR","DATA_TRANSPLANT","DATA_WAITLIST"],
                                   usecols=("TRR_ID_CODE",
                                            # Type of organ (KI=kidney, KP=kidney+pancreas)
                                            "WL_ORG",
                                            # Patient HLA
                                            "A1","A2","B1","B2","DR1","DR2",
                                            # Patient CPRA
                                            "END_CPRA",
                                            # Did graft fail?
                                            "GSTATUS_KI",   # 0 = false, 1 = true
                                            # Graft failure date
                                            "FAILDATE_KI",
                                            # Graft lifespan
                                            "GTIME_KI",
                                            # Patient age and gender, weight (kg)
                                            "AGE", "GENDER", "WGT_KG_CALC",
                                            # Is patient dead?
                                            "PSTATUS",   #1=dead, 0=alive
                                            # Patient BMI
                                            "BMI_CALC",
                                            # Patient blood type
                                            "ABO",
                                            # Donor ABO blood type
                                            "ABO_DON",  
                                            # Donor-recipient ABO match level
                                            "ABO_MAT",
                                            # Donor age and gender, weight (kg)
                                            "AGE_DON", "GENDER_DON", "WGT_KG_DON_CALC",
                                            # Donor BMI
                                            "BMI_DON_CALC",
                                            # Donor HLA
                                            "DA1","DA2","DB1","DB2","DDR1","DDR2",
                                            # Donor Type (Deceased or Living)
                                            "DON_TY",     # C or L (or F for foreign...?)
                                            # Recipient died with functioning graft
                                            "DWFG_KI",    # 0 = false, 1 = true
                                            # HLA Mismatch levels
                                            "AMIS", "BMIS", "DRMIS", "HLAMIS",
                                            ),
                                     invalid_raise=False,
                                   )
    end = time.clock()
    print "Loaded {0} rows in {1} seconds from kidney transplant data.".format(len(transplant_data), end-start)

    print "Saving to {0}.".format(ftransplant_out)
    np.save(ftransplant_out, transplant_data)
    print "Saved as a binary."




# Loads a bunch of raw .DAT files from the OPTN STAR DVD and dumps them into .npy
# binary files, which can be loaded more quickly later in life
def convert_all_raw_data():

    # Load raw files and convert to binaries; ONLY LOAD .npy AFTER THIS
    if False and not os.path.exists( paths().waitlist_binary() ):
        convert_waitlist_data( paths().waitlist_raw, paths().delim_raw, paths().waitlist_binary )
        
    if not os.path.exists( paths().followup_binary() ):
        convert_followup_data( paths().followup_raw(), paths().delim_raw(), paths().followup_binary() )
        
    if not os.path.exists( paths().transplant_binary() ):
        convert_transplant_data( paths().transplant_raw(), paths().delim_raw(), paths().transplant_binary() )
 
    # Need: LIVING_DONOR_DATA (donor age, donor ABO, ...)
    #       KIDPAN_DATA (recipient ABO, 
    #       KIDNEY_FOLLOWUP (Date of Retransplant, Date of Graft Failure
    

def main():
    convert_all_raw_data()

if __name__ == "__main__":
    main()
