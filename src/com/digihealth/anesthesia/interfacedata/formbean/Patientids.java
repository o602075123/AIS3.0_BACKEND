package com.digihealth.anesthesia.interfacedata.formbean;

import java.util.List;

public class Patientids {
	private List<Element> element;

	public List<Element> getElements() {
		return element;
	}

	public void setElements(List<Element> elements) {
		this.element = elements;
	}

	@Override
	public String toString() {
		return "Patientids [element=" + element + "]";
	}
	
	
}
