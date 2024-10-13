    package vm;

    public class PCB {
        private int processId;
        private int programCounter;
        private int[] registers;
        private ProcessState state;

        // Enum for process states (e.g., running, ready, blocked)
        public enum ProcessState {
            READY,
            TERMINATED
        }
        // Register printing function
        public void printRegisters() {
            System.out.println("End of Job.");
            System.out.println("Register values:");
            for (int i = 0; i < registers.length; i++) {
                System.out.println("Register " + i + ": " + registers[i]);
            }
        }
        //Creates a new process in the PCB class
        public PCB(int processId, int numRegisters) {
            this.processId = processId;
            this.programCounter = 0;
            this.registers = new int[numRegisters];
            this.state = ProcessState.READY;
        }

        //Sets the value of a register inside of the relevant process.
        public void setRegisterValue(int value, int registerIndex) {
            if (registerIndex >= 0 && registerIndex < registers.length) {
                registers[registerIndex] = value;
            } else {
                throw new IllegalArgumentException("Invalid register index: " + registerIndex);
            }
        }
        
        //Retrieves the value of a register inside of the relevant process
        public int getRegisterValue(int registerIndex) {
            if (registerIndex >= 0 && registerIndex < registers.length) {
                return registers[registerIndex];
            } else {
                throw new IllegalArgumentException("Invalid register index: " + registerIndex);
            }
        }


        // Getters and setters for PCB attributes
        public int getProcessId() {
            return processId;
        }

        public int getProgramCounter() {
            return programCounter;
        }

        public void setProgramCounter(int programCounter) {
            this.programCounter = programCounter;
        }

        public int[] getRegisters() {
            return registers;
        }
        public int getNumRegisters(){
            return registers.length;
        }

        public ProcessState getState() {
            return state;
        }

        public void setState(ProcessState state) {
            this.state = state;
        }
    }
