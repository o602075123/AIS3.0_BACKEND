package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request")
public class HisReportDetailReq
{
    private String inspectId;

    public String getInspectId()
    {
        return inspectId;
    }

    public void setInspectId(String inspectId)
    {
        this.inspectId = inspectId;
    }
}
