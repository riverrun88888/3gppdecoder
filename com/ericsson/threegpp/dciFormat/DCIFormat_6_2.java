package com.ericsson.threegpp.dciFormat;

public class DCIFormat_6_2 implements DCIDecoder{
	
	//constants
	private final int[] PDSCHRepetitionLevel0 = {1,2,4,8};
	private final int[] PDSCHRepetitionLevel8 = {1,2,4,8};
	private final int[] PDSCHRepetitionLevel16 = {1,4,8,16};
	private final int[] PDSCHRepetitionLevel32 = {1,4,16,32};
	
	//input parameters
	private int N_DLRB;
	private int pdsch_maxNumRepetitionCEmodeA;
	
	private int counter;
	private int flag;
	private int narrowbandIndex;
	private int mcs;
	private int repetitionNumber;
	private int dciSubframeRepetitionNumber;
	
	@Override
	public void decodeInputHEX(String dciHexString, String dci6_2_Paras) {
		//convert HEX to bit string
		dciHexString = Long.toBinaryString(Long.parseLong(dciHexString, 16));
		char[] hexChars = dciHexString.toCharArray();
		//parse parameters
		String[] paras = dci6_2_Paras.split(",");
		N_DLRB = Integer.parseInt(paras[0]);
		pdsch_maxNumRepetitionCEmodeA = Integer.parseInt(paras[1]);
		//start decoding
		flag = hexChars[0]-48;
		if(flag == 1){
			System.out.println("This DCI Format 6-2 is for Paging");
			//Resource block assignment, narrowband index
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
				narrowbandIndex += ((hexChars[1+i]-48)<<(counter-i-1));
			}
			System.out.println("Narrowband Index is "+narrowbandIndex);
			//MCS - 3 bits as defined in 36.213 7.1.7
			mcs = ((hexChars[counter+1]-48)<<2)
					+ ((hexChars[counter+2]-48)<<1)
					+ ((hexChars[counter+3]-48));
			System.out.println("MCS is "+mcs);
			//Repetition number - 3 bits as defined in 36.213 7.1.11
			repetitionNumber = ((hexChars[counter+4]-48)<<2)
					+ ((hexChars[counter+5]-48)<<1)
					+ ((hexChars[counter+6]-48));
			if(pdsch_maxNumRepetitionCEmodeA == 0){
				System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PDSCHRepetitionLevel0[repetitionNumber]);
			}
			else if(pdsch_maxNumRepetitionCEmodeA == 8){
				System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PDSCHRepetitionLevel8[repetitionNumber]);
			}
			else if(pdsch_maxNumRepetitionCEmodeA == 16){
				System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PDSCHRepetitionLevel16[repetitionNumber]);
			}
			else if(pdsch_maxNumRepetitionCEmodeA == 32){
				System.out.println("Repetition Number is " + repetitionNumber+ " -> " + PDSCHRepetitionLevel32[repetitionNumber]);
			}
			//DCI subframe repetition number - 2 bits as defined in 36.213 9.1.5
			dciSubframeRepetitionNumber = ((hexChars[counter+7]-48)<<1) + (hexChars[counter+8]-48);
			System.out.println("DCI Subframe Repetition Number is " + dciSubframeRepetitionNumber);
		}
		else{
			System.out.println("This DCI Format 6-2 is for Direct Indication");
		}
	}
}
