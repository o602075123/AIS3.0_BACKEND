package com.digihealth.anesthesia.interfacedata.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.digihealth.anesthesia.interfacedata.formbean.HisCancleOptFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.po.OperList;

@WebService
public interface GzqnzService
{
    @WebMethod
    public @WebResult(name = "response")
    String cancleRegOpt(@WebParam(name = "request")String request);
    
    @WebMethod
    public @WebResult(name = "response")
    String getHisOperateNotice(@WebParam(name = "request")String request);
    
    @WebMethod
    public @WebResult(name = "response")
    String searchStewardSco(@WebParam(name = "request")String request);
}
