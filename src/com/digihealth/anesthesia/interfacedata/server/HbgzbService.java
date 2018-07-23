package com.digihealth.anesthesia.interfacedata.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.formbean.Request;
import com.digihealth.anesthesia.interfacedata.formbean.Response;
import com.digihealth.anesthesia.interfacedata.po.OperList;

@WebService
public interface HbgzbService {

	@WebMethod
	public @WebResult(name = "response")
	HisResponse getHisOperateNotice(@WebParam(name = "request") OperList request);
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@WebMethod
	public @WebResult(name="response")Response process(@WebParam(name = "request") Request request);
	
	
	
}
