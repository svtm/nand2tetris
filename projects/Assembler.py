import sys, Parser, Code

for arg in sys.argv[1:]:
    if (not arg.split('.')[-1] == 'asm') or len(arg.split('.')) > 2:
        print(arg + " is an invalid file")
        sys.exit()
    inFile = open(arg)
    outFile = open(arg.split('.')[0] + ".hack", 'w')
    print("Parsing " + arg)
    for line in f:
        line = line.rstrip()
        print(line)
        line = line.split("//")[0]
    print("--------------------------")
