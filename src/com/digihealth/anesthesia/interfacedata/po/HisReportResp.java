package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Respone")
public class HisReportResp
{
    private String resultCode;
    private String resultMessage;
    private HisReport hisReport;
    public String getResultCode()
    {
        return resultCode;
    }
    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }
    public String getResultMessage()
    {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }
    
    @XmlElement(name = "List")
    public HisReport getHisReport()
    {
        return hisReport;
    }
    public void setHisReport(HisReport hisReport)
    {
        this.hisReport = hisReport;
    }
}
