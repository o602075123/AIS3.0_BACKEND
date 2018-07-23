package com.digihealth.anesthesia.interfacedata.formbean;

import java.io.Serializable;
import java.util.Date;

public class TakeMedRecordFormbean implements Serializable{ 

	private String medicineName;//药品名称
	private String batch;//批次
	private String spec;//规格
	private Float number;//数量
	private String type;//类型
	private String speed;//流速
	private Date taketime;//取药时间
	private String takeUser;//取药人
	
	public String getMedicineName() {
		return medicineName;
	}
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public Float getNumber() {
		return number;
	}
	public void setNumber(Float number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public Date getTaketime() {
		return taketime;
	}
	public void setTaketime(Date taketime) {
		this.taketime = taketime;
	}
	public String getTakeUser() {
		return takeUser;
	}
	public void setTakeUser(String takeUser) {
		this.takeUser = takeUser;
	}
	
	
	
}