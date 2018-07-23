package com.digihealth.anesthesia.interfacedata.server;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.digihealth.anesthesia.interfacedata.formbean.HisGetOperationResponse;
import com.digihealth.anesthesia.interfacedata.formbean.RequestFormBean;
import com.digihealth.anesthesia.sysMng.po.BasUser;

@WebService
public interface HelloService {
	public BasUser hello(@WebParam(name = "name")String name);
	
	public String sayHi(@WebParam(name="name") String name);
	
	public String hisToDigi(BasUser user);
	
	public @WebResult(name = "response") HisGetOperationResponse testReq(@WebParam(name = "request")RequestFormBean request);
}
