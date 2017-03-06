package com.ericsson.threegpp.pucchResource;

public class PUCCHHarqResource implements PUCCHResourceCalculator{
	//calculation is based on 36.211 5.4.3
	//constant parameters
	private final String distributed = "distributed";
	private final String localized = "localized";
	private final String pucchFormat1a = "1a";
	private final String pucchFormat1b = "1b";
	private final String pucchFormat2 = "2";
	private final String pucchFormat2a = "2a";
	private final String pucchFormat2b = "2b";
	private final int n_ECCE_q = 0; //For 1 UE, this parameter is 0. 36.213 9.1.5
	private final int c = 3; //For normal cyclic shift c = 2 for extended cyclic shift
	private final int n_RB_SC = 12;
	
	//input parameters
	private int deltaPUCCH_Shift;
	private int nRB_CQI;
	private int nCS_AN;
	private int n1PUCCH_AN;
	private String EPDCCH_SetConfig_r11;
	private int interval_ULHoppingConfigCommonMode;
	private int pucch_NumRepetitionCE;
	private int HARQ_ACK_resource_offset;
	private int ulBandwidth;
	private int iUlSubframe;
	private String pucchFormat;
	
	//calculated parameters
	private int n_1_p0_PUCCH;
	private int deltaARO;
	private int n_m1_PUCCH_q;
	private int m;
	private int j;
	private int m_j;
	private int n_PRB;
	
	public PUCCHHarqResource(String parameters){
		String[] paras = parameters.split(",");
		this.deltaPUCCH_Shift = Integer.parseInt(paras[0]);
		this.nRB_CQI = Integer.parseInt(paras[1]);
		this.nCS_AN = Integer.parseInt(paras[2]);
		this.n1PUCCH_AN = Integer.parseInt(paras[3]);
		this.EPDCCH_SetConfig_r11 = paras[4];
		this.interval_ULHoppingConfigCommonMode = Integer.parseInt(paras[5]);
		this.pucch_NumRepetitionCE = Integer.parseInt(paras[6]);
		this.HARQ_ACK_resource_offset = Integer.parseInt(paras[7]);
		this.ulBandwidth = Integer.parseInt(paras[8]);
		this.iUlSubframe = Integer.parseInt(paras[9]);
		this.pucchFormat = paras[10];
	}
	
	@Override
	public void calculatePUCCHPRBResource() {
		deltaARO = MappingOfACKNACKResourceOffsetToDeltaARO.getByAckNackResourceOffset(HARQ_ACK_resource_offset).getDeltaARO();
		n_m1_PUCCH_q = n1PUCCH_AN;
		if(EPDCCH_SetConfig_r11.equals(distributed)){
			n_1_p0_PUCCH = n_ECCE_q + deltaARO + n_m1_PUCCH_q;
		}
		else if(EPDCCH_SetConfig_r11.equals(localized)){
			//TODO
		}
		if(pucchFormat.equals(pucchFormat1a) || pucchFormat.equals(pucchFormat1b)){
			if(n_1_p0_PUCCH < (c*(nCS_AN/deltaPUCCH_Shift))){
				m = nRB_CQI;
			}
			else{
				if(nCS_AN%8 == 0){
					m = (n_1_p0_PUCCH - c*(nCS_AN/deltaPUCCH_Shift))/(c*(n_RB_SC/deltaPUCCH_Shift)) + nRB_CQI + nCS_AN/8;
				}
				else{
					m = (n_1_p0_PUCCH - c*(nCS_AN/deltaPUCCH_Shift))/(c*(n_RB_SC/deltaPUCCH_Shift)) + nRB_CQI + nCS_AN/8+1;
				}
			}
		}
		else if(pucchFormat.equals(pucchFormat2) || pucchFormat.equals(pucchFormat2a) || pucchFormat.equals(pucchFormat2b)){
			//TODO, not needed for CAT-M
		}
		for(int i=iUlSubframe;i<(iUlSubframe+pucch_NumRepetitionCE);i++){
			j = i/interval_ULHoppingConfigCommonMode;
			if(j%2 == 0){
				m_j = m;
			}
			else if(j%2 == 1 && m%2 == 0){
				m_j = m+1;
			}
			else if(j%2 == 1 && m%2 == 1){
				m_j = m-1;
			}
			if(m_j%2 == 0){
				n_PRB = m_j/2;
			}
			else if(m_j%2 == 1){
				n_PRB = ulBandwidth - 1 - m_j/2;
			}
			System.out.println("Calculated PUCCH PRB Index for PUCCH format "+pucchFormat+" in UL subframe " +i+" is "+n_PRB);
		}
	}
}
