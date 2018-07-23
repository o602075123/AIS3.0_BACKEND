package com.digihealth.anesthesia.doc.formbean;

public class AnalgesicPumpFormbean
{
    private String medId;
    private String name;
    private String unit;
    private Float dosage;
    public String getMedId()
    {
        return medId;
    }
    public void setMedId(String medId)
    {
        this.medId = medId;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getUnit()
    {
        return unit;
    }
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    public Float getDosage()
    {
        return dosage;
    }
    public void setDosage(Float dosage)
    {
        this.dosage = dosage;
    }
}
