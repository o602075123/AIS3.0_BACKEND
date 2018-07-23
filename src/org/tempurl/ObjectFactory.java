
package org.tempurl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tempurl package. 
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

    private final static QName _String_QNAME = new QName("http://tempurl.org", "string");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempurl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OpreationArrangement }
     * 
     */
    public OpreationArrangement createOpreationArrangement() {
        return new OpreationArrangement();
    }

    /**
     * Create an instance of {@link OpreationArrangementResponse }
     * 
     */
    public OpreationArrangementResponse createOpreationArrangementResponse() {
        return new OpreationArrangementResponse();
    }

    /**
     * Create an instance of {@link OpreationModify }
     * 
     */
    public OpreationModify createOpreationModify() {
        return new OpreationModify();
    }

    /**
     * Create an instance of {@link OpreationModifyResponse }
     * 
     */
    public OpreationModifyResponse createOpreationModifyResponse() {
        return new OpreationModifyResponse();
    }

    /**
     * Create an instance of {@link OperationInser }
     * 
     */
    public OperationInser createOperationInser() {
        return new OperationInser();
    }

    /**
     * Create an instance of {@link OperationInserResponse }
     * 
     */
    public OperationInserResponse createOperationInserResponse() {
        return new OperationInserResponse();
    }

    /**
     * Create an instance of {@link OperationBlood }
     * 
     */
    public OperationBlood createOperationBlood() {
        return new OperationBlood();
    }

    /**
     * Create an instance of {@link OperationBloodResponse }
     * 
     */
    public OperationBloodResponse createOperationBloodResponse() {
        return new OperationBloodResponse();
    }

    /**
     * Create an instance of {@link OperationInseryp }
     * 
     */
    public OperationInseryp createOperationInseryp() {
        return new OperationInseryp();
    }

    /**
     * Create an instance of {@link OperationInserypResponse }
     * 
     */
    public OperationInserypResponse createOperationInserypResponse() {
        return new OperationInserypResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempurl.org", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

}
