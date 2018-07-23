package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
public class HisOptChargResponse
{
    private String code;
    private String message;
    
    private HisOptChargResult hisOptChargResult;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    @XmlElement(name = "resultList")
    public HisOptChargResult getHisOptChargResult()
    {
        return hisOptChargResult;
    }

    public void setHisOptChargResult(HisOptChargResult hisOptChargResult)
    {
        this.hisOptChargResult = hisOptChargResult;
    }
}
