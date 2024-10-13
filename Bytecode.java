package vm;
//Class that allows for word instructions to be put inside of the int memory array.
public class Bytecode {
    public static final int LOAD = 1;
    public static final int ADD = 3;
    public static final int SUBTRACT = 2;
    public static final int HALT = 4;
    public static final int STORE = 5;
    public static final int CLEARREG=6;
    public static final int ADDREG=7;
    public static final int SUBREG=8;
}