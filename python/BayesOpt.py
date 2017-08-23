from GPyOpt.methods import BayesianOptimization
import numpy as np
import subprocess
import pickle
import os

print "Starting..."

TRAJECTORIES = 100
PAIRS_0 = 500
ALTS_0 = 10
E_P = 15
E_A = 1

cacheIN = np.empty([0,6])
cacheOUT = np.empty([0,TRAJECTORIES,14])

XL = []
for i in range(6):
    XL.append(np.empty([0,6]))
for i in range(4):
    XL.append(np.empty([0,7]))
for i in range(2):
    XL.append(np.empty([0,6]))

YL = []
for i in range(6):
    YL.append(np.empty([0,1]))


MD = []

def subCacheEval(x=False):
    global cacheIN
    global cacheOUT
    global fi

    if fi < 6 or fi == 11:
        X = np.empty([len(cacheIN),6])
    else:
        X = np.empty([len(cacheIN),7])
    Y = np.empty([len(cacheIN),1])
    for T in range(len(cacheIN)):
        if x:
            X[T] = np.append(cacheIN[T],x)
        else:
            X[T] = cacheIN[T]
        valAvg = 0
        for t in range(TRAJECTORIES):
            if fi < 6 or fi == 11:
                valAvg += cacheOUT[T][t][fi]
            elif fi >= 6 and fi < 10:
                valAvg += (1 if x == cacheOUT[T][t][fi] else 0)
            else:
                valAvg += cacheOUT[T][t][fi]/x
        valAvg /= TRAJECTORIES
        Y[T] = valAvg
    return (X, Y)

def cacheEval():
    global fi
    global XL
    global YL
    global MD
    X_i = XL[fi]
    Y_i = YL[fi]
    md = MD[fi]

    if fi < 6 or fi == 11:
        X_k, Y_k = subCacheEval()

    elif fi >= 6 and fi <10:
        for l in md[fi]["domain"]:
            X_k, Y_k = subCacheEval(x=l)
            
    else:
        for l in range(md[0]["domain"][0],md[0]["domain"][1]):
            X_k, Y_k = subCacheEval(x=l)


    X_i = np.append(X_i, X_k)
    Y_i = np,append(Y_i, Y_k)
    


def f(X):
    global fi
    for i in range(TRAJECTORIES):
        args = []
        x = False
        if fi < 6 or fi == 11:
            args = X
        elif fi >= 6 and f < 10:
            args = X[:-1]
            x = X[-1]
        else:
            args = X[1:]
            x = X[0]




    

complete_domain =[{'name': 'patientWeight', 'type': 'continuous', 'domain': (0,500)}, #0
                  {'name': 'PatientCPRA', 'type': 'continuous', 'domain': (0,1)}, #1
                  {'name': 'BloodTypePatient', 'type': 'discrete', 'domain': (0,1,2,3)}, #2
                  {'name': 'BloodTypeDonor', 'type': 'discrete', 'domain': (0,1,2,3)}, #3
                  {'name': 'isWifePatient', 'type': 'discrete', 'domain': (0,1)}, #4
                  {'name': 'isCompatible', 'type': 'discrete', 'domain': (0,1)}, #5
                  {'name': 'isPatientMale', 'type': 'discrete', 'domain': (0,1)}, #6
                  {'name': 'patientHLAB1', 'type': 'discrete', 'domain': (5,7,8,12,13,14,15,16,17,18,21,22,27,35,37,38,39,40,41,42,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,67,70,71,72,73,75,76,77,78,81,82,703,804,1304,2708,3901,3902,3905,4005,5102,5103,7801,8201)}, #7
                  {'name': 'patientHLAB2', 'type': 'discrete', 'domain': (5,7,8,12,13,14,15,16,17,18,21,22,27,35,37,38,39,40,41,42,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,67,70,71,72,73,75,76,77,78,81,82,703,804,1304,2708,3901,3902,3905,4005,5102,5103,7801,8201)}, #8
                  {'name': 'patientHLADR1', 'type': 'discrete', 'domain': (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,103,1403,1404)}, #9
                  {'name': 'patientHLADR2', 'type': 'discrete', 'domain': (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,103,1403,1404)}] #10

complete_range = ["Donor Age","Donor eGFR","Donor BMI","Donor systolic BP","Is the donor African American?","Is the donor a cigarette user?","Are both donor and patient male?","Is the donor ABO compatible with the patient?","HLAB1 mismatch?","HLAB2 mismatch?","HLADR1 mismatch?","HLADR2 mismatch?","Donor to patient weight ratio","Match time"]

for fi in range(12): #feature index - between 0 and 11
    print "Starting Bayesian Optimization for feature: "+complete_range[fi]
    mixed_domain = complete_domain[1:6]
    X_i = None
    X_0 = None
    Y_0 = None
    if os.stat("cacheIN.txt").st_size != 0:
        CI = open("cacheIN.txt","rb")
        cacheIN = pickle.load(CI)
        CI.close()
    if os.stat("cacheOUT.txt").st_size != 0:
        CO = open("cacheOUT.txt","rb")
        cacheOUT = pickle.load(CO)
        CO.close()

    #All features are discrete, so samples can be reused
    if fi >= 6 and fi < 10:
        mixed_domain.append(complete_domain[fi+6])
        X_i = np.empty([0,7])
        X_0 = np.empty([len(cacheIN),7])
        Y_0 = np.empty([len(cacheIN),0])
        for i in range(len(cacheIN)):
            for j in mixed_domain[-1]["domain"]:
                x = np.append(cacheIN[i], j)
                y = np.array([1 if cacheOUT[i][fi] == j else 0])

                X_0[i] = x
                Y_0[y] = y

    elif fi == 10:
        X_i = np.empty([0,7])
        X_0 = np.empty([len(cacheIN),7])
        Y_0 = np.empty([len(cacheIN),0])
        md = complete_domain[0]
        for k in mixed_domain:
            md.append(k)
        for i in range(len(cacheIN)):
            for j in range(md[0]["domain"][0],md[0]["domain"][1]): #Add every integer number to the cross section
                x = np.append(cacheIN[i], j)
                y = np.array([cacheOUT[i][fi]/j])

                X_0[i] = x
                Y_0[i] = y

    else:
        X_i = np.empty([0,6])
        X_0 = np.empty([len(cacheIN),6])
        Y_0 = np.empty([len(cacheIN),0])
        for i in range(len(cacheIN)):
            x = cacheIN[i]
            y = cacheOUT[i][fi]

            X_0[i] = x
            Y_0[i] = y

    X_i = np.append(X_i, X_0, axis=0)
    fxi = open("Feature_"+str(fi)+"_inputs.txt","wb")
    pickle.dump(X_i)
    fxi.close()
    myBopt = BayesianOptimization(f=f, domain=mixed_domain, acquisition_type='LCB', X=X_0, Y=Y_0, num_cores=8)
    myBopt.run_optimization(max_iter=100, eps=.1) #Continue optimization until maximized normalized standard deviation is 0.1
    myBopt.plot_acquisition()

