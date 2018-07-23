package com.digihealth.anesthesia.research.formbean;

import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "查询参数对象")
public class SearchAnesTypeFormBean {

	@ApiModelProperty(value = "麻醉类型")
	private String anesType;

	@ApiModelProperty(value = "avgTime")
	private Float avgTime;

	@ApiModelProperty(value = "总数量")
	private Integer totalNum;
	
	@ApiModelProperty(value = "总时长")
	private Float totalTime;
	
	@ApiModelProperty(value = "麻醉类型名称")
	private String anesTypeName;
	
	private List<SearchAnesDetailFormbean> anesDetailList;

	public String getAnesTypeName()
    {
        return anesTypeName;
    }

    public void setAnesTypeName(String anesTypeName)
    {
        this.anesTypeName = anesTypeName;
    }

    public List<SearchAnesDetailFormbean> getAnesDetailList()
    {
        return anesDetailList;
    }

    public void setAnesDetailList(List<SearchAnesDetailFormbean> anesDetailList)
    {
        this.anesDetailList = anesDetailList;
    }

    public Float getTotalTime()
    {
        return totalTime;
    }

    public void setTotalTime(Float totalTime)
    {
        this.totalTime = totalTime;
    }

    public String getAnesType() {
		return anesType;
	}

	public void setAnesType(String anesType) {
		this.anesType = anesType;
	}

	public Float getAvgTime() {
		return avgTime;
	}

	public void setAvgTime(Float avgTime) {
		this.avgTime = avgTime;
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

}
