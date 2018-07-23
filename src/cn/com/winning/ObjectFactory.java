
package cn.com.winning;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the cn.com.winning package. 
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

    private final static QName _String_QNAME = new QName("http://www.winning.com.cn/", "string");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cn.com.winning
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HelloWorld }
     * 
     */
    public HelloWorld createHelloWorld() {
        return new HelloWorld();
    }

    /**
     * Create an instance of {@link HelloWorldResponse }
     * 
     */
    public HelloWorldResponse createHelloWorldResponse() {
        return new HelloWorldResponse();
    }

    /**
     * Create an instance of {@link UpdateZt }
     * 
     */
    public UpdateZt createUpdateZt() {
        return new UpdateZt();
    }

    /**
     * Create an instance of {@link UpdateZtResponse }
     * 
     */
    public UpdateZtResponse createUpdateZtResponse() {
        return new UpdateZtResponse();
    }

    /**
     * Create an instance of {@link UpdateYs }
     * 
     */
    public UpdateYs createUpdateYs() {
        return new UpdateYs();
    }

    /**
     * Create an instance of {@link UpdateYsResponse }
     * 
     */
    public UpdateYsResponse createUpdateYsResponse() {
        return new UpdateYsResponse();
    }

    /**
     * Create an instance of {@link GetPatientOrder }
     * 
     */
    public GetPatientOrder createGetPatientOrder() {
        return new GetPatientOrder();
    }

    /**
     * Create an instance of {@link GetPatientOrderResponse }
     * 
     */
    public GetPatientOrderResponse createGetPatientOrderResponse() {
        return new GetPatientOrderResponse();
    }

    /**
     * Create an instance of {@link GetIcuCount }
     * 
     */
    public GetIcuCount createGetIcuCount() {
        return new GetIcuCount();
    }

    /**
     * Create an instance of {@link GetIcuCountResponse }
     * 
     */
    public GetIcuCountResponse createGetIcuCountResponse() {
        return new GetIcuCountResponse();
    }

    /**
     * Create an instance of {@link GetIcuZcrs }
     * 
     */
    public GetIcuZcrs createGetIcuZcrs() {
        return new GetIcuZcrs();
    }

    /**
     * Create an instance of {@link GetIcuZcrsResponse }
     * 
     */
    public GetIcuZcrsResponse createGetIcuZcrsResponse() {
        return new GetIcuZcrsResponse();
    }

    /**
     * Create an instance of {@link GetIcuHzxq }
     * 
     */
    public GetIcuHzxq createGetIcuHzxq() {
        return new GetIcuHzxq();
    }

    /**
     * Create an instance of {@link GetIcuHzxqResponse }
     * 
     */
    public GetIcuHzxqResponse createGetIcuHzxqResponse() {
        return new GetIcuHzxqResponse();
    }

    /**
     * Create an instance of {@link GetIcuHzxxCx }
     * 
     */
    public GetIcuHzxxCx createGetIcuHzxxCx() {
        return new GetIcuHzxxCx();
    }

    /**
     * Create an instance of {@link GetIcuHzxxCxResponse }
     * 
     */
    public GetIcuHzxxCxResponse createGetIcuHzxxCxResponse() {
        return new GetIcuHzxxCxResponse();
    }

    /**
     * Create an instance of {@link GetIcuZcxx }
     * 
     */
    public GetIcuZcxx createGetIcuZcxx() {
        return new GetIcuZcxx();
    }

    /**
     * Create an instance of {@link GetIcuZcxxResponse }
     * 
     */
    public GetIcuZcxxResponse createGetIcuZcxxResponse() {
        return new GetIcuZcxxResponse();
    }

    /**
     * Create an instance of {@link GetIcuZkxx }
     * 
     */
    public GetIcuZkxx createGetIcuZkxx() {
        return new GetIcuZkxx();
    }

    /**
     * Create an instance of {@link GetIcuZkxxResponse }
     * 
     */
    public GetIcuZkxxResponse createGetIcuZkxxResponse() {
        return new GetIcuZkxxResponse();
    }

    /**
     * Create an instance of {@link GetIcuYzxx }
     * 
     */
    public GetIcuYzxx createGetIcuYzxx() {
        return new GetIcuYzxx();
    }

    /**
     * Create an instance of {@link GetIcuYzxxResponse }
     * 
     */
    public GetIcuYzxxResponse createGetIcuYzxxResponse() {
        return new GetIcuYzxxResponse();
    }

    /**
     * Create an instance of {@link GetIcuYzzxxx }
     * 
     */
    public GetIcuYzzxxx createGetIcuYzzxxx() {
        return new GetIcuYzzxxx();
    }

    /**
     * Create an instance of {@link GetIcuYzzxxxResponse }
     * 
     */
    public GetIcuYzzxxxResponse createGetIcuYzzxxxResponse() {
        return new GetIcuYzzxxxResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.winning.com.cn/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

}
