from enum import Enum, auto

class Parser:
    def __init__(self, path):
        self.f = open(path)

    def 

class CommandType(Enum):
    A_COMMAND = auto()
    C_COMMAND = auto()
    L_COMMAND = auto()

def get_command_type(line):
    if line[0] == "@":
        return CommandType.A_COMMAND
    elif line[0] == "(":
        return CommandType.L_COMMAND
    else:
        return CommandType.C_COMMAND

def get_symbol(line, ctype):
    if ctype == CommandType.A_COMMAND:
        return line[1:]
    else:
        return line.lstrip('(').rstrip(')')

def get_dest(line):
    if '=' in line:
        return line.split('=')[0]
    else:
        return None

def get_comp(line):
    if '=' in line:
        line = line.split('=')[1]
    if ';' in line:
        line = line.split(';')[0]
    return line

def get_jump(line):
    if ';' in line:
        return line.split(';')[1]
    return None
