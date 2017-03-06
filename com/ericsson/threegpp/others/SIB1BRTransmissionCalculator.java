package com.ericsson.threegpp.others;

public class SIB1BRTransmissionCalculator {
	//constants
	private final String frameFDD = "fdd";
	private final String frameTDD = "tdd";
	private final int[] SIB1_BR_PDSCH_Repetition_Table = {0,4,8,16,4,8,16,4,8,16,4,8,16,4,8,16,4,8,16};
	private final int[] SIB1_BR_PDSCH_TBS_Table = {0,208,208,208,256,256,256,328,328,328,504,504,504,712,712,712,
			936,936,936};
	private final int[] narrowbandsSetFor25PRBs = {0,3};
	private final int[] narrowbandsSetFor50PRBs = {0,1,2,5,6,7};
	private final int[] narrowbandsSetFor75PRBs = {0,1,2,3,4,7,8,9,10,11};
	private final int[] narrowbandsSetFor100PRBs = {0,1,2,3,4,5,6,9,10,11,12,13,14,15};
	
	//input parameters
	private int schedulingInfoSIB1_BR_r13;
	private int cellId;
	private int dlBandwidth;
	private String frameType;
	
	//local parameters
	private int n_sib1br_pdsch;
	private int sib1br_tbs;
	private int m;
	private int nNB;
	private int nSNB;
	
	public void calculateSIB1BRTransmission(String parameters){
		//parse input parameters
		String[] paras = parameters.split(",");
		schedulingInfoSIB1_BR_r13 = Integer.parseInt(paras[0]);
		cellId = Integer.parseInt(paras[1]);
		dlBandwidth = Integer.parseInt(paras[2]);
		frameType = paras[3];
		//calculate local parameters
		n_sib1br_pdsch = SIB1_BR_PDSCH_Repetition_Table[schedulingInfoSIB1_BR_r13];
		sib1br_tbs = SIB1_BR_PDSCH_TBS_Table[schedulingInfoSIB1_BR_r13];
		sib1BRTransmissionSfnAndSubframe(cellId, dlBandwidth, n_sib1br_pdsch, frameType);
		System.out.println("SIB1-BR TBS is "+sib1br_tbs+" bits");
		if(dlBandwidth < 12){
			m = 1;
		}
		else if(dlBandwidth >= 12 && dlBandwidth <= 50){
			m = 2;
			if(dlBandwidth == 25){
				nSNB = narrowbandsSetFor25PRBs.length;
			}
			else if(dlBandwidth == 50){
				nSNB = narrowbandsSetFor50PRBs.length;
			}
		}
		else if(dlBandwidth > 50){
			m = 4;
			if(dlBandwidth == 75){
				nSNB = narrowbandsSetFor75PRBs.length;
			}
			else if(dlBandwidth == 100){
				nSNB = narrowbandsSetFor100PRBs.length;
			}
		}
		for(int i=0;i<m;i++){
			nNB =((cellId%nSNB)+i*(nSNB/m))%nSNB;
			if(nSNB == narrowbandsSetFor25PRBs.length){
				System.out.println("Narrowband Index to transmit SIB1-BR is "+narrowbandsSetFor25PRBs[nNB]);
			}
			else if(nSNB == narrowbandsSetFor50PRBs.length){
				System.out.println("Narrowband Index to transmit SIB1-BR is "+narrowbandsSetFor50PRBs[nNB]);
			}
			else if(nSNB == narrowbandsSetFor75PRBs.length){
				System.out.println("Narrowband Index to transmit SIB1-BR is "+narrowbandsSetFor75PRBs[nNB]);
			}
			else if(nSNB == narrowbandsSetFor100PRBs.length){
				System.out.println("Narrowband Index to transmit SIB1-BR is "+narrowbandsSetFor100PRBs[nNB]);
			}
		}
	}
	
	private void sib1BRTransmissionSfnAndSubframe(int cellId, int dlBandwidth, int n_sib1br_pdsch, String frameType){
		if(dlBandwidth > 15){
			if(frameType.equals(frameFDD)){
				if(cellId%2 == 0 && n_sib1br_pdsch == 4){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 0 and subframe 4");
				}
				else if(cellId%2 == 1 && n_sib1br_pdsch == 4){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 1 and subframe 4");
				}
				else if(cellId%2 == 0 && n_sib1br_pdsch == 8){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 0, 1 and subframe 4");
				}
				else if(cellId%2 == 1 && n_sib1br_pdsch == 8){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 0, 1 and subframe 9");
				}
				else if(cellId%2 == 0 && n_sib1br_pdsch == 16){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 0, 1 and subframe 4, 9");
				}
				else if(cellId%2 == 1 && n_sib1br_pdsch == 16){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 0, 1 and subframe 0, 9");
				}
			}
			else if(frameType.equals(frameFDD)){
				//TODO
			}
		}
		else{
			if(frameType.equals(frameFDD)){
				if(cellId%2 == 0){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 0 and subframe 4");
				}
				else{
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 1 and subframe 4");
				}
			}
			else if(frameType.equals(frameTDD)){
				if(cellId%2 == 0){
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 1 and subframe 5");
				}
				else{
					System.out.println("SIB1-BR is transmitted "+n_sib1br_pdsch+" times"+" every 80ms starting sfn 1 and subframe 5");
				}
			}
		}
	}
}
