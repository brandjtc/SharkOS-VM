package vm;
public class Main {
    public static void main(String[] args) {
        VirtualMachine vm = new VirtualMachine(1024,100); // Specify memory size
        /* Runs processes programattically 
        vm.createProcess(0, 2);
        int[] program1 = { Bytecode.LOAD, 69, 0, Bytecode.SUBTRACT, 10, 0, Bytecode.ADD, 30, 0, Bytecode.HALT,0,0};
        vm.loadProgram(program1, 0);

        vm.createProcess(1, 2);
        int[] program2 = { Bytecode.LOAD, 42, 1, Bytecode.SUBTRACT, 10, 1, Bytecode.ADD, 30, 1, Bytecode.HALT,0,0 };
        vm.loadProgram(program2, 1);
        
        vm.createProcess(2, 2);
        int[] program3 = { Bytecode.LOAD, 0, 0, Bytecode.SUBTRACT, 10, 1, Bytecode.ADD, 30, 1, Bytecode.HALT,0,0 };
        vm.loadProgram(program3, 2);
        */
        vm.loadProgramsFromFolder();

        vm.runProcesses();

        vm.printStorage();
    }
}