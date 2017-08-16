import time
import numpy as np

# Converts a string to a date
# E.g., "11/19/12" -> November 19, 2012
def strToDate(s):
    time.strptime(s, "%m/%d/%y")


def aboStrToint(abo_str):
    # possible strings: ['A' 'A1' 'A1B' 'A2' 'A2B' 'AB' 'B' 'O' 'UNK']
    if abo_str == 'O':
        return 0
    elif abo_str in ['A', 'A1', 'A2']:
        return 1
    elif abo_str == 'B':
        return 2
    elif abo_str in ['AB', 'A1B', 'A2B']:
        return 3
    else:
        return 4

def aboIntToStr(abo_int):
    return ['O', 'A', 'B', 'AB', 'UNK'][abo_int]
