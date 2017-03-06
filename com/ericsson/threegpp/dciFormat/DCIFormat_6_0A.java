package com.ericsson.threegpp.dciFormat;

public class DCIFormat_6_0A implements DCIDecoder {
	
	//constants
	private final int n_ULRB = 6;
	private final int[] PUSCHRepetitionLevel0 = {1,2,4,8};
	private final int[] PUSCHRepetitionLevel8 = {1,2,4,8};
	private final int[] PUSCHRepetitionLevel16 = {1,4,8,16};
	private final int[] PUSCHRepetitionLevel32 = {1,4,16,32};
	private final int[] tpcPuschMapping = {-1,0,1,3};
	
	//parameters
	private int narrowbandIndex;
	private int RBstart;
	private int L_CRBs;
	private int N_ULRB;
	private int pusch_maxNumRepetitionCEmodeA;
//	private int bitLengthForDCIFormat6_1A;
	
	//local parameters
	private String prePaddings = "";
	private String trueFalseIndicator;
	private int counter;
	private int mcs;
	private int repetitionNumber;
	private int harqProcessNumber;
	private int rv;
	private int tpcPusch;
	private int dciSubframeRepetitionNumber;
		
	@Override
	public void decodeInputHEX(String dciHexString, String dci6_0A_Paras) {
		//parse parameters
		String[] paras = dci6_0A_Paras.split(",");
		N_ULRB = Integer.parseInt(paras[0]);
		pusch_maxNumRepetitionCEmodeA = Integer.parseInt(paras[1]);
//		bitLengthForDCIFormat6_1A = Integer.parseInt(paras[2]);
		//convert HEX to bit string
		dciHexString = Long.toBinaryString(Long.parseLong(dciHexString, 16));
		int len = dciHexString.length();
		for(int i=0;i<29-len;i++){
			prePaddings +="0";
		}
		if(prePaddings.equals("") == false){
			dciHexString = prePaddings+dciHexString;
		}
		char[] hexChars = dciHexString.toCharArray();
		//started decoding
		System.out.println("Decoding result for DCI format 6-0A is following:");
		//Flag format 6-0A/6-1A differentiation - 1 bit, where value 0 indicates format 6-0A and value 1 indicates format 6-1A
		System.out.println("Format flag "+hexChars[0]+" -> "+" 6-"+hexChars[0]+"A");
		//Frequency hopping flag
		trueFalseIndicator = (hexChars[1]-48) == 1 ? "True" : "False";
		System.out.println("Frequency Hopping Flag "+hexChars[1]+" -> "+trueFalseIndicator);
		//Resource block assignment ceiling(log2(floor(N_DLRB/6))) + 5 bits
		//Decode narrowband index
		int nrOfNarrowbands = N_ULRB/6;
		double log = Math.log(nrOfNarrowbands);
		if(log%2 == 0){
			counter = (int)log;
		}
		else{
			counter = ((int)log)+1;
		}
		for(int i=0;i<counter;i++){
			narrowbandIndex += ((hexChars[2+i]-48)<<(counter-i-1));
		}
		System.out.println("Narrowband Index is "+ narrowbandIndex);
		//Decode Resource Allocation using DL resource allocation type 2 within the indicated narrowband
		int RIV = ((hexChars[counter+2]-48)<<4)
				+ ((hexChars[counter+3]-48)<<3)
				+ ((hexChars[counter+4]-48)<<2)
				+ ((hexChars[counter+5]-48)<<1)
				+ ((hexChars[counter+6]-48));
//		System.out.println("RIV is " + RIV);
		RBstart = RIV%n_ULRB;
		L_CRBs = RIV/n_ULRB+1;
		if(L_CRBs-1>n_ULRB/2 || L_CRBs > (n_ULRB-RBstart)){
			RBstart = (RIV+1-n_ULRB)%n_ULRB;
			L_CRBs = n_ULRB+1-RIV/n_ULRB;
		}
		System.out.println("PUSCH Allocation: RB start index is " + RBstart +" and number of contiguous RB is " + L_CRBs);
		//MCS - 4 bits as defined in 36.213 7.1.7
		for(int i=0;i<4;i++){
			mcs += ((hexChars[counter+7+i]-48)<<(4-i-1));
		}
		System.out.println("MCS is "+mcs);
		//Repetition number - 2bits defined in 36.213 7.1.11
		repetitionNumber = ((hexChars[counter+11]-48)<<1) + (hexChars[counter+12]-48);
		if(pusch_maxNumRepetitionCEmodeA == 0){
			System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PUSCHRepetitionLevel0[repetitionNumber]);
		}
		else if(pusch_maxNumRepetitionCEmodeA == 8){
			System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PUSCHRepetitionLevel8[repetitionNumber]);
		}
		else if(pusch_maxNumRepetitionCEmodeA == 16){
			System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PUSCHRepetitionLevel16[repetitionNumber]);
		}
		else if(pusch_maxNumRepetitionCEmodeA == 32){
			System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PUSCHRepetitionLevel32[repetitionNumber]);
		}
		//HARQ Process number - 3 bits for FDD primary cell, 4 bits for TDD primary cell
		harqProcessNumber = ((hexChars[counter+13]-48)<<2) 
				+ ((hexChars[counter+14]-48)<<1)
				+ (hexChars[counter+15]-48);
		System.out.println("HARQ Process Number is "+harqProcessNumber);
		//New Data Indicator - 1 bit
		trueFalseIndicator = (hexChars[counter+16]-48) == 1 ? "True" : "False";
		System.out.println("New Data Indicator is "+(hexChars[counter+16]-48)+" -> "+trueFalseIndicator);
		//Redundancy version
		rv = ((hexChars[counter+17]-48)<<1)+(hexChars[counter+18]-48);
		System.out.println("Redundancy Version is "+rv);
		//TPC Command for PUSCH - 2 bits as defined in 36.213 5.1.1.1
		tpcPusch = ((hexChars[counter+19]-48)<<1)+(hexChars[counter+20]-48);
		System.out.println("TPC command for PUCCH is "+tpcPusch+" -> "+tpcPuschMapping[tpcPusch]+"dB");
		//CSI Request - 1 bit as defined in 36.213 7.2.1
		trueFalseIndicator = (hexChars[counter+21]-48) == 1 ? "True" : "False";
		System.out.println("CSI Request is "+(hexChars[counter+21]-48)+" -> "+trueFalseIndicator);
		//SRS Request - 1 bit as defined in 36.213 8.2
		trueFalseIndicator = (hexChars[counter+22]-48) == 1 ? "True" : "False";
		System.out.println("SRS Request is " + (hexChars[counter+22]-48)+" -> "+trueFalseIndicator);
		//DCI subframe repetition number - 2 bits as defined in 36.213 9.1.5
		dciSubframeRepetitionNumber = ((hexChars[counter+23]-48)<<1) + (hexChars[counter+24]-48);
		System.out.println("DCI Subframe Repetition Number is " + dciSubframeRepetitionNumber);
	}
}
