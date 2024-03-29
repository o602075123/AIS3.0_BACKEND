package org.tempuri;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.1.4
 * 2017-11-09T13:47:51.241+08:00
 * Generated source version: 3.1.4
 * 
 */
@WebService(targetNamespace = "http://tempuri.org/", name = "AIMSServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface AIMSServiceSoap {

    /**
     * 4.患者计费  version = 1.0
     */
    @WebResult(name = "ChargeResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "Charge", targetNamespace = "http://tempuri.org/", className = "org.tempuri.Charge")
    @WebMethod(operationName = "Charge", action = "http://tempuri.org/Charge")
    @ResponseWrapper(localName = "ChargeResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.ChargeResponse")
    public java.lang.String charge(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 7.患者退费
     */
    @WebResult(name = "ReturnFeeResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ReturnFee", targetNamespace = "http://tempuri.org/", className = "org.tempuri.ReturnFee")
    @WebMethod(operationName = "ReturnFee", action = "http://tempuri.org/ReturnFee")
    @ResponseWrapper(localName = "ReturnFeeResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.ReturnFeeResponse")
    public java.lang.String returnFee(
        @WebParam(name = "inpuXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inpuXml
    );

    /**
     * 9.查询患者检验报告项目结果
     */
    @WebResult(name = "QueryPatientListResultResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "QueryPatientListResult", targetNamespace = "http://tempuri.org/", className = "org.tempuri.QueryPatientListResult")
    @WebMethod(operationName = "QueryPatientListResult", action = "http://tempuri.org/QueryPatientListResult")
    @ResponseWrapper(localName = "QueryPatientListResultResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.QueryPatientListResultResponse")
    public java.lang.String queryPatientListResult(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 6.创建急诊手术 version = 1.0
     */
    @WebResult(name = "CreateUrgentOperationResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CreateUrgentOperation", targetNamespace = "http://tempuri.org/", className = "org.tempuri.CreateUrgentOperation")
    @WebMethod(operationName = "CreateUrgentOperation", action = "http://tempuri.org/CreateUrgentOperation")
    @ResponseWrapper(localName = "CreateUrgentOperationResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.CreateUrgentOperationResponse")
    public java.lang.String createUrgentOperation(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 2.修改手术排台信息 version = 1.0
     */
    @WebResult(name = "UpdateOperationStageResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "UpdateOperationStage", targetNamespace = "http://tempuri.org/", className = "org.tempuri.UpdateOperationStage")
    @WebMethod(operationName = "UpdateOperationStage", action = "http://tempuri.org/UpdateOperationStage")
    @ResponseWrapper(localName = "UpdateOperationStageResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.UpdateOperationStageResponse")
    public java.lang.String updateOperationStage(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 8.查询患者检验报告
     */
    @WebResult(name = "QueryPatientLisReportResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "QueryPatientLisReport", targetNamespace = "http://tempuri.org/", className = "org.tempuri.QueryPatientLisReport")
    @WebMethod(operationName = "QueryPatientLisReport", action = "http://tempuri.org/QueryPatientLisReport")
    @ResponseWrapper(localName = "QueryPatientLisReportResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.QueryPatientLisReportResponse")
    public java.lang.String queryPatientLisReport(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 1.修改手术申请 version = 1.0
     */
    @WebResult(name = "UpdateOperationApplyResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "UpdateOperationApply", targetNamespace = "http://tempuri.org/", className = "org.tempuri.UpdateOperationApply")
    @WebMethod(operationName = "UpdateOperationApply", action = "http://tempuri.org/UpdateOperationApply")
    @ResponseWrapper(localName = "UpdateOperationApplyResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.UpdateOperationApplyResponse")
    public java.lang.String updateOperationApply(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 3.修改手术状态 version = 1.0
     */
    @WebResult(name = "UpdateOperationStateResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "UpdateOperationState", targetNamespace = "http://tempuri.org/", className = "org.tempuri.UpdateOperationState")
    @WebMethod(operationName = "UpdateOperationState", action = "http://tempuri.org/UpdateOperationState")
    @ResponseWrapper(localName = "UpdateOperationStateResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.UpdateOperationStateResponse")
    public java.lang.String updateOperationState(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );

    /**
     * 5.患者医嘱查询 version = 1.0
     */
    @WebResult(name = "QueryPatientOrderResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "QueryPatientOrder", targetNamespace = "http://tempuri.org/", className = "org.tempuri.QueryPatientOrder")
    @WebMethod(operationName = "QueryPatientOrder", action = "http://tempuri.org/QueryPatientOrder")
    @ResponseWrapper(localName = "QueryPatientOrderResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.QueryPatientOrderResponse")
    public java.lang.String queryPatientOrder(
        @WebParam(name = "inputXml", targetNamespace = "http://tempuri.org/")
        java.lang.String inputXml
    );
}
