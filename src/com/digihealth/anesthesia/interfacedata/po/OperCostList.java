package com.digihealth.anesthesia.interfacedata.po;

import java.io.Serializable;
import java.util.List;

public class OperCostList implements Serializable{
	
	private List<OperCost> operCost;

	public List<OperCost> getOperCost() {
		return operCost;
	}

	public void setOperCost(List<OperCost> operCost) {
		this.operCost = operCost;
	}
	
	
}
