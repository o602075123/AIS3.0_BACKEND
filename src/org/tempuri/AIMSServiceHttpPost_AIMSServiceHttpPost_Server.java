
package org.tempuri;

import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 3.1.4
 * 2017-11-09T13:47:51.301+08:00
 * Generated source version: 3.1.4
 * 
 */
 
public class AIMSServiceHttpPost_AIMSServiceHttpPost_Server{

    protected AIMSServiceHttpPost_AIMSServiceHttpPost_Server() throws java.lang.Exception {
        System.out.println("Starting Server");
        Object implementor = new AIMSServiceHttpPostImpl();
        String address = "http://192.168.1.95:8088/AIMSService.asmx";
        Endpoint.publish(address, implementor);
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new AIMSServiceHttpPost_AIMSServiceHttpPost_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
