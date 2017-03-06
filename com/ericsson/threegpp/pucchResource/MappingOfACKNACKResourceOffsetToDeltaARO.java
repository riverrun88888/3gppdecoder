package com.ericsson.threegpp.pucchResource;

public enum MappingOfACKNACKResourceOffsetToDeltaARO {
	I0 (0, 0),
	I1 (1,-1),
	I2 (2,-2),
	I3 (3, 2);
	
	private int ackNackResourceOffset;
	private int deltaARO;
	
	private MappingOfACKNACKResourceOffsetToDeltaARO(int ackNackResourceOffset, int deltaARO){
		this.ackNackResourceOffset = ackNackResourceOffset;
		this.deltaARO = deltaARO;
	}

	public static MappingOfACKNACKResourceOffsetToDeltaARO getByAckNackResourceOffset(int ackNackResourceOffset){
		for(MappingOfACKNACKResourceOffsetToDeltaARO mapping : MappingOfACKNACKResourceOffsetToDeltaARO.values()){
			if(mapping.getAckNackResourceOffset() == ackNackResourceOffset){
				return mapping;
			}
		}
		return null;
	}
	
	public int getAckNackResourceOffset() {
		return ackNackResourceOffset;
	}

	public void setAckNackResourceOffset(int ackNackResourceOffset) {
		this.ackNackResourceOffset = ackNackResourceOffset;
	}

	public int getDeltaARO() {
		return deltaARO;
	}

	public void setDeltaARO(int deltaARO) {
		this.deltaARO = deltaARO;
	}
}
