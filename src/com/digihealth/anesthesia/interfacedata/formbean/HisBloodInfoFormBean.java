package com.digihealth.anesthesia.interfacedata.formbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.digihealth.anesthesia.interfacedata.po.BloodList;

@XmlRootElement(name = "request")
public class HisBloodInfoFormBean implements Serializable{
	
	private String reservenumber;//预约ID
	private String bloodType;//血型 A,B,AB,O
	private String deptId;//科室ID
	private String deptName;//科室名称
	private String useDate;
	private String antGlo;//直接抗人球蛋白实验：阴性 阳性
	private String antScr;//不规则抗体筛查 阴性 阳性
	private String rhType;//Rh(D)血型:阴性，阳性
	
	private BloodList blood_sum;
	
	public String getReservenumber() {
		return reservenumber;
	}
	public void setReservenumber(String reservenumber) {
		this.reservenumber = reservenumber;
	}
	public String getBloodType() {
		return bloodType;
	}
	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getUseDate() {
		return useDate;
	}
	public void setUseDate(String useDate) {
		this.useDate = useDate;
	}
	public String getAntGlo() {
		return antGlo;
	}
	public void setAntGlo(String antGlo) {
		this.antGlo = antGlo;
	}
	public String getAntScr() {
		return antScr;
	}
	public void setAntScr(String antScr) {
		this.antScr = antScr;
	}
	public String getRhType() {
		return rhType;
	}
	public void setRhType(String rhType) {
		this.rhType = rhType;
	}
	public BloodList getBlood_sum() {
		return blood_sum;
	}
	public void setBlood_sum(BloodList blood_sum) {
		this.blood_sum = blood_sum;
	}
	


}
