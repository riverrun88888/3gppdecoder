package com.ericsson.threegpp.pucchResource;

public class PUCCHSRResource implements PUCCHResourceCalculator{
	
	//calculation is based on 36.211 5.4.3
	//constant parameters
	private final int c = 3; //For normal cyclic shift c = 2 for extended cyclic shift
	private final int n_RB_SC = 12;
	
	//input parameters
	private int deltaPUCCH_Shift;
	private int nRB_CQI;
	private int nCS_AN;
	private int ulBandwidth;
	private int interval_ULHoppingConfigCommonMode;
	private int pucch_NumRepetitionCE;
	private int sr_PUCCH_ResourceIndex;
	private int sr_ConfigIndex;
	
	//calculated parameters
	private int srPeriodicity;
	private int srSubframeOffset;
	private int srSubframe;
	private int n_1_p0_PUCCH;
	private int m;
	private int j;
	private int m_j;
	private int n_PRB;
	
	public PUCCHSRResource(String parameters){
		String[] paras = parameters.split(",");
		this.deltaPUCCH_Shift = Integer.parseInt(paras[0]);
		this.nRB_CQI = Integer.parseInt(paras[1]);
		this.nCS_AN = Integer.parseInt(paras[2]);
		this.interval_ULHoppingConfigCommonMode = Integer.parseInt(paras[3]);
		this.pucch_NumRepetitionCE = Integer.parseInt(paras[4]);
		this.sr_PUCCH_ResourceIndex = Integer.parseInt(paras[5]);
		this.sr_ConfigIndex = Integer.parseInt(paras[6]);
		this.ulBandwidth = Integer.parseInt(paras[7]);
	}
	
	@Override
	public void calculatePUCCHPRBResource() {
		//36.213 10.1.5
		if(sr_ConfigIndex >=0 && sr_ConfigIndex <=4){
			srPeriodicity = 5;
			srSubframeOffset = sr_ConfigIndex;
		}
		else if(sr_ConfigIndex >=5 && sr_ConfigIndex <=14){
			srPeriodicity = 10;
			srSubframeOffset = sr_ConfigIndex-5;
		}
		else if(sr_ConfigIndex >=15 && sr_ConfigIndex <=34){
			srPeriodicity = 20;
			srSubframeOffset = sr_ConfigIndex-15;
		}
		else if(sr_ConfigIndex >=35 && sr_ConfigIndex <=74){
			srPeriodicity = 40;
			srSubframeOffset = sr_ConfigIndex-35;
		}
		else if(sr_ConfigIndex >=75 && sr_ConfigIndex <=154){
			srPeriodicity = 80;
			srSubframeOffset = sr_ConfigIndex-75;
		}
		else if(sr_ConfigIndex >=155 && sr_ConfigIndex <=156){
			srPeriodicity = 2;
			srSubframeOffset = sr_ConfigIndex-155;
		}
		else if(sr_ConfigIndex == 157){
			srPeriodicity = 1;
			srSubframeOffset = sr_ConfigIndex-157;
		}
		n_1_p0_PUCCH = sr_PUCCH_ResourceIndex;
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
		srSubframe = srSubframeOffset%10;
		for(int i=srSubframe;i<(srSubframe+pucch_NumRepetitionCE);i++){
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
			System.out.println("Calculated PUCCH PRB Index for SR is "+n_PRB);
		}
		srPeriodicity = srPeriodicity+10*(srSubframeOffset/10);
		System.out.println("Calculated SR opportunity is subframe " +srSubframe+" for every "+srPeriodicity+" ms");
	}

}
