package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request")
public class HisReportReq
{
    private String operSource;  //0：住院 1：门诊
    private String hid;  //患者流水号
    public String getOperSource()
    {
        return operSource;
    }
    public void setOperSource(String operSource)
    {
        this.operSource = operSource;
    }
    
    @XmlElement(name = "InpatientNo")
    public String getHid()
    {
        return hid;
    }
    public void setHid(String hid)
    {
        this.hid = hid;
    }
   
}
