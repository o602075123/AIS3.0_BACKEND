package com.digihealth.anesthesia.interfacedata.formbean;

public class Request {
	private String method;
	private Para para;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Para getPara() {
		return para;
	}

	public void setPara(Para para) {
		this.para = para;
	}

	@Override
	public String toString() {
		return "Request [method=" + method + ", para=" + para + "]";
	}
	
	

}
