from files import *
import converters
import numpy as np

neededData = ["A1","A2","B1","B2","DR1","DR2","AGE", "GENDER", "WGT_KG_CALC","ABO","ABO_DON","AGE_DON", "GENDER_DON", "WGT_KG_DON_CALC","BMI_DON_CALC","DA1","DA2","DB1","DB2","DDR1","DDR2"]
print "Acquiring data..."
#convert_transplant_data( paths().transplant_raw(), paths().delim_raw(), paths().transplant_binary() )
#Living kidney donations
trans = [row for row in transplant_data() if 
             row["WL_ORG"] == "KI" and
             row["DON_TY"] == "L"
             ]
for feature in neededData:
    print "Writing distribution data for feature: "+feature
    O = open("distributions/"+feature+".txt", "w")
    s = ""
    for i in range(len(trans)):
        if str(trans[i][feature]) != "nan":
            s += str(trans[i][feature]) + "\n"
    O.write(s)
    O.close()