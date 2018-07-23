package com.digihealth.anesthesia.interfacedata.po;

import javax.xml.bind.annotation.XmlRootElement;

public class Report
{
    private String hid;
    private String inspectId;
    private String inspectName;
    private String inspectTime;
    private String inspectStatus;
    private String reportTime;
    private String examTime;
    private String deptCode;
    private String deptName;
    private String doctorCode;
    private String doctorName;
    private String exeDeptCode;
    private String exeDeptName;
    private String reporter;
    private String auditor;
    public String getHid()
    {
        return hid;
    }
    public void setHid(String hid)
    {
        this.hid = hid;
    }
    public String getInspectId()
    {
        return inspectId;
    }
    public void setInspectId(String inspectId)
    {
        this.inspectId = inspectId;
    }
    public String getInspectName()
    {
        return inspectName;
    }
    public void setInspectName(String inspectName)
    {
        this.inspectName = inspectName;
    }
    public String getInspectTime()
    {
        return inspectTime;
    }
    public void setInspectTime(String inspectTime)
    {
        this.inspectTime = inspectTime;
    }
    public String getInspectStatus()
    {
        return inspectStatus;
    }
    public void setInspectStatus(String inspectStatus)
    {
        this.inspectStatus = inspectStatus;
    }
    public String getReportTime()
    {
        return reportTime;
    }
    public void setReportTime(String reportTime)
    {
        this.reportTime = reportTime;
    }
    public String getExamTime()
    {
        return examTime;
    }
    public void setExamTime(String examTime)
    {
        this.examTime = examTime;
    }
    public String getDeptCode()
    {
        return deptCode;
    }
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    public String getDeptName()
    {
        return deptName;
    }
    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }
    public String getDoctorCode()
    {
        return doctorCode;
    }
    public void setDoctorCode(String doctorCode)
    {
        this.doctorCode = doctorCode;
    }
    public String getDoctorName()
    {
        return doctorName;
    }
    public void setDoctorName(String doctorName)
    {
        this.doctorName = doctorName;
    }
    public String getExeDeptCode()
    {
        return exeDeptCode;
    }
    public void setExeDeptCode(String exeDeptCode)
    {
        this.exeDeptCode = exeDeptCode;
    }
    public String getExeDeptName()
    {
        return exeDeptName;
    }
    public void setExeDeptName(String exeDeptName)
    {
        this.exeDeptName = exeDeptName;
    }
    public String getReporter()
    {
        return reporter;
    }
    public void setReporter(String reporter)
    {
        this.reporter = reporter;
    }
    public String getAuditor()
    {
        return auditor;
    }
    public void setAuditor(String auditor)
    {
        this.auditor = auditor;
    }
}
