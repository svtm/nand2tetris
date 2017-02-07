// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

    @R2     // Load R2, this will hold our result
    M=0     // Initialize to 0
    @R1     // Load R1
    D=M     // Put R1 in D
    @i      // Load variable i
    M=D     // Set i to value of D, that is value of R1
(LOOP)
    @i      // Load i
    D=M     // Put i into D-register
    @END    // Load END
    D;JEQ   // Jump to END if D==0, that is if i is 0

    @R0     // Load R0
    D=M     // Put R0 into D-register
    @R2     // Load R2
    M=D+M   // R2 = R2 + R0

    @i
    M=M-1   // Subtract one from i
    @LOOP
    0;JMP   // Restart loop
(END)
    @END
    0;JMP
