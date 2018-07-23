package com.digihealth.anesthesia.evt.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.po.BasMedicalTakeWay;
import com.digihealth.anesthesia.basedata.po.BasMedicine;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.utils.Arith;
import com.digihealth.anesthesia.basedata.utils.LogUtils;
import com.digihealth.anesthesia.basedata.utils.UserUtils;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.exception.CustomException;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.CompareObject;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.JsonType;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.evt.formbean.ChangeValueFormbean;
import com.digihealth.anesthesia.evt.formbean.MedicalDetailFormbean;
import com.digihealth.anesthesia.evt.formbean.RegOptOperMedicaleventFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchOptOperMedicalevent;
import com.digihealth.anesthesia.evt.po.EvtAnaesModifyRecord;
import com.digihealth.anesthesia.evt.po.EvtMedicalEvent;
import com.digihealth.anesthesia.evt.po.EvtMedicalEventDetail;

@Service
public class EvtMedicalEventService extends BaseService {

	//private static final String MEDICAL_EVENT_TYPE_ANAES="02";//麻醉用药
	//private static final String MEDICAL_EVENT_TYPE_TREAT="01";//治疗用药

	/**
	 * 根据用药事件信息关联查询药品表数据
	 * 
	 * @param docId
	 * @return
	 */
	public List<SearchOptOperMedicalevent> searchMedicaleventList(SearchFormBean searchBean) {
		List<SearchOptOperMedicalevent> resultList = new ArrayList<SearchOptOperMedicalevent>();
	    if (StringUtils.isBlank(searchBean.getBeid())) {
            searchBean.setBeid(getBeid());
        }
	    resultList = evtMedicaleventDao.searchMedicaleventList(searchBean);
		for(SearchOptOperMedicalevent obj : resultList) {
			obj.setMedTakeWayName(getMedTakeWayName(obj));
			
			List<EvtMedicalEventDetail> medDetailList = evtMedicalEventDetailDao.selectByMedEventandDocId(obj.getMedEventId());
			obj.setMedDetailList(medDetailList); 
			if (null != obj.getPackageDosageAmount() && 0 != obj.getPackageDosageAmount().floatValue())
			{
			    Float quantity = new Float(Math.ceil(obj.getDosage()/obj.getPackageDosageAmount()));
			    obj.setQuantity(quantity);
			    
			    if (null != obj.getPriceMinPackage())
			    {
			        Float amount = Arith.multiply(obj.getPriceMinPackage(),quantity);
			        obj.setAmout(amount);
			    }
			}
			
		}
		return resultList;
	}

	/**
	 * 按药品名称分组显示药品信息
	 * 
	 * @param searchBean
	 * @return
	 */
	public List<RegOptOperMedicaleventFormBean> searchMedicaleventGroupByCodeList(SearchFormBean searchBean) {
	    if (StringUtils.isBlank(searchBean.getBeid()))
        {
            searchBean.setBeid(getBeid());
        }
		// 将相同药品的数据重新封装
		List<RegOptOperMedicaleventFormBean> resultList = null;
		resultList = evtMedicaleventDao.getMedicalGroupByNameList(searchBean);
		if (null != resultList && resultList.size() > 0) {
			for (RegOptOperMedicaleventFormBean regOptOperMedicaleventFormBean : resultList) {
				// 麻醉用药事件列表
				searchBean.setCode(regOptOperMedicaleventFormBean.getCode());
				// 传递持续 or 非持续 or tci模式
				searchBean.setDurable(regOptOperMedicaleventFormBean.getDurable());
				if("1".equals(regOptOperMedicaleventFormBean.getDurable())){
                    searchBean.setMedEventId(regOptOperMedicaleventFormBean.getMedEventId());
                }else{
                    searchBean.setMedEventId(null);
                }
				List<SearchOptOperMedicalevent> medicaleventList = evtMedicaleventDao.searchMedicaleventList(searchBean);
				float dosage = 0f;
				if (null != medicaleventList && medicaleventList.size() > 0) {
					for (int i = 0; i < medicaleventList.size(); i++) {
						SearchOptOperMedicalevent medicalevent = medicaleventList.get(i);
						dosage += medicalevent.getDosage();
						List<EvtMedicalEventDetail> medDetailList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId());
						// 设值到medDetailList对象中
						medicalevent.setMedDetailList(medDetailList);
						
						medicalevent.setMedTakeWayName(getMedTakeWayName(medicalevent));
					}
				}
				regOptOperMedicaleventFormBean.setDosage(dosage);
				regOptOperMedicaleventFormBean.setDosageUnit(regOptOperMedicaleventFormBean.getDosageUnit());
				regOptOperMedicaleventFormBean.setMedicalEventList(medicaleventList);
			}
		}
		
		return resultList;
	}
	
	/**
     * 按药品名称分组显示药品信息
     * 
     * @param searchBean
     * @return
     */
    public List<RegOptOperMedicaleventFormBean> searchMedicaleventGroupByCodeListQNZ(SearchFormBean searchBean) {
        if (StringUtils.isBlank(searchBean.getBeid()))
        {
            searchBean.setBeid(getBeid());
        }
        // 将相同药品的数据重新封装
        List<RegOptOperMedicaleventFormBean> resultList = null;
        resultList = evtMedicaleventDao.getMedicalGroupByNameListQNZ(searchBean);
        if (null != resultList && resultList.size() > 0) {
            for (RegOptOperMedicaleventFormBean regOptOperMedicaleventFormBean : resultList) {
                // 麻醉用药事件列表
                searchBean.setCode(regOptOperMedicaleventFormBean.getCode());
                /*// 传递持续 or 非持续 or tci模式
                searchBean.setDurable(regOptOperMedicaleventFormBean.getDurable());
                if("1".equals(regOptOperMedicaleventFormBean.getDurable())){
                    searchBean.setMedEventId(regOptOperMedicaleventFormBean.getMedEventId());
                }else{
                    searchBean.setMedEventId(null);
                }*/ 
                List<SearchOptOperMedicalevent> medicaleventList = evtMedicaleventDao.searchMedicaleventList(searchBean);
                List<String> medWayList = new ArrayList<String>();
                float dosage = 0f;
                if (null != medicaleventList && medicaleventList.size() > 0) {
                    for (int i = 0; i < medicaleventList.size(); i++) {
                        SearchOptOperMedicalevent medicalevent = medicaleventList.get(i);
                        dosage += medicalevent.getDosage();
                        List<EvtMedicalEventDetail> medDetailList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId());
                        // 设值到medDetailList对象中
                        medicalevent.setMedDetailList(medDetailList);
                        
                        medicalevent.setMedTakeWayName(getMedTakeWayName(medicalevent));
                        if (!medWayList.contains(medicalevent.getMedTakeWayName()))
                        {
                            medWayList.add(medicalevent.getMedTakeWayName());
                        }
                    }
                }
                regOptOperMedicaleventFormBean.setMedWay(StringUtils.getStringByList(medWayList)); 
                regOptOperMedicaleventFormBean.setDosage(dosage);
                regOptOperMedicaleventFormBean.setDosageUnit(regOptOperMedicaleventFormBean.getDosageUnit());
                regOptOperMedicaleventFormBean.setMedicalEventList(medicaleventList);
                
                if (null != regOptOperMedicaleventFormBean.getPackageDosageAmount() && 0 != regOptOperMedicaleventFormBean.getPackageDosageAmount().floatValue())
                {
                    Float quantity = new Float(Math.ceil(regOptOperMedicaleventFormBean.getDosage()/regOptOperMedicaleventFormBean.getPackageDosageAmount()));
                    regOptOperMedicaleventFormBean.setQuantity(quantity);
                    
                    if (null != regOptOperMedicaleventFormBean.getPriceMinPackage())
                    {
                        Float amount = Arith.multiply(regOptOperMedicaleventFormBean.getPriceMinPackage(),quantity);
                        regOptOperMedicaleventFormBean.setAmout(amount);
                    }
                }
            }
        }
        
        return resultList;
    }
	
	
	/**
     * 按药品名称分组显示药品信息
     * 
     * @param searchBean
     * @return
     */
    public List<RegOptOperMedicaleventFormBean> searchMedicaleventGroupByCodeListNHFE(SearchFormBean searchBean) {
        if (StringUtils.isBlank(searchBean.getBeid()))
        {
            searchBean.setBeid(getBeid());
        }
        // 将相同药品的数据重新封装
        List<RegOptOperMedicaleventFormBean> resultList = null;
        resultList = evtMedicaleventDao.getMedicalGroupByNameListNHFE(searchBean);
        if(null != resultList && resultList.size()>0){
            for (RegOptOperMedicaleventFormBean regOptOperMedicaleventFormBean : resultList) {
                //麻醉用药事件列表
                searchBean.setCode(regOptOperMedicaleventFormBean.getCode());
                searchBean.setDurable(regOptOperMedicaleventFormBean.getDurable());
                if("1".equals(regOptOperMedicaleventFormBean.getDurable())){
                    searchBean.setMedEventId(regOptOperMedicaleventFormBean.getMedEventId());
                }else{
                    searchBean.setMedEventId(null);
                }
                RegOptOperMedicaleventFormBean romf = evtMedicaleventDao.getUseMedicalTotalById(searchBean);
                
                List<SearchOptOperMedicalevent> operMedicaleventList = evtMedicaleventDao.searchMedicaleventList(searchBean);
                float dosage = 0f;
				//同一个药品的剂量要相加
				if(null != operMedicaleventList && operMedicaleventList.size()>0)
				{
					for(SearchOptOperMedicalevent searchOptOperMedicalevent :operMedicaleventList)
					{
						dosage += searchOptOperMedicalevent.getDosage();
						List<EvtMedicalEventDetail> medDetailList = evtMedicalEventDetailDao.selectByMedEventandDocId(searchOptOperMedicalevent.getMedEventId());
                        // 设值到medDetailList对象中
						searchOptOperMedicalevent.setMedDetailList(medDetailList);
					}
				}
				regOptOperMedicaleventFormBean.setDosage(dosage);
                regOptOperMedicaleventFormBean.setDosageUnit(romf.getDosageUnit());
                setMedTakeWayList(operMedicaleventList);
                regOptOperMedicaleventFormBean.setMedicalEventList(operMedicaleventList);
            }
        }
        /*if (null != resultList && resultList.size() > 0) {
            for (RegOptOperMedicaleventFormBean regOptOperMedicaleventFormBean : resultList) {
                // 麻醉用药事件列表
                searchBean.setCode(regOptOperMedicaleventFormBean.getCode());
                // 传递持续 or 非持续 or tci模式
                searchBean.setDurable(regOptOperMedicaleventFormBean.getDurable());
                List<SearchOptOperMedicalevent> medicaleventList = evtMedicaleventDao.searchMedicaleventList(searchBean);

                if (null != medicaleventList && medicaleventList.size() > 0) {
                    for (int i = 0; i < medicaleventList.size(); i++) {
                        SearchOptOperMedicalevent medicalevent = medicaleventList.get(i);
                        List<EvtMedicalEventDetail> medDetailList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId());
                        // 设值到medDetailList对象中
                        medicalevent.setMedDetailList(medDetailList);
                    }
                }
                regOptOperMedicaleventFormBean.setMedicalEventList(medicaleventList);
            }
        }*/
        for (RegOptOperMedicaleventFormBean formBean : resultList) {
            List<SearchOptOperMedicalevent> medicalEventList = formBean.getMedicalEventList();
            for (SearchOptOperMedicalevent medicalevent : medicalEventList) {
                medicalevent.setMedTakeWayName(getMedTakeWayName(medicalevent));
            }
        }
        return resultList;
    }

	/**
	 * 查询用药事件表数据
	 * 
	 * @param searchBean
	 * @return
	 */
	public List<EvtMedicalEvent> queryMedicaleventListById(SearchFormBean searchBean) {
		return evtMedicaleventDao.queryMedicaleventListById(searchBean);
	}

	public List<SearchOptOperMedicalevent> getPacuMedicaleventList(String docId, String medIds, List<String> medIdLs) {
		return evtMedicaleventDao.getPacuMedicaleventList(docId, medIds, medIdLs, getBeid());
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional
	public void saveMedicalevent(EvtMedicalEvent medicalevent, ResponseValue value) {
		//if (medicalevent.getDocType() != 2) { // 1为麻醉记录单 2为复苏单
		//	DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicalevent.getDocId());
		//}

		SearchFormBean searchFormBean = new SearchFormBean();
		searchFormBean.setDocId(medicalevent.getDocId());
		searchFormBean.setId(medicalevent.getMedEventId());
		List<EvtMedicalEvent> List = evtMedicaleventDao.checkMedicaleventCanInsert(searchFormBean, medicalevent.getMedicineId());
		
		// 持续用药
		if ("1".equals(medicalevent.getDurable())) {
			for (EvtMedicalEvent event : List) {
				Date startTime = medicalevent.getStartTime();
				Date eventStartTime = event.getStartTime();
				Date endTime = medicalevent.getEndTime();
				Date eventEndTime = event.getEndTime();
				// (startTime < eventStartTime && endTime < eventStartTime) || startTime > eventEndTime  则继续往下走，并存入
				if ((startTime.getTime() < eventStartTime.getTime() && endTime.getTime()<eventStartTime.getTime()) || startTime.getTime()>eventEndTime.getTime()) {
					continue;
				} else {
					value.setResultCode("10000001");
					value.setResultMessage("该药品在开始时间：" + medicalevent.getStartTime() + "至结束时间：" + medicalevent.getEndTime() + ", 已经存在持续用药情况,请勿重复添加!");
					return;
				}
			}
		} else {// 普通用药
			for (EvtMedicalEvent event : List) {
				Date startTime = medicalevent.getStartTime();
				Date eventStartTime = event.getStartTime();
				//Date endTime = medicalevent.getEndTime();
				Date eventEndTime = event.getEndTime();
				// !(startTime > eventStartTime && startTime < eventEndTime) 则进行插入
				if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime())) {
					continue;
				} else {
					value.setResultCode("10000001");
					value.setResultMessage("该药品在开始时间：" + medicalevent.getStartTime() + ", 已经存在持续用药情况,请勿重复添加!");
					return;
				}
			}
		}
		String mtwId = "";
		if (medicalevent.getMedTakeWayIdList() != null && medicalevent.getMedTakeWayIdList().size() > 0) {
			for (String medTakeWayId : medicalevent.getMedTakeWayIdList()) {
				if (StringUtils.isBlank(mtwId)) {
					mtwId = medTakeWayId;
				}else {
					mtwId += "," + medTakeWayId;
				}
			}
		}else if (StringUtils.isNotBlank(medicalevent.getMedTakeWayId())) {
			mtwId = medicalevent.getMedTakeWayId();
		}
		if (StringUtils.isNotBlank(medicalevent.getMedEventId())) {
			
			medicalevent.setMedTakeWayId(mtwId);
			
			List<EvtMedicalEventDetail> mdList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId()); // 根据时间排序
			if (null != mdList && mdList.size() > 0) {
				if (mdList.size() == 1) {// 一个说明只有一条浓度的记录
					EvtMedicalEventDetail mdDetail = mdList.get(0);

					if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
						if (null != medicalevent.getTciValue()) {
							mdDetail.setFlow(medicalevent.getTciValue());
						}
						if (null != medicalevent.getTciUnit()) {
							mdDetail.setFlowUnit(medicalevent.getFlowUnit());
						}
					} else {
						if (null != medicalevent.getFlow() && medicalevent.getFlow() > 0) {
							mdDetail.setFlow(medicalevent.getFlow());
						}
						if (null != medicalevent.getFlowUnit()) {
							mdDetail.setFlowUnit(medicalevent.getFlowUnit());
						}
					}

					if (null != medicalevent.getThickness() && medicalevent.getThickness() > 0) {
						mdDetail.setThickness(medicalevent.getThickness());
					}
					if (null != medicalevent.getThicknessUnit()) {
						mdDetail.setThicknessUnit(medicalevent.getThicknessUnit());
					}
					if (null != medicalevent.getStartTime()) {
						Date startDate = medicalevent.getStartTime();
						mdDetail.setStartTime(startDate);
					}
//					if (null != medicalevent.getEndTime()) {
//						Date endDate = medicalevent.getEndTime();
//						mdDetail.setEndTime(endDate);
//					}
					evtMedicalEventDetailDao.updateByPrimaryKey(mdDetail);
					
				} else { // 多条记录，则说明修改过浓度, 修改开始时间不大于第一条结束时间，结束时间不能小于最后一条的开始时间
					EvtMedicalEventDetail firstMd = mdList.get(0);
					EvtMedicalEventDetail lastMd = mdList.get(mdList.size() - 1);
					if (null != medicalevent.getStartTime()) {
						Date startDate = medicalevent.getStartTime();
						//Date firstMdEndTime = firstMd.getEndTime();
						Date firstMdEndTime = firstMd.getStartTime();
						
						if (startDate.getTime() >= firstMdEndTime.getTime()) { // 开始时间大于第一条记录的结束时间
							value.setResultCode("10000001");
							value.setResultMessage("该药品开始时间大于等于修改浓度后的第一条的结束时间，不能修改！");
							return;
						}
						firstMd.setStartTime(startDate);

						if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
							if (null != medicalevent.getTciValue()) {
								firstMd.setFlow(medicalevent.getTciValue());
							}
							if (null != medicalevent.getTciUnit()) {
								firstMd.setFlowUnit(medicalevent.getFlowUnit());
							}
						} else {
							if (medicalevent.getFlow() > 0) {
								firstMd.setFlow(medicalevent.getFlow());
							}
							if (null != medicalevent.getFlowUnit()) {
								firstMd.setFlowUnit(medicalevent.getFlowUnit());
							}
						}

						/*
						 * if(medicalevent.getFlow() > 0){
						 * firstMd.setFlow(medicalevent.getFlow()); } if(null !=
						 * medicalevent.getFlowUnit()){
						 * firstMd.setFlowUnit(medicalevent.getFlowUnit()); }
						 */
						if (medicalevent.getThickness() > 0) {
							firstMd.setThickness(medicalevent.getThickness());
						}
						if (null != medicalevent.getThicknessUnit()) {
							firstMd.setThicknessUnit(medicalevent.getThicknessUnit());
						}
						evtMedicalEventDetailDao.updateByPrimaryKey(firstMd);
					}
					if (null != medicalevent.getEndTime()) {
						Date endDate = medicalevent.getEndTime();
						Date lastMdStartTime = lastMd.getStartTime();
						if (endDate.getTime() <= lastMdStartTime.getTime()) {
							value.setResultCode("10000001");
							value.setResultMessage("该药品结束时间小于等于修改浓度后的最后一条记录的开始时间，不能修改！");
							return;
						}
						//lastMd.setEndTime(endDate);
						evtMedicalEventDetailDao.updateByPrimaryKey(lastMd);
					}

				}
			}
			
			/**
    		 * 2017-10-30沈阳本溪
    		 * 将修改痕迹保存到表中
    		 */
            DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicalevent.getDocId());
            if(null!=anaesRecord){
            	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
    	        //如果当前状态不为术中时，则需要记录变更信息
            	if(null!=regOpt && !"04".equals(regOpt.getState())){
    	        	if (StringUtils.isNotBlank(medicalevent.getMedEventId())) {
    					EvtMedicalEvent oldEvt = evtMedicaleventDao.selectByPrimaryKey(medicalevent.getMedEventId());
    					
    					CompareObject compare = new CompareObject();
    					List<ChangeValueFormbean> changeList = new ArrayList<ChangeValueFormbean>();
    					try {
    						changeList = compare.getCompareResultByFields(oldEvt, medicalevent);
    						if(null!=changeList && changeList.size()>0){
    							
    							for (ChangeValueFormbean changeValueFormbean : changeList) {
    								//排除非表内字段产生的差异，如medTakeWayIdList等
    								Map<String,String> hisMap = compare.getColumnListByTableName("evt_medicalevent");
    								if(hisMap.containsKey(changeValueFormbean.getModProperty())){
    									EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
    									evtModRd.setBeid(getBeid());
    									evtModRd.setIp(getIP());
    									evtModRd.setOperId(getUserName());
    									evtModRd.setEventId(medicalevent.getMedEventId());
    									evtModRd.setRegOptId(anaesRecord.getRegOptId());
    									evtModRd.setModifyDate(new Date());
    									evtModRd.setModTable("evt_medicalevent(用药事件表)");
    									evtModRd.setOperModule("术中用药("+basMedicineDao.selectByPrimaryKey(medicalevent.getMedicineId()).getName()+")");
    									evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
    									evtModRd.setModProperty(compare.getColumnContentByProperty("evt_medicalevent", changeValueFormbean.getModProperty()));
    									evtModRd.setOldValue(changeValueFormbean.getOldValue());
    	    							evtModRd.setNewValue(changeValueFormbean.getNewValue());
    									evtModRd.setRemark("修改");
    									evtAnaesModifyRecordDao.insert(evtModRd);
    								}
    							}
    						}
    					} catch (Exception e) {
    						logger.info("------getCompareResultByFields-----"+Exceptions.getStackTraceAsString(e));
    						throw new CustomException(Exceptions.getStackTraceAsString(e));
    					}
    	        	}
    	        }
            }
            
            evtMedicaleventDao.updateByPrimaryKeySelective(medicalevent);
            
			// medicaleventDetailDao.updateByPrimaryKeySelective(medicaleventDetail);
		} else {
			String medEventId = GenerateSequenceUtil.generateSequenceNo();
			medicalevent.setMedEventId(medEventId);
			medicalevent.setMedTakeWayId(mtwId);
			Integer durable = medicalevent.getDurable();
			if (null != durable) {
				evtMedicaleventDao.insert(medicalevent);
			} else {
				medicalevent.setDurable(0);// 如果页面未传值，则为非持续用药 ，默认为0
				evtMedicaleventDao.insert(medicalevent);
			}

			EvtMedicalEventDetail md = new EvtMedicalEventDetail();
			md.setId(GenerateSequenceUtil.generateSequenceNo());
			//md.setDocId(medicalevent.getDocId());
			md.setMedEventId(medEventId);
			if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
				if (null != medicalevent.getTciValue()) {
					md.setFlow(medicalevent.getTciValue());
				}
				if (null != medicalevent.getTciUnit()) {
					md.setFlowUnit(medicalevent.getFlowUnit());
				}
			} else {
				md.setFlow(medicalevent.getFlow());
				md.setFlowUnit(medicalevent.getFlowUnit());
			}

			md.setThickness(medicalevent.getThickness());
			md.setThicknessUnit(medicalevent.getThicknessUnit());
			Date startDate = medicalevent.getStartTime();
			md.setStartTime(startDate);
//			Date endtime = medicalevent.getEndTime();
//			if (null != endtime) {
//				md.setEndTime(endtime);
//			}
			
			/**
             * 2017-11-02
             * 记录用药术后用药事件明细历史痕迹
             */
            DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicalevent.getDocId());
            if(null!=anaesRecord){
            	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
            	
            	String medName = basMedicineDao.selectByPrimaryKey(medicalevent.getMedicineId()).getName();
            	
    	        //如果当前状态不为术中时，则需要记录变更信息
            	if(null!=regOpt && !"04".equals(regOpt.getState())){
            		
            		
		            EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
					evtModRd.setBeid(getBeid());
					evtModRd.setIp(getIP());
					evtModRd.setOperId(getUserName());
					evtModRd.setEventId(medEventId);
					evtModRd.setRegOptId(anaesRecord.getRegOptId());
					evtModRd.setModifyDate(new Date());
					evtModRd.setModTable("evt_medicalevent(用药事件表)");
					evtModRd.setOperModule("术中用药");
					evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
					evtModRd.setModProperty("新增用药("+medName+")");
					
					StringBuffer buffer = new StringBuffer();
					buffer.append("开始时间:"+DateUtils.formatDateTime(medicalevent.getStartTime()));
					if(null!=medicalevent.getDosage() && medicalevent.getDosage()>0){
						buffer.append("; 剂量:"+medicalevent.getDosage());
					}
					if(null!=medicalevent.getFlow() && medicalevent.getFlow()>0){
						buffer.append("; 流速:"+medicalevent.getFlow());
						buffer.append("; 流速单位:"+medicalevent.getFlowUnit());
					}
					if(null!=medicalevent.getThickness() && medicalevent.getThickness()>0){
						buffer.append("; 浓度:"+medicalevent.getThickness());
						buffer.append("; 浓度单位:"+medicalevent.getThicknessUnit());
					}
					
					evtModRd.setNewValue(buffer.toString());
					evtModRd.setRemark("新增");
					evtAnaesModifyRecordDao.insert(evtModRd);
		            
//		        	evtModRd = new EvtAnaesModifyRecord();
//					evtModRd.setBeid(getBeid());
//					evtModRd.setIp(getIP());
//					evtModRd.setOperId(getUserName());
//					evtModRd.setEventId(medEventId);
//					evtModRd.setRegOptId(anaesRecord.getRegOptId());
//					evtModRd.setModifyDate(new Date());
//					evtModRd.setModTable("evt_medicalevent_detail(用药事件明细表)");
//					evtModRd.setOperModule("术中用药明细");
//					evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
//					evtModRd.setModProperty("新增用药明细("+medName+")");
//					evtModRd.setNewValue(buffer.toString());
//					evtModRd.setRemark("新增");
//					evtAnaesModifyRecordDao.insert(evtModRd);
            	}
            }

			evtMedicalEventDetailDao.insert(md);
		}
		value.put("medicineId", medicalevent.getMedEventId());
		LogUtils.saveOperateLog(medicalevent.getDocId(), LogUtils.OPT_TYPE_INFO_SAVE, LogUtils.OPT_MODULE_INTERFACE, "术中添加用药事件保存", JsonType.jsonType(medicalevent), UserUtils.getUserCache(), getBeid());
	}

	   @Transactional
	    public void saveMedicaleventQNZ(EvtMedicalEvent medicalevent, ResponseValue value) {
	        SearchFormBean searchFormBean = new SearchFormBean();
	        searchFormBean.setDocId(medicalevent.getDocId());
	        searchFormBean.setId(medicalevent.getMedEventId());
	        List<EvtMedicalEvent> List = evtMedicaleventDao.checkMedicaleventCanInsert(searchFormBean, medicalevent.getMedicineId());
	        // 持续用药
	        if (null != medicalevent.getDurable() && 1 ==medicalevent.getDurable()) {
	            for (EvtMedicalEvent event : List) {
	                Date startTime = medicalevent.getStartTime();
	                Date eventStartTime = event.getStartTime();
	                Date endTime = medicalevent.getEndTime();
	                Date eventEndTime = event.getEndTime();
	                if (null == endTime && null == eventEndTime)
	                {
	                    //前一条用药事件的结束时间设置为后一条的开始时间
	                    if (startTime.getTime() < eventStartTime.getTime())
	                    {
	                        medicalevent.setEndTime(eventStartTime);
	                        continue;
	                    }
	                    else
	                    {
	                        event.setEndTime(startTime);
                            evtMedicaleventDao.updateByPrimaryKeySelective(event);
	                        continue;
	                    }
	                }
	                else if (null != endTime && null == eventEndTime)
	                {
	                    if (eventStartTime.getTime() >= endTime.getTime())
	                    {
	                        continue;
	                    }
	                    else if (eventStartTime.getTime() <= startTime.getTime())
	                    {
	                        event.setEndTime(startTime);
	                        evtMedicaleventDao.updateByPrimaryKeySelective(event);
	                        continue;
	                    }
	                    else
	                    {
	                        value.setResultCode("10000001");
	                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间：" + DateUtils.formatDateTime(endTime) + ", 已经存在持续用药情况,请勿重复添加!");
	                        return;
	                    }
	                }
	                else if (null == endTime && null != eventEndTime)
	                {
	                    if (startTime.getTime() >= eventEndTime.getTime())
	                    {
	                        continue;
	                    }
	                    else if (startTime.getTime() < eventStartTime.getTime())
	                    {
	                        medicalevent.setEndTime(eventStartTime);
	                        continue;
	                    }
	                    else
	                    {
	                        value.setResultCode("10000001");
	                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间：" + DateUtils.formatDateTime(eventEndTime) + ", 已经存在持续用药情况,请勿重复添加!");
	                        return;
	                    }
	                } 
	                else
	                {
	                    if (startTime.getTime() >= eventEndTime.getTime() || endTime.getTime() < eventStartTime.getTime())
	                    {
	                        continue;
	                    }
	                    else
	                    {
	                        value.setResultCode("10000001");
	                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间：" + DateUtils.formatDateTime(eventEndTime) + ", 已经存在持续用药情况,请勿重复添加!");
	                        return;
	                    }
	                }
	            }
	        } else if (null != medicalevent.getDurable() && 0 ==medicalevent.getDurable()){// 普通用药 
	            for (EvtMedicalEvent event : List) {
                Date startTime = medicalevent.getStartTime();
                Date eventStartTime = event.getStartTime();
                Date eventEndTime = event.getEndTime();
                if (null == eventEndTime)
                {
                    event.setEndTime(startTime);
                    evtMedicaleventDao.updateByPrimaryKeySelective(event);
                    continue;
                }
                else
                {
                    
                    if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime()))
                    {
                        continue;
                    }
                    else
                    {
                        value.setResultCode("10000001");
                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间：" + DateUtils.formatDateTime(eventEndTime) + ", 已经存在持续用药情况,请勿重复添加!");
                        return;
                    }
                }
	            }
	        }
	        String mtwId = "";
	        if (medicalevent.getMedTakeWayIdList() != null && medicalevent.getMedTakeWayIdList().size() > 0) {
	            for (String medTakeWayId : medicalevent.getMedTakeWayIdList()) {
	                if (StringUtils.isBlank(mtwId)) {
	                    mtwId = medTakeWayId;
	                }else {
	                    mtwId += "," + medTakeWayId;
	                }
	            }
	        }else if (StringUtils.isNotBlank(medicalevent.getMedTakeWayId())) {
	            mtwId = medicalevent.getMedTakeWayId();
	        }
	        if (StringUtils.isNotBlank(medicalevent.getMedEventId())) {
	            
	            medicalevent.setMedTakeWayId(mtwId);
	            
	            List<EvtMedicalEventDetail> mdList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId()); // 根据时间排序
	            if (null != mdList && mdList.size() > 0) {
	                if (mdList.size() == 1) {// 一个说明只有一条浓度的记录
	                    EvtMedicalEventDetail mdDetail = mdList.get(0);

	                    if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
	                        if (null != medicalevent.getTciValue()) {
	                            mdDetail.setFlow(medicalevent.getTciValue());
	                        }
	                        if (null != medicalevent.getTciUnit()) {
	                            mdDetail.setFlowUnit(medicalevent.getFlowUnit());
	                        }
	                    } else {
	                        if (null != medicalevent.getFlow() && medicalevent.getFlow() > 0) {
	                            mdDetail.setFlow(medicalevent.getFlow());
	                        }
	                        if (null != medicalevent.getFlowUnit()) {
	                            mdDetail.setFlowUnit(medicalevent.getFlowUnit());
	                        }
	                    }

	                    if (null != medicalevent.getThickness() && medicalevent.getThickness() > 0) {
	                        mdDetail.setThickness(medicalevent.getThickness());
	                    }
	                    if (null != medicalevent.getThicknessUnit()) {
	                        mdDetail.setThicknessUnit(medicalevent.getThicknessUnit());
	                    }
	                    if (null != medicalevent.getStartTime()) {
	                        Date startDate = medicalevent.getStartTime();
	                        mdDetail.setStartTime(startDate);
	                    }
//	                    if (null != medicalevent.getEndTime()) {
//	                        Date endDate = medicalevent.getEndTime();
//	                        mdDetail.setEndTime(endDate);
//	                    }
	                    evtMedicalEventDetailDao.updateByPrimaryKey(mdDetail);
	                } else { // 多条记录，则说明修改过浓度, 修改开始时间不大于第一条结束时间，结束时间不能小于最后一条的开始时间
	                    EvtMedicalEventDetail firstMd = mdList.get(0);
	                    EvtMedicalEventDetail lastMd = mdList.get(mdList.size() - 1);
	                    if (null != medicalevent.getStartTime()) {
	                        Date startDate = medicalevent.getStartTime();
	                        //Date firstMdEndTime = firstMd.getStartTime();
	                        if (startDate.getTime() >= mdList.get(1).getStartTime().getTime()) { // 开始时间大于第一条记录的结束时间
	                            value.setResultCode("10000001");
	                            value.setResultMessage("该药品开始时间大于等于修改浓度后的第一条的结束时间，不能修改！");
	                            return;
	                        }
	                        firstMd.setStartTime(startDate);

	                        if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
	                            if (null != medicalevent.getTciValue()) {
	                                firstMd.setFlow(medicalevent.getTciValue());
	                            }
	                            if (null != medicalevent.getTciUnit()) {
	                                firstMd.setFlowUnit(medicalevent.getFlowUnit());
	                            }
	                        } else {
	                            if (medicalevent.getFlow() > 0) {
	                                firstMd.setFlow(medicalevent.getFlow());
	                            }
	                            if (null != medicalevent.getFlowUnit()) {
	                                firstMd.setFlowUnit(medicalevent.getFlowUnit());
	                            }
	                        }

	                        /*
	                         * if(medicalevent.getFlow() > 0){
	                         * firstMd.setFlow(medicalevent.getFlow()); } if(null !=
	                         * medicalevent.getFlowUnit()){
	                         * firstMd.setFlowUnit(medicalevent.getFlowUnit()); }
	                         */
	                        if (medicalevent.getThickness() > 0) {
	                            firstMd.setThickness(medicalevent.getThickness());
	                        }
	                        if (null != medicalevent.getThicknessUnit()) {
	                            firstMd.setThicknessUnit(medicalevent.getThicknessUnit());
	                        }
	                        evtMedicalEventDetailDao.updateByPrimaryKey(firstMd);
	                    }
	                    if (null != medicalevent.getEndTime()) {
	                        Date endDate = medicalevent.getEndTime();
	                        Date lastMdStartTime = lastMd.getStartTime();
	                        if (endDate.getTime() <= lastMdStartTime.getTime()) {
	                            value.setResultCode("10000001");
	                            value.setResultMessage("该药品结束时间小于等于修改浓度后的最后一条记录的开始时间，不能修改！");
	                            return;
	                        }
	                        //lastMd.setEndTime(endDate);
	                        evtMedicalEventDetailDao.updateByPrimaryKey(lastMd);
	                    }

	                }
	            }
	            
	            evtMedicaleventDao.updateByPrimaryKeySelective(medicalevent);
	            
	            // medicaleventDetailDao.updateByPrimaryKeySelective(medicaleventDetail);
	        } else {
	            String medEventId = GenerateSequenceUtil.generateSequenceNo();
	            medicalevent.setMedEventId(medEventId);
	            medicalevent.setMedTakeWayId(mtwId);
	            Integer durable = medicalevent.getDurable();
	            if (null != durable) {
	                evtMedicaleventDao.insert(medicalevent);
	            } else {
	                medicalevent.setDurable(0);// 如果页面未传值，则为非持续用药 ，默认为0
	                evtMedicaleventDao.insert(medicalevent);
	            }

	            EvtMedicalEventDetail md = new EvtMedicalEventDetail();
	            md.setId(GenerateSequenceUtil.generateSequenceNo());
	            //md.setDocId(medicalevent.getDocId());
	            md.setMedEventId(medEventId);
	            if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
	                if (null != medicalevent.getTciValue()) {
	                    md.setFlow(medicalevent.getTciValue());
	                }
	                if (null != medicalevent.getTciUnit()) {
	                    md.setFlowUnit(medicalevent.getFlowUnit());
	                }
	            } else {
	                md.setFlow(medicalevent.getFlow());
	                md.setFlowUnit(medicalevent.getFlowUnit());
	            }

	            md.setThickness(medicalevent.getThickness());
	            md.setThicknessUnit(medicalevent.getThicknessUnit());
	            Date startDate = medicalevent.getStartTime();
	            md.setStartTime(startDate);
//	            Date endtime = medicalevent.getEndTime();
//	            if (null != endtime) {
//	                md.setEndTime(endtime);
//	            }

	            evtMedicalEventDetailDao.insert(md);
	        }
	        value.put("medicineId", medicalevent.getMedEventId());
	        LogUtils.saveOperateLog(medicalevent.getDocId(), LogUtils.OPT_TYPE_INFO_SAVE, LogUtils.OPT_MODULE_INTERFACE, "术中添加用药事件保存", JsonType.jsonType(medicalevent), UserUtils.getUserCache(), getBeid());
	    }

	@Transactional
    public void updateMedicalEventTime(SearchFormBean searchBean, ResponseValue value)
    {
        EvtMedicalEvent medicalevent = evtMedicaleventDao.selectByPrimaryKey(searchBean.getId());
        if (null == medicalevent)
        {
            value.setResultCode("10000001");
            value.setResultMessage("用药事件不存在");
            return;
        }
        searchBean.setDocId(medicalevent.getDocId());
        List<EvtMedicalEvent> List =
            evtMedicaleventDao.checkMedicaleventCanInsert(searchBean, medicalevent.getMedicineId());
        
        if (null != searchBean.getStartTime())
        {
            medicalevent.setStartTime(DateUtils.getParseTime(searchBean.getStartTime()));
        }
        if (StringUtils.isNotBlank(searchBean.getEndTime()))
        {
            medicalevent.setEndTime(DateUtils.getParseTime(searchBean.getEndTime()));
        }
        
        // 持续用药
        if (null != medicalevent.getDurable() && 1 == medicalevent.getDurable())
        {
            Date startTime = medicalevent.getStartTime();
            Date endTime = medicalevent.getEndTime();
            for (EvtMedicalEvent event : List)
            {
                if (event.getMedEventId().equals(medicalevent.getMedEventId()))
                {
                    continue;
                }
                Date eventStartTime = event.getStartTime();
                Date eventEndTime = event.getEndTime();
                if (null == endTime && null == eventEndTime)
                {
                    // 前一条用药事件的结束时间设置为后一条的开始时间
                    if (startTime.getTime() < eventStartTime.getTime())
                    {
                        medicalevent.setEndTime(eventStartTime);
                        continue;
                    }
                    else
                    {
                        event.setEndTime(startTime);
                        evtMedicaleventDao.updateByPrimaryKeySelective(event);
                        continue;
                    }
                }
                else if (null != endTime && null == eventEndTime)
                {
                    if (eventStartTime.getTime() >= endTime.getTime())
                    {
                        continue;
                    }
                    else if (eventStartTime.getTime() <= startTime.getTime())
                    {
                        event.setEndTime(startTime);
                        evtMedicaleventDao.updateByPrimaryKeySelective(event);
                        continue;
                    }
                    else
                    {
                        value.setResultCode("10000001");
                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间："
                            + DateUtils.formatDateTime(endTime) + ", 已经存在持续用药情况,请勿重复添加!");
                        return;
                    }
                }
                else if (null == endTime && null != eventEndTime)
                {
                    if (startTime.getTime() >= eventEndTime.getTime())
                    {
                        continue;
                    }
                    else if (startTime.getTime() < eventStartTime.getTime())
                    {
                        medicalevent.setEndTime(eventStartTime);
                        continue;
                    }
                    else
                    {
                        value.setResultCode("10000001");
                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间："
                            + DateUtils.formatDateTime(eventEndTime) + ", 已经存在持续用药情况,请勿重复添加!");
                        return;
                    }
                }
                else
                {
                    if (startTime.getTime() >= eventEndTime.getTime() || endTime.getTime() < eventStartTime.getTime())
                    {
                        continue;
                    }
                    else
                    {
                        value.setResultCode("10000001");
                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间："
                            + DateUtils.formatDateTime(eventEndTime) + ", 已经存在持续用药情况,请勿重复添加!");
                        return;
                    }
                }
            }
            
            //修改用药时间需增加判断持续用药明细的时间是否包含在修改后的时间内
            List<EvtMedicalEvent> evtList = evtMedicaleventDao.queryMedicaleventListById(searchBean);
            if(evtList.size()>0){
                EvtMedicalEvent evt = evtList.get(0);
                if (null != evt.getDurable() && 1 == evt.getDurable()){
                    List<EvtMedicalEventDetail> mdList = evtMedicalEventDetailDao.selectByMedEventandDocId(evt.getMedEventId()); // 根据时间排序
                    if(null!=mdList && mdList.size()>1){
                        //获取第二个点的开始时间来比较
                        Date st = mdList.get(1).getStartTime();
                        Date et = mdList.get(mdList.size()-1).getStartTime();
    
                        if(startTime.getTime() >= st.getTime()){
                            value.setResultCode("10000001");
                            value.setResultMessage("修改后的用药开始时间:"+startTime+",不能晚于:"+DateUtils.formatLongTime(st.getTime()));
                            return;
                        }
                        if(endTime.getTime() <= et.getTime()){
                            value.setResultCode("10000001");
                            value.setResultMessage("修改后的用药结束时间:"+startTime+",不能早于:"+DateUtils.formatLongTime(et.getTime()));
                            return;
                        }
                    }
                }
            }
        }
        else if (null != medicalevent.getDurable() && 0 == medicalevent.getDurable())
        {// 普通用药
            for (EvtMedicalEvent event : List)
            {
                if (event.getMedEventId().equals(medicalevent.getMedEventId()))
                {
                    continue;
                }
                
                Date startTime = medicalevent.getStartTime();
                Date eventStartTime = event.getStartTime();
                Date eventEndTime = event.getEndTime();
                if (null == eventEndTime)
                {
                    event.setEndTime(startTime);
                    evtMedicaleventDao.updateByPrimaryKeySelective(event);
                    continue;
                }
                else
                {
                    
                    if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime()))
                    {
                        continue;
                    }
                    else
                    {
                        value.setResultCode("10000001");
                        value.setResultMessage("该药品在开始时间：" + DateUtils.formatDateTime(eventStartTime) + "至结束时间："
                            + DateUtils.formatDateTime(eventEndTime) + ", 已经存在持续用药情况,请勿重复添加!");
                        return;
                    }
                }
            }
        }
        
        evtMedicaleventDao.updateByPrimaryKeySelective(medicalevent);
        List<EvtMedicalEventDetail> mdList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId());
        if (null != mdList && mdList.size() > 0)
        {
            EvtMedicalEventDetail evtMedicalEventDetail = mdList.get(0);
            evtMedicalEventDetail.setStartTime(medicalevent.getStartTime());
            evtMedicalEventDetailDao.updateByPrimaryKey(evtMedicalEventDetail);
        }
        LogUtils.saveOperateLog(medicalevent.getDocId(), LogUtils.OPT_TYPE_INFO_SAVE, LogUtils.OPT_MODULE_INTERFACE, "修改用药事件时间", JsonType.jsonType(medicalevent), UserUtils.getUserCache(), getBeid());
    }
	
	
	@Transactional
	public void batchSaveMedicalevent(List<EvtMedicalEvent> medicaleventList, ResponseValue value) {
		if (null != medicaleventList && medicaleventList.size() > 0) {
			List<String> successList = new ArrayList<String>();
			List<String> failList = new ArrayList<String>();
			//DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicaleventList.get(0).getDocId());
			for (EvtMedicalEvent medicalevent : medicaleventList) {

				SearchFormBean searchFormBean = new SearchFormBean();
				searchFormBean.setDocId(medicalevent.getDocId());
				searchFormBean.setId(medicalevent.getMedEventId());
				List<EvtMedicalEvent> List = evtMedicaleventDao.checkMedicaleventCanInsert(searchFormBean, medicalevent.getMedicineId() + "");

				boolean flag = false;
				// 持续用药
				if ("1".equals(medicalevent.getDurable())) {
					for (EvtMedicalEvent event : List) {
						Date startTime = medicalevent.getStartTime();
						Date eventStartTime = event.getStartTime();
						Date endTime = medicalevent.getEndTime();
						Date eventEndTime = event.getEndTime();
						// (startTime < eventStartTime && endTime < eventStartTime) || startTime > eventEndTime  则继续往下走，并存入
						if ((startTime.getTime() < eventStartTime.getTime() && endTime.getTime()<eventStartTime.getTime()) || startTime.getTime()>eventEndTime.getTime()) {
						//if ((DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getStartTime())) < 0 && DateUtils.parseDate(medicalevent.getEndTime()).compareTo(DateUtils.parseDate(event.getStartTime())) < 0) || DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getEndTime())) > 0) {
							continue;
						} else {
							// value.setResultCode("10000001");
							// value.setResultMessage("该药品在开始时间："+medicalevent.getStarttime()+"至结束时间："+medicalevent.getEndtime()+", 已经存在持续用药情况,请勿重复添加!");
							// return;

							flag = true;
						}
					}
				} else {// 普通用药
					for (EvtMedicalEvent event : List) {
						Date startTime = medicalevent.getStartTime();
						Date eventStartTime = event.getStartTime();
						//Date endTime = medicalevent.getEndTime();
						Date eventEndTime = event.getEndTime();
						// !(startTime > eventStartTime && startTime < eventEndTime) 则进行插入
						if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime())) {
						//if (!(DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getStartTime())) > 0 && DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getEndTime())) < 0)) {
							continue;
						} else {
							// value.setResultCode("10000001");
							// value.setResultMessage("该药品在开始时间："+medicalevent.getStarttime()+", 已经存在持续用药情况,请勿重复添加!");
							// return;
							flag = true;
						}
					}
				}
				BasMedicine medicine = basMedicineDao.queryMedicineById(medicalevent.getMedicineId() + "");

				if (flag) {
					failList.add(medicine.getName());
					continue;
				}

				if (!StringUtils.isNotBlank(medicalevent.getMedEventId())) {
					medicalevent.setMedEventId(GenerateSequenceUtil.generateSequenceNo());
					evtMedicaleventDao.insert(medicalevent);
					successList.add(medicine.getName());
				}
				
				EvtMedicalEventDetail md = new EvtMedicalEventDetail();
				md.setId(GenerateSequenceUtil.generateSequenceNo());
				//md.setDocId(medicalevent.getDocId());
				md.setMedEventId(medicalevent.getMedEventId());
				if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
					if (null != medicalevent.getTciValue()) {
						md.setFlow(medicalevent.getTciValue());
					}
					if (null != medicalevent.getTciUnit()) {
						md.setFlowUnit(medicalevent.getFlowUnit());
					}
				} else {
					md.setFlow(medicalevent.getFlow());
					md.setFlowUnit(medicalevent.getFlowUnit());
				}

				md.setThickness(medicalevent.getThickness());
				md.setThicknessUnit(medicalevent.getThicknessUnit());
				Date startDate = medicalevent.getStartTime();
				md.setStartTime(startDate);
//				Date endtime = medicalevent.getEndTime();
//				if (null != endtime) {
//					md.setEndTime(endtime);
//				}

				evtMedicalEventDetailDao.insert(md);
				
			}
			value.put("success", successList);
			value.put("fail", failList);

		}
	}
	
	@Transactional
    public void saveMedicaleventNHFE(EvtMedicalEvent medicalevent, ResponseValue value) {
        //if (medicalevent.getDocType() != 2) { // 1为麻醉记录单 2为复苏单
        //  DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicalevent.getDocId());
        //}

        SearchFormBean searchFormBean = new SearchFormBean();
        searchFormBean.setDocId(medicalevent.getDocId());
        searchFormBean.setId(medicalevent.getMedEventId());
        List<EvtMedicalEvent> List = evtMedicaleventDao.checkMedicaleventCanInsert(searchFormBean, medicalevent.getMedicineId());
        
        // 持续用药
        /*if ("1".equals(medicalevent.getDurable())) {
            for (EvtMedicalEvent event : List) {
                Date startTime = medicalevent.getStartTime();
                Date eventStartTime = event.getStartTime();
                Date endTime = medicalevent.getEndTime();
                Date eventEndTime = event.getEndTime();
                // (startTime < eventStartTime && endTime < eventStartTime) || startTime > eventEndTime  则继续往下走，并存入
                if ((startTime.getTime() < eventStartTime.getTime() && endTime.getTime()<eventStartTime.getTime()) || startTime.getTime()>eventEndTime.getTime()) {
                    continue;
                } else {
                    value.setResultCode("10000001");
                    value.setResultMessage("该药品在开始时间：" + medicalevent.getStartTime() + "至结束时间：" + medicalevent.getEndTime() + ", 已经存在持续用药情况,请勿重复添加!");
                    return;
                }
            }
        } else {// 普通用药
            for (EvtMedicalEvent event : List) {
                Date startTime = medicalevent.getStartTime();
                Date eventStartTime = event.getStartTime();
                //Date endTime = medicalevent.getEndTime();
                Date eventEndTime = event.getEndTime();
                // !(startTime > eventStartTime && startTime < eventEndTime) 则进行插入
                if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime())) {
                    continue;
                } else {
                    value.setResultCode("10000001");
                    value.setResultMessage("该药品在开始时间：" + medicalevent.getStartTime() + ", 已经存在持续用药情况,请勿重复添加!");
                    return;
                }
            }
        }*/
        String mtwId = "";
        if (medicalevent.getMedTakeWayIdList() != null && medicalevent.getMedTakeWayIdList().size() > 0) {
            for (String medTakeWayId : medicalevent.getMedTakeWayIdList()) {
                if (StringUtils.isBlank(mtwId)) {
                    mtwId = medTakeWayId;
                }else {
                    mtwId += "," + medTakeWayId;
                }
            }
        }else if (StringUtils.isNotBlank(medicalevent.getMedTakeWayId())) {
            mtwId = medicalevent.getMedTakeWayId();
        }
        
        if (StringUtils.isNotBlank(medicalevent.getMedEventId())) {
        	
            List<EvtMedicalEventDetail> mdList = evtMedicalEventDetailDao.selectByMedEventandDocId(medicalevent.getMedEventId()); // 根据时间排序
            if (null != mdList && mdList.size() > 0) {
                if (mdList.size() == 1) {// 一个说明只有一条浓度的记录
                    EvtMedicalEventDetail mdDetail = mdList.get(0);

                    if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
                        if (null != medicalevent.getTciValue()) {
                            mdDetail.setFlow(medicalevent.getTciValue());
                        }
                        if (null != medicalevent.getTciUnit()) {
                            mdDetail.setFlowUnit(medicalevent.getFlowUnit());
                        }
                    } else {
                        if (null != medicalevent.getFlow() && medicalevent.getFlow() > 0) {
                            mdDetail.setFlow(medicalevent.getFlow());
                        }
                        if (null != medicalevent.getFlowUnit()) {
                            mdDetail.setFlowUnit(medicalevent.getFlowUnit());
                        }
                    }

                    if (null != medicalevent.getThickness() && medicalevent.getThickness() > 0) {
                        mdDetail.setThickness(medicalevent.getThickness());
                    }
                    if (null != medicalevent.getThicknessUnit()) {
                        mdDetail.setThicknessUnit(medicalevent.getThicknessUnit());
                    }
                    if (null != medicalevent.getStartTime()) {
                        Date startDate = medicalevent.getStartTime();
                        mdDetail.setStartTime(startDate);
                    }
//                    if (null != medicalevent.getEndTime()) {
//                        Date endDate = medicalevent.getEndTime();
//                        mdDetail.setEndTime(endDate);
//                    }
                    evtMedicalEventDetailDao.updateByPrimaryKey(mdDetail);
                } else { // 多条记录，则说明修改过浓度, 修改开始时间不大于第一条结束时间，结束时间不能小于最后一条的开始时间
                    EvtMedicalEventDetail firstMd = mdList.get(0);
                    EvtMedicalEventDetail lastMd = mdList.get(mdList.size() - 1);
                    if (null != medicalevent.getStartTime()) {
                        Date startDate = medicalevent.getStartTime();
                        //Date firstMdEndTime = firstMd.getStartTime();
                        if (startDate.getTime() >= mdList.get(1).getStartTime().getTime()) { // 开始时间大于第二条记录的开始时间就是第一条记录的结束时间
                            value.setResultCode("10000001");
                            value.setResultMessage("该药品开始时间大于等于修改浓度后的第一条的结束时间，不能修改！");
                            return;
                        }
                        firstMd.setStartTime(startDate);

                        if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
                            if (null != medicalevent.getTciValue()) {
                                firstMd.setFlow(medicalevent.getTciValue());
                            }
                            if (null != medicalevent.getTciUnit()) {
                                firstMd.setFlowUnit(medicalevent.getFlowUnit());
                            }
                        } else {
                        	if (null != medicalevent.getFlow() && medicalevent.getFlow() > 0) {
                                firstMd.setFlow(medicalevent.getFlow());
                            }
                            if (null != medicalevent.getFlowUnit()) {
                                firstMd.setFlowUnit(medicalevent.getFlowUnit());
                            }
                        }

                        /*
                         * if(medicalevent.getFlow() > 0){
                         * firstMd.setFlow(medicalevent.getFlow()); } if(null !=
                         * medicalevent.getFlowUnit()){
                         * firstMd.setFlowUnit(medicalevent.getFlowUnit()); }
                         */
                        if (null != medicalevent.getThickness() && medicalevent.getThickness() > 0) {
                            firstMd.setThickness(medicalevent.getThickness());
                        }
                        if (null != medicalevent.getThicknessUnit()) {
                            firstMd.setThicknessUnit(medicalevent.getThicknessUnit());
                        }
                        evtMedicalEventDetailDao.updateByPrimaryKey(firstMd);
                    }
                    if (null != medicalevent.getEndTime()) {
                        Date endDate = medicalevent.getEndTime();
                        Date lastMdStartTime = lastMd.getStartTime();
                        if (endDate.getTime() <= lastMdStartTime.getTime()) {
                            value.setResultCode("10000001");
                            value.setResultMessage("该药品结束时间小于等于修改浓度后的最后一条记录的开始时间，不能修改！");
                            return;
                        }
                        //lastMd.setEndTime(endDate);
                        evtMedicalEventDetailDao.updateByPrimaryKey(lastMd);
                    }

                }
            }
            
            /**
    		 * 2017-10-30沈阳本溪
    		 * 将修改痕迹保存到表中
    		 */
            DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicalevent.getDocId());
            if(null!=anaesRecord){
            	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
    	        //如果当前状态不为术中时，则需要记录变更信息
            	if(null!=regOpt && !"04".equals(regOpt.getState())){
    	        	if (StringUtils.isNotBlank(medicalevent.getMedEventId())) {
    					EvtMedicalEvent oldEvt = evtMedicaleventDao.selectByPrimaryKey(medicalevent.getMedEventId());
    					
    					CompareObject compare = new CompareObject();
    					List<ChangeValueFormbean> changeList = new ArrayList<ChangeValueFormbean>();
    					try {
    						changeList = compare.getCompareResultByFields(oldEvt, medicalevent);
    						if(null!=changeList && changeList.size()>0){
    							
    							for (ChangeValueFormbean changeValueFormbean : changeList) {
    								//排除非表内字段产生的差异，如medTakeWayIdList等
    								Map<String,String> hisMap = compare.getColumnListByTableName("evt_medicalevent");
    								if(hisMap.containsKey(changeValueFormbean.getModProperty())){
    									EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
    									evtModRd.setBeid(getBeid());
    									evtModRd.setIp(getIP());
    									evtModRd.setOperId(getUserName());
    									evtModRd.setEventId(medicalevent.getMedEventId());
    									evtModRd.setRegOptId(anaesRecord.getRegOptId());
    									evtModRd.setModifyDate(new Date());
    									evtModRd.setModTable("evt_medicalevent(用药事件表)");
    									evtModRd.setOperModule("术中用药("+basMedicineDao.selectByPrimaryKey(medicalevent.getMedicineId()).getName()+")");
    									evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
    									evtModRd.setModProperty(compare.getColumnContentByProperty("evt_medicalevent", changeValueFormbean.getModProperty()));
    									evtModRd.setOldValue(changeValueFormbean.getOldValue());
    	    							evtModRd.setNewValue(changeValueFormbean.getNewValue());
    									evtModRd.setRemark("修改");
    									evtAnaesModifyRecordDao.insert(evtModRd);
    								}
    							}
    						}
    					} catch (Exception e) {
    						logger.info("------getCompareResultByFields-----"+Exceptions.getStackTraceAsString(e));
    						throw new CustomException(Exceptions.getStackTraceAsString(e));
    					}
    	        	}else{
    	        		
    	        	}
    	        }
            }
            
            evtMedicaleventDao.updateByPrimaryKeySelective(medicalevent);
            
            // medicaleventDetailDao.updateByPrimaryKeySelective(medicaleventDetail);
        } else {
           
        	String medEventId = GenerateSequenceUtil.generateSequenceNo();
        	
            medicalevent.setMedEventId(medEventId);
            medicalevent.setMedTakeWayId(mtwId);
            Integer durable = medicalevent.getDurable();
            if (null != durable) {
                evtMedicaleventDao.insert(medicalevent);
            } else {
                medicalevent.setDurable(0);// 如果页面未传值，则为非持续用药 ，默认为0
                evtMedicaleventDao.insert(medicalevent);
            }

            EvtMedicalEventDetail md = new EvtMedicalEventDetail();
            md.setId(GenerateSequenceUtil.generateSequenceNo());
            //md.setDocId(medicalevent.getDocId());
            md.setMedEventId(medEventId);
            if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
                if (null != medicalevent.getTciValue()) {
                    md.setFlow(medicalevent.getTciValue());
                }
                if (null != medicalevent.getTciUnit()) {
                    md.setFlowUnit(medicalevent.getFlowUnit());
                }
            } else {
                md.setFlow(medicalevent.getFlow());
                md.setFlowUnit(medicalevent.getFlowUnit());
            }

            md.setThickness(medicalevent.getThickness());
            md.setThicknessUnit(medicalevent.getThicknessUnit());
            Date startDate = medicalevent.getStartTime();
            md.setStartTime(startDate);
//            Date endtime = medicalevent.getEndTime();
//            if (null != endtime) {
//                md.setEndTime(endtime);
//            }
            evtMedicalEventDetailDao.insert(md);
            
            /**
             * 2017-11-02
             * 记录用药术后用药事件明细历史痕迹
             */
            DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicalevent.getDocId());
            if(null!=anaesRecord){
            	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
            	
            	String medName = basMedicineDao.selectByPrimaryKey(medicalevent.getMedicineId()).getName();
            	
    	        //如果当前状态不为术中时，则需要记录变更信息
            	if(null!=regOpt && !"04".equals(regOpt.getState())){
            		
		            EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
					evtModRd.setBeid(getBeid());
					evtModRd.setIp(getIP());
					evtModRd.setOperId(getUserName());
					evtModRd.setEventId(medEventId);
					evtModRd.setRegOptId(anaesRecord.getRegOptId());
					evtModRd.setModifyDate(new Date());
					evtModRd.setModTable("evt_medicalevent(用药事件表)");
					evtModRd.setOperModule("术中用药");
					evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
					evtModRd.setModProperty("新增用药("+medName+")");
					
					StringBuffer buffer = new StringBuffer();
					buffer.append("开始时间:"+DateUtils.formatDateTime(medicalevent.getStartTime()));
					if(null!=medicalevent.getDosage() && medicalevent.getDosage()>0){
						buffer.append("; 剂量:"+medicalevent.getDosage());
					}
					if(null!=medicalevent.getFlow() && medicalevent.getFlow()>0){
						buffer.append("; 流速:"+medicalevent.getFlow());
						buffer.append("; 流速单位:"+medicalevent.getFlowUnit());
					}
					if(null!=medicalevent.getThickness() && medicalevent.getThickness()>0){
						buffer.append("; 浓度:"+medicalevent.getThickness());
						buffer.append("; 浓度单位:"+medicalevent.getThicknessUnit());
					}
					
					evtModRd.setNewValue(buffer.toString());
					evtModRd.setRemark("新增");
					evtAnaesModifyRecordDao.insert(evtModRd);
		            
		        	evtModRd = new EvtAnaesModifyRecord();
					evtModRd.setBeid(getBeid());
					evtModRd.setIp(getIP());
					evtModRd.setOperId(getUserName());
					evtModRd.setEventId(medEventId);
					evtModRd.setRegOptId(anaesRecord.getRegOptId());
					evtModRd.setModifyDate(new Date());
					evtModRd.setModTable("evt_medicalevent_detail(用药事件明细表)");
					evtModRd.setOperModule("术中用药明细");
					evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
					evtModRd.setModProperty("新增用药明细("+medName+")");
					evtModRd.setNewValue(buffer.toString());
					evtModRd.setRemark("新增");
					evtAnaesModifyRecordDao.insert(evtModRd);
            	}
            }
            
        }
        value.put("medicineId", medicalevent.getMedEventId());
        LogUtils.saveOperateLog(medicalevent.getDocId(), LogUtils.OPT_TYPE_INFO_SAVE, LogUtils.OPT_MODULE_INTERFACE, "术中添加用药事件保存", JsonType.jsonType(medicalevent), UserUtils.getUserCache(), getBeid());
    }

    @Transactional
    public void batchSaveMedicaleventNHFE(List<EvtMedicalEvent> medicaleventList, ResponseValue value) {
        if (null != medicaleventList && medicaleventList.size() > 0) {
            List<String> successList = new ArrayList<String>();
            List<String> failList = new ArrayList<String>();
            //DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicaleventList.get(0).getDocId());
            for (EvtMedicalEvent medicalevent : medicaleventList) {

                SearchFormBean searchFormBean = new SearchFormBean();
                searchFormBean.setDocId(medicalevent.getDocId());
                searchFormBean.setId(medicalevent.getMedEventId());
                List<EvtMedicalEvent> List = evtMedicaleventDao.checkMedicaleventCanInsert(searchFormBean, medicalevent.getMedicineId() + "");

                boolean flag = false;
               /* // 持续用药
                if ("1".equals(medicalevent.getDurable())) {
                    for (EvtMedicalEvent event : List) {
                        Date startTime = medicalevent.getStartTime();
                        Date eventStartTime = event.getStartTime();
                        Date endTime = medicalevent.getEndTime();
                        Date eventEndTime = event.getEndTime();
                        // (startTime < eventStartTime && endTime < eventStartTime) || startTime > eventEndTime  则继续往下走，并存入
                        if ((startTime.getTime() < eventStartTime.getTime() && endTime.getTime()<eventStartTime.getTime()) || startTime.getTime()>eventEndTime.getTime()) {
                        //if ((DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getStartTime())) < 0 && DateUtils.parseDate(medicalevent.getEndTime()).compareTo(DateUtils.parseDate(event.getStartTime())) < 0) || DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getEndTime())) > 0) {
                            continue;
                        } else {
                            // value.setResultCode("10000001");
                            // value.setResultMessage("该药品在开始时间："+medicalevent.getStarttime()+"至结束时间："+medicalevent.getEndtime()+", 已经存在持续用药情况,请勿重复添加!");
                            // return;

                            flag = true;
                        }
                    }
                } else {// 普通用药
                    for (EvtMedicalEvent event : List) {
                        Date startTime = medicalevent.getStartTime();
                        Date eventStartTime = event.getStartTime();
                        //Date endTime = medicalevent.getEndTime();
                        Date eventEndTime = event.getEndTime();
                        // !(startTime > eventStartTime && startTime < eventEndTime) 则进行插入
                        if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime())) {
                        //if (!(DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getStartTime())) > 0 && DateUtils.parseDate(medicalevent.getStartTime()).compareTo(DateUtils.parseDate(event.getEndTime())) < 0)) {
                            continue;
                        } else {
                            // value.setResultCode("10000001");
                            // value.setResultMessage("该药品在开始时间："+medicalevent.getStarttime()+", 已经存在持续用药情况,请勿重复添加!");
                            // return;
                            flag = true;
                        }
                    }
                }*/
                BasMedicine medicine = basMedicineDao.queryMedicineById(medicalevent.getMedicineId() + "");

                if (flag) {
                    failList.add(medicine.getName());
                    continue;
                }

                if (!StringUtils.isNotBlank(medicalevent.getMedEventId())) {
                    medicalevent.setMedEventId(GenerateSequenceUtil.generateSequenceNo());
                    evtMedicaleventDao.insert(medicalevent);
                    successList.add(medicine.getName());
                }

            }
            value.put("success", successList);
            value.put("fail", failList);

        }
    }
    
    @Transactional
    public void batchSaveMedicaleventQNZ(List<EvtMedicalEvent> medicaleventList, ResponseValue value) 
    {
        if (null != medicaleventList && medicaleventList.size() > 0) {
            List<String> successList = new ArrayList<String>();
            List<String> failList = new ArrayList<String>();
            //DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medicaleventList.get(0).getDocId());
            for (EvtMedicalEvent medicalevent : medicaleventList) {

                SearchFormBean searchFormBean = new SearchFormBean();
                searchFormBean.setDocId(medicalevent.getDocId());
                searchFormBean.setId(medicalevent.getMedEventId());
                List<EvtMedicalEvent> List = evtMedicaleventDao.checkMedicaleventCanInsert(searchFormBean, medicalevent.getMedicineId() + "");

                boolean flag = false;
                // 持续用药
                if (1 ==medicalevent.getDurable()) {
                    for (EvtMedicalEvent event : List) {
                        Date startTime = medicalevent.getStartTime();
                        Date eventStartTime = event.getStartTime();
                        Date endTime = medicalevent.getEndTime();
                        Date eventEndTime = event.getEndTime();
                        if (null == endTime && null == eventEndTime)
                        {
                            //前一条用药事件的结束时间设置为后一条的开始时间
                            if (startTime.getTime() < eventStartTime.getTime())
                            {
                                medicalevent.setEndTime(eventStartTime);
                                continue;
                            }
                            else
                            {
                                event.setEndTime(startTime);
                                evtMedicaleventDao.updateByPrimaryKeySelective(event);
                                flag = true;
                                continue;
                            }
                        }
                        else if (null != endTime && null == eventEndTime)
                        {
                            if (eventStartTime.getTime() >= endTime.getTime())
                            {
                                continue;
                            }
                            else if (eventStartTime.getTime() <= startTime.getTime())
                            {
                                event.setEndTime(startTime);
                                evtMedicaleventDao.updateByPrimaryKeySelective(event);
                                continue;
                            }
                            else
                            {
                                flag = true;
                            }
                        }
                        else if (null == endTime && null != eventEndTime)
                        {
                            if (startTime.getTime() >= eventEndTime.getTime())
                            {
                                continue;
                            }
                            else if (startTime.getTime() < eventStartTime.getTime())
                            {
                                medicalevent.setEndTime(eventStartTime);
                                continue;
                            }
                            else
                            {
                                flag = true;
                            }
                        } 
                        else
                        {
                            if (startTime.getTime() >= eventEndTime.getTime() || endTime.getTime() < eventStartTime.getTime())
                            {
                                continue;
                            }
                            else
                            {
                                flag = true;
                            }
                        }
                    }
                } else {// 普通用药
                    for (EvtMedicalEvent event : List) {
                    Date startTime = medicalevent.getStartTime();
                    Date eventStartTime = event.getStartTime();
                    Date eventEndTime = event.getEndTime();
                    if (null == eventEndTime)
                    {
                        event.setEndTime(startTime);
                        evtMedicaleventDao.updateByPrimaryKeySelective(event);
                        continue;
                    }
                    else
                    {
                        
                        if (!(startTime.getTime() > eventStartTime.getTime() && startTime.getTime() < eventEndTime.getTime()))
                        {
                            continue;
                        }
                        else
                        {
                            flag = true;
                        }
                    }
                    }
                }
                BasMedicine medicine = basMedicineDao.queryMedicineById(medicalevent.getMedicineId() + "");
                if (null == medicine)
                {
                   continue;
                }
                
                if (flag) {
                    failList.add(medicine.getName());
                    continue;
                }

                if (!StringUtils.isNotBlank(medicalevent.getMedEventId())) {
                    medicalevent.setMedEventId(GenerateSequenceUtil.generateSequenceNo());
                    evtMedicaleventDao.insert(medicalevent);
                    successList.add(medicine.getName());
                }
                
                EvtMedicalEventDetail md = new EvtMedicalEventDetail();
                md.setId(GenerateSequenceUtil.generateSequenceNo());
                //md.setDocId(medicalevent.getDocId());
                md.setMedEventId(medicalevent.getMedEventId());
                if (null != medicalevent.getDurable() && medicalevent.getDurable().equals("2")) {
                    if (null != medicalevent.getTciValue()) {
                        md.setFlow(medicalevent.getTciValue());
                    }
                    if (null != medicalevent.getTciUnit()) {
                        md.setFlowUnit(medicalevent.getFlowUnit());
                    }
                } else {
                    md.setFlow(medicalevent.getFlow());
                    md.setFlowUnit(medicalevent.getFlowUnit());
                }

                md.setThickness(medicalevent.getThickness());
                md.setThicknessUnit(medicalevent.getThicknessUnit());
                Date startDate = medicalevent.getStartTime();
                md.setStartTime(startDate);
//              Date endtime = medicalevent.getEndTime();
//              if (null != endtime) {
//                  md.setEndTime(endtime);
//              }

                evtMedicalEventDetailDao.insert(md);

            }
            value.put("success", successList);
            value.put("fail", failList);

        }
    }

	/**
	 * 删除用药事件
	 * 
	 * @param medicalevent
	 */
	@Transactional
	public void deleteMedicalevent(EvtMedicalEvent medicalevent) {
		
		logger.info("-----------------start-----------deleteMedicalevent------------");
		
		EvtMedicalEvent evtMedicalEvent = evtMedicaleventDao.selectByPrimaryKey(medicalevent.getMedEventId());
		
		int cnt = evtMedicaleventDao.deleteByPrimaryKey(medicalevent.getMedEventId());
		// 删除用药详情相关的记录
		evtMedicalEventDetailDao.deleteByMedEventId(medicalevent.getMedEventId());
		
		logger.info("---------------------deleteMedicalevent------------cnt:"+cnt);
		
		if(cnt>0){
			 /**
			 * 2017-10-30沈阳本溪
			 * 将修改痕迹保存到表中
			 */
	        DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(evtMedicalEvent.getDocId());
	        if(null!=anaesRecord){
	        	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
		        //如果当前状态不为术中时，则需要记录变更信息
	        	if(null!=regOpt && !"04".equals(regOpt.getState())){
	        		EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
	    			evtModRd.setBeid(getBeid());
	    			evtModRd.setIp(getIP());
	    			evtModRd.setOperId(getUserName());
	    			evtModRd.setEventId(evtMedicalEvent.getMedEventId());
	    			evtModRd.setRegOptId(anaesRecord.getRegOptId());
	    			evtModRd.setModifyDate(new Date());
	    			evtModRd.setModTable("evt_medicalevent(用药事件表)");
	    			evtModRd.setOperModule("术中用药");
	    			evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
	    			evtModRd.setModProperty("删除用药");
	    			
	    			StringBuffer buffer = new StringBuffer();
	    			if (null != evtMedicalEvent.getStartTime()) 
	    			{
	    			    buffer.append("开始时间:"+DateUtils.formatDateTime(evtMedicalEvent.getStartTime()));
	    			}
					if(null!= evtMedicalEvent.getDosage() && evtMedicalEvent.getDosage()>0){
						buffer.append("; 剂量:"+evtMedicalEvent.getDosage());
					}
					if(null!=evtMedicalEvent.getFlow() && evtMedicalEvent.getFlow()>0){
						buffer.append("; 流速:"+evtMedicalEvent.getFlow());
						buffer.append("; 流速单位:"+evtMedicalEvent.getFlowUnit());
					}
					if(null!=evtMedicalEvent.getThickness() && evtMedicalEvent.getThickness()>0){
						buffer.append("; 浓度:"+evtMedicalEvent.getThickness());
						buffer.append("; 浓度单位:"+evtMedicalEvent.getThicknessUnit());
					}
					evtModRd.setOldValue(buffer.toString());
	    			evtModRd.setRemark("删除");
	    			evtAnaesModifyRecordDao.insert(evtModRd);
	        	}
	        }
	        logger.info("---------------------evtAnaesModifyRecordDao.insert(evtModRd)------------");
		}
	}

	@Transactional
	public void saveMedicalEventDetail(MedicalDetailFormbean bean, ResponseValue value) {
		String id = bean.getId();
		
		StringBuffer buffer = new StringBuffer();
		
		//先判断是不是修改剂量
		if (null != bean.getDosage())
		{
		    EvtMedicalEvent medEvt = evtMedicaleventDao.selectByPrimaryKey(bean.getMedEventId());
		    if (null == medEvt)
		    {
		        value.setResultCode("10000001");
                value.setResultMessage("未找到对应的用药事件！");
                return;
		    }
		    medEvt.setDosage(bean.getDosage());
		    medEvt.setShowOption(bean.getShowOption());
		    evtMedicaleventDao.updateByPrimaryKey(medEvt);
		}
		
		
		if (null != id && StringUtils.isNotBlank(id)) { // 修改记录
			EvtMedicalEventDetail mdDetail = evtMedicalEventDetailDao.selectByPrimaryKey(id);
			//SearchFormBean searchBean = new SearchFormBean();
			//searchBean.setId(id);
			EvtMedicalEvent mdEvent = evtMedicaleventDao.selectByPrimaryKey(mdDetail.getMedEventId());
			Date insertTime = bean.getInsertTime();
			if (null != mdEvent) {
				List<EvtMedicalEventDetail> mdDetailList = evtMedicalEventDetailDao.selectByStartTimeWithEndTime(mdDetail.getMedEventId(), insertTime);
				//判断在拖动流速、浓度时间是否存在重合
				if (null != mdDetailList && mdDetailList.size() > 0) {
					for (EvtMedicalEventDetail evtMedicalEventDetail : mdDetailList) {
						if(!evtMedicalEventDetail.getId().equals(id)){
							if(evtMedicalEventDetail.getStartTime().getTime()==insertTime.getTime()){
								value.setResultCode("10000001");
								value.setResultMessage(DateUtils.formatDateTime(insertTime)+",在此药品事件已经存在相同的时间点,请核对！");
								return;
							}
						}
					}
				}
				
				//EvtMedicalEvent medicalevent = mdEventList.get(0);
				//用药事件表中浓度和流速字段是和用药事件详情表中第一条数据中的浓度和流速保持一致，如果修改的是第一个点的浓度和流速，这里也将用药事件表中的浓度和流速同步修改一下
				Date starttime = mdEvent.getStartTime();
				if (null != insertTime) {
					if (starttime.getTime() == insertTime.getTime()) {
						if (null != bean.getFlow() && bean.getFlow() > 0) {
							mdEvent.setFlow(bean.getFlow());
						}
						if (null != bean.getThickness() && bean.getThickness() > 0) {
							mdEvent.setThickness(bean.getThickness());
						}
						mdEvent.setFlowUnit(bean.getFlowUnit());
						mdEvent.setThicknessUnit(bean.getThicknessUnit());
						evtMedicaleventDao.updateByPrimaryKeySelective(mdEvent); // 修改浓度或流速
					}
				}
			}
			if (null != mdDetail) {
				if (null != bean.getFlow() && bean.getFlow() > 0) {
					mdDetail.setFlow(bean.getFlow());
				}
				if (null != bean.getThickness() && bean.getThickness() > 0) {
					mdDetail.setThickness(bean.getThickness());
				}
				if (null != insertTime) {
					mdDetail.setStartTime(insertTime);
				}
			    mdDetail.setFlowUnit(bean.getFlowUnit());
			    mdDetail.setThicknessUnit(bean.getThicknessUnit());
			    mdDetail.setShowFlow(bean.getShowFlow());
			    mdDetail.setShowThick(bean.getShowThick());
				evtMedicalEventDetailDao.updateByPrimaryKey(mdDetail);
				
				buffer.append("开始时间:"+DateUtils.formatDateTime(mdDetail.getStartTime()));

				if(null!=mdDetail.getFlow() && mdDetail.getFlow()>0){
					buffer.append("; 流速:"+mdDetail.getFlow());
					buffer.append("; 流速单位:"+mdDetail.getFlowUnit());
				}
				if(null!=mdDetail.getThickness() && mdDetail.getThickness()>0){
					buffer.append("; 浓度:"+mdDetail.getThickness());
					buffer.append("; 浓度单位:"+mdDetail.getThicknessUnit());
				}
				
			} else {
				value.setResultCode("10000001");
				value.setResultMessage("未找到对应的用药记录详情！");
				return;
			}

		} else { // 新增，根据时间拆分
			Date insertTime = bean.getInsertTime();
			//String docId = bean.getDocId();
			String medEventId = bean.getMedEventId();
			EvtMedicalEvent medEvt = evtMedicaleventDao.selectByPrimaryKey(bean.getMedEventId());
			if(medEvt.getStartTime().getTime()<insertTime.getTime() && insertTime.getTime()<medEvt.getEndTime().getTime()){
				List<EvtMedicalEventDetail> mdDetailList = evtMedicalEventDetailDao.selectByStartTimeWithEndTime(medEventId, insertTime);
				if (null != mdDetailList && mdDetailList.size() > 0) {
					
					for (EvtMedicalEventDetail evtMedicalEventDetail : mdDetailList) {
						if(evtMedicalEventDetail.getStartTime().getTime()==insertTime.getTime()){
							value.setResultCode("10000001");
							value.setResultMessage(DateUtils.formatDateTime(insertTime)+",在此药品事件已经存在相同的时间点,请核对！");
							return;
						}
					}
					
					EvtMedicalEventDetail evtMedDet = new EvtMedicalEventDetail();
					BeanUtils.copyProperties(mdDetailList.get(0), evtMedDet, new String[] { "id", "startTime", "flow", "thickness" });
					if (null != bean.getFlow() && bean.getFlow() > 0) {
						evtMedDet.setFlow(bean.getFlow());
					}
					if (null != bean.getThickness() && bean.getThickness() > 0) {
						evtMedDet.setThickness(bean.getThickness());
					}
					evtMedDet.setFlowUnit(bean.getFlowUnit());
					evtMedDet.setThicknessUnit(bean.getThicknessUnit());
					evtMedDet.setShowFlow(bean.getShowFlow());
					evtMedDet.setShowThick(bean.getShowThick());
					evtMedDet.setId(GenerateSequenceUtil.generateSequenceNo());
					evtMedDet.setStartTime(insertTime);
					evtMedicalEventDetailDao.insertSelective(evtMedDet);
					
					buffer.append("开始时间:"+DateUtils.formatDateTime(evtMedDet.getStartTime()));

					if(null!=evtMedDet.getFlow() && evtMedDet.getFlow()>0){
						buffer.append("; 流速:"+evtMedDet.getFlow());
						buffer.append("; 流速单位:"+evtMedDet.getFlowUnit());
					}
					if(null!=evtMedDet.getThickness() && evtMedDet.getThickness()>0){
						buffer.append("; 浓度:"+evtMedDet.getThickness());
						buffer.append("; 浓度单位:"+evtMedDet.getThicknessUnit());
					}
				}
			}else {
				value.setResultCode("10000001");
				value.setResultMessage("修改浓度的时间不在用药时间之内，无法新增！");
				return;
			}
			
			
			/*List<EvtMedicalEventDetail> mdDetailList = evtMedicalEventDetailDao.selectByStartTimeWithEndTime(medEventId, insertTime);
			if (null != mdDetailList && mdDetailList.size() > 0) {
				EvtMedicalEventDetail mdDetail = mdDetailList.get(0);
				// 存入第一条数据
				EvtMedicalEventDetail firstMd = new EvtMedicalEventDetail();
				BeanUtils.copyProperties(mdDetail, firstMd, new String[] { "id", "endTime" });
				firstMd.setId(GenerateSequenceUtil.generateSequenceNo());
				//firstMd.setEndTime(insertTime);
				evtMedicalEventDetailDao.insertSelective(firstMd);

				// 存入第二条数据
				EvtMedicalEventDetail secondMd = new EvtMedicalEventDetail();
				BeanUtils.copyProperties(mdDetail, secondMd, new String[] { "id", "startTime", "flow", "thickness" });
				if (null != bean.getFlow() && bean.getFlow() > 0) {
					secondMd.setFlow(bean.getFlow());
				}
				if (null != bean.getThickness() && bean.getThickness() > 0) {
					secondMd.setThickness(bean.getThickness());
				}
				secondMd.setId(GenerateSequenceUtil.generateSequenceNo());
				secondMd.setStartTime(insertTime);
				evtMedicalEventDetailDao.insertSelective(secondMd);
				
        		
				buffer.append("开始时间:"+DateUtils.formatDateTime(secondMd.getStartTime()));

				if(null!=secondMd.getFlow() && secondMd.getFlow()>0){
					buffer.append("; 流速:"+secondMd.getFlow());
					buffer.append("; 流速单位:"+secondMd.getFlowUnit());
				}
				if(null!=secondMd.getThickness() && secondMd.getThickness()>0){
					buffer.append("; 浓度:"+secondMd.getThickness());
					buffer.append("; 浓度单位:"+secondMd.getThicknessUnit());
				}
				
				// 删除mdDetail数据
				evtMedicalEventDetailDao.deleteByPrimaryKey(mdDetail.getId());
			} else {
				value.setResultCode("10000001");
				value.setResultMessage("修改浓度的时间不在用药时间之内，无法新增！");
				return;
			}*/
			
			
			
			DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medEvt.getDocId());
	        if(null!=anaesRecord){
	        	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
	        	//如果当前状态不为术中时，则需要记录变更信息
	        	if(null!=regOpt && !"04".equals(regOpt.getState())){
					String medName = "";
					if(null!=medEvt){
						medName = basMedicineDao.selectByPrimaryKey(medEvt.getMedicineId()).getName();
					}
					EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
					evtModRd.setBeid(getBeid());
					evtModRd.setIp(getIP());
					evtModRd.setOperId(getUserName());
					evtModRd.setEventId(bean.getMedEventId());
					evtModRd.setRegOptId(anaesRecord.getRegOptId());
					evtModRd.setModifyDate(new Date());
					evtModRd.setModTable("evt_medicalevent_detail(用药事件明细表)");
					evtModRd.setOperModule("术中用药明细");
					evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
					evtModRd.setModProperty("修改用药明细("+medName+")");
					evtModRd.setNewValue(buffer.toString());
					evtModRd.setRemark("修改");
					evtAnaesModifyRecordDao.insert(evtModRd);
	        	}
	        }
		}
	}

	@Transactional
	public void deleteMedicalEventDetail(MedicalDetailFormbean bean, ResponseValue value) {
		
		evtMedicalEventDetailDao.deleteByPrimaryKey(bean.getId());
		
		//String docId = bean.getDocId();
//		String medEventId = bean.getMedEventId();
//		Date insertTime = bean.getInsertTime();

//		List<EvtMedicalEventDetail> mdDetailList = evtMedicalEventDetailDao.getMedEventDetailListByTime(medEventId, insertTime);
//		if (null != mdDetailList && mdDetailList.size() > 0) {
//			if (mdDetailList.size() == 1) {
//				value.setResultCode("10000001");
//				value.setResultMessage("当前用药记录详情只有一条记录，不能删除！");
//				return;
//			} else if (mdDetailList.size() == 2) {
//				EvtMedicalEventDetail firstMd = mdDetailList.get(0);
//				EvtMedicalEventDetail secondMd = mdDetailList.get(1);
//				//firstMd.setEndTime(secondMd.getEndTime());
//				evtMedicalEventDetailDao.updateByPrimaryKeySelective(firstMd); // 第一条修改结束时间为第二条的结束时间
//				evtMedicalEventDetailDao.deleteByPrimaryKey(secondMd.getId()); // 第二条删除
//				
//				
//				EvtMedicalEvent medEvt = evtMedicaleventDao.selectByPrimaryKey(bean.getMedEventId());
//				DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordById(medEvt.getDocId());
//		        if(null!=anaesRecord){
//		        	BasRegOpt regOpt = basRegOptDao.searchRegOptById(anaesRecord.getRegOptId());
//		        	//如果当前状态不为术中时，则需要记录变更信息
//		        	if(null!=regOpt && !"04".equals(regOpt.getState())){
//		        		StringBuffer buffer = new StringBuffer();
//		        		buffer.append("开始时间:"+DateUtils.formatDateTime(secondMd.getStartTime()));
//
//		        		if(null!=secondMd.getFlow() && secondMd.getFlow()>0){
//							buffer.append("; 流速:"+secondMd.getFlow());
//							buffer.append("; 流速单位:"+secondMd.getFlowUnit());
//						}
//						if(null!=secondMd.getThickness() && secondMd.getThickness()>0){
//							buffer.append("; 浓度:"+secondMd.getThickness());
//							buffer.append("; 浓度单位:"+secondMd.getThicknessUnit());
//						}
//		        		
//						String medName = "";
//						if(null!=medEvt){
//							medName = basMedicineDao.selectByPrimaryKey(medEvt.getMedicineId()).getName();
//						}
//						EvtAnaesModifyRecord evtModRd = new EvtAnaesModifyRecord();
//						evtModRd.setBeid(getBeid());
//						evtModRd.setIp(getIP());
//						evtModRd.setOperId(getUserName());
//						evtModRd.setEventId(bean.getMedEventId());
//						evtModRd.setRegOptId(anaesRecord.getRegOptId());
//						evtModRd.setModifyDate(new Date());
//						evtModRd.setModTable("evt_medicalevent_detail(用药事件明细表)");
//						evtModRd.setOperModule("术中用药明细");
//						evtModRd.setId(GenerateSequenceUtil.generateSequenceNo());
//						evtModRd.setModProperty("删除用药明细("+medName+")");
//						evtModRd.setNewValue(buffer.toString());
//						evtModRd.setRemark("删除");
//						evtAnaesModifyRecordDao.insert(evtModRd);
//		        	}
//		        }
//				
//				
//			} else {
//				value.setResultCode("10000001");
//				value.setResultMessage("查询出来的用药记录详情多于两条记录，数据有误！");
//				return;
//			}
//		}
	}
	
	public String getMedTakeWayName(SearchOptOperMedicalevent obj) {
		String medTakeWayName = "";
		if (StringUtils.isNotBlank(obj.getMedTakeWayId())) {
			String[] medTakeWayIds = obj.getMedTakeWayId().split(",");
			for(String id : medTakeWayIds) {
				BasMedicalTakeWay basMedicalTakeWay = basMedicalTakeWayDao.selectByPrimaryKey(id);
				if (basMedicalTakeWay != null) {
					if (StringUtils.isBlank(medTakeWayName)) {
						medTakeWayName = basMedicalTakeWay.getName();
					}else {
						medTakeWayName += "、" + basMedicalTakeWay.getName();
					}
				}
			}
		}
		return medTakeWayName;
	}
	
	public void setMedTakeWayList(List<SearchOptOperMedicalevent> medEventLs){
        
        for (SearchOptOperMedicalevent searchOptOperMedicalevent : medEventLs) {
            List<Map> medTakeWayList = new ArrayList<Map>();
            if(StringUtils.isNotBlank(searchOptOperMedicalevent.getMedTakeWayId())){
                String medTakeArr[] = searchOptOperMedicalevent.getMedTakeWayId().split(",");
                for (int i = 0; i < medTakeArr.length; i++) {
                    BasMedicalTakeWay medicalTakeWay = basMedicalTakeWayDao.queryMedicalTakeWayById(medTakeArr[i]);
                    Map map = new HashMap();
                    map.put("medTakeWayId", medicalTakeWay.getMedTakeWayId());
                    map.put("name", medicalTakeWay.getName());
                    medTakeWayList.add(map);
                }
            }
            searchOptOperMedicalevent.setMedTakeWayList(medTakeWayList);
        }
    }
	
	public List<EvtMedicalEventDetail> getMedEventDetailByEvtId(String medEventId){
		List<EvtMedicalEventDetail> mdList = evtMedicalEventDetailDao.selectByMedEventandDocId(medEventId); // 根据时间排序
		return mdList;
	}
	
	
	
	public String searchNoEndTimeList(SearchFormBean searchBean) {
        List<String> nameList = evtMedicaleventDao.searchNoEndTimeList(searchBean.getDocId());
        String name = "";
        if (null != nameList && nameList.size() > 0)
        {
            for (String s : nameList)
            {
                name = name + s + ",";
            }
        }
        if (!"".equals(name))
        {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
}
