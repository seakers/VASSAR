import os
import jpype

libs = ''.join([":../"+f for f in os.listdir('../') if f.endswith('.jar')])

# Start the JVM
jpype.startJVM(jpype.getDefaultJVMPath(), '-ea', '-Djava.class.path=./'+libs)

# Get the RBSAEOSS class
rbsaPkg = jpype.JPackage('rbsa').eoss.local
RBSAEOSS = rbsaPkg.RBSAEOSS

rbsaeoss = RBSAEOSS()

orbits = "AB", "C", "ABCDL"
input_arch = jpype.java.util.ArrayList()
for o in orbits:
    input_arch.add(o)

result = rbsaeoss.evaluateArch(input_arch)

print "Science: " + str(result[0]) + " Cost: " + str(result[1])

jpype.shutdownJVM()
