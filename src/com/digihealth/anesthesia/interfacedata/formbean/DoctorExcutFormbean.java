package com.digihealth.anesthesia.interfacedata.formbean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class DoctorExcutFormbean implements Serializable{ 
	
	private List<DoctorOrderFormbean> doctorExcutList;

	public List<DoctorOrderFormbean> getDoctorExcutList() {
		return doctorExcutList;
	}

	public void setDoctorExcutList(List<DoctorOrderFormbean> doctorExcutList) {
		this.doctorExcutList = doctorExcutList;
	}
	
	
}