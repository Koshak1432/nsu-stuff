import numpy as np
import matplotlib.pyplot as plt

N = 50

x = np.fromfile("vecX.bin", dtype=np.float32)
b = np.fromfile("vecB.bin", dtype=np.float32)
outX = np.fromfile("outX.bin", dtype=np.float32)
a = np.fromfile("matA.bin", dtype=np.float32).reshape((N*N, N*N))

y = a.dot(x)

print(np.linalg.norm(x-outX) / np.linalg.norm(x))

fig, (ax1, ax2) = plt.subplots(ncols=2)

ax1.imshow(outX.reshape((N, N)))
ax1.set_title('Vector X')

ax2.imshow(-y.reshape((N, N)))
ax2.set_title('Vector B')

plt.show()

