package com.ericsson.threegpp.dciFormat;

public class ULGrantInRAR implements DCIDecoder {
	
	//constants
	private final int[] msg2CommandForScheduledPUSCH = {-6,-4,-2,0,2,4,6,8};
	
	private final String CEModeA = "A";
	private final int N_ULRB = 6;
	private final int[] Msg3RepetitionLevelDivider = {8,4,2,1};
	
	private int narrowbandIndex;
	private int RBstart;
	private int L_CRBs;
	
	@Override
	public void decodeInputHEX(String dciHexString, String ulGrantInRAR_Paras) {
		//convert HEX to bit string
		dciHexString = Integer.toBinaryString(Integer.parseInt(dciHexString, 16));
		char[] hexChars = dciHexString.toCharArray();
		//parse UL grant required input parameters
		String[] paras = ulGrantInRAR_Paras.split(",");
		int n_DLRB = Integer.parseInt(paras[0]);
		String CEMode = paras[1];
		int Ya = Integer.parseInt(paras[2]);
		int NBRAR = Integer.parseInt(paras[3]);
		int NNB2 = n_DLRB/6;
		//calculate narrowbandIndex, first 3 bits in HEX string
		System.out.println("Decoding result for UL Grant in RAR is following:");
		narrowbandIndex = ((hexChars[0]-48)<<2)
				+((hexChars[1]-48)<<1)
				+((hexChars[2]-48));
		System.out.println("Msg3 narrowbandIndex is " + narrowbandIndex);
		//calculate RBstart and L_CRBs
		if(CEMode.equals(CEModeA)){
			int RIV = ((hexChars[3]-48)<<3)
					+ ((hexChars[4]-48)<<2)
					+ ((hexChars[5]-48)<<1)
					+ ((hexChars[6]-48));
			RBstart = RIV%N_ULRB;
			L_CRBs = RIV/N_ULRB+1;
			if(L_CRBs-1>N_ULRB/2 || L_CRBs > (n_DLRB-RBstart)){
				RBstart = (RIV+1-N_ULRB)%N_ULRB;
				L_CRBs = N_ULRB+1-RIV/N_ULRB;
			}
		}
		System.out.println("Msg3 PUSCH allocation, RB start index is " + RBstart +" and number of contiguous RB is " + L_CRBs);
		//calculate number of repetitions for Msg3
		int index = ((hexChars[7]-48)<<1) + (hexChars[8]-48);
		System.out.println("Number of repetitions for Msg3 is " + Ya/Msg3RepetitionLevelDivider[index]);
		//calculate MCS for Msg3
		int mcs = ((hexChars[9]-48)<<2) + ((hexChars[10]-48)<<1) + (hexChars[11]-48);
		System.out.println("MCS for Msg3 is " + mcs);
		//calculate TPC for Msg3
		int tpc = ((hexChars[12]-48)<<2) + ((hexChars[13]-48)<<1) + (hexChars[14]-48);
		System.out.println("TPC for Msg3 is " + tpc + " -> "+msg2CommandForScheduledPUSCH[tpc]+" dB");
		//calculate CSI Request bit
		System.out.println("CSI Requst bit is " + (hexChars[15]-48));
		//calculate UL delay bit
		System.out.println("UL delay bit is " + (hexChars[16]-48));
		// calculate Msg3/Msg4 MPDCCH narrowband index
		int mpdcchNarrowbandIndex = ((hexChars[17]-48)<<1) + (hexChars[18]-48);
		if(mpdcchNarrowbandIndex == 0){
			System.out.println("Msg3/Msg4 MPDCCH narrowband index is " + NBRAR%NNB2);
		}
	}
}
