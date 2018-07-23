package com.digihealth.anesthesia.interfacedata.formbean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request")
public class HisGetOperationFormBean {
	private String syxh; //默认传0
	private String kssj; //开始时间
	private String jssj; //结束时间
	
	/*
	 * <Request> <syxh>0</syxh> <kssj>20170905</kssj> <jssj></jssj> </Request>
	 */

	public String getSyxh() {
		return syxh;
	}

	public void setSyxh(String syxh) {
		this.syxh = syxh;
	}

	public String getKssj() {
		return kssj;
	}

	public void setKssj(String kssj) {
		this.kssj = kssj;
	}

	public String getJssj() {
		return jssj;
	}

	public void setJssj(String jssj) {
		this.jssj = jssj;
	}

}
