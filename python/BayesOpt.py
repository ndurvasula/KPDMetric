from GPyOpt.methods import BayesianOptimization
import numpy as np
import subprocess
import pickle
import sklearn

print "Starting..."
cacheIN = np.empty([0,20])
cacheOUT = np.empty([0, 12])
TRAJECTORIES = 1
PAIRS_0 = 500
ALTS_0 = 15
E_P = 15
E_A = 1

fi = 0 #feature index - between 0 and 11

def f(X):
    global cacheIN
    global cacheOUT
    global fi

    print "Running the function"
    Y = np.empty([0,1])
    for i in X:
        flag = True
        #Have we already computed this result?
        for j in range(len(cacheIN)):
            if np.array_equal(cacheIN[j],i):
                print "Using cached result"
                Y = np.append(Y,[np.array([cacheOUT[j][fi]])],axis=0)
                flag = False
                break
        if flag:
            #We need to compute the value
            I = ['java','-jar','Simulation.jar']
            for j in i:
                I.append(str(j))
            I.append(str(TRAJECTORIES))
            I.append(str(PAIRS_0))
            I.append(str(ALTS_0))
            I.append(str(E_P))
            I.append(str(E_A))
            print "Running the simulator"
            out = [float(x) for x in subprocess.check_output(I).split(" ")]
            print "Caching new result"
            #Change output to respective types
            #Cache result

            cacheIN = np.append(cacheIN,[i],axis=0)

            cacheOUT = np.append(cacheOUT,[np.array(out)],axis=0)

            pickle.dump(cacheIN,"cacheIN.txt")
            pickle.dump(cacheOUT, "cacheOUT.txt")

            Y = np.append(Y,[np.array([out[fi]])],axis=0)

    return Y

mixed_domain =[{'name': 'BloodTypePatient', 'type': 'discrete', 'domain': (0,1,2,3)}, #0
               {'name': 'BloodTypeDonor', 'type': 'discrete', 'domain': (0,1,2,3)}, #1
               {'name': 'isWifePatient', 'type': 'discrete', 'domain': (0,1)}, #2
               {'name': 'PatientCPRA', 'type': 'continuous', 'domain': (0,1)}, #3
               {'name': 'isCompatible', 'type': 'discrete', 'domain': (0,1)}, #4
               {'name': 'donorGFR', 'type': 'continuous', 'domain': (0,10)}, #5 eGFR per 10 units
               {'name': 'donorBMI', 'type': 'continuous', 'domain': (0,50)}, #6
               {'name': 'patientWeight', 'type': 'continuous', 'domain': (0,500)}, #7
               {'name': 'donorWeight', 'type': 'continuous', 'domain': (0,500)}, #8
               {'name': 'isAfricanAmerican', 'type': 'discrete', 'domain': (0,1)}, #9
               {'name': 'isCigaretteUser', 'type': 'discrete', 'domain': (0,1)}, #10
               {'name': 'isPatientMale', 'type': 'discrete', 'domain': (0,1)}, #11
               {'name': 'isDonorMale', 'type': 'discrete', 'domain': (0,1)}, #12
               {'name': 'donorAge', 'type': 'continuous', 'domain': (0,80)}, #13
               {'name': 'donorSBP', 'type': 'continuous', 'domain': (110,160)}, #14
               {'name': 'patientHLAB1', 'type': 'discrete', 'domain': tuple(range(65))}, #15
               {'name': 'patientHLAB2', 'type': 'discrete', 'domain': tuple(range(65))}, #16
               {'name': 'patientHLADR1', 'type': 'discrete', 'domain': tuple(range(21))}, #17
               {'name': 'patientHLADR2', 'type': 'discrete', 'domain': tuple(range(21))}] #18
print "Prepping the GP"
myBopt = BayesianOptimization(f=f, domain=mixed_domain, acquisition_type='LCB', num_cores=4)
print "Running the optimization"
myBopt.run_optimization(max_iter=2)
myBopt.plot_acquisition()

