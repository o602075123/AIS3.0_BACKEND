package com.digihealth.anesthesia.interfacedata.server;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.digihealth.anesthesia.basedata.dao.BasRegOptDao;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.interfacedata.formbean.HisGetOperationResponse;
import com.digihealth.anesthesia.interfacedata.formbean.HisOperationFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.OperList;
import com.digihealth.anesthesia.interfacedata.formbean.RequestFormBean;
import com.digihealth.anesthesia.sysMng.dao.BasUserDao;
import com.digihealth.anesthesia.sysMng.po.BasUser;

@WebService(endpointInterface = "com.digihealth.anesthesia.interfacedata.server.HelloService")
public class HelloServiceImpl implements HelloService {

	@Autowired
	private BasUserDao basUserDao;
	@Autowired
	private BasRegOptDao basRegOptDao;

	public BasUser hello(String name) {
		BasUser user = basUserDao.selectByPrimaryKey(name, "102");
		return user;
	}

	public String sayHi(String name) {
		return "hi,hi";
	}

	public String hisToDigi(BasUser user) {
		return "hisToDigi," + user.getName();
	}

	@Override
	public HisGetOperationResponse testReq(RequestFormBean request) {
		String syxh = request.getSyxh();
		String kssj = request.getKssj();
		String jssj = request.getJssj();
		System.out.println("testReq:syxh=="+syxh+",kssj=="+kssj);
		HisGetOperationResponse response = new HisGetOperationResponse();
		response.setResultCode("0");
		response.setResultMessage("成功！");
		OperList operList = new OperList();
		List<HisOperationFormBean> item = new ArrayList<HisOperationFormBean>();
		BasRegOpt regOpt = basRegOptDao.searchRegOptById("201709270855037099");
		BasRegOpt regOpt1 = basRegOptDao.searchRegOptById("201709270958430351");
		BasRegOpt regOpt2 = basRegOptDao.searchRegOptById("201709270959450352");
		HisOperationFormBean ho = new HisOperationFormBean();
		HisOperationFormBean ho1 = new HisOperationFormBean();
		HisOperationFormBean ho2 = new HisOperationFormBean();
		try {
			BeanUtils.copyProperties(ho, regOpt);
			BeanUtils.copyProperties(ho1, regOpt1);
			BeanUtils.copyProperties(ho2, regOpt2);
		} catch (Exception e) {
			System.out.println("logger:errror"+e.getMessage());
		}
		item.add(ho);
		item.add(ho1);
		item.add(ho2);
		operList.setItem(item );
		response.setOperList(operList );
		return response;
	}
}
