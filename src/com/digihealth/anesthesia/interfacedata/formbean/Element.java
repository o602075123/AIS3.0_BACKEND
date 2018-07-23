package com.digihealth.anesthesia.interfacedata.formbean;

public class Element {
	private String PATIENT_ID;
	private String VISIT_ID;

	public Element() {
	}

	public String getPATIENT_ID() {
		return PATIENT_ID;
	}

	public void setPATIENT_ID(String pATIENT_ID) {
		PATIENT_ID = pATIENT_ID;
	}

	public String getVISIT_ID() {
		return VISIT_ID;
	}

	public void setVISIT_ID(String vISIT_ID) {
		VISIT_ID = vISIT_ID;
	}

	@Override
	public String toString() {
		return "Element [PATIENT_ID=" + PATIENT_ID + ", VISIT_ID=" + VISIT_ID + "]";
	}

}
