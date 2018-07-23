
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tempuri package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _String_QNAME = new QName("http://tempuri.org/", "string");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateOperationApply }
     * 
     */
    public UpdateOperationApply createUpdateOperationApply() {
        return new UpdateOperationApply();
    }

    /**
     * Create an instance of {@link UpdateOperationApplyResponse }
     * 
     */
    public UpdateOperationApplyResponse createUpdateOperationApplyResponse() {
        return new UpdateOperationApplyResponse();
    }

    /**
     * Create an instance of {@link UpdateOperationStage }
     * 
     */
    public UpdateOperationStage createUpdateOperationStage() {
        return new UpdateOperationStage();
    }

    /**
     * Create an instance of {@link UpdateOperationStageResponse }
     * 
     */
    public UpdateOperationStageResponse createUpdateOperationStageResponse() {
        return new UpdateOperationStageResponse();
    }

    /**
     * Create an instance of {@link UpdateOperationState }
     * 
     */
    public UpdateOperationState createUpdateOperationState() {
        return new UpdateOperationState();
    }

    /**
     * Create an instance of {@link UpdateOperationStateResponse }
     * 
     */
    public UpdateOperationStateResponse createUpdateOperationStateResponse() {
        return new UpdateOperationStateResponse();
    }

    /**
     * Create an instance of {@link Charge }
     * 
     */
    public Charge createCharge() {
        return new Charge();
    }

    /**
     * Create an instance of {@link ChargeResponse }
     * 
     */
    public ChargeResponse createChargeResponse() {
        return new ChargeResponse();
    }

    /**
     * Create an instance of {@link QueryPatientOrder }
     * 
     */
    public QueryPatientOrder createQueryPatientOrder() {
        return new QueryPatientOrder();
    }

    /**
     * Create an instance of {@link QueryPatientOrderResponse }
     * 
     */
    public QueryPatientOrderResponse createQueryPatientOrderResponse() {
        return new QueryPatientOrderResponse();
    }

    /**
     * Create an instance of {@link ReturnFee }
     * 
     */
    public ReturnFee createReturnFee() {
        return new ReturnFee();
    }

    /**
     * Create an instance of {@link ReturnFeeResponse }
     * 
     */
    public ReturnFeeResponse createReturnFeeResponse() {
        return new ReturnFeeResponse();
    }

    /**
     * Create an instance of {@link CreateUrgentOperation }
     * 
     */
    public CreateUrgentOperation createCreateUrgentOperation() {
        return new CreateUrgentOperation();
    }

    /**
     * Create an instance of {@link CreateUrgentOperationResponse }
     * 
     */
    public CreateUrgentOperationResponse createCreateUrgentOperationResponse() {
        return new CreateUrgentOperationResponse();
    }

    /**
     * Create an instance of {@link QueryPatientLisReport }
     * 
     */
    public QueryPatientLisReport createQueryPatientLisReport() {
        return new QueryPatientLisReport();
    }

    /**
     * Create an instance of {@link QueryPatientLisReportResponse }
     * 
     */
    public QueryPatientLisReportResponse createQueryPatientLisReportResponse() {
        return new QueryPatientLisReportResponse();
    }

    /**
     * Create an instance of {@link QueryPatientListResult }
     * 
     */
    public QueryPatientListResult createQueryPatientListResult() {
        return new QueryPatientListResult();
    }

    /**
     * Create an instance of {@link QueryPatientListResultResponse }
     * 
     */
    public QueryPatientListResultResponse createQueryPatientListResultResponse() {
        return new QueryPatientListResultResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

}
