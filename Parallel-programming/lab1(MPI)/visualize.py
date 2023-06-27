import numpy as np
import matplotlib.pyplot as plt

N = 2500

x = np.fromfile("X.bin", dtype=np.double)
outX = np.fromfile("result.bin", dtype=np.double)


print(np.linalg.norm(x-outX))
print(np.linalg.norm(x-outX) / np.linalg.norm(x))


