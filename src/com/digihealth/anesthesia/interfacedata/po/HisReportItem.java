package com.digihealth.anesthesia.interfacedata.po;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class HisReportItem
{
    private List<ReportItem> list;

    @XmlElement(name = "Item")
    public List<ReportItem> getList()
    {
        return list;
    }

    public void setList(List<ReportItem> list)
    {
        this.list = list;
    }
}
