package com.ericsson.threegpp.others;

public class PagingOccasionCalculator {
	
	//constant
	private String pdcch = "pdcch";
	private String npdcch = "npdcch";
	private String mpdcch = "mpdcch";
	
	//input parameters
	private long imsi;
	private int defaultPagingCycle;
	private double nB;
	private int paging_narrowBands_r13;
	private String monitoredControlChannnel;
	
	//calculated parameters
	private double N;
	private double Ns;
	private double Nn;
	private long UE_ID;
	private int i_s;
	private int counter;
	
	//results
	private int PF;
	private int PagingNarrowband;
//	private int PO;
	
	public PagingOccasionCalculator(String parameters){
		String[] paras = parameters.split(",");
		this.imsi = Long.parseLong(paras[0]);
		this.defaultPagingCycle = Integer.parseInt(paras[1]);
		this.paging_narrowBands_r13 = Integer.parseInt(paras[2]);
		this.nB = Double.parseDouble(paras[3])*defaultPagingCycle;
		this.monitoredControlChannnel = paras[4];
		N = Math.min(defaultPagingCycle, nB);
		Ns = Math.max(1, nB/defaultPagingCycle);
		if(monitoredControlChannnel.equals(pdcch)){
			UE_ID = imsi%1024; //36.304 7.1
		}
		else if(monitoredControlChannnel.equals(npdcch)){
			UE_ID = imsi%4096; //36.304 7.1
		}
		else if(monitoredControlChannnel.equals(mpdcch)){
			UE_ID = imsi%16384; //36.304 7.1
		}
		i_s = (int) ((UE_ID/N)%Ns);
	}
	
	public void calculatePF(){
		counter = 1024/defaultPagingCycle;
		PF = (int) ((defaultPagingCycle/N)*(UE_ID%N));
		for(int i=0;i<counter;i++){
			if(PF < 1024){
				System.out.println("Paging system frame number is "+(i*defaultPagingCycle+PF));
			}
		}
	}
	
	public void calculatePO(){
		//36.304 7.2 
		if(Ns == 1){
			if(i_s == 0){
				System.out.println("Paging occasion subframe is 9");
			}
		}
		else if(Ns == 2){
			if(i_s == 0){
				System.out.println("Paging occasion subframe is 4");
			}
			else if(i_s == 1){
				System.out.println("Paging occasion subframe is 9");
			}
		}
		else if(Ns == 4){
			if(i_s == 0){
				System.out.println("Paging occasion subframe is 0");
			}
			else if(i_s == 1){
				System.out.println("Paging occasion subframe is 4");
			}
			else if(i_s == 2){
				System.out.println("Paging occasion subframe is 5");
			}
			else if(i_s == 3){
				System.out.println("Paging occasion subframe is 9");
			}
		}
	}
	
	public void calculateNarrowbandIndex(){
		if(this.monitoredControlChannnel.equals(mpdcch) || this.monitoredControlChannnel.equals(npdcch)){
			Nn = this.paging_narrowBands_r13;
			PagingNarrowband = (int) ((UE_ID/(N*Ns))%Nn);
			System.out.println("Paging narrowband index is " + PagingNarrowband);
		}
	}
}
