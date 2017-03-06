package com.ericsson.threegpp.dciFormat;

public class DCIFormat_6_1A implements DCIDecoder {
	
	//constants
	private final int n_DLRB = 6;
	private final int[] PDSCHRepetitionLevel0 = {1,2,4,8};
	private final int[] PDSCHRepetitionLevel8 = {1,2,4,8};
	private final int[] PDSCHRepetitionLevel16 = {1,4,8,16};
	private final int[] PDSCHRepetitionLevel32 = {1,4,16,32};
	private final int[] tpcPucchMapping = {-1,0,1,3};
	private final String reserved = "reserved";
	
	//parameters
	private int narrowbandIndex;
	private int RBstart;
	private int L_CRBs;
	private int N_DLRB;
	private int tm;
	private boolean isPdcchOrder = false;
	private int pdsch_maxNumRepetitionCEmodeA;
	private int pdschRepetitionLevel;
	private boolean isRA_RNTI = false;
	
	//local parameters
	private String trueFalseIndicator;
	private int N_1A_PRB;
	private int counter;
	private int mcs;
	private int repetitionNumber;
	private int harqProcessNumber;
	private int rv;
	private int tpcPucch;
	private int harqResourceOffset;
	private int dciSubframeRepetitionNumber;
	
	@Override
	public void decodeInputHEX(String dciHexString, String dci6_1A_Paras) {
		//convert HEX to bit string
		dciHexString = Long.toBinaryString(Long.parseLong(dciHexString, 16));
		char[] hexChars = dciHexString.toCharArray();
		//parse parameters
		String[] paras = dci6_1A_Paras.split(",");
		N_DLRB = Integer.parseInt(paras[0]);
		tm = Integer.parseInt(paras[1]);
		if(paras[2].equals("true")){
			isPdcchOrder = true;
		}
		pdsch_maxNumRepetitionCEmodeA = Integer.parseInt(paras[3]);
		if(paras[4].equals("true")){
			isRA_RNTI = true;
		}
		//started decoding
		System.out.println("Decoding result for DCI format 6-1A is following:");
		//Flag format 6-0A/6-1A differentiation - 1 bit, where value 0 indicates format 6-0A and value 1 indicates format 6-1A
		System.out.println("Format flag "+hexChars[0]+" -> "+" 6-"+hexChars[0]+"A");
		if(!isPdcchOrder){
			//Frequency hopping flag
			trueFalseIndicator = (hexChars[1]-48) == 1 ? "True" : "False";
			System.out.println("Frequency Hopping Flag "+hexChars[1]+" -> "+trueFalseIndicator);
			//Resource block assignment ceiling(log2(floor(N_DLRB/6))) + 5 bits
			//Decode narrowband index
			int nrOfNarrowbands = N_DLRB/6;
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
//			System.out.println("RIV is " + RIV);
			RBstart = RIV%n_DLRB;
			L_CRBs = RIV/n_DLRB+1;
			if(L_CRBs-1>n_DLRB/2 || L_CRBs > (n_DLRB-RBstart)){
				RBstart = (RIV+1-n_DLRB)%n_DLRB;
				L_CRBs = n_DLRB+1-RIV/n_DLRB;
			}
			System.out.println("PDSCH Allocation: RB start index is " + RBstart +" and number of contiguous RB is " + L_CRBs);
			//MCS - 4 bits as defined in 36.213 7.1.7
			for(int i=0;i<4;i++){
				mcs += ((hexChars[counter+7+i]-48)<<(4-i-1));
			}
			System.out.println("MCS is "+mcs);
			//Repetition number - 2bits defined in 36.213 7.1.11
			repetitionNumber = ((hexChars[counter+11]-48)<<1) + (hexChars[counter+12]-48);
			if(pdsch_maxNumRepetitionCEmodeA == 0){
				pdschRepetitionLevel = PDSCHRepetitionLevel0[repetitionNumber];
			}
			if(pdsch_maxNumRepetitionCEmodeA == 8){
				pdschRepetitionLevel = PDSCHRepetitionLevel8[repetitionNumber];
			}
			else if(pdsch_maxNumRepetitionCEmodeA == 16){
				pdschRepetitionLevel = PDSCHRepetitionLevel16[repetitionNumber];
			}
			else if(pdsch_maxNumRepetitionCEmodeA == 32){
				pdschRepetitionLevel = PDSCHRepetitionLevel32[repetitionNumber];
			}
			System.out.println("Repetition Number is " + repetitionNumber+ " -> " + pdschRepetitionLevel);
			if(!isRA_RNTI){
				//HARQ Process number - 3 bits for FDD primary cell, 4 bits for TDD primary cell
				harqProcessNumber = ((hexChars[counter+13]-48)<<2) 
						+ ((hexChars[counter+14]-48)<<1)
						+ (hexChars[counter+15]-48);
				System.out.println("HARQ Process Number is "+harqProcessNumber);
				//New Data Indicator - 1 bit
				trueFalseIndicator = (hexChars[counter+16]-48) == 1 ? "True" : "False";
				System.out.println("New Data Indicator is "+(hexChars[counter+16]-48) + " -> "+trueFalseIndicator);
				//Redundancy version
				rv = ((hexChars[counter+17]-48)<<1)+(hexChars[counter+18]-48);
				System.out.println("Redundancy Version is "+rv);
				//TPC Command for PUCCH - 2 bits as defined in 36.213 5.1.2.1
				tpcPucch = ((hexChars[counter+19]-48)<<1)+(hexChars[counter+20]-48);
				System.out.println("TPC command for PUCCH is "+tpcPucch+" -> "+tpcPucchMapping[tpcPucch]+"dB");
				//Downlink Assignment Index as specified in 36.212 Table 5.3.3.1.2-2
				//For UEs configured with no more than five DL cells, or for UEs configured by higher layers with codebooksizeDetermination-r13 = cc, 
				//or for UEs configured by higher layers with codebooksizeDetermination-r13 = dai 
				//and when a DCI format scheduling PDSCH is not mapped onto the UE specific search space given by the C-RNTI as defined in [3], 
				//this field is not present for FDD or TDD operation, for cases with FDD primary cell.
				//In current eNB SW, UE-specific search space is not used, number of bits for this field is 0
				System.out.println("Downlink Assignment Index has 0 bits");
				if(pdschRepetitionLevel == 1){
					//DAI field is reserved if repetition number is greater than 1
					if(tm == 3 || tm == 2 || tm == 4 || tm == 7){
						//SRS Request - 1 bit as defined in 36.213 8.2
						trueFalseIndicator = (hexChars[counter+21]-48) == 1 ? "True" : "False";
						System.out.println("SRS Request is " + (hexChars[counter+21]-48)+ " -> "+trueFalseIndicator);
						//HARQ-ACK Resource offset - 2 bits as defined in 36.213 10.1
						harqResourceOffset = ((hexChars[counter+22]-48)<<1) + (hexChars[counter+23]-48);
						System.out.println("HARQ-ACK Resource Offset is "+harqResourceOffset);
						//DCI subframe repetition number - 2 bits as defined in 36.213 9.1.5
						dciSubframeRepetitionNumber = ((hexChars[counter+24]-48)<<1) + (hexChars[counter+25]-48);
						System.out.println("DCI Subframe Repetition Number is " + dciSubframeRepetitionNumber);
					}
					else if(tm == 6){
						//TPMI
						//TODO
						//PMI
					}
					else if(tm == 9){
						//Antenna ports and scrambling identity - 2 bits indicates the values 0 to 3 as specified in 36.212 Table 5.3.3.1.5C-1
						//this field is only present if PDSCH transmission is configured with TM9
					}
				}
				else{
					//TODO
				}
			}
			else{
				//DCI format 6-1A is scrambled by RA-RNTI
				//HARQ Process number - 3 bits reserved
				System.out.println("HARQ Process Number is "+reserved);
				//New Data Indicator - 1 bit reserved
				System.out.println("New Data Indicator is "+reserved);
				//Redundancy version
				rv = ((hexChars[counter+17]-48)<<1)+(hexChars[counter+18]-48);
				System.out.println("Redundancy Version is "+rv);
				//TPC Command for PUCCH - 2 bits as defined in 36.213 5.1.2.1. If format 6-1A CRC is scrambled with RA-RNTI. 
				//The most significant bit of the TPC command is reserved
				//The least significant bit of the TPC command indicates N_1A_PRB. If 0, N_1A_PRB=2, else N_1A_PRB=3
				tpcPucch = (hexChars[counter+20]-48);
				N_1A_PRB = tpcPucch < 1 ? 2 : 3;
				System.out.println("TPC command indicates "+tpcPucch+" -> N_1A_PRB is "+N_1A_PRB);
				//Downlink Assignment Index as specified in 36.212 Table 5.3.3.1.2-2
				//For UEs configured with no more than five DL cells, or for UEs configured by higher layers with codebooksizeDetermination-r13 = cc, 
				//or for UEs configured by higher layers with codebooksizeDetermination-r13 = dai 
				//and when a DCI format scheduling PDSCH is not mapped onto the UE specific search space given by the C-RNTI as defined in [3], 
				//this field is not present for FDD or TDD operation, for cases with FDD primary cell.
				//In current eNB SW, UE-specific search space is not used, number of bits for this field is 0
				System.out.println("Downlink Assignment Index has 0 bits and is "+reserved);
				if(pdschRepetitionLevel == 1){
					//DAI field is reserved if repetition number is greater than 1
					if(tm == 3 || tm == 2 || tm == 4 || tm == 7){
						//SRS Request - 1 bit as defined in 36.213 8.2
						trueFalseIndicator = (hexChars[counter+21]-48) == 1 ? "True" : "False";
						System.out.println("SRS Request is " + (hexChars[counter+21]-48)+ " -> "+trueFalseIndicator);
						//HARQ-ACK Resource offset - 2 bits as defined in 36.213 10.1
						System.out.println("HARQ-ACK Resource Offset is "+reserved);
						//DCI subframe repetition number - 2 bits as defined in 36.213 9.1.5
						dciSubframeRepetitionNumber = ((hexChars[counter+24]-48)<<1) + (hexChars[counter+25]-48);
						System.out.println("DCI Subframe Repetition Number is " + dciSubframeRepetitionNumber);
					}
					else if(tm == 6){
						//TPMI
						//TODO
						//PMI
					}
					else if(tm == 9){
						//Antenna ports and scrambling identity - 2 bits indicates the values 0 to 3 as specified in 36.212 Table 5.3.3.1.5C-1
						//this field is only present if PDSCH transmission is configured with TM9
					}
				}
				else{
					//TODO
				}
			}
			
		}
		else{
			
		}
	}
}
