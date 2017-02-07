// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed.
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

    @filled
    M=0
    @screenSize
    D=M
    @8192
    D=A
    @screenSize
    M=D            // 32 words per row, 256 rows ==> 32*256 = 8192
(LOOP)
    @i            // Init count variable
    M=0
    @KBD          // Look at keyboard
    D=M
    @CLEAR
    D;JEQ         // Jump to CLEAR if no key down
    @filled       // If we're here, key is held
    D=M
    @FILL
    D;JEQ         // Jump if not already filled

    @LOOP
    0;JMP

    (CLEAR)
        @filled
        D=M
        @LOOP
        D;JEQ
        (CLEARLOOP)
            @i
            D=M
            @screenSize
            D=D-M
            @CLEARFILLED
            D;JEQ

            @i
            D=M
            @SCREEN
            A=A+D
            M=0

            @i
            M=M+1
            @CLEARLOOP
            0;JMP

    (FILL)
        @i
        D=M
        @screenSize
        D=D-M
        @SETFILLED
        D;JEQ

        @i
        D=M
        @SCREEN
        A=A+D
        M=-1

        @i
        M=M+1
        @FILL
        0;JMP

    (CLEARFILLED)
        @filled
        M=0
        @LOOP
        0;JMP

    (SETFILLED)
        @filled
        M=1
        @LOOP
        0;JMP
