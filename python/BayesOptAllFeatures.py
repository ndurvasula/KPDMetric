from GPyOpt.methods import BayesianOptimization
import numpy as np
import subprocess
import pickle
import os

print "Starting..."

TRAJECTORIES = 100
PAIRS_0 = 250
ALTS_0 = 10
E_P = 10/7
E_A = 1/7

cacheIN = np.empty([0,5])
cacheOUT = np.empty([0,TRAJECTORIES,14])

XL = []
for i in range(6):
    XL.append(np.empty([0,5]))
XL.append(np.empty([0,6]))
XL.append(np.empty([0,5]))
for i in range(5):
    XL.append(np.empty([0,6]))
XL.append(np.empty([0,5]))

YL = []
for i in range(14):
    YL.append(np.empty([0,1]))


MD = []
for i in range(14):
    MD.append(None)

def fCacheEval(x=None):
    print "Solving the function for the given value"
    global cacheIN
    global cacheOUT
    global fi

    if fi < 6 or fi == 13 or fi == 7:
        X = np.empty([1,5])
    else:
        X = np.empty([1,6])
    Y = np.empty([1,1])

    if x != None and fi != 7:
        X[0] = np.append(cacheIN[-1],x)
    else:
        X[0] = cacheIN[-1]
    valAvg = 0
    for t in range(TRAJECTORIES):
        if fi < 6 or fi == 13:
            valAvg += cacheOUT[-1][t][fi]
        elif fi >= 6 and fi < 12:
            valAvg += (1 if x == cacheOUT[-1][t][fi] else 0)
        else:
            valAvg += cacheOUT[T][t][fi]/x
    valAvg /= TRAJECTORIES
    Y[0] = valAvg
    return (X, Y)

def subCacheEval(x=None):
    global cacheIN
    global cacheOUT
    global fi
    print "x="+str(x)
    if fi < 6 or fi == 13 or fi == 7:
        X = np.empty([len(cacheIN),5])
    else:
        X = np.empty([len(cacheIN),6])
    Y = np.empty([len(cacheIN),1])
    for T in range(len(cacheIN)):
        if x != None and fi != 7:
            X[T] = np.append(cacheIN[T],x)
        else:
            X[T] = cacheIN[T]
        valAvg = 0
        for t in range(TRAJECTORIES):
            if fi < 6 or fi == 13:
                valAvg += cacheOUT[T][t][fi]
            elif fi >= 6 and fi < 12:
                valAvg += (1 if x == cacheOUT[T][t][fi] else 0)
            else:
                valAvg += cacheOUT[T][t][fi]/x
        valAvg /= TRAJECTORIES
        Y[T] = valAvg
    return (X, Y)

def cacheEval():
    print "Using cached values from previous calculations"
    global fi
    global XL
    global YL
    global MD
    X_i = XL[fi]
    Y_i = YL[fi]
    md = MD[fi]

    if fi < 6 or fi == 13:
        X_k, Y_k = subCacheEval()
        X_i = np.append(X_i, X_k, axis=0)
        Y_i = np.append(Y_i, Y_k, axis=0)
    elif fi == 7:
        for l in md[1]["domain"]:
            X_k, Y_k = subCacheEval(x=l)
            X_i = np.append(X_i, X_k, axis=0)
            Y_i = np.append(Y_i, Y_k, axis=0)
    elif fi >= 6 and fi < 12:
        for l in md[-1]["domain"]:
            X_k, Y_k = subCacheEval(x=l)
            X_i = np.append(X_i, X_k, axis=0)
            Y_i = np.append(Y_i, Y_k, axis=0)
            
    else:
        for l in range(md[0]["domain"][0],md[0]["domain"][1]):
            X_k, Y_k = subCacheEval(x=l)
            X_i = np.append(X_i, X_k, axis=0)
            Y_i = np.append(Y_i, Y_k, axis=0)
    
    xf = open("X"+str(fi),"wb")
    yf = open("Y"+str(fi),"wb")

    pickle.dump(X_i, xf)
    pickle.dump(Y_i, yf)

    xf.close()
    yf.close()

    XL[fi] = X_i
    YL[fi] = Y_i


def f(Xl):
    global fi
    global cacheIN
    global cacheOUT
    global XL
    global YL
    X_i = XL[fi]
    Y_i = YL[fi]
    Y = np.empty([0,1])
    for XI in range(len(Xl)):
        X = Xl[XI]
        xp = None
    
        if fi < 6 or fi == 13 or fi == 7:
            args = X
        elif fi >= 6 and f < 12:
            args = X[:-1]
            xp = X[-1]
        else:
            args = X[1:]
            xp = X[0]
        
        cacheIN = np.append(cacheIN, [args], axis=0)
        Yn = np.empty([TRAJECTORIES,14])
        for i in range(TRAJECTORIES):
            I = ["java","-jar","Simulation.jar"]
            for j in args:
                I.append(str(j))
            I.append(str(PAIRS_0))
            I.append(str(ALTS_0))
            I.append(str(E_P))
            I.append(str(E_A))
            print "Running the simulator"
            out = subprocess.check_output(I)
            out = out.split(" ")
            print "Finished"
            for j in range(4):
                out[j] = float(out[j])
            for j in range(4,7):
                out[j] = 1 if out[j]=='true' else 0
            if out[7] == "O":
                out[7] = 0
            elif out[7] == "A":
                out[7] = 1
            elif out[7] == "B":
                out[7] = 2
            else:
                out[7] = 3
            for j in range(8,12):
                out[j] = int(float(out[j]))
            out[12] = float(out[12])
            out[13] = float(out[13])
            Yn[i] = out
        cacheOUT= np.append(cacheOUT, [Yn], axis=0)
        ci = open("cacheIN","wb")
        co = open("cacheOUT","wb")
        pickle.dump(cacheIN,ci)
        pickle.dump(cacheOUT,co)
        ci.close()
        co.close()
        
        Xu, Yu = fCacheEval(x=xp)
        
        X_i = np.append(X_i, Xu, axis=0)
        Y_i = np.append(Y_i, Yu, axis=0)
        
        xf = open("X"+str(fi),"wb")
        yf = open("Y"+str(fi),"wb")

        pickle.dump(X_i, xf)
        pickle.dump(Y_i, yf)

        xf.close()
        yf.close()

        XL[fi] = X_i
        YL[fi] = Y_i

        Y = np.append(Y, Yu)
    return Y


         
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
if __name__ == '__main__':
    for fi in range(13,14): #feature index - between 0 and 14
        print "Starting Bayesian Optimization for feature: "+complete_range[fi]
        mixed_domain = complete_domain[1:6]
        initCache = False

        if os.stat("cacheIN").st_size != 0:
            CI = open("cacheIN","rb")
            cacheIN = pickle.load(CI)
            initCache = True
            CI.close()
        if os.stat("cacheOUT").st_size != 0:
            CO = open("cacheOUT","rb")
            cacheOUT = pickle.load(CO)
            CO.close()

        if fi == 6:
            mixed_domain.append(complete_domain[fi])
        elif fi > 7 and fi < 12:
            mixed_domain.append(complete_domain[fi-1])
        elif fi == 12:
            md = [complete_domain[0]]
            for k in mixed_domain:
                md.append(k)
            mixed_domain = md

        MD[fi] = mixed_domain
        if initCache:
            cacheEval()

        if not(np.array_equal(XL[fi],np.empty([0,5])) or np.array_equal(XL[fi],np.empty([0,6]))):
            print "Using computed intial values from cache"
            X_0 = XL[fi]
            Y_0 = YL[fi]
        else:
            print "No available cached values"
            X_0 = None
            Y_0 = None

        myBopt = BayesianOptimization(f=f, domain=mixed_domain, acquisition_type='LCB', X=X_0, Y=Y_0, num_cores=8)
        myBopt.run_optimization(max_iter=100, eps=.1, evaluations_file="E"+str(fi)+".txt", models_file="M"+str(fi)+".txt") #Continue optimization until maximized normalized standard deviation is 0.1


