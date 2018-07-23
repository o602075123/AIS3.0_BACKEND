package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
public class HisReportDetailResp
{
    private String resultCode;
    private String resultMessage;
    private HisReportItem hisReportItem;
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
    public HisReportItem getHisReportItem()
    {
        return hisReportItem;
    }
    public void setHisReportItem(HisReportItem hisReportItem)
    {
        this.hisReportItem = hisReportItem;
    }
}
