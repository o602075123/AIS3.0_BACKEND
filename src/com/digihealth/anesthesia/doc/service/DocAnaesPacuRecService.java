/**     
 * @discription 在此输入一句话描述此文件的作用
 * @author chengwang       
 * @created 2015-10-10 下午5:32:33    
 * tags     
 * see_to_target     
 */

package com.digihealth.anesthesia.doc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.formbean.PacuBedEventConfigFormBean;
import com.digihealth.anesthesia.basedata.formbean.PacuDeviceConfigFormBean;
import com.digihealth.anesthesia.basedata.formbean.PacuDeviceEventFormBean;
import com.digihealth.anesthesia.basedata.formbean.PacuDeviceSpecFormBean;
import com.digihealth.anesthesia.basedata.formbean.SysCodeFormbean;
import com.digihealth.anesthesia.basedata.formbean.SystemSearchFormBean;
import com.digihealth.anesthesia.basedata.po.BasPacuBedEventConfig;
import com.digihealth.anesthesia.basedata.po.BasPacuDeviceConfig;
import com.digihealth.anesthesia.basedata.po.BasPacuMonitorConfig;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.BasRegionBed;
import com.digihealth.anesthesia.basedata.po.Controller;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.common.utils.SysUtil;
import com.digihealth.anesthesia.doc.formbean.AnaesPacuRecFormBean;
import com.digihealth.anesthesia.doc.po.DocAnaesPacuRec;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.evt.formbean.Filter;
import com.digihealth.anesthesia.evt.po.EvtAnaesEvent;
import com.digihealth.anesthesia.evt.service.EvtAnaesEventService;
import com.digihealth.anesthesia.operProceed.datasync.MessageProcess;
import com.digihealth.anesthesia.operProceed.formbean.CmdMsg;
import com.digihealth.anesthesia.operProceed.formbean.EndOperationFormBean;
import com.digihealth.anesthesia.operProceed.po.BasMonitorConfigDefault;
import com.digihealth.anesthesia.operProceed.po.BasMonitorDisplay;
import com.digihealth.anesthesia.operProceed.po.Observe;
import com.digihealth.anesthesia.pacu.core.MyConstants;
import com.digihealth.anesthesia.pacu.datasync.MsgProcess;
import com.digihealth.anesthesia.websocket.WebSocketHandler;

/**
 * Title: PreVisitService.java Description: 描述
 * 
 * @author chengwang
 * @created 2015-10-10 下午5:32:33
 */
@Service
public class DocAnaesPacuRecService extends BaseService {
	
	/**
	 * 恢复室卡片列表
	 * @return
	 */
	public List<AnaesPacuRecFormBean> getAnaesPacuRecCard(){
		return docAnaesPacuRecDao.getAnaesPacuRecCard(getBeid());
	}
	
	
	public AnaesPacuRecFormBean getOptInfoByPacuId(String id){
		return docAnaesPacuRecDao.getOptInfoByPacuId(id);
	}

	public List<AnaesPacuRecFormBean> searchAnaesPacuRecList(SystemSearchFormBean searchFormBean){
		if(StringUtils.isBlank(searchFormBean.getSort())){
			searchFormBean.setSort("id");
		}
		if(StringUtils.isBlank(searchFormBean.getOrderBy())){
			searchFormBean.setOrderBy("DESC");
		}
		List<Filter> filters = searchFormBean.getFilters();
		if(filters.size()==0){
			filters = new ArrayList<Filter>();
		}
		return docAnaesPacuRecDao.searchAnaesPacuRecList(searchFormBean,filters,getBeid());
	}

	public List<BasPacuMonitorConfig> getPacuMonitorConfigCheck(String deviceId) {
		return basPacuMonitorConfigDao.getPacuMonitorConfigCheck(deviceId,getBeid());
	}
	
	public int searchTotalAnaesPacuRecList(SystemSearchFormBean searchFormBean){
		if(StringUtils.isBlank(searchFormBean.getSort())){
			searchFormBean.setSort("id");
		}
		if(StringUtils.isBlank(searchFormBean.getOrderBy())){
			searchFormBean.setOrderBy("DESC");
		}
		List<Filter> filters = searchFormBean.getFilters();
		if(filters.size()==0){
			filters = new ArrayList<Filter>();
		}
		return docAnaesPacuRecDao.searchTotalAnaesPacuRecList(filters,getBeid());
	}
	
	@Transactional
	public void saveAnaesPacuRec(DocAnaesPacuRec record,ResponseValue resp) {
		//if(StringUtils.isBlank(record.getId())){
			String bedId = record.getBedId();
			if(StringUtils.isNotBlank(bedId)){
				BasRegionBed regionBed = basRegionBedDao.selectByPrimaryKey(bedId);
				if(null != regionBed && !record.getRegOptId().equals(regionBed.getRegOptId())){
					if(regionBed.getStatus()==1){
						resp.setResultCode("100000000");
						resp.setResultMessage("该床位已被占用请选择其他床位!");
						return;
					}
					regionBed.setRegOptId(record.getRegOptId());
					regionBed.setStatus(1);
					basRegionBedDao.updateByPrimaryKey(regionBed);
				}
			}
//			DocAnaesPacuRec p = anaesPacuRecDao.selectPacuByRegOptId(record.getRegOptId());
//			if(p == null){
//				record.setId(GenerateSequenceUtil.generateSequenceNo());
//				record.setProcessState("0");
//				anaesPacuRecDao.insertSelective(record);
//			}
			
		//}else{
			//当leaveTo不为空则代表患者出复苏室，则需要将床位状态改成有效
			
			record.setPortablePipe(StringUtils.getStringByList(record.getPortablePipeList()));
			record.setPortableRes(StringUtils.getStringByList(record.getPortableResList()));
			String processState = record.getProcessState();
			if(StringUtils.isNotBlank(processState) && "END".equals(processState)){
				BasRegionBed regionBed = basRegionBedDao.selectByPrimaryKey(record.getBedId().toString());
				regionBed.setStatus(0);
				regionBed.setRegOptId("");
				basRegionBedDao.updateByPrimaryKey(regionBed);
				record.setProcessState("END");
				record.setAnabioticState(2);
				Controller controller = controllerDao.getControllerById(record.getRegOptId());
				controller.setPreviousState(controller.getState());
				controller.setState("06");
				controllerDao.update(controller);
				//basRegOptDao.updateState(record.getRegOptId(), "06");
				
				EvtAnaesEvent anaesEventPacu = new EvtAnaesEvent();
                anaesEventPacu.setCode(EvtAnaesEventService.OUT_ROOM);
                anaesEventPacu.setOccurTime(record.getExitTime());
                if(null != record){
                    anaesEventPacu.setDocId(record.getId());
                }
                anaesEventPacu.setAnaEventId(GenerateSequenceUtil.generateSequenceNo());
                anaesEventPacu.setDocType(2);
                evtAnaesEventDao.insert(anaesEventPacu);
                
				DocAnaesPacuRec p = docAnaesPacuRecDao.selectPacuByRegOptId(record.getRegOptId());
				//出复苏室时，调用采集结束命令，结束采集床旁设备数据
				if(null != p  && p.getAnabioticState() != 2)
				{
					CmdMsg msg = new CmdMsg();
					msg.setMsgType(MyConstants.STATUS_END);
					msg.setBedId(record.getBedId());
					msg.setRegOptId(record.getRegOptId());
					MsgProcess.process(msg);
				}
				
			}else{
				//regOptDao.updateState(record.getRegOptId(), "05");
				record.setAnabioticState(1);
			}
			
			docAnaesPacuRecDao.updateByPrimaryKeySelective(record);
			
		//}
		
	}
	
	@Transactional
	public void saveAnaesPacuRecXYCD(DocAnaesPacuRec record,ResponseValue resp) {
		String bedId = record.getBedId();
		if(StringUtils.isNotBlank(bedId)){
			BasRegionBed regionBed = basRegionBedDao.selectByPrimaryKey(bedId);
			if(null != regionBed && !record.getRegOptId().equals(regionBed.getRegOptId())){
				if(regionBed.getStatus()==1){
					resp.setResultCode("100000000");
					resp.setResultMessage("该床位已被占用请选择其他床位!");
					return;
				}
				regionBed.setRegOptId(record.getRegOptId());
				regionBed.setStatus(1);
				basRegionBedDao.updateByPrimaryKey(regionBed);
			}
		}
		
		record.setPortablePipe(StringUtils.getStringByList(record.getPortablePipeList()));
		record.setPortableRes(StringUtils.getStringByList(record.getPortableResList()));
		String processState = record.getProcessState();
		if(StringUtils.isNotBlank(processState) && "END".equals(processState)){
			BasRegionBed regionBed = basRegionBedDao.selectByPrimaryKey(record.getBedId().toString());
			regionBed.setStatus(0);
			regionBed.setRegOptId("");
			basRegionBedDao.updateByPrimaryKey(regionBed);
			record.setProcessState("END");
			record.setAnabioticState(2);
			Controller controller = controllerDao.getControllerById(record.getRegOptId());
			controller.setPreviousState(controller.getState());
			controller.setState("06");
			controllerDao.update(controller);
			
			EvtAnaesEvent anaesEventPacu = new EvtAnaesEvent();
            anaesEventPacu.setCode(EvtAnaesEventService.OUT_ROOM);
            anaesEventPacu.setOccurTime(record.getExitTime());
            if(null != record){
                anaesEventPacu.setDocId(record.getId());
            }
            anaesEventPacu.setAnaEventId(GenerateSequenceUtil.generateSequenceNo());
            anaesEventPacu.setDocType(2);
            evtAnaesEventDao.insert(anaesEventPacu);
            
			DocAnaesPacuRec p = docAnaesPacuRecDao.selectPacuByRegOptId(record.getRegOptId());
			//出复苏室时，调用采集结束命令，结束采集床旁设备数据
			if(null != p  && p.getAnabioticState() != 2)
			{
//					CmdMsg msg = new CmdMsg();
//					msg.setMsgType(MyConstants.STATUS_END);
//					msg.setBedId(record.getBedId());
//					msg.setRegOptId(record.getRegOptId());
//					MsgProcess.process(msg);
				
				CmdMsg msg = new CmdMsg();
                msg.setMsgType(com.digihealth.anesthesia.operProceed.core.MyConstants.OPERATION_STATUS_END);
                msg.setRegOptId(record.getRegOptId());
                MessageProcess.process(msg);
			}
		}else{
			record.setAnabioticState(1);
		}
		
        /**
         * 判断在填写pacu出室时间时，是否需要补点
         */
		Date endTime = record.getExitTime(); //获取到页面传过来的结束时间
		if(null != endTime ){
	        Date dbEndTime = basMonitorDisplayDao.findEndTime(record.getRegOptId()); //获取数据库的
	        BasMonitorDisplay md = basMonitorDisplayDao.findMonitorDisplayByInTimeLimit1(record.getRegOptId(), dbEndTime); //获取最后一个正常数据点
	        if(null != dbEndTime){
	            long pageEndTimeLong = endTime.getTime();
	            long dbEndTimeLong = dbEndTime.getTime();
	            if(pageEndTimeLong > dbEndTimeLong){ // 页面传过来的数据  大于 数据库最后一个点的时间
	                if(null != md ){
	                    Integer freq = md.getFreq();
	                    List<BasMonitorDisplay> mds = null;
	                    BasMonitorDisplay mDisplay = null;
	                    List<Observe> observes = basMonitorConfigDao.searchAllAnaesObserveList(getBeid());
	                    observes = removeSameObserve(observes);
	                    
	                    long time = dbEndTimeLong + freq*1000;
	                    for(;time <= pageEndTimeLong;){
	                        // 新增数据点
	                        mds = new ArrayList<BasMonitorDisplay>();
	                        Timestamp tt = SysUtil.getCurrentTimeStamp(new Date(time));
	                        for (Observe observe : observes) {
	                            mDisplay = new BasMonitorDisplay();
	                            BeanUtils.copyProperties(observe, mDisplay);
	                            mDisplay.setFreq(freq);
	                            mDisplay.setValue(null); // 设值为0.0f
	                            mDisplay.setId(GenerateSequenceUtil.generateSequenceNo());
	                            mDisplay.setObserveName(observe.getName());
	                            // 设置新增时间
	                            mDisplay.setTime(tt);
	                            mDisplay.setRegOptId(record.getRegOptId());// 设置regOptId
	                            mDisplay.setIntervalTime(freq); //设置间隔时间
	                            mDisplay.setAmendFlag(3);//3 ： 人为添加  
	                            mDisplay.setOuterFlag(1);//结束手术，数据添加
	                            mds.add(mDisplay);
	                        }
	                        
	                        if (null != mds && mds.size() > 0) {
	                            int count = basMonitorDisplayDao.searchMonitorDisplayByTime(record.getRegOptId(), SysUtil.getTimeFormat(new Date(time)));//查询数据库是否已存在
	                            if(count == 0){ //当没有数据的时候，才添加
	                                for (BasMonitorDisplay monitorDisplay : mds) {
	                                    basMonitorDisplayDao.insertSelective(monitorDisplay);
	                                }
	                            }
	                        }
	                        time += freq*1000;
	                    }
	                }
	                
	            }else{ // 页面传过来的时间  小于等于  数据库最后一个点的时间
	                //删除掉无用数据
	                basMonitorDisplayDao.deleteByEndTime(SysUtil.getTimeFormat(dbEndTime), record.getRegOptId());
	            }
	        }
		}
		
		docAnaesPacuRecDao.updateByPrimaryKeySelective(record);
	}
	
	
	
	
	public DocAnaesPacuRec getAnaesPacuRecById(String id){
		return docAnaesPacuRecDao.selectByPrimaryKey(id);
	}
	
	public DocAnaesPacuRec getAnaesPacuRecByRegOptId(String regOptId){
		return docAnaesPacuRecDao.getAnaesPacuRecByRegOptId(regOptId);
	}
	
	public boolean hasAnaesPacuByRegOptId(String regOptId){
		boolean flag = false;
		Integer count = docAnaesPacuRecDao.hasAnaesPacuRecByRegOptId(regOptId);
		if(count > 0) {
			flag =  true;
		}
		return flag;
	}
	
	@Transactional
    public void updatePacuRecEnterRoomTime(Date enterTime,String pacuRecId){
	    docAnaesPacuRecDao.updatePacuRecEnterRoomTime(enterTime,pacuRecId);
    }
	
	@Transactional
    public void updatePacuRecOutRoomTime(DocAnaesPacuRec record) {
        
		
		docAnaesPacuRecDao.updateByPrimaryKeySelective(record);
		
		
        Date endTime = record.getExitTime(); //获取到页面传过来的结束时间
        Date dbEndTime = basMonitorDisplayDao.findEndTime(record.getRegOptId()); //获取数据库的
        BasMonitorDisplay md = basMonitorDisplayDao.findMonitorDisplayByInTimeLimit1(record.getRegOptId(), dbEndTime); //获取最后一个正常数据点
        if(null != dbEndTime){
            long pageEndTimeLong = endTime.getTime();
            long dbEndTimeLong = dbEndTime.getTime();
            if(pageEndTimeLong > dbEndTimeLong){ // 页面传过来的数据  大于 数据库最后一个点的时间
                if(null != md ){
                    Integer freq = md.getFreq();
                    List<BasMonitorDisplay> mds = null;
                    BasMonitorDisplay mDisplay = null;
                    List<Observe> observes = basMonitorConfigDao.searchAllAnaesObserveList(getBeid());
                    observes = removeSameObserve(observes);
                    
                    long time = dbEndTimeLong + freq*1000;
                    for(;time <= pageEndTimeLong;){
                        // 新增数据点
                        mds = new ArrayList<BasMonitorDisplay>();
                        Timestamp tt = SysUtil.getCurrentTimeStamp(new Date(time));
                        for (Observe observe : observes) {
                            mDisplay = new BasMonitorDisplay();
                            BeanUtils.copyProperties(observe, mDisplay);
                            mDisplay.setFreq(freq);
                            mDisplay.setValue(null); // 设值为0.0f
                            mDisplay.setId(GenerateSequenceUtil.generateSequenceNo());
                            mDisplay.setObserveName(observe.getName());
                            // 设置新增时间
                            mDisplay.setTime(tt);
                            mDisplay.setRegOptId(record.getRegOptId());// 设置regOptId
                            mDisplay.setIntervalTime(freq); //设置间隔时间
                            mDisplay.setAmendFlag(3);//3 ： 人为添加  
                            mDisplay.setOuterFlag(1);//结束手术，数据添加
                            mds.add(mDisplay);
                        }
                        
                        if (null != mds && mds.size() > 0) {
                            int count = basMonitorDisplayDao.searchMonitorDisplayByTime(record.getRegOptId(), SysUtil.getTimeFormat(new Date(time)));//查询数据库是否已存在
                            if(count == 0){ //当没有数据的时候，才添加
                                for (BasMonitorDisplay monitorDisplay : mds) {
                                    basMonitorDisplayDao.insertSelective(monitorDisplay);
                                }
                            }
                        }
                        time += freq*1000;
                    }
                }
                
            }else{ // 页面传过来的时间  小于等于  数据库最后一个点的时间
                //删除掉无用数据
                basMonitorDisplayDao.deleteByEndTime(SysUtil.getTimeFormat(dbEndTime), record.getRegOptId());
            }
        }
    }
	

	public List<PacuDeviceSpecFormBean> getPacuDeviceByType() {
		return basPacuDeviceSpecificationDao.getPacuDeviceByType(getBeid());
	}
	
	public List<PacuDeviceConfigFormBean> getPacuDeviceConfigList(String bedId, String beid) {
		List<PacuDeviceConfigFormBean> deviceConfigList = basPacuDeviceConfigDao.selectByBedId(bedId, beid);
		if(null != deviceConfigList && deviceConfigList.size()>0){
			for (PacuDeviceConfigFormBean pacuDeviceConfig : deviceConfigList) {
				pacuDeviceConfig.setBedEventConfigList(basPacuBedEventConfigDao.selectByBedId(pacuDeviceConfig.getDeviceId(), bedId, beid));
			}
		}
		return deviceConfigList;
	}
	
	
	/**
	 * 
	 * @param anaesPacuRec
	 * @param date
	 */
	@Transactional
    public void startPacuRecEnterRoomTime(DocAnaesPacuRec anaesPacuRec,Date date){
		
		docAnaesPacuRecDao.updatePacuRecEnterRoomTime(date, anaesPacuRec.getId()); // 首次进入pacu存入时间
		anaesPacuRec.setEnterTime(date);
		
		//添加入室事件
		EvtAnaesEvent anaesEventPacu = new EvtAnaesEvent();
        anaesEventPacu.setCode(EvtAnaesEventService.IN_ROOM);
        anaesEventPacu.setOccurTime(date);
        anaesEventPacu.setDocId(anaesPacuRec.getId());
        anaesEventPacu.setDocType(2);
        anaesEventPacu.setAnaEventId(GenerateSequenceUtil.generateSequenceNo());
        anaesEventPacu.setDocType(EvtAnaesEventService.DOC_PACU_RECORD);
        evtAnaesEventDao.insertSelective(anaesEventPacu);
		
		//第一次进入，取数据字典表中的编号，并且将数据字典表中的编号值加1
		List<SysCodeFormbean> values = basSysCodeDao.searchSysCodeByGroupId("pacu_number", getBeid());
		if (null != values && values.size() > 0)
		{
			String pacuNumber = values.get(0).getCodeValue();
		    anaesPacuRec.setPacuNumber(pacuNumber);
		    //防止编号以0000格式开头，加1后在前面补零
		    int size = pacuNumber.length();
		    long l = Long.parseLong(pacuNumber);
		    l = l + 1;
		    String s = StringUtils.leftPad(String.valueOf(l), size, "0");
		    basSysCodeDao.updateValueByGroupId(s, "pacu_number",getBeid());
		}
		
		
		Date endTime = date; //获取到页面传过来的结束时间
        Date dbEndTime = basMonitorDisplayDao.findEndTime(anaesPacuRec.getRegOptId()); //获取数据库的
        BasMonitorDisplay md = basMonitorDisplayDao.findMonitorDisplayByInTimeLimit1(anaesPacuRec.getRegOptId(), dbEndTime); //获取最后一个正常数据点
        if(null != dbEndTime){
            long pageEndTimeLong = endTime.getTime();
            long dbEndTimeLong = dbEndTime.getTime();
            if(pageEndTimeLong > dbEndTimeLong){ // 页面传过来的数据  大于 数据库最后一个点的时间
                if(null != md ){
                    Integer freq = md.getFreq();
                    List<BasMonitorDisplay> mds = null;
                    BasMonitorDisplay mDisplay = null;
                    List<Observe> observes = basMonitorConfigDao.searchAllAnaesObserveList(getBeid());
                    observes = removeSameObserve(observes);
                    
                    long time = dbEndTimeLong + freq*1000;
                    for(;time <= pageEndTimeLong;){
                        // 新增数据点
                        mds = new ArrayList<BasMonitorDisplay>();
                        Timestamp tt = SysUtil.getCurrentTimeStamp(new Date(time));
                        for (Observe observe : observes) {
                            mDisplay = new BasMonitorDisplay();
                            BeanUtils.copyProperties(observe, mDisplay);
                            mDisplay.setFreq(freq);
                            mDisplay.setValue(null); // 设值为0.0f
                            mDisplay.setId(GenerateSequenceUtil.generateSequenceNo());
                            mDisplay.setObserveName(observe.getName());
                            // 设置新增时间
                            mDisplay.setTime(tt);
                            mDisplay.setRegOptId(anaesPacuRec.getRegOptId());// 设置regOptId
                            mDisplay.setIntervalTime(freq); //设置间隔时间
                            mDisplay.setAmendFlag(3);//3 ： 人为添加  
                            mDisplay.setOuterFlag(1);//结束手术，数据添加
                            mds.add(mDisplay);
                        }
                        
                        if (null != mds && mds.size() > 0) {
                            int count = basMonitorDisplayDao.searchMonitorDisplayByTime(anaesPacuRec.getRegOptId(), SysUtil.getTimeFormat(new Date(time)));//查询数据库是否已存在
                            if(count == 0){ //当没有数据的时候，才添加
                                for (BasMonitorDisplay monitorDisplay : mds) {
                                    basMonitorDisplayDao.insertSelective(monitorDisplay);
                                }
                            }
                        }
                        time += freq*1000;
                    }
                }
                
            }else{ // 页面传过来的时间  小于等于  数据库最后一个点的时间
                //删除掉无用数据
                basMonitorDisplayDao.deleteByEndTime(SysUtil.getTimeFormat(dbEndTime), anaesPacuRec.getRegOptId());
            }
        }
    }
	
	private List<Observe> removeSameObserve(List<Observe> observes)
    {
        List<Observe> observeList = new ArrayList<Observe>();
        String eventStr = "";
        for (Observe observe : observes)
        {
            BasMonitorConfigDefault monitorConfigDefault = basMonitorConfigDefaultDao.selectByEventName(observe.getName(), getBeid());
            if (null == monitorConfigDefault)
            {
                observeList.add(observe);
            }
            else if (!eventStr.contains(observe.getName()))
            {
                // 如果监测项有重复，则将监测项的id设置为统一的eventid，并且将其他重复的observe移除
                observe.setObserveId(monitorConfigDefault.getEventId());
                eventStr += observe.getName();
                observeList.add(observe);
            }
        }
        return observeList;
    }
	
	

	@Transactional
	public void savePacuDeviceConfig(PacuDeviceEventFormBean bean) {
		BasPacuDeviceConfig pacuDeviceConfig = bean.getPacuDeviceConfig();
		if (StringUtils.isBlank(pacuDeviceConfig.getBeid())) {
			pacuDeviceConfig.setBeid(getBeid());
		}
		List<BasPacuBedEventConfig> bedEventConfigList = bean.getBedEventConfigList();
		//1、修改pacuDeviceConfig
		BasPacuDeviceConfig pdc = basPacuDeviceConfigDao.selectByPrimaryKey(pacuDeviceConfig.getDeviceId(), pacuDeviceConfig.getBedId());
		if(null!=pdc){
			if(null == pacuDeviceConfig.getRoomId() || StringUtils.isBlank(pacuDeviceConfig.getRoomId())){
				String roomId = "";
				List<SysCodeFormbean> list = basSysCodeDao.searchSysCodeByGroupId("revive_room", getBeid());
				if(null != list && list.size()>0){
					SysCodeFormbean sysCodeFormbean = list.get(0);
					roomId = sysCodeFormbean.getCodeValue();
					pacuDeviceConfig.setRoomId(roomId);
				}
			}
			basPacuDeviceConfigDao.updateByPrimaryKeySelective(pacuDeviceConfig);
		}else{
			if(null == pacuDeviceConfig.getRoomId() || StringUtils.isBlank(pacuDeviceConfig.getRoomId())){
				String roomId = "";
				List<SysCodeFormbean> list = basSysCodeDao.searchSysCodeByGroupId("revive_room", getBeid());
				if(null != list && list.size()>0){
					SysCodeFormbean sysCodeFormbean = list.get(0);
					roomId = sysCodeFormbean.getCodeValue();
					pacuDeviceConfig.setRoomId(roomId);
				}
			}
			if(null == pacuDeviceConfig.getStatus()){
				pacuDeviceConfig.setStatus(-1);
			}
			basPacuDeviceConfigDao.insertSelective(pacuDeviceConfig);
		}
		//2、删除床位和event的列表
		List<PacuBedEventConfigFormBean> pacuBedList = basPacuBedEventConfigDao.selectByBedId(pacuDeviceConfig.getDeviceId(), pacuDeviceConfig.getBedId(), pacuDeviceConfig.getBeid());
		if(null != pacuBedList && pacuBedList.size()>0){
			basPacuBedEventConfigDao.deleteByBedId(pacuDeviceConfig.getDeviceId(), "", pacuDeviceConfig.getBedId());
		}
		// 新增b_pacu_bed_event_config
		if(null != bedEventConfigList && bedEventConfigList.size()>0){
			for (BasPacuBedEventConfig pacuBedEventConfig : bedEventConfigList) {
				pacuBedEventConfig.setBedId(pacuDeviceConfig.getBedId());
				pacuBedEventConfig.setDeviceId(pacuDeviceConfig.getDeviceId());
				pacuBedEventConfig.setBeid(getBeid()); 
				basPacuBedEventConfigDao.insertSelective(pacuBedEventConfig); 
			}
		}
	}

	@Transactional
	public void deletePacuDeviceConfig(String bedId, String deviceId) {
		basPacuDeviceConfigDao.deleteByPrimaryKey(deviceId, bedId);//删除pacu设备配置
		basPacuBedEventConfigDao.deleteByBedId(deviceId, "", bedId); //删除pacu采集配置记录列表
	}
}
