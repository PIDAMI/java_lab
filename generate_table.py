#!/usr/bin/python
import numpy as np



f = open("substitution_table.txt","w")
sz = 256
bytes = [i for i in range(-sz//2,sz//2)]
res = []
rng = np.random.default_rng()
for i in range(2):
    res.append(rng.permutation(bytes))

for i in range(sz):
    f.write(f"{res[0][i]}={res[1][i]}\n")
f.close()