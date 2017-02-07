from bitstring import Bits


def dest_to_bin(mnemonic):
    val = 0b000
    if 'M' in mnemonic:
        val |= 0b001
    if 'D' in mnemonic:
        val |= 0b010
    if 'A' in mnemonic:
        val |= 0b100
    return val

def comp_to_bin(mnemonic):
    val = 0b0000000
    if mnemonic == '0':
        return 0b0101010
    if mnemonic == '1':
        return !val
