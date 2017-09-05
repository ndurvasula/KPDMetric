print "Importing numpy"
import numpy as np
print "Importing pickle"
import pickle
print "Importing GPy"
import GPy
print "Importing random"
import random
print "Importing subprocess"
import subprocess

TRAJECTORIES = 32
PAIRS_0 = 250
ALTS_0 = 10
E_P = 10
E_A = 1

def f(Xl):
    global X
    global Y
    R = np.empty([0,1])
    for XI in range(len(Xl)):
        arg = Xl[XI]
        X = np.append(X, [arg], axis=0)
        MT = 0
        for i in range(TRAJECTORIES):
            I = ["java","-jar","Simulation.jar"]
            I.append(str(arg[0]))
            for j in jargs:
                I.append(str(j))
            I.append(str(PAIRS_0))
            I.append(str(ALTS_0))
            I.append(str(E_P))
            I.append(str(E_A))
            print "Running the simulator"
            out = subprocess.check_output(I)
            out = out.split(" ")
            print "Finished"
            MT += float(out[13])
        MT /= TRAJECTORIES
        Y = np.append(Y, [[MT]], axis=0)
        R = np.append(R, [[MT]], axis=0)
    print "Returned"
    return R

print "Starting..."
GP = []
for a in range(4):
    temp = []
    for b in range(4):
        X = pickle.load(open("X"+str(a)+str(b)+"00","rb"))
        Y = pickle.load(open("Y"+str(a)+str(b)+"00","rb"))
        M = [float(x) for x in open("M"+str(a)+str(b)+"00.txt").readlines()[-1].split("\t")[1:]]
        print "Model parameters for",a,b,"are",M
        kern = GPy.kern.RBF(1,variance=M[0],lengthscale=M[1])
        m = GPy.models.GPRegression(X,Y,kern,noise_var=M[2])
        temp.append(m)
        print "Added model to superstructure"
    GP.append(temp)
print "Completed superstructure"
res = np.empty([4,4,4,5,4])
for btp in range(4):
    for btd in range(4):
        jargs = [btp,btd,0,0]
        if jargs == [1,3,0,0]:
            TRAJECTORIES = 64
        if jargs == [2,1,0,0]:
            TRAJECTORIES = 128
        elif jargs == [3,3,0,0]:
            TRAJECTORIES = 128
        else:
            TRAJECTORIES = 32
        for t in range(4):
            for i in range(5):
                tcpra = random.random() * .25 + .25*t
                print "Trial number",i+1,"for args",jargs,"and CPRA zone",t+1
                print TRAJECTORIES,"trajectories"
                print "Test CPRA:",tcpra
                m = GP[btp][btd]
                x = np.empty([1,1])
                x[0,0] = tcpra
                pred = m.predict(x)[0][0][0]
                print "Predicted:", pred
                true = f(x)[0][0]
                print "Test CPRA:",tcpra
                print "Predicted:", pred
                print "True value:", true
                print "Residual:",pred-true
                res[btp,btd,t,i] = np.array([tcpra,pred,true,pred-true])
                pickle.dump(res,open("Results.bin","wb"))
                print "Updated results"

            


