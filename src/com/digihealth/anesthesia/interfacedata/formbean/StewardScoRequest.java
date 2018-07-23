package com.digihealth.anesthesia.interfacedata.formbean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request")
public class StewardScoRequest
{
    private String startTime;
    private String endTime;
    public String getStartTime()
    {
        return startTime;
    }
    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }
    public String getEndTime()
    {
        return endTime;
    }
    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }
    
}
