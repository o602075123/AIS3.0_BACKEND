/**
 * Copyright &copy; 2012-2013 <a href="httparamMap://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.digihealth.anesthesia.basedata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.formbean.BaseInfoQuery;
import com.digihealth.anesthesia.basedata.formbean.DispatchFormbean;
import com.digihealth.anesthesia.basedata.formbean.DispatchOperationFormBean;
import com.digihealth.anesthesia.basedata.formbean.DispatchPeopleNameFormBean;
import com.digihealth.anesthesia.basedata.formbean.DispatchTimeFormbean;
import com.digihealth.anesthesia.basedata.formbean.PrintNoticeFormBean;
import com.digihealth.anesthesia.basedata.formbean.SearchDispatchFormBean;
import com.digihealth.anesthesia.basedata.formbean.SearchListScheduleFormBean;
import com.digihealth.anesthesia.basedata.formbean.SysCodeFormbean;
import com.digihealth.anesthesia.basedata.po.BasDept;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasOperroom;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.BasRegion;
import com.digihealth.anesthesia.basedata.po.BasSysCode;
import com.digihealth.anesthesia.basedata.po.Controller;
import com.digihealth.anesthesia.basedata.utils.BasRegOptUtils;
import com.digihealth.anesthesia.basedata.utils.UserUtils;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.BeanHelper;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.JsonType;
import com.digihealth.anesthesia.common.utils.SpringContextHolder;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.doc.po.DocAnaesSummary;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAppendixCanal;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAppendixGen;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAppendixVisit;
import com.digihealth.anesthesia.doc.po.DocBloodTransRecord;
import com.digihealth.anesthesia.doc.po.DocOptRiskEvaluation;
import com.digihealth.anesthesia.evt.po.EvtParticipant;
import com.digihealth.anesthesia.evt.service.EvtParticipantService;
import com.digihealth.anesthesia.interfacedata.service.OperBaseDataService;
import com.digihealth.anesthesia.interfacedata.service.OperListService;
import com.digihealth.anesthesia.interfacedata.service.OperListServiceQNZ;
import com.digihealth.anesthesia.sysMng.po.BasUser;

/**
 * 
 * Title: DispatchService.java Description: 手术排程Service
 * 
 * @author liukui
 * @created 2015-10-7 下午6:00:54
 */
@Service
public class BasDispatchService extends BaseService {

	private static final Integer DISPATCH_SAVE = 0;// 保存暂存

	/**
	 * 当传入的查询条件为空时，需要构建一个查询对象
	 * 
	 * @param baseQuery
	 * @return
	 */
	public List<SearchDispatchFormBean> findList(BaseInfoQuery baseQuery) {
		String beid = getBeid();
        if (StringUtils.isBlank(baseQuery.getBeid())) {
			baseQuery.setBeid(beid);
		}
		List<SearchDispatchFormBean> dispatchList = basDispatchDao.findList(baseQuery);
		
		if (null != dispatchList && dispatchList.size() > 0)
		{
		    for (SearchDispatchFormBean formBean : dispatchList)
		    {
		        if (StringUtils.isNotBlank(formBean.getCircunurseId1()) && StringUtils.isBlank(formBean.getCircunurseName1()))
		        {
		            String[] circunurseIds = formBean.getCircunurseId1().split(",");
		            String circunurseName = "";
		            if (null != circunurseIds && circunurseIds.length > 0)
		            {
    		            for (String circunurseId : circunurseIds)
    		            {
    		                BasUser user = basUserDao.searchUserById(circunurseId, beid);
    		                circunurseName = circunurseName + ((null != user && null != user.getName()) ? user.getName() : "") + ",";
    		            }
		            }
		            if (StringUtils.isNotBlank(circunurseName))
	                {
	                    formBean.setCircunurseName1(circunurseName.substring(0, circunurseName.length() - 1));
	                }
		        }
		        
		        if (StringUtils.isNotBlank(formBean.getInstrnurseId1()) && StringUtils.isBlank(formBean.getInstrnurseName1()))
                {
                    String[] instrnurseIds = formBean.getInstrnurseId1().split(",");
                    String instrnurseName = "";
                    if (null != instrnurseIds && instrnurseIds.length > 0)
                    {
                        for (String instrnurseId : instrnurseIds)
                        {
                            BasUser user = basUserDao.searchUserById(instrnurseId, beid);
                            instrnurseName = instrnurseName + ((null != user && null != user.getName()) ? user.getName() : "") + ",";
                        }
                    }
                    
                    if (StringUtils.isNotBlank(instrnurseName))
                    {
                        formBean.setInstrnurseName1(instrnurseName.substring(0, instrnurseName.length() - 1));
                    }
                }
		    }
		}
		
		
		return dispatchList;
	}

	public List<PrintNoticeFormBean> printOperNoticeindList(BaseInfoQuery baseQuery) {
		if (StringUtils.isBlank(baseQuery.getBeid())) {
			baseQuery.setBeid(getBeid());
		}
		return basDispatchDao.printOperNotice(baseQuery);
	}

	public List<SearchDispatchFormBean> printDispatchItem(BaseInfoQuery baseQuery) {
		if (StringUtils.isBlank(baseQuery.getBeid())) {
			baseQuery.setBeid(getBeid());
		}
		List<SearchDispatchFormBean> list = basDispatchDao.printSchudle(baseQuery);
        
		return list;
//		BaseInfoQuery baseQuery1 = new BaseInfoQuery();
//		baseQuery1.setType("01");
//		List<SearchDispatchFormBean> bean = basDispatchDao.findList(baseQuery);
//		List<BasDispatch> dList = basDispatchDao.selectDispatchGbOproomId(baseQuery.getOperDate(),getBeid());d
//		List<String> stateItems = new ArrayList<String>();
//		int sta = 1;
//		if (null != dList && dList.size() > 0) {
//			for (int i = 0; i < dList.size(); i++) {
//				stateItems.add(dList.get(i).getOperRoomId());
//			}
//			sta = 2;
//		}
//
//		List<OperRoomFormBean> list = basOperroomDao.selectPrint("01", stateItems, sta, baseQuery.getBeid());
//		if (list != null) {
//			for (int i = 0; i < list.size(); i++) {
//				OperRoomFormBean or = list.get(i);
//				SearchDispatchFormBean b = new SearchDispatchFormBean();
//				b.setOperRoomId(or.getOperRoomId());
//				b.setOperRoomName(or.getName());
//				BasHealthNurse hn = basHealthNurseDao.selectHealNurse(baseQuery.getOperDate(), or.getOperRoomId(), getBeid());
//				if (hn != null) {
//					b.setHealthNurse(hn.getHealthnurse());
//					b.setHealthNurseName(hn.getHealthnurseName());
//					bean.add(b);
//				}
//
//			}
//		}
//		return bean;
	}
	
	public List<SearchDispatchFormBean> printDispatchItemHBGZB(BaseInfoQuery baseQuery) {
        if (StringUtils.isBlank(baseQuery.getBeid())) {
            baseQuery.setBeid(getBeid());
        }
        List<SearchDispatchFormBean> list = basDispatchDao.printSchudleHBGZB(baseQuery);
        if (null != list && list.size() > 0)
        {
            for (SearchDispatchFormBean bean : list)
            {
                String anesthetistName = null == bean.getAnesthetistName() ? "" : bean.getAnesthetistName();
                String circuanesthetistName = null == bean.getCircuanesthetistName() ? "" : "," + bean.getCircuanesthetistName();
                bean.setAnesthetistName(anesthetistName + circuanesthetistName);
                
                String circunurseName1 = null == bean.getCircunurseName1() ? "" : bean.getCircunurseName1();
                String circunurseName2 = null == bean.getCircunurseName2() ? "" : "," + bean.getCircunurseName2();
                bean.setCircunurseName(circunurseName1+circunurseName2); 
                
                String instrnurseName1 = null == bean.getInstrnurseName1()? "" : bean.getInstrnurseName1();
                String instrnurseName2 = null == bean.getInstrnurseName2() ? "" : "," + bean.getInstrnurseName2();
                bean.setInstrnurseName(instrnurseName1+instrnurseName2); 
            }
        }
        return list;
	}
	
	public List<SearchDispatchFormBean> printDispatchItemQNZ(BaseInfoQuery baseQuery) {
        if (StringUtils.isBlank(baseQuery.getBeid())) {
            baseQuery.setBeid(getBeid());
        }
        List<SearchDispatchFormBean> list = basDispatchDao.printSchudleQNZ(baseQuery);
        if (null != list && list.size() > 0)
        {
            for (SearchDispatchFormBean sd : list)
            {
                String anesthetistName = sd.getAnesthetistName();
                String circuanesthetistName = sd.getCircuanesthetistName();
                if (StringUtils.isNotBlank(anesthetistName) && StringUtils.isNotBlank(circuanesthetistName))
                {
                    sd.setAnesthetistName(anesthetistName + "," + circuanesthetistName);
                }
                else if (StringUtils.isNotBlank(anesthetistName) && StringUtils.isBlank(circuanesthetistName))
                {
                    sd.setAnesthetistName(anesthetistName);
                }
                else if (StringUtils.isBlank(anesthetistName) && StringUtils.isNotBlank(circuanesthetistName))
                {
                    sd.setAnesthetistName(circuanesthetistName);
                }
                   
                String instrnurseName1 = sd.getInstrnurseName1();
                String instrnurseName2 = sd.getInstrnurseName2();
                if (StringUtils.isNotBlank(instrnurseName1) && StringUtils.isNotBlank(instrnurseName2))
                {
                    sd.setInstrnurseName1(instrnurseName1 + "," + instrnurseName2);
                }
                else if (StringUtils.isNotBlank(instrnurseName1) && StringUtils.isBlank(instrnurseName2))
                {
                    sd.setInstrnurseName1(instrnurseName1);
                }
                else if (StringUtils.isBlank(instrnurseName1) && StringUtils.isNotBlank(instrnurseName2))
                {
                    sd.setInstrnurseName1(instrnurseName2);
                }
                
                String circunurseName1 = sd.getCircunurseName1();
                String circunurseName2 = sd.getCircunurseName2();
                if (StringUtils.isNotBlank(circunurseName1) && StringUtils.isNotBlank(circunurseName2))
                {
                    sd.setCircunurseName1(circunurseName1 + "," + circunurseName2);
                }
                else if (StringUtils.isNotBlank(circunurseName1) && StringUtils.isBlank(circunurseName2))
                {
                    sd.setCircunurseName1(circunurseName1);
                }
                else if (StringUtils.isBlank(circunurseName1) && StringUtils.isNotBlank(circunurseName2))
                {
                    sd.setCircunurseName1(circunurseName2);
                }
            }
        }
        return list;
    }
	
	public List<SearchDispatchFormBean> printDispatchItemXYCD(BaseInfoQuery baseQuery) {
        if (StringUtils.isBlank(baseQuery.getBeid())) {
            baseQuery.setBeid(getBeid());
        }
        List<SearchDispatchFormBean> list = basDispatchDao.printSchudleHBGZB(baseQuery);
        if (null != list && list.size() > 0)
        {
            for (SearchDispatchFormBean bean : list)
            {
                if (StringUtils.isNotBlank(bean.getCircunurseId1()))
                {
                    String[] circunurseIds = bean.getCircunurseId1().split(",");
                    String circunurseName = "";
                    if (null != circunurseIds && circunurseIds.length > 0)
                    {
                        for (String circunurseId : circunurseIds)
                        {
                            BasUser user = basUserDao.searchUserById(circunurseId, getBeid());
                            circunurseName = circunurseName + ((null != user && null != user.getName()) ? user.getName() : "") + ",";
                        }
                    }
                    if (StringUtils.isNotBlank(circunurseName))
                    {
                        bean.setCircunurseName1(circunurseName.substring(0, circunurseName.length() - 1));
                    }
                }
                
                if (StringUtils.isNotBlank(bean.getInstrnurseId1()))
                {
                    String[] instrnurseIds = bean.getInstrnurseId1().split(",");
                    String instrnurseName = "";
                    if (null != instrnurseIds && instrnurseIds.length > 0)
                    {
                        for (String instrnurseId : instrnurseIds)
                        {
                            BasUser user = basUserDao.searchUserById(instrnurseId, getBeid());
                            instrnurseName = instrnurseName + ((null != user && null != user.getName()) ? user.getName() : "") + ",";
                        }
                    }
                    
                    if (StringUtils.isNotBlank(instrnurseName))
                    {
                        bean.setInstrnurseName1(instrnurseName.substring(0, instrnurseName.length() - 1));
                    }
                }
            }
        }
        return list;
    }
	
	

	/**
	 * 批量排程信息保存
	 * 
	 * @param dispatchList
	 * @throws Exception
	 */
	@Transactional
	public void saveDispatch(DispatchOperationFormBean dispatchFormBean, ResponseValue result) {

		for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) {
			if (StringUtils.isBlank(dispatch.getBeid())) {
				dispatch.setBeid(getBeid());
			}

			Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
			// 如果此排班信息已经为术中了，则不允许调整排班信息
			if (controller != null) {
				if (!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)) {
					result.put("resultCode", "30000003");
					result.put("resultMessage", "手术排班修改出错，该排班信息状态错误！");
					return;
				}
			}

			// 获取手术人员信息
			BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
			/**
			 * 根据当前操作排程人员类型判断可修改字段的范围 RoleType = N可以修改除麻醉医生意外的其他字段 RoleType =
			 * A只可以修改麻醉医生字段
			 */
			BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
			if (disObj == null) {
				dispatch.setOperRegDate(regOpt.getOperaDate());
				basDispatchDao.insert(dispatch);
			} else {
				dispatch.setOperRegDate(disObj.getOperRegDate());
				String anesthetistId = disObj.getAnesthetistId();
				String perfusiondoctorId = disObj.getPerfusionDoctorId();
				String circuAnesthetistId = disObj.getCircuAnesthetistId();
				if ("ANAES_DIRECTOR".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
					anesthetistId = dispatch.getAnesthetistId();
					perfusiondoctorId = dispatch.getPerfusionDoctorId();
					circuAnesthetistId = dispatch.getCircuAnesthetistId();
				}
				if ("HEAD_NURSE".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
					BeanHelper.copyProperties(dispatch, disObj);
				}
				disObj.setAnesthetistId(anesthetistId);
				disObj.setPerfusionDoctorId(perfusiondoctorId);
				disObj.setCircuAnesthetistId(circuAnesthetistId);
				disObj.setIsHold(dispatch.getIsHold());
				basDispatchDao.update(disObj);
			}

			// 排程完成后，修改手术信息表手术时间
			if (StringUtils.isNotBlank(dispatch.getOperaDate())) {
				regOpt.setOperaDate(dispatch.getOperaDate());
				saveRegOpt(regOpt);
			}

			// 排程完成
			if (DISPATCH_SAVE.equals(dispatch.getIsHold())) {
				saveDispatchSuccess(dispatch, dispatchFormBean.getRoleType());
			}

		}
	}
	
	/**
     * 批量排程信息保存（南华局点）
     * 
     * @param dispatchList
     * @throws Exception
     */
    @Transactional
    public void saveDispatchNHFE(DispatchOperationFormBean dispatchFormBean, ResponseValue result) {

        for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) {
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }
            // 获取手术人员信息
            BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
            
            Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
            // 如果此排班信息已经为术中了，则不允许调整排班信息
            if (controller != null) {
                if (!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)) {
                    
                    List<SysCodeFormbean> codeLs = basSysCodeDao.searchSysCodeByGroupIdAndCodeValue("operation_state", controller.getState(), getBeid());
                    String stateName = "";
                    if(null != codeLs && codeLs.size()>0){
                        stateName = codeLs.get(0).getCodeName();
                    }
                    
                    result.setResultCode("30000003");
                    result.setResultMessage("姓名为："+regOpt.getName()+"手术排班修改出错，该排班信息状态已到'"+stateName+"',不为未排班或术前状态！");
                    return;
                }
            }
            /**
             * 根据当前操作排程人员类型判断可修改字段的范围 RoleType = N可以修改除麻醉医生意外的其他字段 RoleType =
             * A只可以修改麻醉医生字段
             */
            BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
            if (disObj == null) {
                dispatch.setOperRegDate(regOpt.getOperaDate());
                basDispatchDao.insert(dispatch);
            } else {
                dispatch.setOperRegDate(disObj.getOperRegDate());
                String anesthetistId = disObj.getAnesthetistId();
                String circuAnesthetistId = disObj.getCircuAnesthetistId();
                String perfusionDoctorId = disObj.getPerfusionDoctorId();
                if ("ANAES_DIRECTOR".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    anesthetistId = dispatch.getAnesthetistId();
                    perfusionDoctorId = dispatch.getPerfusionDoctorId();
                    
                    disObj.setAnesthetistId(anesthetistId);
                    disObj.setPerfusionDoctorId(perfusionDoctorId);
                    disObj.setCircuAnesthetistId(circuAnesthetistId);
                    disObj.setIsHold(dispatch.getIsHold());
                    basDispatchDao.update(disObj);
                }
                if ("HEAD_NURSE".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    BeanUtils.copyProperties(dispatch, disObj,new String[]{"isHold"}); //不替换isHold的值
                    
                    disObj.setAnesthetistId(anesthetistId);
                    disObj.setPerfusionDoctorId(perfusionDoctorId);
                    disObj.setCircuAnesthetistId(circuAnesthetistId);
                    
                    if(null != dispatch.getIsHold()){ //如果页面没传，则直接取数据库的isHold,页面传了，则修改
                        disObj.setIsHold(dispatch.getIsHold());
                    }
                    basDispatchDao.update(disObj);
                    
                    Integer dispatchToHis = dispatchFormBean.getDispatchToHis();
                    if(null != dispatchToHis && dispatchToHis.intValue() == 1 ){ //只有在手术室排班的时候，发送给his排班消息 
                        
                        logger.info("--------------进入发送排班消息给his---------------");
                            String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
                            if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                                logger.info("--------------进入发送排班sendDispatchInfo---------------");
                                OperListService operListService = SpringContextHolder.getBean("operListService");
                                operListService.sendDispatchInfo(dispatch,regOpt);
                            }
                    }
                }
            }
            
            //排程完成后，修改手术信息表手术时间         
            if(StringUtils.isNotBlank(dispatch.getOperaDate())){
                regOpt.setOperaDate(dispatch.getOperaDate());
//                if("7".equals(regOpt.getDesignedAnaesMethodCode())|| "29".equals(regOpt.getDesignedAnaesMethodCode())){
//                    regOpt.setIsLocalAnaes(1);
//                }else{
//                    regOpt.setIsLocalAnaes(0);
//                }
                BasRegOptUtils.IsLocalAnaesSet(regOpt);
                basRegOptDao.updateByPrimaryKeySelective(regOpt); 
            }
            
            //排程完成
            if(DISPATCH_SAVE.equals(dispatch.getIsHold())){
                saveDispatchSuccess(dispatch, dispatchFormBean.getRoleType());
            }
        }
    }
	
    /**
     * 批量排程信息保存（黔南州局点）
     * 
     * @param dispatchList
     * @throws Exception
     */
    @Transactional
    public void saveDispatchQNZ(DispatchOperationFormBean dispatchFormBean, ResponseValue result) {

        for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) {
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }
            // 获取手术人员信息
            BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
            //BasRegOptUtils.IsLocalAnaesSet(regOpt); 
            Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
            // 如果此排班信息已经为术中了，则不允许调整排班信息
            if (controller != null) {
                if (!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)) {
                    
                    List<SysCodeFormbean> codeLs = basSysCodeDao.searchSysCodeByGroupIdAndCodeValue("operation_state", controller.getState(), getBeid());
                    String stateName = "";
                    if(null != codeLs && codeLs.size()>0){
                        stateName = codeLs.get(0).getCodeName();
                    }
                    
                    result.setResultCode("30000003");
                    result.setResultMessage("姓名为："+regOpt.getName()+"手术排班修改出错，该排班信息状态已到'"+stateName+"',不为未排班或术前状态！");
                    return;
                }
            }
            /**
             * 根据当前操作排程人员类型判断可修改字段的范围 RoleType = N可以修改除麻醉医生意外的其他字段 RoleType =
             * A只可以修改麻醉医生字段
             */
            BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
            if (disObj == null) {
                dispatch.setOperRegDate(regOpt.getOperaDate());
                basDispatchDao.insert(dispatch);
            } else {
                dispatch.setOperRegDate(regOpt.getOperaDate());
                if ("HEAD_NURSE".equals(dispatchFormBean.getRoleType()) || "NURSE".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    disObj.setCircunurseId1(dispatch.getCircunurseId1());
                    disObj.setCircunurseId2(dispatch.getCircunurseId2());
                    disObj.setInstrnurseId1(dispatch.getInstrnurseId1());
                    disObj.setInstrnurseId2(dispatch.getInstrnurseId2());
                    
//                    if(null != dispatch.getIsHold()){ //如果页面没传，则直接取数据库的isHold,页面传了，则修改
//                        disObj.setIsHold(dispatch.getIsHold());
//                    }
//                    basDispatchDao.update(disObj);
                }
                if ("ANAES_DIRECTOR".equals(dispatchFormBean.getRoleType()) || "ANAES_DOCTOR".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    BeanUtils.copyProperties(dispatch, disObj,new String[]{"isHold"}); //不替换isHold的值  
                    disObj.setAnesthetistId(dispatch.getAnesthetistId());
                    disObj.setPerfusionDoctorId(dispatch.getPerfusionDoctorId());
                    //disObj.setCircuAnesthetistId(circuAnesthetistId);
                    
//                    if(null != dispatch.getIsHold()){ //如果页面没传，则直接取数据库的isHold,页面传了，则修改
//                        disObj.setIsHold(dispatch.getIsHold());
//                    }
//                    basDispatchDao.update(disObj);
                    
//                    Integer dispatchToHis = dispatchFormBean.getDispatchToHis();
//                    if(null != dispatchToHis && dispatchToHis.intValue() == 1 ){ //只有在手术室排班的时候，发送给his排班消息 
//                        
//                        logger.info("--------------进入发送排班消息给his---------------");
//                            String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
//                            if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
//                                logger.info("--------------进入发送排班sendDispatchInfo---------------");
//                                OperListService operListService = SpringContextHolder.getBean("operListService");
//                                operListService.sendDispatchInfo(dispatch,regOpt);
//                            }
//                    }
                }
                if (StringUtils.isNotBlank(disObj.getOperRoomId())
                    //&& StringUtils.isNotBlank(disObj.getPcs())
                    && StringUtils.isNotBlank(disObj.getAnesthetistId()) 
                    && StringUtils.isNotBlank(disObj.getCircunurseId1()))
                {
                    disObj.setIsHold(0);
                }
                
                
                basDispatchDao.update(disObj);
            }
            
            /*//排程完成后，修改手术信息表手术时间         
            if(StringUtils.isNotBlank(dispatch.getOperaDate())){
                regOpt.setOperaDate(dispatch.getOperaDate());
//                if("7".equals(regOpt.getDesignedAnaesMethodCode())|| "29".equals(regOpt.getDesignedAnaesMethodCode())){
//                    regOpt.setIsLocalAnaes(1);
//                }else{
//                    regOpt.setIsLocalAnaes(0);
//                }
                basRegOptDao.updateByPrimaryKeySelective(regOpt); 
            }*/
            
            //排程完成
            if(DISPATCH_SAVE.equals(disObj.getIsHold())){
                saveDispatchSuccessQNZ(disObj, dispatchFormBean.getRoleType());
                String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
                if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                    logger.info("===============================发送排程信息到his===========================================");
                    try
                    {
                        OperListServiceQNZ operListServiceQNZ = SpringContextHolder.getBean("operListServiceQNZ");
                        operListServiceQNZ.sendDispatchInfo(disObj, regOpt);
                    }
                    catch (Exception e)
                    {
                        logger.error("同步手术排班信息到his报错：  " + Exceptions.getStackTraceAsString(e));
                    } 
                }
            }
        }
    }
    
    
	
	/**
     * 批量排程信息保存
     * 
     * @param dispatchList
     * @throws Exception
     */
    @Transactional
    public void saveDispatchSYBX(DispatchOperationFormBean dispatchFormBean, ResponseValue result) {

        for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) {
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }

            Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
            // 如果此排班信息已经为术中了，则不允许调整排班信息
            if (controller != null) {
                if (!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)) {
                    result.put("resultCode", "30000003");
                    result.put("resultMessage", "手术排班修改出错，该排班信息状态错误！");
                    return;
                }
            }

            // 获取手术人员信息
            BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
            /**
             * 根据当前操作排程人员类型判断可修改字段的范围 RoleType = N可以修改除麻醉医生意外的其他字段 RoleType =
             * A只可以修改麻醉医生字段
             */
            BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
            if (disObj == null) {
                dispatch.setOperRegDate(regOpt.getOperaDate());
                basDispatchDao.insert(dispatch);
            } else {
                dispatch.setOperRegDate(disObj.getOperRegDate());
                String anesthetistId = disObj.getAnesthetistId();
                String perfusiondoctorId = disObj.getPerfusionDoctorId();
                if ("ANAES_DIRECTOR".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    anesthetistId = dispatch.getAnesthetistId();
                    perfusiondoctorId = dispatch.getPerfusionDoctorId();
                }
                if ("HEAD_NURSE".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    BeanUtils.copyProperties(dispatch, disObj, new String[]{"isHold"});
                }
                disObj.setAnesthetistId(anesthetistId);
                disObj.setPerfusionDoctorId(perfusiondoctorId);
                disObj.setOperRegDate(dispatch.getOperaDate());
                if (StringUtils.isNotBlank(disObj.getOperRoomId())
                    && StringUtils.isNotBlank(disObj.getPcs())
                    && StringUtils.isNotBlank(disObj.getOperRegDate())
                    && StringUtils.isNotBlank(disObj.getStartTime())
                    && ((StringUtils.isNotBlank(disObj.getAnesthetistId()) && 0 == regOpt.getIsLocalAnaes()) || 1 == regOpt.getIsLocalAnaes()) 
                    && StringUtils.isNotBlank(disObj.getCircunurseId1())) 
                {
                    disObj.setIsHold(0);
                }
                basDispatchDao.update(disObj);
            }

            // 排程完成后，修改手术信息表手术时间
            if (StringUtils.isNotBlank(dispatch.getOperaDate())) {
                regOpt.setOperaDate(dispatch.getOperaDate());
                saveRegOpt(regOpt);
            }

            // 排程完成
            if (DISPATCH_SAVE.equals(disObj.getIsHold())) {
                saveDispatchSuccess(dispatch, dispatchFormBean.getRoleType());
                /*OperBaseDataService operBaseDataService = SpringContextHolder.getBean("operBaseDataService");
                operBaseDataService.sendScheduleToHis(regOpt.getRegOptId());*/
            }

        }
    }
    
    
	/**
     * 批量排程信息保存（永兴人民局点）
     * 
     * @param dispatchList
     * @throws Exception
     */
    @Transactional
    public void saveDispatchYXRM(DispatchOperationFormBean dispatchFormBean, ResponseValue result) {

        for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) {
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }
            // 获取手术人员信息
            BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
            
            Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
            // 如果此排班信息已经为术中了，则不允许调整排班信息
            if (controller != null) {
                if (!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)) {
                    
                    List<SysCodeFormbean> codeLs = basSysCodeDao.searchSysCodeByGroupIdAndCodeValue("operation_state", controller.getState(), getBeid());
                    String stateName = "";
                    if(null != codeLs && codeLs.size()>0){
                        stateName = codeLs.get(0).getCodeName();
                    }
                    
                    result.setResultCode("30000003");
                    result.setResultMessage("姓名为："+regOpt.getName()+"手术排班修改出错，该排班信息状态已到'"+stateName+"',不为未排班或术前状态！");
                    return;
                }
            }
            /**
             * 根据当前操作排程人员类型判断可修改字段的范围 RoleType = N可以修改除麻醉医生意外的其他字段 RoleType =
             * A只可以修改麻醉医生字段
             */
            BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
            if (disObj == null) {
                dispatch.setOperRegDate(regOpt.getOperaDate());
                basDispatchDao.insert(dispatch);
            } else {
                dispatch.setOperRegDate(disObj.getOperRegDate());
                String anesthetistId = disObj.getAnesthetistId();
                String circuAnesthetistId = disObj.getCircuAnesthetistId();
                String perfusionDoctorId = disObj.getPerfusionDoctorId();
                if ("ANAES_DOCTOR".equals(dispatchFormBean.getRoleType()) || "ANAES_DIRECTOR".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    anesthetistId = dispatch.getAnesthetistId();
                    perfusionDoctorId = dispatch.getPerfusionDoctorId();
                    
                    disObj.setAnesthetistId(anesthetistId);
                    disObj.setCircuAnesthetistId(circuAnesthetistId);
                    disObj.setIsHold(dispatch.getIsHold());
                    basDispatchDao.update(disObj);
                }
                if ("NURSE".equals(dispatchFormBean.getRoleType()) ||"HEAD_NURSE".equals(dispatchFormBean.getRoleType()) || "ADMIN".equals(dispatchFormBean.getRoleType())) {
                    BeanUtils.copyProperties(dispatch, disObj,new String[]{"isHold"}); //不替换isHold的值
                    
                    disObj.setCircunurseId1(dispatch.getCircunurseId1());
                    disObj.setCircunurseId2(dispatch.getCircunurseId2());
                    disObj.setInstrnurseId1(dispatch.getInstrnurseId1());
                    disObj.setInstrnurseId2(dispatch.getInstrnurseId2());
                    disObj.setAnesthetistId(anesthetistId);
                    disObj.setPerfusionDoctorId(perfusionDoctorId);
                    disObj.setCircuAnesthetistId(circuAnesthetistId);
                    
                    if(null != dispatch.getIsHold()){ //如果页面没传，则直接取数据库的isHold,页面传了，则修改
                        disObj.setIsHold(dispatch.getIsHold());
                    }
                    basDispatchDao.update(disObj);
                }
            }
            
            //排程完成后，修改手术信息表手术时间         
            if(StringUtils.isNotBlank(dispatch.getOperaDate())){
                regOpt.setOperaDate(dispatch.getOperaDate());
                BasRegOptUtils.IsLocalAnaesSet(regOpt);
                basRegOptDao.updateByPrimaryKeySelective(regOpt); 
            }
            
            //排程完成
            if(DISPATCH_SAVE.equals(dispatch.getIsHold())){
                saveDispatchSuccess(dispatch, dispatchFormBean.getRoleType());
            }
        }
    }
    
    
    /**
     * 批量排程信息保存
     * @param dispatchList
     * @throws Exception
     */
    @Transactional
    public void saveDispatchXYCD(DispatchOperationFormBean dispatchFormBean,ResponseValue result){
        
        for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) {
            
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }
            
            Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
            //如果此排班信息已经为术中了，则不允许调整排班信息
            if(controller!=null){
                if(!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)){
                    result.put("resultCode", "30000003");
                    result.put("resultMessage", "手术排班修改出错，该排班信息状态错误！");
                    return;
                }
            }
            
            //获取手术人员信息
            BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
            /**
             * 根据当前操作排程人员类型判断可修改字段的范围
             * RoleType = N可以修改除麻醉医生意外的其他字段
             * RoleType = A只可以修改麻醉医生字段
             */
            BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
            
            if(dispatch.getOperRoomId() != null){
                BasOperroom room = basOperroomDao.queryRoomListById(dispatch.getOperRoomId(), getBeid());
                dispatch.setOperRoomName(room.getName());
                if(disObj != null){
                    disObj.setOperRoomName(room.getName());
                }
            }

//            dispatch.setCircunurseId1(StringUtils.getStringByList(dispatch.getCircunurseIdList()));
//            dispatch.setInstrnurseId1(StringUtils.getStringByList(dispatch.getInstrnurseIdList()));
            
            if(disObj == null){
                dispatch.setOperRegDate(regOpt.getOperaDate());
                basDispatchDao.insert(dispatch);
            }else{
                dispatch.setOperRegDate(disObj.getOperRegDate());
                String anesthetistId = disObj.getAnesthetistId();
                String perfusiondoctorId = disObj.getPerfusionDoctorId();
                if(EvtParticipantService.ROLE_ANESTH.equals(dispatchFormBean.getRoleType())){
                    anesthetistId = dispatch.getAnesthetistId();
                    perfusiondoctorId = dispatch.getPerfusionDoctorId();
                    BeanHelper.copyProperties(dispatch, disObj);
                }
                if(EvtParticipantService.ROLE_NURSE.equals(dispatchFormBean.getRoleType())){
                    BeanHelper.copyProperties(dispatch, disObj);
                }
                disObj.setAnesthetistId(anesthetistId);
                disObj.setPerfusionDoctorId(perfusiondoctorId);
                disObj.setIsHold(dispatch.getIsHold());
                
                basDispatchDao.update(disObj);
            }
            
            //排程完成后，修改手术信息表手术时间         
            if(StringUtils.isNotBlank(dispatch.getOperaDate())){
                regOpt.setOperaDate(dispatch.getOperaDate());
                saveRegOpt(regOpt);
            }
            
            /*if (StringUtils.isNotBlank(dispatch.getOperRoomId())
                //&& StringUtils.isNotBlank(disObj.getPcs())
                && StringUtils.isNotBlank(dispatch.getAnesthetistId()) 
                && StringUtils.isNotBlank(dispatch.getCircunurseId1()))
            {
                dispatch.setIsHold(0);
            }*/
            
            //排程完成
            if(DISPATCH_SAVE.equals(dispatch.getIsHold())){
                saveDispatchSuccessXYCD(dispatch, dispatchFormBean.getRoleType());
            }
        }
    }
	

	/**
	 * 更改手术室
	 * 
	 * @param dispatchList
	 * @throws Exception
	 */
	@Transactional
	public void changeOperRoom(DispatchOperationFormBean dispatchFormBean, ResponseValue result) {

		for (BasDispatch dispatch : dispatchFormBean.getDispatchList()) { 
			if (StringUtils.isBlank(dispatch.getBeid())) {
				dispatch.setBeid(getBeid());
			}

			Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
			// 如果此排班信息已经为术中了，则不允许调整排班信息
			if (controller != null) {
				if (!controller.getState().equals(OperationState.NO_SCHEDULING) && !controller.getState().equals(OperationState.PREOPERATIVE)) {
					result.put("resultCode", "30000003");
					result.put("resultMessage", "手术排班修改出错，该排班信息状态错误！");
					return;
				}
			}

			// 获取手术人员信息
			BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
			/**
			 * 根据当前操作排程人员类型判断可修改字段的范围 RoleType = N可以修改除麻醉医生意外的其他字段 RoleType =
			 * A只可以修改麻醉医生字段
			 */
			BasDispatch disObj = basDispatchDao.getDispatchOper(dispatch.getRegOptId());
			if (disObj == null) {
				dispatch.setOperRegDate(regOpt.getOperaDate());
				basDispatchDao.insert(dispatch);
			} else {
				dispatch.setOperRegDate(disObj.getOperRegDate());
				String anesthetistId = disObj.getAnesthetistId();
				String perfusiondoctorId = disObj.getPerfusionDoctorId();
				if ("A".equals(dispatchFormBean.getRoleType())  || "ADMIN".equals(dispatchFormBean.getRoleType())) {
					anesthetistId = dispatch.getAnesthetistId();
					perfusiondoctorId = dispatch.getPerfusionDoctorId();
					BeanHelper.copyProperties(dispatch, disObj);
				}
				if ("N".equals(dispatchFormBean.getRoleType())  || "ADMIN".equals(dispatchFormBean.getRoleType())) {
					BeanHelper.copyProperties(dispatch, disObj);
				}
				disObj.setAnesthetistId(anesthetistId);
				disObj.setPerfusionDoctorId(perfusiondoctorId); 
				disObj.setIsHold(dispatch.getIsHold());
				int resultPc = basDispatchDao.selectByOprroomOperDateStartTime(dispatch.getOperaDate(), dispatch.getOperRoomId(), dispatch.getStartTime());
				if (resultPc >= 1) {
					result.put("resultCode", "0");
					result.put("resultMessage", "切换的手术间此时间点已有手术，请重新更换！");
				}else{
                    basDispatchDao.update(disObj);
                    result.put("resultCode", "1");
                    result.put("resultMessage", "切换手术室成功！");
                }
			}

			// 排程完成后，修改手术信息表手术时间
			if (StringUtils.isNotBlank(dispatch.getOperaDate())) {
				regOpt.setOperaDate(dispatch.getOperaDate());
				saveRegOpt(regOpt);
			}

			// 排程完成
			if (DISPATCH_SAVE.equals(dispatch.getIsHold())) {
				saveDispatchSuccess(dispatch, dispatchFormBean.getRoleType());
				// operListService.tsurgeryhisid(regOpt);

			}
			// operListService.tsssqhz(regOpt);

		}

		// BasUser user = UserUtils.getUserCache();
		// operlogService.saveOperatelog("", operlogService.OPT_TYPE_INFO_SAVE,
		// operlogService.OPT_MODULE_OPER_DOC,"批量手术排程",
		// JsonType.jsonType(dispatchFormBean.getDispatchList())
		// ,user);
	}

	/**
	 * 将页面传入的排程完成信息进行保存
	 * 
	 * @param dispatch
	 */
	//@Transactional
	public void saveDispatchSuccess(BasDispatch dispatch, String RoleType) {

		Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
		// 只有当controller表中的状态等于02时才允许保存排程信息
		if (null != controller && (controller.getState().equals(OperationState.NO_SCHEDULING) || controller.getState().equals(OperationState.PREOPERATIVE))) {
			controller.setState(OperationState.PREOPERATIVE);
			if (logger.isDebugEnabled()) {
				logger.debug("DispatchService insertDispatch data:controller" + controller.toString());
			}
			controllerDao.update(controller);

			// 获取麻醉记录单数据
			DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordByRegOptId(dispatch.getRegOptId());

			// 避免重复保存时出现数据错误，先删除原来数据再做保存
			EvtParticipant delPant = new EvtParticipant();
			delPant.setDocId(dispatch.getRegOptId());
			delPant.setRole(RoleType);
			evtParticipantDao.delete(delPant);
			// 排程完成后将麻醉医生字段的值写入术中人员表中
			if (StringUtils.isNotBlank(dispatch.getAnesthetistId())) {
				List<EvtParticipant> participantList = new ArrayList<EvtParticipant>();
				EvtParticipant participant = new EvtParticipant();
				participant.setDocId(anaesRecord.getAnaRecordId());
				participant.setRole(EvtParticipantService.ROLE_ANESTH);
				participant.setUserLoginName(dispatch.getAnesthetistId());
				participant.setOperatorType("01"); // 将排程中的麻醉医生设置为主麻醉医生
				participant.setIsShiftChange(1); // 设为交班人员
				participantList.add(participant);
				saveParticipant(participantList);
			}

			List<EvtParticipant> nurseList = new ArrayList<EvtParticipant>();
			// 第一巡回护士
			if (StringUtils.isNotBlank(dispatch.getCircunurseId1())) {
				EvtParticipant participant = new EvtParticipant();
				participant.setDocId(anaesRecord.getAnaRecordId());
				participant.setRole(EvtParticipantService.ROLE_NURSE);
				participant.setUserLoginName(dispatch.getCircunurseId1());
				participant.setOperatorType("05"); // 巡回护士
				nurseList.add(participant);
			}
			// 第二巡回护士
			if (StringUtils.isNotBlank(dispatch.getCircunurseId2())) {
				EvtParticipant participant = new EvtParticipant();
				participant.setDocId(anaesRecord.getAnaRecordId());
				participant.setRole(EvtParticipantService.ROLE_NURSE);
				participant.setUserLoginName(dispatch.getCircunurseId2());
				participant.setOperatorType("05"); // 巡回护士
				nurseList.add(participant);
			}

			// 第一洗手护士
			if (StringUtils.isNotBlank(dispatch.getInstrnurseId1())) {
				EvtParticipant participant = new EvtParticipant();
				participant.setDocId(anaesRecord.getAnaRecordId());
				participant.setRole(EvtParticipantService.ROLE_NURSE);
				participant.setUserLoginName(dispatch.getInstrnurseId1());
				participant.setOperatorType("04"); // 洗手护士
				nurseList.add(participant);
			}
			// 第二洗手护士
			if (StringUtils.isNotBlank(dispatch.getInstrnurseId2())) {
				EvtParticipant participant = new EvtParticipant();
				participant.setDocId(anaesRecord.getAnaRecordId());
				participant.setRole(EvtParticipantService.ROLE_NURSE);
				participant.setUserLoginName(dispatch.getInstrnurseId2());
				participant.setOperatorType("04"); // 洗手护士
				nurseList.add(participant);
			}
			if (nurseList.size() > 0) {
				saveParticipant(nurseList);
			}
		}
	}
	
	public void saveDispatchSuccessQNZ(BasDispatch dispatch, String RoleType) {

        Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
        // 只有当controller表中的状态等于02时才允许保存排程信息
        if (null != controller && (controller.getState().equals(OperationState.NO_SCHEDULING) || controller.getState().equals(OperationState.PREOPERATIVE))) {
            controller.setState(OperationState.PREOPERATIVE);
            if (logger.isDebugEnabled()) {
                logger.debug("DispatchService insertDispatch data:controller" + controller.toString());
            }
            controllerDao.update(controller);

            // 获取麻醉记录单数据
            DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordByRegOptId(dispatch.getRegOptId());

            // 避免重复保存时出现数据错误，先删除原来数据再做保存
            EvtParticipant delPant = new EvtParticipant();
            delPant.setDocId(dispatch.getRegOptId());
            delPant.setRole(RoleType);
            evtParticipantDao.delete(delPant);
            // 排程完成后将麻醉医生字段的值写入术中人员表中
            if (StringUtils.isNotBlank(dispatch.getAnesthetistId())) {
                List<EvtParticipant> participantList = new ArrayList<EvtParticipant>();
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_ANESTH);
                participant.setUserLoginName(dispatch.getAnesthetistId());
                participant.setOperatorType("01"); // 将排程中的麻醉医生设置为主麻醉医生
                participant.setIsShiftChange(1); // 设为交班人员
                participantList.add(participant);
                saveParticipant(participantList);
            }
            
            if (StringUtils.isNotBlank(dispatch.getCircuAnesthetistId())) {
                List<EvtParticipant> participantList = new ArrayList<EvtParticipant>();
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_ANESTH);
                participant.setUserLoginName(dispatch.getCircuAnesthetistId());
                participant.setOperatorType("03"); // 副麻医生
                participantList.add(participant);
                saveParticipant(participantList);
            }
            

            List<EvtParticipant> nurseList = new ArrayList<EvtParticipant>();
            // 第一巡回护士
            if (StringUtils.isNotBlank(dispatch.getCircunurseId1())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getCircunurseId1());
                participant.setOperatorType("05"); // 巡回护士
                nurseList.add(participant);
            }
            // 第二巡回护士
            if (StringUtils.isNotBlank(dispatch.getCircunurseId2())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getCircunurseId2());
                participant.setOperatorType("05"); // 巡回护士
                nurseList.add(participant);
            }

            // 第一洗手护士
            if (StringUtils.isNotBlank(dispatch.getInstrnurseId1())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getInstrnurseId1());
                participant.setOperatorType("04"); // 洗手护士
                nurseList.add(participant);
            }
            // 第二洗手护士
            if (StringUtils.isNotBlank(dispatch.getInstrnurseId2())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getInstrnurseId2());
                participant.setOperatorType("04"); // 洗手护士
                nurseList.add(participant);
            }
            if (nurseList.size() > 0) {
                saveParticipant(nurseList);
            }
        }
    }
	
	/**
     * 将页面传入的排程完成信息进行保存
     * @param dispatch
     */
    @Transactional
    public void saveDispatchSuccessXYCD(BasDispatch dispatch,String RoleType){
        
        Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
        //只有当controller表中的状态等于02时才允许保存排程信息
        if(null != controller && (controller.getState().equals(OperationState.NO_SCHEDULING) || controller.getState().equals(OperationState.PREOPERATIVE))){
            controller.setState(OperationState.PREOPERATIVE);
            if(logger.isDebugEnabled()){
                logger.debug("DispatchService insertDispatch data:controller"+controller.toString());
            }
            controllerDao.update(controller);

            //获取麻醉记录单数据
            DocAnaesRecord anaesRecord = docAnaesRecordDao
                    .searchAnaesRecordByRegOptId(dispatch.getRegOptId());
            
            //避免重复保存时出现数据错误，先删除原来数据再做保存
            EvtParticipant delPant = new EvtParticipant();
            delPant.setDocId(anaesRecord.getAnaRecordId());
            //排程完成后将麻醉医生字段的值写入术中人员表中
            if(StringUtils.isNotBlank(dispatch.getAnesthetistId())){
                delPant.setRole("A");
                evtParticipantDao.delete(delPant);
                List<EvtParticipant> participantList = new ArrayList<EvtParticipant>(); 
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_ANESTH);
                participant.setUserLoginName(dispatch.getAnesthetistId());
                participant.setOperatorType("01");  //将排程中的麻醉医生设置为主麻醉医生
                participant.setIsShiftChange(1); //设为交班人员
                participantList.add(participant);
                saveParticipant(participantList);
            }
            
            delPant.setRole("N");
            evtParticipantDao.delete(delPant);
            
            List<EvtParticipant> nurseList = new ArrayList<EvtParticipant>(); 
            /*List<String> circunnurseIdList =  dispatch.getCircunurseIdList();
            if (null != circunnurseIdList && circunnurseIdList.size() > 0)
            {
                for (String id : circunnurseIdList)
                {
                    EvtParticipant participant = new EvtParticipant();
                    participant.setDocId(anaesRecord.getAnaRecordId());
                    participant.setRole(EvtParticipantService.ROLE_NURSE);
                    participant.setUserLoginName(id);
                    participant.setOperatorType("05");  //巡回护士
                    nurseList.add(participant);
                }
            }
            
            List<String> instrnurseIdList = dispatch.getInstrnurseIdList();
            if (null != instrnurseIdList && instrnurseIdList.size() > 0)
            {
                for (String id : instrnurseIdList)
                {
                    EvtParticipant participant = new EvtParticipant();
                    participant.setDocId(anaesRecord.getAnaRecordId());
                    participant.setRole(EvtParticipantService.ROLE_NURSE);
                    participant.setUserLoginName(id);
                    participant.setOperatorType("04");  //洗手护士
                    nurseList.add(participant);
                }
            }*/
            
         // 第一巡回护士
            if (StringUtils.isNotBlank(dispatch.getCircunurseId1())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getCircunurseId1());
                participant.setOperatorType("05"); // 巡回护士
                nurseList.add(participant);
            }
            // 第二巡回护士
            if (StringUtils.isNotBlank(dispatch.getCircunurseId2())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getCircunurseId2());
                participant.setOperatorType("05"); // 巡回护士
                nurseList.add(participant);
            }

            // 第一洗手护士
            if (StringUtils.isNotBlank(dispatch.getInstrnurseId1())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getInstrnurseId1());
                participant.setOperatorType("04"); // 洗手护士
                nurseList.add(participant);
            }
            // 第二洗手护士
            if (StringUtils.isNotBlank(dispatch.getInstrnurseId2())) {
                EvtParticipant participant = new EvtParticipant();
                participant.setDocId(anaesRecord.getAnaRecordId());
                participant.setRole(EvtParticipantService.ROLE_NURSE);
                participant.setUserLoginName(dispatch.getInstrnurseId2());
                participant.setOperatorType("04"); // 洗手护士
                nurseList.add(participant);
            }
            if(nurseList.size()>0){
                saveParticipant(nurseList);
            }
        }
    }

	/**
	 * 紧急手术创建
	 * 
	 * @param EmgencyOperationFormBean
	 * @throws Exception
	 */
	@Transactional
	public void createEmergencyOperation(BasRegOpt regOpt, BasDispatch dispatch, ResponseValue respValue) {
		if (logger.isDebugEnabled()) {
			logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
		}
		String result = saveRegOpt(regOpt);
		if (result.equals("true")) {
			// 创建排程信息
			dispatch.setRegOptId(regOpt.getRegOptId());
			dispatch.setOperRegDate(regOpt.getOperaDate());
			dispatch.setIsHold(DISPATCH_SAVE);
			if (StringUtils.isBlank(dispatch.getBeid())) {
				dispatch.setBeid(getBeid());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
			}
			basDispatchDao.insert(dispatch);

			// 手术审核
			checkOperation(regOpt.getRegOptId(), OperationState.PREOPERATIVE, "", respValue);

			// 紧急排程时roleType为护士长
			saveDispatchSuccess(dispatch, "N");
			// operListService.tsurgeryhisid(regOpt);
			// operListService.tsssqhz(regOpt);
		}
	}
	
	/**
     * 紧急手术创建
     * 
     * @param EmgencyOperationFormBean
     * @throws Exception
     */
    @Transactional
    public void createEmergencyOperationQNZ(BasRegOpt regOpt, BasDispatch dispatch, ResponseValue respValue) {
        if (logger.isDebugEnabled()) {
            logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
        }
        String result = saveRegOpt(regOpt);
        if (result.equals("true")) {
            // 创建排程信息
            dispatch.setRegOptId(regOpt.getRegOptId());
            dispatch.setOperRegDate(regOpt.getOperaDate());
            dispatch.setIsHold(DISPATCH_SAVE);
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
            }
            basDispatchDao.insert(dispatch);

            // 手术审核
            checkOperation(regOpt.getRegOptId(), OperationState.PREOPERATIVE, "", respValue);

            // 紧急排程时roleType为护士长
            saveDispatchSuccessQNZ(dispatch, "N");
            // operListService.tsurgeryhisid(regOpt);
            // operListService.tsssqhz(regOpt);
        }
    }
	
	/**
     * 紧急手术创建
     * 
     * @param EmgencyOperationFormBean
     * @throws Exception
     */
    @Transactional
    public void createEmergencyOperationSYBX(BasRegOpt regOpt, BasDispatch dispatch, ResponseValue respValue) {
        if (logger.isDebugEnabled()) {
            logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
        }
        String result = saveRegOpt(regOpt);
        if (result.equals("true")) {
            // 创建排程信息
            dispatch.setRegOptId(regOpt.getRegOptId());
            dispatch.setOperRegDate(regOpt.getOperaDate());
            dispatch.setIsHold(DISPATCH_SAVE);
            if (StringUtils.isBlank(dispatch.getBeid())) {
                dispatch.setBeid(getBeid());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
            }
            basDispatchDao.insert(dispatch);

            // 手术审核
            checkOperationSYBX(regOpt.getRegOptId(), OperationState.PREOPERATIVE, "", respValue);

            saveDispatchSuccess(dispatch, "N");
        }
    }
    
    /**
     * 紧急手术创建
     * 
     * @param EmgencyOperationFormBean
     * @throws Exception
     */
    @Transactional
    public void createEmergencyOperationNHFE(BasRegOpt regOpt, BasDispatch dispatch,ResponseValue respValue) {
        if (logger.isDebugEnabled()) {
            logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
        }
        String regOptId = GenerateSequenceUtil.generateSequenceNo();
        regOpt.setRegOptId(regOptId);
        regOpt.setCreateTime(DateUtils.formatDateTime(new Date()));
        basRegOptDao.insert(regOpt);
        Controller controller = new Controller();
        controller.setRegOptId(regOptId);
        controller.setCostsettlementState("0");
        controller.setState(OperationState.NOT_REVIEWED);
        controllerDao.update(controller);
        
        dispatch.setRegOptId(regOpt.getRegOptId());
        dispatch.setOperRegDate(regOpt.getOperaDate());
        dispatch.setIsHold(DISPATCH_SAVE);
        if (StringUtils.isBlank(dispatch.getBeid()))
        {
            dispatch.setBeid(getBeid());
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("DispatchService createEmergencyOperation data:dispatch" + dispatch.toString());
        }
        basDispatchDao.insert(dispatch);
        
        // 手术审核
        checkOperationNHFE(regOpt.getRegOptId(), OperationState.PREOPERATIVE, "", respValue);
        
        saveDispatchSuccess(dispatch, "N");
    }
    

	public BasDispatch getDispatchOper(String regOptId) {
		return basDispatchDao.getDispatchOper(regOptId);
	}

	/**
	 * 批量修改排程信息
	 * 
	 * @param dispatchList
	 */

	@Transactional
	public void updateDispatch(List<BasDispatch> dispatchList) {
		for (BasDispatch dispatch : dispatchList) {
			if (StringUtils.isBlank(dispatch.getBeid())) {
				dispatch.setBeid(getBeid());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("DispatchService updateDispatch data:" + dispatch.toString());
			}
			basDispatchDao.update(dispatch);
		}
	}

	/**
	 * 排班取消 这里只允许对暂存数据做重排操作
	 */
	@Transactional
	public Map cancelDispatchItem(List<String> regOptIdList) {
		if (logger.isDebugEnabled()) {
			logger.debug("DispatchService cancelDispatchItem data:" + regOptIdList.toString());
		}
		Map result = new HashMap();
		for (String regOptId : regOptIdList) {
			// 获取当前手术人员的状态
			Controller controller = controllerDao.getControllerById(regOptId);
			BasDispatch dispatch = basDispatchDao.getDispatchOper(regOptId); 

			if (!OperationState.NO_SCHEDULING.equals(controller.getState()) && !OperationState.PREOPERATIVE.equals(controller.getState())) {
				result.put("resultCode", "30000003");
				result.put("resultMessage", "手术排班取消错误，当前状态不允许取消排班");
				return result;
			}
			/**
			 * 如果当前状态为未排班则直接取消排班信息 当已经排班未进入术中时，允许取消排班，并将手术人员状态回退到未排班状态
			 */
			if (null != controller && controller.getState().equals(OperationState.PREOPERATIVE)) {
				controller.setState(OperationState.NO_SCHEDULING);
				controllerDao.update(controller);
			}

			// 获取手术人员信息将手术日期改成手术申请日期
			BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
			regOpt.setOperaDate(dispatch.getOperRegDate());
			basRegOptDao.updateByPrimaryKey(regOpt); 

			basDispatchDao.deleteDispatchHold(regOptId);
		}
		result.put("resultCode", "1");
		result.put("resultMessage", "手术排程取消成功！");
		return result;
	}

	/**
	 * 批量排班重排
	 */
	@Transactional
	public void redispatchItem(Map map) {
		if (logger.isDebugEnabled()) {
			logger.debug("DispatchService redispatchItem data:" + map.get("operatDate"));
		}

		String beid = getBeid();
        List<BasDispatch> dispatchList = basDispatchDao.getDispatchListByOperateDate(map.get("operatDate").toString(),beid);

		for (BasDispatch dispatch : dispatchList) {
			if (StringUtils.isBlank(dispatch.getBeid())) {
				dispatch.setBeid(beid);
			}
			// 获取当前手术人员的状态
			Controller controller = controllerDao.getControllerById(dispatch.getRegOptId());
			/**
			 * 如果当前状态为排班则直接取消排班信息 当已经排班未进入术中时，允许取消排班，并将手术人员状态回退到未排班状态
			 */
			if (OperationState.NO_SCHEDULING.equals(controller.getState()) || OperationState.PREOPERATIVE.equals(controller.getState())) {
				controller.setState(OperationState.NO_SCHEDULING);
				controllerDao.update(controller);

				// 获取手术人员信息将手术日期改成手术申请日期
				BasRegOpt regOpt = basRegOptDao.searchRegOptById(dispatch.getRegOptId());
				regOpt.setOperaDate(dispatch.getOperRegDate());
				basRegOptDao.updateByPrimaryKey(regOpt); 
				basDispatchDao.deleteDispatchHold(dispatch.getRegOptId());
			}
		}

	}

	/**
	 * 
	 * @discription 根据regOptId获取排班人员的名字
	 * @author chengwang
	 * @created 2015年10月30日 下午2:08:37
	 * @param regOptId
	 * @return
	 */
	public DispatchPeopleNameFormBean searchPeopleNameByRegOptId(String regOptId) {
		return basDispatchDao.searchPeopleNameByRegOptId(regOptId, getBeid());
	}

	public DispatchFormbean getDispatchOperByRegOptId(String regOptId) {
		return basDispatchDao.getDispatchOperByRegOptId(regOptId, getBeid());
	}

	public Integer searchPersonTotalInOperRoomByStartTime(BasDispatch dis) {
		return basDispatchDao.searchPersonTotalInOperRoomByStartTime(dis.getOperaDate(), dis.getStartTime(), dis.getOperRoomId(), getBeid());
	}

	public Integer checkOperateTimeBeUse(BasDispatch dis) {
		return basDispatchDao.checkOperateTimeBeUse(dis.getOperaDate(), dis.getStartTime(), dis.getOperRoomId(), getBeid());
	}

	public List<DispatchTimeFormbean> getNotUseTimeList(BasDispatch dispatch) {
		if (StringUtils.isBlank(dispatch.getBeid())) {
			dispatch.setBeid(getBeid());
		}
		return basDispatchDao.getNotUseTimeList(dispatch);
	}

	public List<BasSysCode> getNoUsePcsList(BasDispatch dispatch) {
		if (StringUtils.isBlank(dispatch.getBeid())) {
			dispatch.setBeid(getBeid());
		}
		return basDispatchDao.getNoUsePcsList(dispatch);
	}

	public List<PrintNoticeFormBean> getOperateInfoByInsideScreen() {

		return basDispatchDao.getOperateInfoByInsideScreen(getBeid());
	}
	
	public List<PrintNoticeFormBean> getOperateInfoByInsideScreenQNZ() {

        return basDispatchDao.getOperateInfoByInsideScreenQNZ(getBeid());
    }

	public List<PrintNoticeFormBean> getOperateInfoByOutsideScreen() {
		return basDispatchDao.getOperateInfoByOutsideScreen(getBeid());
	}

	private String saveRegOpt(BasRegOpt regOpt) {
	    
	    if (StringUtils.isBlank(regOpt.getBeid()))
	    {
	        regOpt.setBeid(getBeid());
	    }
	    //BasRegOptUtils.IsLocalAnaesSet(regOpt);
	    //BasRegOptUtils.getOtherInfo(regOpt);
	    
		BasDept basDept = basDeptDao.searchDeptById(regOpt.getDeptId(),regOpt.getBeid());
		if (basDept != null) {
			regOpt.setDeptName(basDept.getName());
		}
		BasRegion basRegion = basRegionDao.searchRegionById(regOpt.getRegionId(),regOpt.getBeid());
		if (basRegion != null) {
			regOpt.setRegionName(basRegion.getName());
		}
		if (StringUtils.isNotBlank(regOpt.getRegOptId())) {
			basRegOptDao.updateByPrimaryKey(regOpt);
			return "true";
		} else {
			BasUser user = UserUtils.getUserCache();
			String regOptId = GenerateSequenceUtil.generateSequenceNo();
			regOpt.setRegOptId(regOptId);
			regOpt.setCostsettlementState("0");
			regOpt.setState(OperationState.NOT_REVIEWED);
			if (user != null) {
				regOpt.setCreateUser(basUserDao.selectHisIdByUserName(user.getUserName(), getBeid()));
			}
			regOpt.setCreateTime(DateUtils.formatDateTime(new Date()));
			regOpt.setBeid(getBeid());
			basRegOptDao.insert(regOpt);
			if ("1".equals(regOpt.getGetData())) {
				return JsonType.jsonType(basRegOptDao.searchRegOptById(regOpt.getRegOptId()));
			}
			return "true";
		}
	}

    public void checkOperation(String ids, String state,String previousState,ResponseValue respValue) {
        List<String> idsList = new ArrayList<String>();
        String[] idsString = ids.split(",");
        for (int i = 0; i < idsString.length; i++) {
            idsList.add(idsString[i]);
        }
        int succCnt = 0;
        if (idsList != null)
        {
            if (idsList.size() > 0)
            {
                for (int i = 0; i < idsList.size(); i++)
                {
                    String regOptId = idsList.get(i);
                    BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
                    if (regOpt != null)
                    {
                        if (StringUtils.isEmpty(regOpt.getDesignedAnaesMethodName())
                            || StringUtils.isEmpty(regOpt.getDesignedAnaesMethodCode()))
                        {
                            continue;
                        }
                    }
                    Controller c = controllerDao.getControllerById(regOptId);
                    if (c != null)
                    {
                        if (c.getState().equals(OperationState.NOT_REVIEWED))
                        {
                            controllerDao.checkOperation(regOptId, state, previousState);
                            creatDocument(regOpt);
                            succCnt++;
                        }
                    }
                }
            }
            if (idsList.size() - succCnt > 0)
            {
                int failCnt = idsList.size() - succCnt;
                respValue.setResultMessage("批量审核完成!其中成功" + succCnt + "条数据," + "失败" + failCnt + "条数据");
            }
        }
    }
    
    public void checkOperationSYBX(String ids, String state,String previousState,ResponseValue respValue) {
        List<String> idsList = new ArrayList<String>();
        String[] idsString = ids.split(",");
        for (int i = 0; i < idsString.length; i++) {
            idsList.add(idsString[i]);
        }
        int succCnt = 0;
        if (idsList != null)
        {
            if (idsList.size() > 0)
            {
                for (int i = 0; i < idsList.size(); i++)
                {
                    String regOptId = idsList.get(i);
                    BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
                    if (regOpt != null)
                    {
                        if (StringUtils.isEmpty(regOpt.getDesignedAnaesMethodName())
                            || StringUtils.isEmpty(regOpt.getDesignedAnaesMethodCode()))
                        {
                            continue;
                        }
                    }
                    Controller c = controllerDao.getControllerById(regOptId);
                    if (c != null)
                    {
                        if (c.getState().equals(OperationState.NOT_REVIEWED))
                        {
                            controllerDao.checkOperation(regOptId, state, previousState);
                            creatDocument(regOpt);
                            //麻醉总结单
                            DocAnaesSummary anaesSummary = new DocAnaesSummary(); 
                            anaesSummary.setRegOptId(regOpt.getRegOptId());
                            anaesSummary.setProcessState("NO_END");
                            anaesSummary.setAnaSumId(GenerateSequenceUtil.generateSequenceNo());
                            docAnaesSummaryDao.insert(anaesSummary);
                            //椎管内麻醉
                            DocAnaesSummaryAppendixCanal anaesSummaryAppendixCanal = new DocAnaesSummaryAppendixCanal();
                            anaesSummaryAppendixCanal.setAnaSumId(anaesSummary.getAnaSumId());
                            anaesSummaryAppendixCanal.setAnaSumAppCanId(GenerateSequenceUtil.generateSequenceNo());
                            docAnaesSummaryAppendixCanalDao.insert(anaesSummaryAppendixCanal);
                            //全麻
                            DocAnaesSummaryAppendixGen anaesSummaryAppendixGen = new DocAnaesSummaryAppendixGen();
                            anaesSummaryAppendixGen.setAnaSumId(anaesSummary.getAnaSumId());
                            anaesSummaryAppendixGen.setAnaSumAppGenId(GenerateSequenceUtil.generateSequenceNo());
                            docAnaesSummaryAppendixGenDao.insert(anaesSummaryAppendixGen);
                            //术后访视
                            DocAnaesSummaryAppendixVisit anaesSummaryAppendixVisit = new DocAnaesSummaryAppendixVisit();
                            anaesSummaryAppendixVisit.setAnaSumId(anaesSummary.getAnaSumId());
                            anaesSummaryAppendixVisit.setAnesSumVisId(GenerateSequenceUtil.generateSequenceNo());
                            docAnaesSummaryAppendixVisitDao.insert(anaesSummaryAppendixVisit);
                            
                            // 本溪局点文书特殊处理
                            // 局麻时直接将手术风险评估表置为完成
                            if (1 == regOpt.getIsLocalAnaes())
                            {
                                DocOptRiskEvaluation docOptRiskEvaluation = new DocOptRiskEvaluation();
                                docOptRiskEvaluation.setRegOptId(regOpt.getRegOptId());
                                docOptRiskEvaluation =
                                    docOptRiskEvaluationDao.searchOptRiskEvaluationByRegOptId(docOptRiskEvaluation);
                                docOptRiskEvaluation.setProcessState("END");
                                docOptRiskEvaluationDao.updateByPrimaryKey(docOptRiskEvaluation);
                            }
                            succCnt++;
                        }
                    }
                }
            }
            if (idsList.size() - succCnt > 0)
            {
                int failCnt = idsList.size() - succCnt;
                respValue.setResultMessage("批量审核完成!其中成功" + succCnt + "条数据," + "失败" + failCnt + "条数据");
            }
        }
    }
    
    /**
     * 
     * @discription 改变状态
     * @author chengwang
     * @created 2015-10-19 下午4:44:04
     * @param id
     * @param state
     * @return
     */
    public void checkOperationNHFE(String ids, String state,String previousState,ResponseValue respValue) {
        List<String> idsList = new ArrayList<String>();
        String[] idsString = ids.split(",");
        for (int i = 0; i < idsString.length; i++) {
            idsList.add(idsString[i]);
        }
        int succCnt = 0;
        if (idsList != null)
        {
            if (idsList.size() > 0)
            {
                for (int i = 0; i < idsList.size(); i++)
                {
                    String regOptId = idsList.get(i);
                    BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
                    if (regOpt != null)
                    {
                        if (StringUtils.isEmpty(regOpt.getDesignedAnaesMethodName())
                            || StringUtils.isEmpty(regOpt.getDesignedAnaesMethodCode()))
                        {
                            continue;
                        }
                    }
                    Controller c = controllerDao.getControllerById(regOptId);
                    if (c != null)
                    {
                        if (c.getState().equals(OperationState.NOT_REVIEWED))
                        {
                            controllerDao.checkOperation(regOptId, state, previousState);
                            creatDocument(regOpt);
                            
                            //护士术中输血单
                            DocBloodTransRecord bloodTransRecord = new DocBloodTransRecord();
                            bloodTransRecord.setRegOptId(regOptId);
                            bloodTransRecord.setBloodTransId(GenerateSequenceUtil.generateSequenceNo());
                            docBloodTransRecordDao.insertSelective(bloodTransRecord);

                            //局麻时直接设置状态为完成
                            if (1 == regOpt.getIsLocalAnaes())
                            {
                                DocOptRiskEvaluation optRiskEvaluatio = new DocOptRiskEvaluation();
                                optRiskEvaluatio.setRegOptId(regOptId);
                                optRiskEvaluatio = docOptRiskEvaluationDao.searchOptRiskEvaluationByRegOptId(optRiskEvaluatio);
                                optRiskEvaluatio.setProcessState("END");
                                docOptRiskEvaluationDao.updateByPrimaryKey(optRiskEvaluatio);
                            }
                            succCnt++;
                        }
                    }
                }
            }
            if (idsList.size() - succCnt > 0)
            {
                int failCnt = idsList.size() - succCnt;
                respValue.setResultMessage("批量审核完成!其中成功" + succCnt + "条数据," + "失败" + failCnt + "条数据");
            }
        }
    }

	public void saveParticipant(List<EvtParticipant> participantList) {
		String regOptId = "";
		if (participantList.size() > 0) {
			DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(participantList.get(0).getDocId());
			regOptId = anaesRecord.getRegOptId();
		}

		// operlogService.saveOperatelog(regOptId,
		// operlogService.OPT_TYPE_INFO_SAVE,
		// operlogService.OPT_MODULE_OPER_RECORD,"手术人员保存",
		// JsonType.jsonType(participantList));

		if (participantList.size() > 0) {
			EvtParticipant part = participantList.get(0);

			evtParticipantDao.deleteByOperatorType(part);
			// 如果保存为麻醉医生这里需要特殊处理下
			if (part.getRole().equals("A")) {
				List<EvtParticipant> mainAnesDocList = evtParticipantDao.getMainDocList(part.getDocId(), part.getRole(), "01");
				for (EvtParticipant participant1 : participantList) {
					boolean flag = true;
					for (EvtParticipant participant : mainAnesDocList) {
						// 如果界面传入过来的麻醉医生列表的数据已经存在则不新增
						if (participant1.getUserLoginName().equals(participant.getUserLoginName())) {
							flag = false;
							break;
						}
					}
					if (flag) {
						if (StringUtils.isNotBlank(participant1.getUserLoginName())) {
							participant1.setPartpId(GenerateSequenceUtil.generateSequenceNo());
							evtParticipantDao.insert(participant1);
						}
					}
				}
			} else if (part.getRole().equals("O")) {
				List<EvtParticipant> mainOperateDocList = evtParticipantDao.getMainDocList(part.getDocId(), part.getRole(), "06");
				for (EvtParticipant participant1 : participantList) {
					boolean flag = true;
					for (EvtParticipant participant : mainOperateDocList) {
						// 如果界面传入过来的手术医生列表的数据已经存在则不新增
						if (participant1.getUserLoginName().equals(participant.getUserLoginName())) {
							flag = false;
							break;
						}
					}
					if (flag) {
						if (StringUtils.isNotBlank(participant1.getUserLoginName())) {
							participant1.setPartpId(GenerateSequenceUtil.generateSequenceNo());
							evtParticipantDao.insert(participant1);
						}
					}
				}
			} else {
				for (EvtParticipant participant : participantList) {
					if (StringUtils.isNotBlank(participant.getUserLoginName())) {
						participant.setPartpId(GenerateSequenceUtil.generateSequenceNo());
						evtParticipantDao.insert(participant);
					}
				}
			}
		}
	}
	
	/**
     * 根据条件查询未排班的的列表
     * @param baseQuery
     * @return
     */
    public List<SearchListScheduleFormBean> searchAllDispatchList(BaseInfoQuery baseQuery){
        if (StringUtils.isBlank(baseQuery.getBeid()))
        {
            baseQuery.setBeid(getBeid());
        }
        return basDispatchDao.searchAllDispatchList(baseQuery);
    }

    public int searchAllDispatchListTotal(BaseInfoQuery baseQuery){
        if (StringUtils.isBlank(baseQuery.getBeid()))
        {
            baseQuery.setBeid(getBeid());
        }
        return basDispatchDao.searchAllDispatchListTotal(baseQuery);
    }

    /**
     * 根据条件查询未排班的的列表
     * @param baseQuery
     * @return
     */
    public List<SearchListScheduleFormBean> searchAllDispatchListSYBX(BaseInfoQuery baseQuery){
        if (StringUtils.isBlank(baseQuery.getBeid()))
        {
            baseQuery.setBeid(getBeid());
        }
        return basDispatchDao.searchAllDispatchListSYBX(baseQuery);
    }
    
    public List<SearchListScheduleFormBean> searchAllDispatchListNHFE(BaseInfoQuery baseQuery){
        if (StringUtils.isBlank(baseQuery.getBeid()))
        {
            baseQuery.setBeid(getBeid());
        }
        return basDispatchDao.searchAllDispatchListNHFE(baseQuery);
    }
    
    public List<SearchListScheduleFormBean> searchAllDispatchListQNZ(BaseInfoQuery baseQuery){
        if (StringUtils.isBlank(baseQuery.getBeid()))
        {
            baseQuery.setBeid(getBeid());
        }
        return basDispatchDao.searchAllDispatchListQNZ(baseQuery);
    }
    
    public List<SearchListScheduleFormBean> searchAllDispatchListYXRM(BaseInfoQuery baseQuery){
        if (StringUtils.isBlank(baseQuery.getBeid()))
        {
            baseQuery.setBeid(getBeid());
        }
        return basDispatchDao.searchAllDispatchListYXRM(baseQuery);
    }
    /**
     * 手术室排班取消
     */
    @Transactional
    public void cancelOperroomDispatch(String regOptId,ResponseValue resp){
        if(logger.isDebugEnabled()){
            logger.debug("DispatchService cancelOperroomDispatch data:"+regOptId);
        }
        //获取当前手术人员的状态
        BasDispatch dispatch = basDispatchDao.getDispatchOper(regOptId);
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        
        if(DISPATCH_SAVE.equals(dispatch.getIsHold())){
            resp.setResultCode("300000001");
            resp.setResultMessage(regOpt.getName()+"的排班已完成，不允许撤销手术室排班");
            return;
        }
        /**
         * 如果当前状态为未排班则直接取消排班信息
         */
        basDispatchDao.cancelDispatch(regOptId);
    }
    
    /**
     * 手术室排班取消
     */
    @Transactional
    public void cancelOperroomDispatchNHFE(String regOptId,ResponseValue resp){
        if(logger.isDebugEnabled()){
            logger.debug("DispatchService cancelOperroomDispatch data:"+regOptId);
        }
        //获取当前手术人员的状态
        BasDispatch dispatch = basDispatchDao.getDispatchOper(regOptId);
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        
        if(DISPATCH_SAVE.equals(dispatch.getIsHold())){
            resp.setResultCode("300000001");
            resp.setResultMessage(regOpt.getName()+"的排班已完成，不允许撤销手术室排班");
            return;
        }
        /**
         * 如果当前状态为未排班则直接取消排班信息
         */
        int result = basDispatchDao.cancelDispatch(regOptId);
        
        if(result >0 ){
            // 发送给his 取消手术的消息
            OperListService operListService = SpringContextHolder.getBean("operListService");
            operListService.sendCancleRegOpt(regOptId);
        }
    }
    
    /**
     * 手术室排班取消
     */
    @Transactional
    public void cancelOperroomDispatchQNZ(String regOptId,ResponseValue resp){
        if(logger.isDebugEnabled()){
            logger.debug("DispatchService cancelOperroomDispatch data:"+regOptId);
        }
        //获取当前手术人员的状态
        BasDispatch dispatch = basDispatchDao.getDispatchOper(regOptId);
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        
        if(DISPATCH_SAVE.equals(dispatch.getIsHold())){
            resp.setResultCode("300000001");
            resp.setResultMessage(regOpt.getName()+"的排班已完成，不允许撤销手术室排班");
            return;
        }
        /**
         * 如果当前状态为未排班则直接取消排班信息
         */
        basDispatchDao.cancelDispatchQNZ(regOptId);
    }
    
    /**
     * 护士将排班未完成的数据推送给麻醉医生排班
     */
    @Transactional
    public void dispatchDataPush(List<String> regOptList){
        if(logger.isDebugEnabled()){
            logger.debug("DispatchService dispatchDataPush data:"+regOptList);
        }
        for (String regOptId : regOptList) {
            BasDispatch dispatch = basDispatchDao.getDispatchOper(regOptId);
            dispatch.setIsHold(9);//护士排班数据推送给麻醉医生
            basDispatchDao.update(dispatch);
        }
    }

}
