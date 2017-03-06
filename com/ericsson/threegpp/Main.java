package com.ericsson.threegpp;

import java.util.Hashtable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.ericsson.threegpp.dciFormat.DCIDecoder;
import com.ericsson.threegpp.dciFormat.DCIFormat_6_0A;
import com.ericsson.threegpp.dciFormat.DCIFormat_6_1A;
import com.ericsson.threegpp.dciFormat.DCIFormat_6_2;
import com.ericsson.threegpp.dciFormat.ULGrantInRAR;
import com.ericsson.threegpp.others.PagingOccasionCalculator;
import com.ericsson.threegpp.others.SIB1BRTransmissionCalculator;
import com.ericsson.threegpp.pucchResource.PUCCHHarqResource;
import com.ericsson.threegpp.pucchResource.PUCCHResourceCalculator;
import com.ericsson.threegpp.pucchResource.PUCCHSRResource;

public class Main {
	
	private final String ulGrantInRAR = "ulGrantInRAR";
    private final String dciFormat_6_0A = "dciFormat_6_0A";
    private final String dciFormat_6_1A = "dciFormat_6_1A";
    private final String dciFormat_6_2 = "dciFormat_6_2";
    private final String puccehHARQResource = "pucchHARQResource";
    private final String puccehSRResource = "pucchSRResource";
    private final String pagingOccasion = "pagingOccasion";
    private final String sib1BRTransmission = "sib1BR";
    private final String initlSequence= "initSequence";
	
	@Parameter(names={"--decoderType", "-t"})
    String decoderType;
	@Parameter(names={"--dciType", "-d"})
	boolean iseNBTrace;
	@Parameter(names={"--dciHexString", "-hex"})
	String dciHexString;
	@Parameter(names={"--resourceBlockAssignment", "-rba"})
	String rbaHexString;
    @Parameter(names={"--parameters", "-paras"})
    String parameters;
    @Parameter(names={"--manual", "-m"})
    boolean manual = false;
    
	public static void main(String[] args) {
		Main main = new Main();
		new JCommander(main, args);
		main.run();
	}
	
	public void run(){
		DCIDecoder dciDecoder;
		PUCCHResourceCalculator pucchResourceCalculator;
		if(manual){
			System.out.println("PLEASE REFER TO FOLLOWING EXAPMLES TO USE THIS TOOL. FOR SUPPORT, PLEASE CONTACT RIVER.HE@ERICSSON.COM"+"\n"
					+"\n"
					+ "1. To decode UL Grant in RAR, use following example command:"+"\n"
					+ "-t ulGrantInRAR -hex EE260 -paras 50,A,8,2"+"\n"
					+ "Parameters from left to right is DLBandwidth in PRBs, CEMode, pusch-maxNumRepetitionCEmodeA-r13, mpdcch-NarrowbandsToMonitor-r13"+"\n"
					+ "\n"
					+ "2. To calculate PUCCH HARQ Resource, use following example command:"+"\n"
					+ "-t pucchHARQResource -paras 1,2,0,0,distributed,1,1,0,50,1,1a" + "\n"
					+ "Parameters from left to right is deltaPUCCH-shift, nRB-CQI, nCS-AN, n1PUCCH-AN, EPDCCH-SetConfig-r11, "
					+ "interval-ULHoppingConfigCommonMode, pucch-NumRepetitionCE-MSG4-Level0-r13 or pucch-NumRepetitionCE-format1, HARQ-ACK resource offset (most recent DCI format 6-1A), "
					+ "UL Bandwidth, UL absolute subframe to send PUCCH, PUCCH format"+"\n"
					+ "\n"
					+ "3. To calculate PUCCH SR Resource, use following exapmle command:"+"\n"
					+ "-t pucchSRResource -paras 1,2,0,1,1,18,76,50" + "\n"
					+ "Parameters from left to right is deltaPUCCH-shift, nRB-CQI, nCS-AN, "
					+ "interval-ULHoppingConfigCommonMode, pucch-NumRepetitionCE-MSG4-Level0-r13 or pucch-NumRepetitionCE-format1, "
					+ "sr-PUCCH-ResourceIndex, sr-ConfigIndex, UL Bandwidth"+"\n"
					+ "\n"
					+ "4. To calculate PF and PO, use following example command:"+"\n"
					+ "-t pagingOccasion -paras 262800522170001,128,1,1,mpdcch"+"\n"
					+ "Parameters from left to right is IMSI, defaultPagingCycle, paging-narrowBands-r13, nB, monitoredControlChannel"+"\n"
					+"\n"
					+ "5. To decode DCI Format 6-1A, use following example command:"+"\n"
					+ "-t dciFormat_6_1A -hex 8AD41000 -paras 50,3,false,0,false"+"\n"
					+ "Parameters from left to right is DLbandwidth in PRBs, transmissionMode, isPdcchOrder, pdsch-maxNumRepetitionCEmodeA-r13, isRA-RNTI"+"\n"
					+"\n"
					+ "6. To decode DCI Format 6-0A, use following example command:"+"\n"
					+ "-t dciFormat_6_0A -hex 0AD80100 -paras 50,8"+"\n"
					+ "Parameters from left to right is ULbandwidth in PRBs, pusch-maxNumRepetitionCEmodeA-r13"+"\n"
					+"\n"
					+ "7. To decode DCI Format 6-2, use following example command:"+"\n"
					+ "-t dciFormat_6_2 -hex 0B200 -paras 50,0"+"\n"
					+ "Parameters from left to right is DLbandwidth in PRBs, pdsch-maxNumRepetitionCEmodeA-r13"+"\n"
					+"\n"
					+ "8. To decode SIB1-BR Transmission, use following example command:"+"\n"
					+ "-t sib1BR -paras 13,333,50,fdd"+"\n"
					+ "Parameters from left to right is schedulingInfoSIB1-BR-r13, cellId, dLBandwidth in PRBs, frameType"+"\n"
					+ "\n");
		}
		else{
			if(decoderType != null){
				if(decoderType.equals(ulGrantInRAR)){
					dciDecoder = new ULGrantInRAR();
					//36.213. 6.2
					//For UL Grant in RAR, order is DLBandwidth in PRBs, CEMode, pusch-maxNumRepetitionCEmodeA-r13, mpdcch-NarrowbandsToMonitor-r13
					//Example -paras 50,A,8,2
					dciDecoder.decodeInputHEX(dciHexString, parameters);
				}
				else if(decoderType.equals(dciFormat_6_0A)){
					dciDecoder = new DCIFormat_6_0A();
					//Parameters to decode DCI format 6-0A
					//From left to right, order is ULbandwidth in PRB, pusch-maxNumRepetitionCEmodeA, bitLengthForDCIFormat6-1A
					dciDecoder.decodeInputHEX(dciHexString, parameters);
				}
				else if(decoderType.equals(dciFormat_6_1A)){
					dciDecoder = new DCIFormat_6_1A();
					//Parameters to decode DCI format 6-1A
					//From left to right, order is DLbandwidth in PRBs, transmissionMode, isPdcchOrder, pdsch-maxNumRepetitionCEmodeA-r13, isRA-RNTI
					dciDecoder.decodeInputHEX(dciHexString, parameters);
				}
				else if(decoderType.equals(dciFormat_6_2)){
					dciDecoder = new DCIFormat_6_2();
					//Parameters to decode DCI format 6-2
					//From left to right, order is DLbandwidth in PRBs, maxNumRepetitionCEmodeA-r13
					dciDecoder.decodeInputHEX(dciHexString, parameters);
				}
				else if(decoderType.equals(puccehHARQResource)){
					pucchResourceCalculator = new PUCCHHarqResource(parameters);
					//Parameters to calculate PUCCH PRB resource, order is deltaPUCCH-shift, nRB-CQI, nCS-AN, n1PUCCH-AN, EPDCCH-SetConfig-r11, 
					//interval-ULHoppingConfigCommonMode, pucch-NumRepetitionCE-MSG4-Level0-r13 or pucch-NumRepetitionCE-format1, HARQ-ACK resource offset (most recent DCI format 6-1A)
					//UL Bandwidth, UL absolute subframe to send PUCCH, PUCCH format
					//Example -paras 1,0,0,0,distributed,1,1,0,50,1,1a
					pucchResourceCalculator.calculatePUCCHPRBResource();
				}
				else if(decoderType.equals(puccehSRResource)){
					pucchResourceCalculator = new PUCCHSRResource(parameters);
					//Parameters to calculate PUCCH PRB resource, order is deltaPUCCH-shift, nRB-CQI, nCS-AN, 
					//interval-ULHoppingConfigCommonMode, pucch-NumRepetitionCE-MSG4-Level0-r13 or pucch-NumRepetitionCE-format1, sr-PUCCH-ResourceIndex, sr-ConfigIndex, UL Bandwidth
					//Example -paras 1,2,0,1,1,18,76,50
					pucchResourceCalculator.calculatePUCCHPRBResource();
				}
				else if(decoderType.equals(pagingOccasion)){
					PagingOccasionCalculator paging = new PagingOccasionCalculator(parameters);
					//36.304 7
					//Prameters to calculate PF and PO, order is IMSI, defaultPagingCycle, paging-narrowBands-r13, nB, monitoredControlChannel
					paging.calculatePF();
					paging.calculatePO();
					paging.calculateNarrowbandIndex();
				}
				else if(decoderType.equals(sib1BRTransmission)){
					SIB1BRTransmissionCalculator SIB1BRCalculator = new SIB1BRTransmissionCalculator();
					//Parameters to calculate SIB1-BR transmission
					//From left to right, schedulingInfoSIB1-BR-r13, cellId, dLBandwidth in PRBs, frameType
					//Example -paras 13, 333, 50, fdd
					SIB1BRCalculator.calculateSIB1BRTransmission(parameters);
				}
				else if(decoderType.equals(initlSequence)){
					Hashtable<Integer, Integer> hash = new Hashtable<Integer, Integer>();
				}
			}
		}
	}
}
