
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.tempuri;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.4
 * 2017-11-09T13:47:51.201+08:00
 * Generated source version: 3.1.4
 * 
 */

@javax.jws.WebService(
                      serviceName = "AIMSService",
                      portName = "AIMSServiceHttpGet",
                      targetNamespace = "http://tempuri.org/",
                      wsdlLocation = "http://192.168.1.95:8088/AIMSService.asmx?wsdl",
                      endpointInterface = "org.tempuri.AIMSServiceHttpGet")
                      
public class AIMSServiceHttpGetImpl implements AIMSServiceHttpGet {

    private static final Logger LOG = Logger.getLogger(AIMSServiceHttpGetImpl.class.getName());

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#charge(java.lang.String  inputXml )*
     */
    public java.lang.String charge(java.lang.String inputXml) { 
        LOG.info("Executing operation charge");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#returnFee(java.lang.String  inpuXml )*
     */
    public java.lang.String returnFee(java.lang.String inpuXml) { 
        LOG.info("Executing operation returnFee");
        System.out.println(inpuXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#queryPatientListResult(java.lang.String  inputXml )*
     */
    public java.lang.String queryPatientListResult(java.lang.String inputXml) { 
        LOG.info("Executing operation queryPatientListResult");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#createUrgentOperation(java.lang.String  inputXml )*
     */
    public java.lang.String createUrgentOperation(java.lang.String inputXml) { 
        LOG.info("Executing operation createUrgentOperation");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#updateOperationStage(java.lang.String  inputXml )*
     */
    public java.lang.String updateOperationStage(java.lang.String inputXml) { 
        LOG.info("Executing operation updateOperationStage");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#queryPatientLisReport(java.lang.String  inputXml )*
     */
    public java.lang.String queryPatientLisReport(java.lang.String inputXml) { 
        LOG.info("Executing operation queryPatientLisReport");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#updateOperationApply(java.lang.String  inputXml )*
     */
    public java.lang.String updateOperationApply(java.lang.String inputXml) { 
        LOG.info("Executing operation updateOperationApply");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#updateOperationState(java.lang.String  inputXml )*
     */
    public java.lang.String updateOperationState(java.lang.String inputXml) { 
        LOG.info("Executing operation updateOperationState");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tempuri.AIMSServiceHttpGet#queryPatientOrder(java.lang.String  inputXml )*
     */
    public java.lang.String queryPatientOrder(java.lang.String inputXml) { 
        LOG.info("Executing operation queryPatientOrder");
        System.out.println(inputXml);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}