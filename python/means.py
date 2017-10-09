import pickle
import numpy as np
import matplotlib.pyplot as plt
results = pickle.load(open("Results.bin","rb"))

def n2bt(a):
    if a == 0:
        return "O"
    if a == 1:
        return "A"
    if a == 2:
        return "B"
    if a == 3:
        return "AB"

gmeans = [0,0,0,0]

for BTP in range(4):
    for BTD in range(4):
        means = [0,0,0,0]
        for cpzone in range(4):
            for i in range(5):
                means[cpzone] += abs(results[BTP,BTD,cpzone,i,3])
                gmeans[cpzone] += abs(results[BTP,BTD,cpzone,i,3])
            means[cpzone] /= 5

        cpras = ('0 - .25', '.25 - .5', '.5 - .75', '.75 - 1')
        y_pos = np.arange(len(cpras))
 
        plt.bar(y_pos, means, align='center', alpha=0.5)
        plt.xticks(y_pos, cpras)
        plt.ylabel('Mean Residual')

        plt.title(n2bt(BTD)+"-type Paired Donor, "+n2bt(BTP)+"-type patient")
 
        plt.gcf().savefig("Residual"+n2bt(BTP)+n2bt(BTD)+".png")
        plt.show()

for i in range(4):
    gmeans[i] /= 80

cpras = ('0 - .25', '.25 - .5', '.5 - .75', '.75 - 1')
y_pos = np.arange(len(cpras))
 
plt.bar(y_pos, gmeans, align='center', alpha=0.5)
plt.xticks(y_pos, cpras)
plt.ylabel('Mean Residual')

plt.title("CPRA vs Mean Residual over all pairs")
 
plt.gcf().savefig("GlobalResidual.png")
plt.show()


