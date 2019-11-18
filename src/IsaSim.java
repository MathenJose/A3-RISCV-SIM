import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * RISC-V Instruction Set Simulator
 * 
 * A tiny first step to 
 the simulator started. Can execute just a single
 * RISC-V instruction.
 * 
 * @author Martin Schoeberl (martin@jopdesign.com)
 *
 */
public class IsaSim {

	static int pc;
	static int reg[] = new int[32]; // 32 registers
	
	static int progr[];
	
	//conversion of signed to unsigned
	static int getSigned(int x){
		if(x>0){
			return x;
		}
		else{
			x=~x;
			return x=x+1;
		}
	}

	public static void main(String[] args) {

		System.out.println("Hello RISC-V World!");

		pc = 0;
		
		 // The name of the file to open.
        String fileName = "C:\\Users\\mjos0003\\Desktop\\02155 compArch\\ass3\\src\\addneg.bin";

        try {
            // Use this for reading the data.
            byte[] buffer = new byte[1000];

            FileInputStream inputStream = 
                new FileInputStream(fileName);

            // read fills buffer with data and returns
            // the number of bytes read (which of course
            // may be less than the buffer size, but
            // it will never be more).
            int total = 0;
            int nRead = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                total += nRead; // number of bytes read
            }   

            // Always close files.
            inputStream.close();        
            
            progr = new int[total/4];
            System.out.println("Read " + total + " bytes");

            int i = 0;
            String instruction = "";
            int instructionInt;
            
            byte[] word = new byte[4];
            
            while(i < total) {
	            
	            String s2 = String.format("%8s", Integer.toBinaryString(buffer[i] & 0xFF)).replace(' ', '0');
	            //System.out.println(s2); // prints each byte
	            
	            
	            
	            // prints the words in binary
	            instruction = s2 + instruction;
	            if (i%4 == 3) {
	            	System.out.println();
	            	System.out.print("+");
	            	System.out.println(instruction);
	            	
	            	instruction = "";
	            	instructionInt = 0;
	            }
	            
	            // this separates the bytes and then puts them together into a word
	            if(i%4 == 0) {
	            	word[3] = (byte) (buffer[i] & 0xFF);
	            }
	            else if(i%4 == 1) {
	            	word[2] = (byte) (buffer[i] & 0xFF);
	            }
	            else if(i%4 == 2) {
	            	word[1] = (byte) (buffer[i] & 0xFF);
	            }
	            else if(i%4 == 3) {
	            	word[0] = (byte) (buffer[i] & 0xFF);
	            	instructionInt = ((word[0] & 0xFF) << 24) | ((word[1] & 0xFF) << 16) | ((word[2] & 0xFF) << 8) | ((word[3] & 0xFF) << 0);
	            	
	            	/*
	            	System.out.print("%");
	            	//System.out.print(" 0: ");
	            	System.out.print(Integer.toBinaryString(word[0]));
	            	//System.out.print(" 1: ");
	            	System.out.print(Integer.toBinaryString(word[1]));
	            	//System.out.print(" 2: ");
	            	System.out.print(Integer.toBinaryString(word[2]));
	            	//System.out.print(" 3: ");
	            	System.out.println(Integer.toBinaryString(word[3]));
	            	*/
	            	
	            	System.out.print("-");
	            	System.out.println(Integer.toBinaryString(instructionInt)); // prints the word in int (2's complement 32 bits)
	            	progr[pc] = instructionInt;
	            	pc ++;
	            }
	            
	            i++;
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");
        }
        
        pc = 0; // resetting pc to zero for simulating (used for loading progr array before)

		for (;;) {

			int instr = progr[pc];
			int opcode = instr & 0x7f;//leaves first 7 digits
			int rd = (instr >> 7) & 0x01f;// 7bit shift to right and clears everything except for first 5 digits
			int rs1 = (instr >> 15) & 0x01f;
			int rs2 = (instr >> 20) & 0x01f;
			int funct3 = (instr>> 12) & 0x7;
			int funct7 = (instr>> 23);
			int imm_25_31 = (instr>> 25);
			int imm_20_31 = (instr >> 20);
			int imm_12_31 =(instr >> 12);
			
			System.out.print("Opcode: ");
			System.out.println(Integer.toHexString(opcode));
			
			System.out.print("funct7: ");
			System.out.println(Integer.toBinaryString(funct7));
			
			System.out.print("funct3: ");
			System.out.println(Integer.toBinaryString(funct3));
			

			switch (opcode) {
			
			case 0x73: // e-call
				funct3=000;
				break;	
				
			case 0x23: //load and store 0100011
				reg[rd] = reg[rs1] + imm_12_31;
				break;
				
			case 0x13: //instructions with immediate 0010011
				switch(funct3){
				 	case 000:
						//addi
				 		System.out.println("addi");
				 		System.out.println(rd);
				 		System.out.println(rs1);
						reg[rd]=reg[rs1]+imm_20_31;
						break;
					case 001://***************
						//slli-shifr left logical immediate
						rs2 = getSigned(rs2);
						reg[rd]=reg[rs1] << rs2;
						break;	
					case 010:
						//slti-set less than immediate
						if(reg[rs1]<imm_20_31){
						reg[rd] = 1;
						}
						else{
							reg[rd] = 0;
						}
						break;
					case 011:
						//sltiu-set less than immediate unsigned
						//*****************
						imm_20_31 = getSigned(imm_20_31);
						if(reg[rs1]>imm_20_31){
						reg[rd] = 1;
						}
						else{
							reg[rd] = 0;
						}
						break;	
					case 100:
						//xori
						reg[rd]=reg[rs1]^imm_20_31;
						
					case 101://******************
						//srli and srai- shift right logical and arithmetic immediate
						//SHAMPT as rs2
						if(funct7==0b0000000){
					
						reg[rd]=reg[rs1]<<rs2;
						}
						if(funct7==0b0100000){
						reg[rd]=reg[rs1]>>rs2;
						}
						break;
					case 110:
						//ori
						reg[rd]=reg[rs1]|imm_20_31;
							
					case 111:
						//andi
						reg[rd]=reg[rs1]&imm_20_31;
				}
				break;
				
			case 0x33: // 0110011
				switch(funct3){
					case 000://
						//add
						System.out.println("add");
						if(funct7==0b0000000){
							//add
							reg[rd]=reg[rs1]+reg[rs2];
						}
						if(funct7==0b0100000){
						//sub
							reg[rd]=reg[rs1]-reg[rs2];
						}
						break;
					case 001:
						//sll
						//shift left logical or unsigned is same as arithmetic. << is used	
						//no conversion to unsigned number
						reg[rd]=reg[rs1]<<reg[rs2]; // FIX 
						break;	
					case 010:
						//slt-set less than. slt rd, rs1, rs2.
						//rd is 1 if rs1<rs2
						if(reg[rs1]<reg[rs2]){
						reg[rd] = 1;
						}
						else{
							reg[rd] = 0;
						}
						break;
					case 011:
						//sltu
						//*****************
						rs2 = getSigned(rs2);
						if(reg[rs1]<reg[rs2]){
						reg[rd] = 1;
						}
						else{
							reg[rd] = 0;
						}
						break;
					case 100:
						//xor
						reg[rd]=reg[rs1]^reg[rs2];
						break;	
							
					case 101://**************
						//srl and  sra
						//rs2 = getSigned(rs2);
							//used >>> and >> for shift to right for unsigned and signed
						if(funct7 == 0b0000000){
						//srl
						reg[rd]=reg[rs1]>>>reg[rs2];
						}
						//sra
						if(funct7 == 0b0100000){
						
						reg[rd]=reg[rs1]>>reg[rs2];
						}
						break;
					case 110:
						//or
						reg[rd]=reg[rs1]|reg[rs2];
						break;
					case 111:
						//and
						reg[rd]=reg[rs1]&reg[rs2];
						break;
				}
				break;
				
			case 0x43: // 1000011
				switch(funct3){
				 	case 000:
						//SB
						break;
					case 001://***************
						//SH	
						break;	
					case 010:
						//SW
						break;
				}
				break;
			
			
			case 0x37://lui	110111
				System.out.println("lui inst");
				reg[rd] = imm_12_31;	
				break;
				
			default:
				System.out.println("Opcode " + opcode + " not yet implemented");
				break;
		}
			
			
			if(opcode==0x43){ // TO DO
				switch(funct3){
				 	case 000:
						//SB
						break;
					case 001://***************
						//SH	
						break;	
					case 010:
						//SW
						break;
				}
			}
			
				
			// printing registers
			
			System.out.print("Registers: ");
			for (int i = 0; i < reg.length; ++i) {
				System.out.print(reg[i] + " ");
			}
			System.out.println();
			
			++pc; // We count in 4 byte words
			if (pc >= progr.length) {
				break;
			}
		
		}
		System.out.println("Program exit");

	}
}

