        package vm;
        import java.util.List;
        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Arrays;

        //Virtual machine class
        public class VirtualMachine {
            private int[] memory;
            private int memoryLength;
            private int timeLimit;
            private int[] storage;
            //private int[] accumulator;
            private List<PCB> processes; //List of currently loaded processes
            private int currentProcessIndex; // Index of the currently running process

            //Constructor for virtual machine class
            public VirtualMachine(int memorySize,int tL) {
                timeLimit=tL;
                memory = new int[memorySize];
                storage = new int[memorySize];
                processes = new ArrayList<>();
                currentProcessIndex = -1;
                memoryLength=0;
            }

            //noarg constructor
            public VirtualMachine() {
                timeLimit=5;
                memory = new int[1024];
                storage = new int[1024];
                processes = new ArrayList<>();
                currentProcessIndex = -1;
                memoryLength=0;

            }
            //Loads all programs in a folder in numerical folder
            public void loadProgramsFromFolder() {
                String folderLoc=(System.getProperty("user.dir")+File.separator+"programs");
                //System.out.println(folderLoc);
                File folder = new File(folderLoc);
                File[] files = folder.listFiles();
                //System.out.println("files: "+files==null);

                if (files != null) {
                    // Sort files based on their numerical order
                    Arrays.sort(files, (file1, file2) -> {
                        int number1 = extractNumber(file1.getName());
                        int number2 = extractNumber(file2.getName());
                        return Integer.compare(number1, number2);
                    });

                    // Load programs from sorted files
                    int processCounter=0;
                    for (File file : files) {
                        //System.out.println(file);
                        if (file.isFile()) {
                            try {
                                loadProgramFromFile(file.getAbsolutePath(),processCounter);
                                processCounter++;
                            } catch (Exception IOException) {
                                System.out.println("IOException");
                            }
                            
                        }
                    }
                }
            }
            //Extracts number from loaded file to sort numerically.
            private int extractNumber(String fileName) {
                String name = fileName.replaceFirst("[^\\d].+", ""); // Extract numeric part from file name
                return name.isEmpty() ? 0 : Integer.parseInt(name);
            }

            //Helper function that loads the code contained in individual files.
            private void loadProgramFromFile(String filePath, int processId) {
                System.out.println("Loading program from file: " + filePath);
                createProcess(processId, 2);
                List<Integer> bytecode = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        //Variable initialization
                        int operand=0;
                        int regIndex=0;
                        //Breaks lines up into tokens with spaces as the separating character
                        String[] tokens = line.split(" ");
                        int opcode = getOpcode(tokens[0]);
                        //System.out.println("Opcode: "+opcode);
                        if (opcode!=Bytecode.HALT && opcode!=Bytecode.CLEARREG){
                            //System.out.println("We entered if");
                            operand = Integer.parseInt(tokens[1]);
                            regIndex = Integer.parseInt(tokens[2]);
                        } else if(opcode==Bytecode.CLEARREG){
                            //System.out.println("We entered else");
                            operand=Integer.parseInt(tokens[1]);
                        }
                        bytecode.add(opcode);
                        bytecode.add(operand);
                        //System.out.println("operand: "+operand);
                        bytecode.add(regIndex);
                        //System.out.println("regIndex: "+regIndex);
                    }
                    int[] program = new int[bytecode.size()];
                    for (int i = 0; i < bytecode.size(); i++) {
                        program[i] = bytecode.get(i);
                    }
                    
                    // Load the program into memory
                    loadProgram(program, processId);
        
                } catch (FileNotFoundException e) {
                    System.out.println("File not found: " + filePath);
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Error loading program from file: " + filePath);
                    e.printStackTrace();
                }
            }
            
            //Retrieves opcodes in file.
            private int getOpcode(String opcode) {
                switch (opcode) {
                    case "STORE":
                        return Bytecode.STORE;
                    case "CLEARREG":
                        return Bytecode.CLEARREG;
                    case "ADDREG":
                        return Bytecode.ADDREG;
                    case "SUBREG":
                        return Bytecode.SUBREG;
                    case "LOAD":
                        return Bytecode.LOAD;
                    case "ADD":
                        return Bytecode.ADD;
                    case "SUBTRACT":
                        return Bytecode.SUBTRACT;
                    case "HALT":
                        return Bytecode.HALT;
                    default:
                        throw new IllegalArgumentException("Unknown opcode: " + opcode);
                }
            }

            //method for creating processes in the PCB block. Can be done programatically in main.
            public void createProcess(int processId, int numRegisters) {
                    PCB process = new PCB(processId, numRegisters);
                    //Zero Filling registers to work w/ operations.
                    for (int i = 0; i < numRegisters; i++) {
                        process.setRegisterValue(0, i);
                    }
                    process.setState(PCB.ProcessState.READY); // Set the process state to READY
                    processes.add(process);
                }
            //method for loading a program's instructions into PCB block. Can be done programatically in main.
            public void loadProgram(int[] bytecode, int processId) {
                if (processId >= 0 && processId < processes.size()) {
                    PCB process = processes.get(processId);
                    int startIndex = memoryLength;
                    for (int i = 0; i < bytecode.length; i++) {
                        memory[memoryLength] = bytecode[i];
                        memoryLength+=1;
                    }
                    /*
                    for (int i=0; i<memoryLength;i++){
                            System.out.print(memory[i]);
                    

                    }*/
                    //Keeps track of what index in the memory array housing instructions was last used inbetween program loads.
                    process.setProgramCounter(startIndex);
                    /* Test Prints
                    System.out.println();
                    System.out.println("Memory: "+memory[2]);
                    System.out.println("Bytecode instructions for process #" + processId + ":");
                    for (int instruction : bytecode) {
                        System.out.print(instruction + " ");
                    }
                    
                    System.out.println();
                    */
                } else {
                    throw new IllegalArgumentException("Invalid process ID: " + processId);
                }
            }
            
            //Method for running all programs loaded into memory.
            public void runProcesses() {
                if (processes.isEmpty()) {
                    System.out.println("No processes to run.");
                    return;
                }
            
                currentProcessIndex = 0;
                int instructionsExecuted = 0;
                while (!allProcessesTerminated()) { //While loop, runs until all processes are marked as terminated

                    //System.out.println("processind "+currentProcessIndex);
                    PCB currentProcess = processes.get(currentProcessIndex);
                    //System.out.println("Current process id: "+currentProcess.getProcessId());
                    //System.out.println(currentProcess.getState());
                    // Only run further code if the process is in the "READY" state
                    //System.out.println();

                    //Checks if the state is READY, if not the process is skipped.
                    if (currentProcess.getState() == PCB.ProcessState.READY) {
                        
                        int programCounter = currentProcess.getProgramCounter();
                        int opcode = memory[programCounter];
                        int operand = memory[programCounter + 1];
                        int registerIndex = memory[programCounter + 2];
                            
                            // Switch for opcode to ensure correct operation is done
                            switch (opcode) {
                                
                                case Bytecode.LOAD:
                                    loadValue(currentProcess, operand, registerIndex);
                                    //System.out.println("Performing LOAD");
                                    break;

                                case Bytecode.ADD:
                                    addValue(currentProcess, operand, registerIndex);
                                    //System.out.println("Performing ADD");
                                    break;

                                case Bytecode.SUBTRACT:
                                    subtractValue(currentProcess, operand, registerIndex);
                                    //System.out.println("Performing SUBTRACT");
                                    
                                    break;
                                case Bytecode.STORE:
                                    storeValue(currentProcess, operand, registerIndex);
                                    break;
                                case Bytecode.CLEARREG:
                                    clearRegister(currentProcess, registerIndex);
                                    //System.out.println("Clearing register #" + registerIndex + " for process #" + currentProcess.getProcessId());  
                                    break;

                                case Bytecode.ADDREG:
                                    addRegisters(currentProcess, operand, registerIndex);
                                    //System.out.println("Performing ADD_REGISTERS");
                                    break;
                                case Bytecode.SUBREG:
                                    subtractRegisters(currentProcess, operand, registerIndex);
                                    //System.out.println("Performing SUBTRACT_REGISTERS");
                                    break;

                                case Bytecode.HALT:
                                    System.out.println("Terminated process id: "+currentProcess.getProcessId());
                                    //prints register values
                                    currentProcess.printRegisters();
                                    //sets state to terminated so it's skipped over.
                                    currentProcess.setState(PCB.ProcessState.TERMINATED);
                                    break;

                                default:
                                    throw new IllegalArgumentException("Unknown opcode: " + opcode);
                            }
                            instructionsExecuted++;
            
                            // Check if the quantum time limit is reached
                            if (instructionsExecuted >= timeLimit) {
                                // Reset the instructions executed counter
                                instructionsExecuted = 0;
            
                                // Switch to the next process in a circular manner
                                currentProcessIndex=(currentProcessIndex+1)%processes.size();
                                
                            }
            
                            // Update the program counter to move onto the next instruction for said process.
                            currentProcess.setProgramCounter(programCounter + 3);
                        }

                        currentProcessIndex=(currentProcessIndex+1)%processes.size();
                    }
                    

                }
            //Checks if all processes have been terminated yet.
            private boolean allProcessesTerminated() {
                for (PCB process : processes) {
                    if (process.getState() != PCB.ProcessState.TERMINATED) {
                        return false;
                    }
                }
                return true;
            }
            //Prints every non-0 item in storage.
            public void printStorage(){
                System.out.println();
                System.out.println("Stored Values: ");
                for (int i=0;i<storage.length; i++){
                    if (storage[i]!=0){
                        System.out.print("Index#"+i+":"+storage[i]+" ");
                    }
                }
                System.out.println();
            }
            // LOAD instruction logic: Load value from memory and store it in specified index
            private void loadValue(PCB process, int registerIndex, int address) {
                if (address >= 0 && address < storage.length) {
                    int value = storage[address];
                    /*System.out.println("Loading value " + value + " from storage address #" + address +
                            " into register #" + registerIndex + " for process #" + process.getProcessId());*/
                    process.setRegisterValue(value, registerIndex);
                } else {
                    throw new IllegalArgumentException("Invalid storage address: " + address);
                }
            }
            //ADD instruction logic: add value from memory and store it in specified index.
            private void addValue(PCB process, int value, int registerIndex) {
                //System.out.println("Modifying register #" + registerIndex + " for process #" + process.getProcessId());
                int currentValue = process.getRegisterValue(registerIndex);
                process.setRegisterValue(currentValue + value, registerIndex);
            }
            //Subtract instruction logic
            private void subtractValue(PCB process, int value, int registerIndex) {
                //System.out.println("Modifying register #" + registerIndex + " for process #" + process.getProcessId());
                int currentValue = process.getRegisterValue(registerIndex);
                process.setRegisterValue(currentValue - value, registerIndex);
            }
            //storeValuye logic, stores value in specified area in storage.
            private void storeValue(PCB process, int registerIndex, int address) {
                int value = process.getRegisterValue(registerIndex);
                /*System.out.println("Storing value " + value + " from index #" + registerIndex +
                            " into address " + address + " for process #" + process.getProcessId());*/
                storage[address] = value;
            }
            //Clears specified register index, 0 filling it.
            private void clearRegister(PCB process, int registerIndex) {
                process.setRegisterValue(0, registerIndex);
            }
            //Adds registers together, stores in first input register
            private void addRegisters(PCB process, int regIndex1, int regIndex2) {
                int value1 = process.getRegisterValue(regIndex1);
                int value2 = process.getRegisterValue(regIndex2);
                process.setRegisterValue(value1 + value2, regIndex1);
            }
            //Subtracts second input register from first input register, stores in first input register
            private void subtractRegisters(PCB process, int regIndex1, int regIndex2) {
                int value1 = process.getRegisterValue(regIndex1);
                int value2 = process.getRegisterValue(regIndex2);
                process.setRegisterValue(value1 - value2, regIndex1);
            }
        }
