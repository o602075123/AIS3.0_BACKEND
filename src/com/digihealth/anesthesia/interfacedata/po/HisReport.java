package com.digihealth.anesthesia.interfacedata.po;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class HisReport
{
    private List<Report> report;

    @XmlElement(name = "Report")
    public List<Report> getReport()
    {
        return report;
    }

    public void setReport(List<Report> report)
    {
        this.report = report;
    }
}
