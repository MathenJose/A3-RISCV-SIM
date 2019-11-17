import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * RISC-V Instruction Set Simulator
 * 
 * A tiny first step to get the simulator started. Can execute just a single
 * RISC-V instruction.
 * 
 * @author Martin Schoeberl (martin@jopdesign.com)
 *
 */
public class IsaSim {

	static int pc;
	static int reg[] = new int[32]; // 32 registers

	// Here the first program hard coded as an array
	/* from template
	static int progr[] = {
			// As minimal RISC-V assembler example
			0x00200093, // addi x1 x0 2
			0x00300113, // addi x2 x0 3
			0x002081b3, // add x3 x1 x2
	};
	*/
	
	static int progr[] = new int[250];

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
                total += nRead;
            }   

            // Always close files.
            inputStream.close();        
            
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
	            	System.out.println(instruction);
	            	instruction = "";
	            	instructionInt = 0;
	            }
	            
	            // this separates the bytes and then puts them together into a word
	            if(i%4 == 0) {
	            	word[3] = buffer[i];
	            }
	            else if(i%4 == 1) {
	            	word[2] = buffer[i];
	            }
	            else if(i%4 == 2) {
	            	word[1] = buffer[i];
	            }
	            else if(i%4 == 3) {
	            	word[0] = buffer[i];
	            	instructionInt = (word[0] << 24) | (word[1] << 16) | (word[2] << 8) | (word[3] << 0);
	            	//System.out.println(Integer.toHexString(instructionInt)); // prints the word in int (2's complement 32 bits)
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
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        
        pc = 0; // resetting pc to zero for simulating (used for loading progr array before)
		
		//conversion of signed to unsigned
		public static long getSigned(int x){
		if(x>0){
			return x;
			}
			else{
				x=~x;
				return x=x+1;
		
				}
		}

		for (;;) {

			int instr = progr[pc];
			int opcode = instr & 0x7f;//leaves first 7 digits
			int rd = (instr >> 7) & 0x01f;// 7bit shift to right and clears everything except for first 5 digits
			int rs1 = (instr >> 15) & 0x01f;
			int rs2 = (instr >> 20) & 0x05f;
			int funct3 = (instr>> 13) & 0x03f;
			int funct7 = (instr>> 23) & 0x07f;
			int imm5 = (instr>> 23) & 0x07f;
			int imm = (instr >> 20);

			switch (opcode) {

			case 0x13: //
				reg[rd] = reg[rs1] + imm;
				break;
			case 0x23: //
				reg[rd] = reg[rs1] + imm;
				break;
			case 0x63:
				reg[rd] = reg[rs1]+reg[rs2];
				break;
				
			default:
				System.out.println("Opcode " + opcode + " not yet implemented");
				break;
			}
			
			
			//when opcode=0x13
			if(opcode==0x13){
				switch(funct3){
						
					//sehwa	
				}
			}
			
			//when opcode=0x23
			if(opcode==0x23){
				switch(funct3){
				 	case 000:
						//addi
						reg[rd]=reg[rs1]+imm;
						break;
					case 001://***************
						//slli-shifr left logical immediate
						reg[rd]=reg[rs1]<<<imm;
						break;	
					case 010:
						//slti-set less than immediate
						if(reg[rs1]>imm){
						reg[rd]==1;
						}
						else{reg[rd]==0;}
						break;
					case 011:
						//sltiu-set less than immediate unsigned
						//*****************
						if(reg[rs1]>imm){
						reg[rd]==1;
						}
						else{reg[rd]==0;}
						break;	
					case 100:
						//xori
						reg[rd]=reg[rs1]^imm;
						break;	
					case 101://******************
						//srli and sral- shift right logical and arithmetic immediate
						
						if(funct7==0000000){
						reg[rd]=reg[rs1]>>>imm;
						}
						if(funct7==0100000){
						reg[rd]=reg[rs1]>>imm;
						}
						break;
					case 110:
						//ori
						reg[rd]=reg[rs1]|imm;
						break;	
						break;	
					case 111:
						//andi
						reg[rd]=reg[rs1]&imm;
						break;		
				}
				
					
			}
			//opcode is 0x43
			
			if(opcode==0x43){
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
			//when opcode=0x63
			
			if(opcode== 0x63){
				switch(funct3){
					case 000://***************
						//add and sub
						if(funct7==0000000){
							//add
							reg[rd]=reg[rs1]+reg[rs2];
						}
						else{
						//sub
							reg[rd]=reg[rs1]-reg[rs2];
						}
						break;
					case 001:
						//sll
						reg[rd]=reg[rs1]<<<reg[rs2];
						break;	
					case 010:
						//slt-set less than. slt rd, rs1, rs2.
						//rd is 1 if rs1<rs2
						if(reg[rs1]<reg[rs2]){
						reg[rd]==1;
						}
						else{reg[rd]==0;}
						break;
					case 011:
						//sltu
						//*****************
						break;	
					case 100:
						//xor
						reg[rd]=reg[rs1]^reg[rs2];
						break;	
					case 101://**************
						//srl and  sra
						if(funct7=0000000){
						//srl
						reg[rd]=reg[rs1]>>>reg[rs2];
						}
						//sra
						if(funct7=0100000){
						//srl
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
			}
			
			

			++pc; // We count in 4 byte words
			if (pc >= progr.length) {
				break;
			}
			for (int i = 0; i < reg.length; ++i) {
				System.out.print(reg[i] + " ");
			}
			System.out.println();
		}

		System.out.println("Program exit");

	}

}
