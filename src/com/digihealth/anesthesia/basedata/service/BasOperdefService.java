/**
 * Copyright &copy; 2012-2013 <a href="httparamMap://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.digihealth.anesthesia.basedata.service;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.formbean.BaseInfoQuery;
import com.digihealth.anesthesia.basedata.formbean.OperDefFormBean;
import com.digihealth.anesthesia.basedata.formbean.SystemSearchFormBean;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.PingYinUtil;
import com.digihealth.anesthesia.evt.formbean.Filter;

/**
 * 
     * Title: OperdefService.java    
     * Description: 手术名称Service
     * @author liukui       
     * @created 2015-10-7 下午6:00:54
 */
@Service
public class BasOperdefService extends BaseService {
	
	
	public List<OperDefFormBean> findList(BaseInfoQuery baseQuery) {
	    if (StringUtils.isEmpty(baseQuery.getBeid()))
        {
	        baseQuery.setBeid(getBeid());
        }
		return basOperdefDao.findList(baseQuery == null?new BaseInfoQuery():baseQuery);
	}
	
	
	
	public BasOperdef queryOperdefById(String operdefId) {
		return basOperdefDao.queryOperdefById(operdefId);
	}
	
	
	public List<BasOperdef> queryOperdefList(SystemSearchFormBean systemSearchFormBean){
		BasOperdef params = new BasOperdef();
		params.setBeid("110");
		List<BasOperdef> list = basOperdefDao.selectEntityList(params);
		for (BasOperdef basOperdef : list) {
			String sql = "insert into `bas_operdef` (`operdefId`, `code`, `name`, `pinYin`, `enable`, `beid`) values(";
			sql += "'" + GenerateSequenceUtil.generateSequenceNo() + "','" + basOperdef.getCode() + "','" + basOperdef.getName() + "','" + basOperdef.getPinYin() + "','" + basOperdef.getEnable() + "','110');";
			System.out.println(sql);
		}
		
	    if (StringUtils.isEmpty(systemSearchFormBean.getBeid()))
        {
	        systemSearchFormBean.setBeid(getBeid());
        }
		if(StringUtils.isEmpty(systemSearchFormBean.getSort())){
			systemSearchFormBean.setSort("operdefId");
		}
		if(StringUtils.isEmpty(systemSearchFormBean.getOrderBy())){
			systemSearchFormBean.setOrderBy("ASC");
		}
		String filter = "";
		List<Filter> filters = systemSearchFormBean.getFilters();
		if(filters!=null&&filters.size()>0){
			for(int i = 0;i<filters.size();i++){
				if(!StringUtils.isEmpty(filters.get(i).getValue().toString())){
					filter = filter + " AND "+filters.get(i).getField() +" like '%"+filters.get(i).getValue()+"%' ";
				}
			}
		}
		return basOperdefDao.queryOperdefList(filter, systemSearchFormBean);
	}
	
	public int queryOperdefListTotal(SystemSearchFormBean systemSearchFormBean){
	    if (StringUtils.isEmpty(systemSearchFormBean.getBeid()))
        {
            systemSearchFormBean.setBeid(getBeid());
        }
		if(StringUtils.isEmpty(systemSearchFormBean.getSort())){
			systemSearchFormBean.setSort("operdefId");
		}
		if(StringUtils.isEmpty(systemSearchFormBean.getOrderBy())){
			systemSearchFormBean.setOrderBy("ASC");
		}
		String filter = "";
		List<Filter> filters = systemSearchFormBean.getFilters();
		if(filters!=null&&filters.size()>0){
			for(int i = 0;i<filters.size();i++){
				if(!StringUtils.isEmpty(filters.get(i).getValue().toString())){
					filter = filter + " AND "+filters.get(i).getField() +" like '%"+filters.get(i).getValue()+"%' ";
				}
			}
		}
		return basOperdefDao.queryOperdefListTotal(filter, systemSearchFormBean);
	}
	
	/**
	 * 保存手术名称
	 * @param operdef
	 */
	@Transactional
	public String saveOperdef(BasOperdef operdef){
	    if (StringUtils.isEmpty(operdef.getBeid()))
        {
	        operdef.setBeid(getBeid());
        }
		if(StringUtils.isNotBlank(operdef.getOperdefId())){
			basOperdefDao.update(operdef);
		}else{
			if(StringUtils.isNotBlank(operdef.getName())){
				operdef.setPinYin(PingYinUtil.getFirstSpell(operdef.getName()));
			}
			operdef.setOperdefId(GenerateSequenceUtil.generateSequenceNo()); 
			basOperdefDao.insert(operdef);
		}
		return "保存成功";
	}
	/**
	 * 删除手术名称
	 * @param operdef
	 */
	@Transactional
	public String deleteOperdef(List<String> operdefIdList){
		for (String operdefId : operdefIdList) {
			basOperdefDao.deleteOperdef(operdefId);
		}
		return "删除成功";
	}
	
	public List<BasOperdef> selectByCode(String code){
		return basOperdefDao.selectByCode(code, getBeid());
	}
	@Transactional
	public int updateEnable(){
		return basOperdefDao.updateEnable(getBeid());
	}
	
	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("#");
		for (int i = 1; i <= 120; i++) {
			String sql = "INSERT INTO `bas_reg_opt` (`regOptId`, `name`, `sex`, `birthday`, `age`, `ageMon`, `ageDay`, `preengagementnumber`, `medicalType`, `identityNo`, `hid`, `cid`, `bed`, `regionId`, `regionName`, `deptId`, `deptName`, `designedOptName`, `designedOptCode`, `diagnosisName`, `diagnosisCode`, `operaDate`, `startTime`, `endTime`, `createUser`, `createTime`, `latierDiagName`, `latierDiagCode`, `realOperationName`, `realOperationCode`, `emergency`, `hyperSusceptiBility`, `optLevel`, `intermitCause`, `reasons`, `remark`, `isLocalAnaes`, `designedAnaesMethodName`, `designedAnaesMethodCode`, `operatorId`, `operatorName`, `height`, `weight`, `changeOperroomReason`, `realDesignedAnaesMethodName`, `realDesignedAnaesMethodCode`, `hbsag`, `hcv`, `hiv`, `hp`, `assistantId`, `assistantName`, `archState`, `nurseArchState`, `state`, `costsettlementState`, `previousState`, `msId`, `operTime`, `frontOperForbidTake`, `frontOperSpecialCase`, `operSource`, `cutLevel`, `origin`, `outMedicine`, `beid`, `operateNumber`) VALUES('";
			sql += "201712211349" + df.format(Math.random() * 1000000) + "','" + "gzb1221." + i + "','男',NULL,'54','6','76',NULL,'医保',NULL,'764622564',NULL,'7','1208','ICU病区','7','外三科','Carbol手术','201709011651002172','1,2,3-丙三醇三硝酸酯的毒性效应','76072','2017-12-21',NULL,NULL,'chengw','2017-12-21 13:49:18',NULL,NULL,NULL,NULL,'0',NULL,'一级',NULL,NULL,NULL,'0','全身麻醉','201709251216390002','1836','冯晓慧','179.00','76.00',NULL,NULL,NULL,'阴性','阴性','阳性','阴性','','',NULL,NULL,'01','0',NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL,'0','102',NULL)";
			System.out.println(sql);
		}
	}
}

