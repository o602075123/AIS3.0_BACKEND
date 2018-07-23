package org.tempurl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.1.8
 * 2017-04-21T09:33:07.220+08:00
 * Generated source version: 3.1.8
 * 
 */
@WebService(targetNamespace = "http://tempurl.org", name = "n_smxtjkSoap")
@XmlSeeAlso({ObjectFactory.class})
public interface NSmxtjkSoap {

    @WebMethod(operationName = "opreation_arrangement", action = "http://tempurl.org/opreation_arrangement")
    @RequestWrapper(localName = "opreation_arrangement", targetNamespace = "http://tempurl.org", className = "org.tempurl.OpreationArrangement")
    @ResponseWrapper(localName = "opreation_arrangementResponse", targetNamespace = "http://tempurl.org", className = "org.tempurl.OpreationArrangementResponse")
    @WebResult(name = "opreation_arrangementResult", targetNamespace = "http://tempurl.org")
    public java.lang.String opreationArrangement(
        @WebParam(name = "as_xml", targetNamespace = "http://tempurl.org")
        java.lang.String asXml
    );

    @WebMethod(operationName = "operation_blood", action = "http://tempurl.org/operation_blood")
    @RequestWrapper(localName = "operation_blood", targetNamespace = "http://tempurl.org", className = "org.tempurl.OperationBlood")
    @ResponseWrapper(localName = "operation_bloodResponse", targetNamespace = "http://tempurl.org", className = "org.tempurl.OperationBloodResponse")
    @WebResult(name = "operation_bloodResult", targetNamespace = "http://tempurl.org")
    public java.lang.String operationBlood(
        @WebParam(name = "as_xml", targetNamespace = "http://tempurl.org")
        java.lang.String asXml
    );

    @WebMethod(operationName = "operation_inseryp", action = "http://tempurl.org/operation_inseryp")
    @RequestWrapper(localName = "operation_inseryp", targetNamespace = "http://tempurl.org", className = "org.tempurl.OperationInseryp")
    @ResponseWrapper(localName = "operation_inserypResponse", targetNamespace = "http://tempurl.org", className = "org.tempurl.OperationInserypResponse")
    @WebResult(name = "operation_inserypResult", targetNamespace = "http://tempurl.org")
    public java.lang.String operationInseryp(
        @WebParam(name = "as_xml", targetNamespace = "http://tempurl.org")
        java.lang.String asXml
    );

    @WebMethod(operationName = "opreation_modify", action = "http://tempurl.org/opreation_modify")
    @RequestWrapper(localName = "opreation_modify", targetNamespace = "http://tempurl.org", className = "org.tempurl.OpreationModify")
    @ResponseWrapper(localName = "opreation_modifyResponse", targetNamespace = "http://tempurl.org", className = "org.tempurl.OpreationModifyResponse")
    @WebResult(name = "opreation_modifyResult", targetNamespace = "http://tempurl.org")
    public java.lang.String opreationModify(
        @WebParam(name = "as_xml", targetNamespace = "http://tempurl.org")
        java.lang.String asXml
    );

    @WebMethod(operationName = "operation_inser", action = "http://tempurl.org/operation_inser")
    @RequestWrapper(localName = "operation_inser", targetNamespace = "http://tempurl.org", className = "org.tempurl.OperationInser")
    @ResponseWrapper(localName = "operation_inserResponse", targetNamespace = "http://tempurl.org", className = "org.tempurl.OperationInserResponse")
    @WebResult(name = "operation_inserResult", targetNamespace = "http://tempurl.org")
    public java.lang.String operationInser(
        @WebParam(name = "as_xml", targetNamespace = "http://tempurl.org")
        java.lang.String asXml
    );
}
