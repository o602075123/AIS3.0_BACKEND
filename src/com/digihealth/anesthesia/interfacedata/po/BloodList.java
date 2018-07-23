package com.digihealth.anesthesia.interfacedata.po;

import java.io.Serializable;
import java.util.List;

public class BloodList implements Serializable{
	
	private List<Blood> blood;

	public List<Blood> getBlood() {
		return blood;
	}

	public void setBlood(List<Blood> blood) {
		this.blood = blood;
	}


	
}
