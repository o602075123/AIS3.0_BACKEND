package com.digihealth.anesthesia.interfacedata.po;

public class ReportItem
{
    private String inspectId;
    private String itemName;
    private String result;
    private String refRange;
    private String abnormal;
    private String unit;
    public String getInspectId()
    {
        return inspectId;
    }
    public void setInspectId(String inspectId)
    {
        this.inspectId = inspectId;
    }
    public String getItemName()
    {
        return itemName;
    }
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }
    public String getResult()
    {
        return result;
    }
    public void setResult(String result)
    {
        this.result = result;
    }
    public String getRefRange()
    {
        return refRange;
    }
    public void setRefRange(String refRange)
    {
        this.refRange = refRange;
    }
    public String getAbnormal()
    {
        return abnormal;
    }
    public void setAbnormal(String abnormal)
    {
        this.abnormal = abnormal;
    }
    public String getUnit()
    {
        return unit;
    }
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
}
