package com.digihealth.anesthesia.interfacedata.formbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.digihealth.anesthesia.interfacedata.po.OperCostList;

@XmlRootElement(name = "request")
public class HisOptcostFormBean implements Serializable{
	
	private String reservenumber;//预约ID
	private String costType;//费用类别 1麻醉科收费清单 2手术核算单
	private String createUser;//记账人
	private String regDate;//记账日期
	private OperCostList operCostSum;
	
	public String getReservenumber() {
		return reservenumber;
	}
	public void setReservenumber(String reservenumber) {
		this.reservenumber = reservenumber;
	}
	public String getCostType() {
		return costType;
	}
	public void setCostType(String costType) {
		this.costType = costType;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
    public OperCostList getOperCostSum()
    {
        return operCostSum;
    }
    public void setOperCostSum(OperCostList operCostSum)
    {
        this.operCostSum = operCostSum;
    }


}
