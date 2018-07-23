package com.digihealth.anesthesia.interfacedata.formbean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
public class HisGetOperationResponse {
	private String resultCode; //
	private String resultMessage;
	private OperList operList;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

    public OperList getOperList()
    {
        return operList;
    }

    public void setOperList(OperList operList)
    {
        this.operList = operList;
    }


}
