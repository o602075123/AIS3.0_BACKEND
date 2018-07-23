package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request")
public class HisOptChargeRequest
{
    private String hid; //住院号
    private String operSource; //患者类别
    private String recipeDept;  //开单科室
    private String recipeDoctorCode; //开单医生编码
    private String execDept; //执行科室编码（药品为对应药房）
    private String chargeOperCode; //计费人编码
    private String userID;
    
    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    private HisChargItem hisChargItem;

    @XmlElement(name = "InpatientNo")
    public String getHid()
    {
        return hid;
    }

    public void setHid(String hid)
    {
        this.hid = hid;
    }

    public String getOperSource()
    {
        return operSource;
    }

    public void setOperSource(String operSource)
    {
        this.operSource = operSource;
    }

    public String getRecipeDept()
    {
        return recipeDept;
    }

    public void setRecipeDept(String recipeDept)
    {
        this.recipeDept = recipeDept;
    }

    public String getRecipeDoctorCode()
    {
        return recipeDoctorCode;
    }

    public void setRecipeDoctorCode(String recipeDoctorCode)
    {
        this.recipeDoctorCode = recipeDoctorCode;
    }

    public String getExecDept()
    {
        return execDept;
    }

    public void setExecDept(String execDept)
    {
        this.execDept = execDept;
    }
    @XmlElement(name = "ChargeOperCode")
    public String getChargeOperCode()
    {
        return chargeOperCode;
    }

    public void setChargeOperCode(String chargeOperCode)
    {
        this.chargeOperCode = chargeOperCode;
    }
    @XmlElement(name = "itemList")
    public HisChargItem getHisChargItem()
    {
        return hisChargItem;
    }

    public void setHisChargItem(HisChargItem hisChargItem)
    {
        this.hisChargItem = hisChargItem;
    }
}
