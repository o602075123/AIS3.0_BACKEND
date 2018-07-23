package com.digihealth.anesthesia.interfacedata.formbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import com.digihealth.anesthesia.interfacedata.po.OperList;

@XmlRootElement(name = "Request")
public class HisCancleOptFormBean implements Serializable{
	
	private String reservenumber;
	private String state;
	public String getReservenumber() {
		return reservenumber;
	}
	public void setReservenumber(String reservenumber) {
		this.reservenumber = reservenumber;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
	
}
