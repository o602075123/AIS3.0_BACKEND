/*
 * DocAnaesPacuRec.java
 * Copyright(C) 2016 digihealth
 * All rights reserved.
 * ----------------------------------------
 * @author cy
 * 2017-03-30 Created
 */
package com.digihealth.anesthesia.doc.po;

import java.util.Date;
import java.util.List;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "复苏室记录单对象")
public class DocAnaesPacuRec {
    /**
     * 主键
     */
	@ApiModelProperty(value = "主键id")
    private String id;

	@ApiModelProperty(value = "患者id")
    private String regOptId;

    /**
     * PACU床位Id
     */
	@ApiModelProperty(value = "PACU床位Id")
    private String bedId;

    /**
     * PACU房间Id
     */
	@ApiModelProperty(value = "PACU房间Id")
    private Integer pacuRoomId;

    /**
     * 完成状态:END,NO_END
     */
	@ApiModelProperty(value = "完成状态:END,NO_END")
    private String processState;

    /**
     * 入室时间
     */
	@ApiModelProperty(value = "入室时间")
    private Date enterTime;

    /**
     * 出室时间
     */
	@ApiModelProperty(value = "出室时间")
    private Date exitTime;

    /**
     * 完成时间
     */
	@ApiModelProperty(value = "完成时间")
    private Date finishTime;

    /**
     * 出室去向 1=回病房 2=ICU 3=离院 4=死亡
     */
	@ApiModelProperty(value = "出室去向 1=回病房 2=ICU 3=离院 4=死亡")
    private Integer leaveTo;

    /**
     * 随身管道
     */
	@ApiModelProperty(value = "随身管道")
    private String portablePipe;
	
	@ApiModelProperty(value = "随身管道集合")
	private List<String> portablePipeList;

    /**
     * 麻醉方式
     */
	@ApiModelProperty(value = "麻醉方式")
    private String anaesType;

    /**
     * 随身物品，1，影像；2，衣服；3：裤子；4：被子；5：鞋子；6：药物
     */
	@ApiModelProperty(value = "随身物品，1，影像；2，衣服；3：裤子；4：被子；5：鞋子；6：药物")
    private String portableRes;
	
	@ApiModelProperty(value = "随身物品集合")
    private List<String> portableResList;

    /**
     * 其他随身物品
     */
	@ApiModelProperty(value = "其他随身物品")
    private String portableResOther;

    /**
     * steward评分得分
     */
	@ApiModelProperty(value = "steward评分得分")
    private Integer stewardScore;

    /**
     * 医生签名
     */
	@ApiModelProperty(value = "医生签名")
    private String docSign;

	@ApiModelProperty(value = "医生签名id")
	private String doctorId;
	
	@ApiModelProperty(value = "护士签名id")
	private String nurseId;
	
    /**
     * 护士签名
     */
	@ApiModelProperty(value = "护士签名")
    private String nurseSign;

    /**
     * 入室体温
     */
	@ApiModelProperty(value = "入室体温")
    private Float enterTemp;

    /**
     * 当前患者复苏状态：0，未开始；1，开始复苏；2，复苏完成
     */
	@ApiModelProperty(value = "当前患者复苏状态：0，未开始；1，开始复苏；2，复苏完成")
    private Integer anabioticState;

	@ApiModelProperty(value = "pacu编号")
	private String pacuNumber;
	
	@ApiModelProperty(value = "气管插管  1是 0否")
	private String trachealIntub;

	@ApiModelProperty(value = "气管导管  1已拔 0未拔")
	private String trachealCatheter;
	
	@ApiModelProperty(value = "意识  1未清醒 2会意 3睁眼 4哭啼")
	private String conscious;
	
	@ApiModelProperty(value = "上呼吸机时间")
    private Date ventilatorTime;
	
	@ApiModelProperty(value = "拔气管导管 1未拔")
    private String trachealCatheterExt;
	
	@ApiModelProperty(value = "拔气管导管时间")
    private Date trachealExtTime;
	
	@ApiModelProperty(value = "切口敷料  1干净 2渗血 3脱落")
	private String cutDressings;
	
	@ApiModelProperty(value = "吸痰 1有 0无")
	private String sputumSuction;
	
	@ApiModelProperty(value = "呕吐 1有 0无")
	private String vomit;
	
	@ApiModelProperty(value = "神志")
	private String sense;
	
	@ApiModelProperty(value = "静脉通畅 1通畅 0不通畅")
	private String venousPatency;
	
	@ApiModelProperty(value = "steward评分")
	private String stewardSco;
	
	
    public String getTrachealIntub() {
		return trachealIntub;
	}

	public void setTrachealIntub(String trachealIntub) {
		this.trachealIntub = trachealIntub;
	}

	public String getTrachealCatheter() {
		return trachealCatheter;
	}

	public void setTrachealCatheter(String trachealCatheter) {
		this.trachealCatheter = trachealCatheter;
	}

	public String getConscious() {
		return conscious;
	}

	public void setConscious(String conscious) {
		this.conscious = conscious;
	}

	public Date getVentilatorTime() {
		return ventilatorTime;
	}

	public void setVentilatorTime(Date ventilatorTime) {
		this.ventilatorTime = ventilatorTime;
	}

	public String getTrachealCatheterExt() {
		return trachealCatheterExt;
	}

	public void setTrachealCatheterExt(String trachealCatheterExt) {
		this.trachealCatheterExt = trachealCatheterExt;
	}

	public Date getTrachealExtTime() {
		return trachealExtTime;
	}

	public void setTrachealExtTime(Date trachealExtTime) {
		this.trachealExtTime = trachealExtTime;
	}

	public String getCutDressings() {
		return cutDressings;
	}

	public void setCutDressings(String cutDressings) {
		this.cutDressings = cutDressings;
	}

	public String getSputumSuction() {
		return sputumSuction;
	}

	public void setSputumSuction(String sputumSuction) {
		this.sputumSuction = sputumSuction;
	}

	public String getVomit() {
		return vomit;
	}

	public void setVomit(String vomit) {
		this.vomit = vomit;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public String getVenousPatency() {
		return venousPatency;
	}

	public void setVenousPatency(String venousPatency) {
		this.venousPatency = venousPatency;
	}

	public String getStewardSco() {
		return stewardSco;
	}

	public void setStewardSco(String stewardSco) {
		this.stewardSco = stewardSco;
	}

	public String getPacuNumber()
    {
        return pacuNumber;
    }

    public void setPacuNumber(String pacuNumber)
    {
        this.pacuNumber = pacuNumber;
    }

    public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getNurseId() {
		return nurseId;
	}

	public void setNurseId(String nurseId) {
		this.nurseId = nurseId;
	}

	public List<String> getPortablePipeList()
    {
        return portablePipeList;
    }

    public void setPortablePipeList(List<String> portablePipeList)
    {
        this.portablePipeList = portablePipeList;
    }

    public List<String> getPortableResList()
    {
        return portableResList;
    }

    public void setPortableResList(List<String> portableResList)
    {
        this.portableResList = portableResList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getRegOptId() {
        return regOptId;
    }

    public void setRegOptId(String regOptId) {
        this.regOptId = regOptId == null ? null : regOptId.trim();
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public Integer getPacuRoomId() {
        return pacuRoomId;
    }

    public void setPacuRoomId(Integer pacuRoomId) {
        this.pacuRoomId = pacuRoomId;
    }

    public String getProcessState() {
        return processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState == null ? null : processState.trim();
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getLeaveTo() {
        return leaveTo;
    }

    public void setLeaveTo(Integer leaveTo) {
        this.leaveTo = leaveTo;
    }

    public String getPortablePipe() {
        return portablePipe;
    }

    public void setPortablePipe(String portablePipe) {
        this.portablePipe = portablePipe == null ? null : portablePipe.trim();
    }

    public String getAnaesType() {
        return anaesType;
    }

    public void setAnaesType(String anaesType) {
        this.anaesType = anaesType == null ? null : anaesType.trim();
    }

    public String getPortableRes() {
        return portableRes;
    }

    public void setPortableRes(String portableRes) {
        this.portableRes = portableRes == null ? null : portableRes.trim();
    }

    public String getPortableResOther() {
        return portableResOther;
    }

    public void setPortableResOther(String portableResOther) {
        this.portableResOther = portableResOther == null ? null : portableResOther.trim();
    }

    public Integer getStewardScore() {
        return stewardScore;
    }

    public void setStewardScore(Integer stewardScore) {
        this.stewardScore = stewardScore;
    }

    public String getDocSign() {
        return docSign;
    }

    public void setDocSign(String docSign) {
        this.docSign = docSign == null ? null : docSign.trim();
    }

    public String getNurseSign() {
        return nurseSign;
    }

    public void setNurseSign(String nurseSign) {
        this.nurseSign = nurseSign == null ? null : nurseSign.trim();
    }

    public Float getEnterTemp() {
        return enterTemp;
    }

    public void setEnterTemp(Float enterTemp) {
        this.enterTemp = enterTemp;
    }

    public Integer getAnabioticState() {
        return anabioticState;
    }

    public void setAnabioticState(Integer anabioticState) {
        this.anabioticState = anabioticState;
    }
}