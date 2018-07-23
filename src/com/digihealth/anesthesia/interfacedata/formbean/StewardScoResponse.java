package com.digihealth.anesthesia.interfacedata.formbean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Response")
public class StewardScoResponse
{
    private String resultCode;
    private String resultMessage;
    private StewardScoData datas;
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
    public StewardScoData getDatas()
    {
        return datas;
    }
    public void setDatas(StewardScoData datas)
    {
        this.datas = datas;
    }
}
