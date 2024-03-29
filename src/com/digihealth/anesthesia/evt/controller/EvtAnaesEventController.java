package com.digihealth.anesthesia.evt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digihealth.anesthesia.basedata.formbean.AnaesEventFormBean;
import com.digihealth.anesthesia.basedata.formbean.SystemSearchFormBean;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.utils.SpringContextHolder;
import com.digihealth.anesthesia.common.web.BaseController;
import com.digihealth.anesthesia.doc.po.DocAnaesPacuRec;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.evt.formbean.RegOptOperEgressFormBean;
import com.digihealth.anesthesia.evt.formbean.RegOptOperIoeventFormBean;
import com.digihealth.anesthesia.evt.formbean.RegOptOperMedicaleventFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchOptOperEgress;
import com.digihealth.anesthesia.evt.formbean.SearchOptOperIoevent;
import com.digihealth.anesthesia.evt.formbean.SearchOptOperMedicalevent;
import com.digihealth.anesthesia.evt.po.EvtAnaesEvent;
import com.digihealth.anesthesia.evt.po.EvtCheckEvent;
import com.digihealth.anesthesia.evt.po.EvtCtlBreath;
import com.digihealth.anesthesia.evt.po.EvtOtherEvent;
import com.digihealth.anesthesia.evt.po.EvtRescueevent;
import com.digihealth.anesthesia.evt.po.EvtShiftChange;
import com.digihealth.anesthesia.evt.service.EvtAnaesEventService;
import com.digihealth.anesthesia.interfacedata.service.OperBaseDataService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value = "/operation")
@Api(value = "EvtAnaesEventController", description = "麻醉事件处理类")
public class EvtAnaesEventController extends BaseController {

	@RequestMapping(value = "/searchAnaeseventList")
	@ResponseBody
	@ApiOperation(value = "查询所有的麻醉事件", httpMethod = "POST", notes = "查询所有的麻醉事件")
	public String searchAnaeseventList(@ApiParam(name = "searchBean", value = "参数") @RequestBody SearchFormBean searchBean) {
		logger.info("begin searchAnaeseventList");
		ResponseValue resp = new ResponseValue();
		List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventList(searchBean);
		resp.put("resultList", resultList);
		logger.info("end searchAnaeseventList");
		return resp.getJsonStr();
	}
	
	@RequestMapping(value = "/searchAnaeseventListQNZ")
    @ResponseBody
    @ApiOperation(value = "查询所有的麻醉事件", httpMethod = "POST", notes = "查询所有的麻醉事件")
    public String searchAnaeseventListQNZ(@ApiParam(name = "searchBean", value = "参数") @RequestBody SearchFormBean searchBean) {
        logger.info("begin searchAnaeseventList");
        ResponseValue resp = new ResponseValue();
        List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventListQNZ(searchBean);
        resp.put("resultList", resultList);
        logger.info("end searchAnaeseventList");
        return resp.getJsonStr();
    }
	

    @RequestMapping(value = "/searchAnaesEventPacuList")
    @ResponseBody
	@ApiOperation(value = "查询所有的PACU麻醉事件", httpMethod = "POST", notes = "查询所有的PACU麻醉事件")
    public String searchAnaesEventPacuList(@ApiParam(name = "searchBean", value = "查询参数") @RequestBody SearchFormBean searchBean) {
    	logger.info("begin searchAnaesEventPacuList");
		ResponseValue resp = new ResponseValue();
		List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaesEventPacuList(searchBean);
		resp.put("resultList", resultList);
		logger.info("end searchAnaesEventPacuList");
		return resp.getJsonStr();
    }

    @RequestMapping(value = "/saveAnaeseventPacu")
    @ResponseBody
	@ApiOperation(value = "保存PACU麻醉事件", httpMethod = "POST", notes = "保存PACU麻醉事件")
    public String saveAnaeseventPacu(@ApiParam(name = "anaesEventPacu", value = "保存参数") @RequestBody EvtAnaesEvent anaesEventPacu) {
    	logger.info("begin saveAnaeseventPacu");
		ResponseValue resp = new ResponseValue();
		evtAnaesEventService.saveAnaeseventPacu(anaesEventPacu, resp);
		logger.info("end saveAnaeseventPacu");
        return resp.getJsonStr();
    }
    
    @RequestMapping(value = "/deleteAnaeseventPacu")
    @ResponseBody
	@ApiOperation(value = "删除PACU麻醉事件", httpMethod = "POST", notes = "删除PACU麻醉事件")
    public String deleteAnaeseventPacu(@ApiParam(name = "anaesEventPacu", value = "删除参数") @RequestBody EvtAnaesEvent anaesEventPacu) {
    	logger.info("begin deleteAnaeseventPacu");
		ResponseValue resp = new ResponseValue();
		evtAnaesEventService.deleteAnaeseventPacu(anaesEventPacu);
		logger.info("end deleteAnaeseventPacu");
        return resp.getJsonStr();
    }
    
	@RequestMapping(value = "/searchAnaesEventType")
	@ResponseBody
	@ApiOperation(value = "查询麻醉事件", httpMethod = "POST", notes = "查询麻醉事件")
	public String searchAnaesEventType(@ApiParam(name = "searchBean", value = "参数") @RequestBody SystemSearchFormBean systemSearchFormBean) {
		logger.info("begin searchAnaesEventType");
		ResponseValue resp = new ResponseValue();
		List<AnaesEventFormBean> resultList = basSysCodeService.searchAnaesEvent(systemSearchFormBean);
		resp.put("resultList", resultList);
		resp.put("total", basSysCodeService.searchAnaesEventTotal(systemSearchFormBean));
		logger.info("end searchAnaesEventType");
		return resp.getJsonStr();
	}

	@RequestMapping(value = "/saveAnaesevent")
	@ResponseBody
	@ApiOperation(value = "保存麻醉事件", httpMethod = "POST", notes = "保存麻醉事件")
	public String saveAnaesevent(@ApiParam(name = "anaesevent", value = "麻醉事件对象") @RequestBody EvtAnaesEvent anaesevent) {
		logger.info("begin saveAnaesevent");
		ResponseValue resp = new ResponseValue();
		if (anaesevent != null) {
			evtAnaesEventService.saveAnaesevent(anaesevent, resp);
			resp.put("anaEventId", anaesevent.getAnaEventId());
			SearchFormBean searchBean = new SearchFormBean();
			searchBean.setDocId(anaesevent.getDocId());
			List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventList(searchBean);
			resp.put("resultList", resultList);
		} else {
			resp.setResultCode("70000000");
			resp.setResultMessage(Global.getRetMsg(resp.getResultCode()));
		}
		logger.info("end saveAnaesevent");
		return resp.getJsonStr();
	}
	
	@RequestMapping(value = "/saveAnaeseventSYBX")
    @ResponseBody
    @ApiOperation(value = "保存麻醉事件", httpMethod = "POST", notes = "保存麻醉事件")
    public String saveAnaeseventSYBX(@ApiParam(name = "anaesevent", value = "麻醉事件对象") @RequestBody EvtAnaesEvent anaesevent) {
        logger.info("begin saveAnaesevent");
        ResponseValue resp = new ResponseValue();
        if (anaesevent != null) {
            evtAnaesEventService.saveAnaesevent(anaesevent, resp);
            resp.put("anaEventId", anaesevent.getAnaEventId());
            SearchFormBean searchBean = new SearchFormBean();
            searchBean.setDocId(anaesevent.getDocId());
            List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventList(searchBean);
            resp.put("resultList", resultList);
            
            //如果是手术开始，则将排程信息回传到his
            if (EvtAnaesEventService.OPER_START.equals(anaesevent.getCode()))
            {
                DocAnaesRecord anaesRecord = docAnaesRecordService.searchAnaesRecordById(anaesevent.getDocId());
                OperBaseDataService operBaseDataService = SpringContextHolder.getBean("operBaseDataService");
                operBaseDataService.sendScheduleToHis(anaesRecord.getRegOptId());
            }
        } else {
            resp.setResultCode("70000000");
            resp.setResultMessage(Global.getRetMsg(resp.getResultCode()));
        }
        logger.info("end saveAnaesevent");
        return resp.getJsonStr();
    }
	
	@RequestMapping(value = "/saveAnaeseventQNZ")
    @ResponseBody
    @ApiOperation(value = "保存麻醉事件", httpMethod = "POST", notes = "保存麻醉事件")
    public String saveAnaeseventQNZ(@ApiParam(name = "anaesevent", value = "麻醉事件对象") @RequestBody EvtAnaesEvent anaesevent) {
        logger.info("begin saveAnaesevent");
        ResponseValue resp = new ResponseValue();
        if (anaesevent != null) {
            evtAnaesEventService.saveAnaeseventQNZ(anaesevent, resp);
            resp.put("anaEventId", anaesevent.getAnaEventId());
            SearchFormBean searchBean = new SearchFormBean();
            searchBean.setDocId(anaesevent.getDocId());
            List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventList(searchBean);
            resp.put("resultList", resultList);
        } else {
            resp.setResultCode("70000000");
            resp.setResultMessage(Global.getRetMsg(resp.getResultCode()));
        }
        logger.info("end saveAnaesevent");
        return resp.getJsonStr();
    }
	
	@RequestMapping(value = "/batchSaveAnaesevent")
    @ResponseBody
    @ApiOperation(value = "批量保存麻醉事件", httpMethod = "POST", notes = "批量保存麻醉事件")
    public String batchSaveAnaesevent(@ApiParam(name = "anaesevent", value = "麻醉事件对象") @RequestBody List<EvtAnaesEvent> anaeseventList) {
        logger.info("begin batchSaveAnaesevent");
        ResponseValue resp = new ResponseValue();
        if (anaeseventList != null && anaeseventList.size() > 0) {
            for (EvtAnaesEvent anaesevent : anaeseventList)
            {
                evtAnaesEventService.saveAnaeseventQNZ(anaesevent, resp);
            }
            SearchFormBean searchBean = new SearchFormBean();
            searchBean.setDocId(anaeseventList.get(0).getDocId());
            List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventList(searchBean);
            resp.put("resultList", resultList);
        } else {
            resp.setResultCode("70000000");
            resp.setResultMessage(Global.getRetMsg(resp.getResultCode()));
        }
        logger.info("end batchSaveAnaesevent");
        return resp.getJsonStr();
    }

	@RequestMapping(value = "/deleteByCodeAndDocId")
	@ResponseBody
	@ApiOperation(value = "删除麻醉事件根据code和文书id", httpMethod = "POST", notes = "删除麻醉事件根据code和文书id")
	public String deleteByCodeAndDocId(@ApiParam(name = "anaesevent", value = "麻醉事件对象") @RequestBody EvtAnaesEvent anaesevent) {
		logger.info("begin deleteByCodeAndDocId");
		ResponseValue resp = new ResponseValue();
		evtAnaesEventService.deleteByCodeAndDocId(anaesevent, resp);
		SearchFormBean searchBean = new SearchFormBean();
		searchBean.setDocId(anaesevent.getDocId());
		List<EvtAnaesEvent> resultList = evtAnaesEventService.searchAnaeseventList(searchBean);
		resp.put("resultList", resultList);
		logger.info("end deleteByCodeAndDocId");
		return resp.getJsonStr();
	}

	@RequestMapping(value = "/searchAllEventList")
	@ResponseBody
	@ApiOperation(value = "查询所有的事件", httpMethod = "POST", notes = "查询所有的事件")
	public String searchAllEventList(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
		logger.info("begin searchAllEventList");
		ResponseValue resp = new ResponseValue();
		// 麻醉事件
		searchBean.setCode(null);
		List<EvtAnaesEvent> anaeseventList = evtAnaesEventService.searchAnaeseventList(searchBean);
		resp.put("anaesevent", anaeseventList);

		 //		 检测事件
		 List<EvtCheckEvent> checkeventList = evtCheckEventService.serarchCheckevent(searchBean);
		 resp.put("checkeventList", checkeventList);

		// 其他事件
		List<EvtOtherEvent> othereventList = evtOtherEventService.searchOthereventList(searchBean);
		resp.put("otherevent", othereventList);

		// 呼吸事件
		List<EvtCtlBreath> ctlBreathList = evtCtlBreathService.searchCtlBreathList(searchBean);
		resp.put("ctlBreath", ctlBreathList);

		// 抢救事件
		List<EvtRescueevent> rescueeventList = evtRescueeventService.searchRescueeventList(searchBean);
		if (rescueeventList != null && rescueeventList.size() > 0) {
			for (int i = 0; i < rescueeventList.size(); i++) {
//				String date = DateUtils.strToStr(DateUtils.DateToString(rescueeventList.get(i).getStartTime()), "yyyy-MM-dd HH:mm:ss", "HH:mm");
				rescueeventList.get(i).setStartTime(rescueeventList.get(i).getStartTime());
			}
		}
		resp.put("rescueevent", rescueeventList);

		// 交换班事件
		List<EvtShiftChange> shiftChangeList = evtShiftChangeService.searchShiftChangeList(searchBean);
//		if (shiftChangeList != null && shiftChangeList.size() > 0) {
//			for (int i = 0; i < shiftChangeList.size(); i++) {
//				String date = DateUtils.strToStr(shiftChangeList.get(i).getShiftChangeTime(), "yyyy-MM-dd HH:mm:ss", "HH:mm");
//				shiftChangeList.get(i).setShiftChangeTime(date);
//			}
//		}
		resp.put("shiftChange", shiftChangeList);
		Map map = new HashMap();
		DocAnaesRecord anaesRecord = docAnaesRecordService.searchAnaesRecordById(searchBean.getDocId());
        if(null != anaesRecord)
        {
            map.put("analgesicMethod", anaesRecord.getAnalgesicMethod());
        }
		
		//南华局点输液输血事件合在了一起，本溪局点输液输血事件需要分开
		if (null == searchBean.getBloodNum() || 0 == searchBean.getBloodNum())
		{
		    // 用药事件
	        searchBean.setType("01");
	        List<RegOptOperMedicaleventFormBean> list = evtMedicaleventService.searchMedicaleventGroupByCodeListNHFE(searchBean);
	        List<RegOptOperMedicaleventFormBean> medicaleventList = new ArrayList<RegOptOperMedicaleventFormBean>();
	        if (list != null && list.size() > 0 && null != searchBean.getMedEventNum()) {
	            for (int i = searchBean.getMedEventNum(); i < list.size(); i++) {
	                medicaleventList.add(list.get(i));
	            }
	        }
	        resp.put("medicalevent", medicaleventList);
	        
	        /*//麻醉用药事件
	        searchBean.setType("02");
	        List<RegOptOperMedicaleventFormBean> list2 = evtMedicaleventService.searchMedicaleventGroupByCodeListNHFE(searchBean);
	        List<RegOptOperMedicaleventFormBean> anaesMedeventList = new ArrayList<RegOptOperMedicaleventFormBean>();
	        if (list2 != null && list2.size() > 0 && null != searchBean.getAnaesMedNum())
	        {
	            for (int i = searchBean.getAnaesMedNum(); i < list2.size(); i++)
	            {
	                anaesMedeventList.add(list2.get(i));
	            }
	        }
	        resp.put("anaesMedevent", anaesMedeventList);*/
	        
	        //镇痛用药事件
            searchBean.setType("03");
            List<RegOptOperMedicaleventFormBean> list3 = evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
            //resp.put("analgesicMedEvtList", list3); 
            map.put("analgesicMedEvtList", list3);
		    
		    // 输液事件 
            searchBean.setSubType(null);
            List<RegOptOperIoeventFormBean> infusionAllList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
            List<RegOptOperIoeventFormBean> infusionList = new ArrayList<RegOptOperIoeventFormBean>();
            if (null != infusionAllList && infusionAllList.size() > 0 && null != searchBean.getInfusionNum()) {
                for (int i = searchBean.getInfusionNum(); i < infusionAllList.size(); i++) {
                    infusionList.add(infusionAllList.get(i));
                }
            }
            resp.put("infusionList", infusionList);
		}
		else
		{
		    // 用药事件
	        searchBean.setType("01");
	        List<RegOptOperMedicaleventFormBean> list = evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
	        List<RegOptOperMedicaleventFormBean> medicaleventList = new ArrayList<RegOptOperMedicaleventFormBean>();
	        if (list != null && list.size() > 0 && null != searchBean.getMedEventNum()) {
	            for (int i = searchBean.getMedEventNum(); i < list.size(); i++) {
	                medicaleventList.add(list.get(i));
	            }
	        }
	        resp.put("medicalevent", medicaleventList);
	        
	        /*//麻醉用药事件
	        searchBean.setType("02");
	        List<RegOptOperMedicaleventFormBean> list2 = evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
	        List<RegOptOperMedicaleventFormBean> anaesMedeventList = new ArrayList<RegOptOperMedicaleventFormBean>();
	        if (list2 != null && list2.size() > 0 && null != searchBean.getAnaesMedNum())
	        {
	            for (int i = searchBean.getAnaesMedNum(); i < list2.size(); i++)
	            {
	                anaesMedeventList.add(list2.get(i));
	            }
	        }
	        resp.put("anaesMedevent", anaesMedeventList);*/
	        
	        //镇痛用药事件
            searchBean.setType("03");
            List<RegOptOperMedicaleventFormBean> list3 = evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
            //resp.put("analgesicMedEvtList", list3);
            map.put("analgesicMedEvtList", list3);
		    // 输液事件
            searchBean.setSubType("1");
            List<RegOptOperIoeventFormBean> infusionAllList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
            List<RegOptOperIoeventFormBean> infusionList = new ArrayList<RegOptOperIoeventFormBean>();
            if (null != infusionAllList && infusionAllList.size() > 0 && null != searchBean.getInfusionNum()) {
                for (int i = searchBean.getInfusionNum(); i < infusionAllList.size(); i++) {
                    infusionList.add(infusionAllList.get(i));
                }
            }
            resp.put("infusionList", infusionList); 
    
            // 输血事件
            searchBean.setSubType("2");
            List<RegOptOperIoeventFormBean> bloodAllList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
            List<RegOptOperIoeventFormBean> bloodList = new ArrayList<RegOptOperIoeventFormBean>();
            if (null != bloodAllList && bloodAllList.size() > 0 && null != searchBean.getBloodNum()) {
                for (int i = searchBean.getBloodNum(); i < bloodAllList.size(); i++) {
                    bloodList.add(bloodAllList.get(i));
                }
            }
            resp.put("bloodList", bloodList);
		}
        resp.put("analgesicMedEvt", map); 
		
		// 出量事件
		List<RegOptOperEgressFormBean> egressAllList = evtEgressService.searchEgressGroupByDefIdList(searchBean);
		List<RegOptOperEgressFormBean> egressList = new ArrayList<RegOptOperEgressFormBean>();
		if (null != egressAllList && egressAllList.size() > 0 && null != searchBean.getEgressNum()) {
			for (int i = searchBean.getEgressNum(); i < egressAllList.size(); i++) {
				egressList.add(egressAllList.get(i));
			}
		}
		resp.put("egress", egressList);

		logger.info("end searchAllEventList");
		return resp.getJsonStr();
	}
	
	@RequestMapping(value = "/searchAllEventListQNZ")
    @ResponseBody
    @ApiOperation(value = "查询所有的事件", httpMethod = "POST", notes = "查询所有的事件")
    public String searchAllEventListQNZ(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
        logger.info("begin searchAllEventList");
        ResponseValue resp = new ResponseValue();
        Map map = new HashMap();
        DocAnaesRecord anaesRecord = docAnaesRecordService.searchAnaesRecordById(searchBean.getDocId());
        if(null != anaesRecord)
        {
        	map.put("analgesicMethod", anaesRecord.getAnalgesicMethod());
            map.put("flow1", anaesRecord.getFlow1());
            map.put("flowUnit1", anaesRecord.getFlowUnit1());
            map.put("flow2", anaesRecord.getFlow2());
            map.put("flowUnit2", anaesRecord.getFlowUnit2());
        }
        Long startTime = Long.parseLong(searchBean.getStartTime()); 
        Long endTime = Long.parseLong(searchBean.getEndTime());
        
        
        // 麻醉事件
        searchBean.setCode(null);
        List<EvtAnaesEvent> anaeseventList = evtAnaesEventService.searchAnaeseventListQNZ(searchBean);
        if (null != anaeseventList && anaeseventList.size() > 0 && null != searchBean.getAnaesEvtNum())
        {
            int anaesEvtNum = searchBean.getAnaesEvtNum();
            for (int i = anaeseventList.size() - 1; i >= 0; i--)
            {
                EvtAnaesEvent evtAnaesEvent = anaeseventList.get(i);
                Long time = evtAnaesEvent.getOccurTime().getTime();
                if ((evtAnaesEvent.getCode() <= anaesEvtNum && 8 != anaesEvtNum) || time < startTime || time > endTime)
                {
                    anaeseventList.remove(evtAnaesEvent);
                }
            }
        }
        resp.put("anaesevent", anaeseventList); 

         //检测事件
         List<EvtCheckEvent> checkeventList = evtCheckEventService.serarchCheckevent(searchBean);
         if (null != checkeventList && checkeventList.size() > 0)
         {
             for (int i = checkeventList.size() - 1; i >=0; i--)
             {
                 Long time = checkeventList.get(i).getOccurTime().getTime();
                 if (time < startTime || time > endTime)
                 {
                     checkeventList.remove(i);
                 }
             }
         }
         resp.put("checkeventList", checkeventList);

        // 其他事件
        List<EvtOtherEvent> othereventList = evtOtherEventService.searchOthereventList(searchBean);
        if (null != othereventList && othereventList.size() > 0)
        {
            for (int i = othereventList.size() - 1; i >=0; i--)
            {
                Long time = othereventList.get(i).getStartTime().getTime();
                if (time < startTime || time > endTime)
                {
                    othereventList.remove(i);
                }
            }
        }
        resp.put("otherevent", othereventList);

        // 呼吸事件
        List<EvtCtlBreath> ctlBreathList = evtCtlBreathService.searchCtlBreathList(searchBean);
        if (null != ctlBreathList && ctlBreathList.size() > 0)
        {
            for (int i = ctlBreathList.size() - 1; i >=0; i--)
            {
                Long time = ctlBreathList.get(i).getStartTime().getTime();
                if (time < startTime || time > endTime)
                {
                    ctlBreathList.remove(i);
                }
            }
        }
        resp.put("ctlBreath", ctlBreathList);

        // 抢救事件
        List<EvtRescueevent> rescueeventList = evtRescueeventService.searchRescueeventList(searchBean);
        if (null != rescueeventList && rescueeventList.size() > 0)
        {
            for (int i = rescueeventList.size() - 1; i >=0; i--)
            {
                Long time = rescueeventList.get(i).getStartTime().getTime();
                if (time < startTime || time > endTime)
                {
                    rescueeventList.remove(i);
                }
            }
        }
        resp.put("rescueevent", rescueeventList);

        // 交换班事件
        List<EvtShiftChange> shiftChangeList = evtShiftChangeService.searchShiftChangeList(searchBean);
        if (null != shiftChangeList && shiftChangeList.size() > 0)
        {
            for (int i = shiftChangeList.size() - 1; i >=0; i--)
            {
                Long time = shiftChangeList.get(i).getShiftChangeTime().getTime();
                if (time < startTime || time > endTime)
                {
                    shiftChangeList.remove(i);
                }
            }
        }
        resp.put("shiftChange", shiftChangeList);
        
        // 用药事件
        searchBean.setType("01");
        List<RegOptOperMedicaleventFormBean> list =
            evtMedicaleventService.searchMedicaleventGroupByCodeListQNZ(searchBean);
        List<RegOptOperMedicaleventFormBean> medicaleventList = new ArrayList<RegOptOperMedicaleventFormBean>();
        if (list != null && list.size() > 0 && null != searchBean.getMedEventNum())
        {
            for (int i = searchBean.getMedEventNum(); i < list.size(); i++)
            {
                RegOptOperMedicaleventFormBean medicalEventFormBean = list.get(i);
                List<SearchOptOperMedicalevent> medicalEventList = medicalEventFormBean.getMedicalEventList();
                if (null != medicalEventList && medicalEventList.size() > 0)
                {
                    for (int k = medicalEventList.size() - 1; k >=0; k--)
                    {
                        SearchOptOperMedicalevent medicalevent = medicalEventList.get(k);
                        Long eventStartTime = medicalevent.getStartTime().getTime();
                        Long eventEndTime = medicalevent.getStartTime().getTime();
                        if ("1".equals(medicalevent.getDurable()) && null != medicalevent.getEndTime())
                        {
                            eventEndTime = medicalevent.getEndTime().getTime();
                        }
                        if (eventStartTime > endTime || eventEndTime < startTime)
                        {
                            medicalEventList.remove(k);
                        }
                    }
                }
                if (null != medicalEventList && medicalEventList.size() > 0)
                {
                    list.get(i).setMedicalEventList(medicalEventList); 
                    medicaleventList.add(list.get(i));
                }
            }
        }
        resp.put("medicalevent", medicaleventList);
        
        // 镇痛用药事件
        searchBean.setType("03");
        List<RegOptOperMedicaleventFormBean> list3 =
            evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
        map.put("analgesicMedEvtList", list3);
        resp.put("analgesicMedEvt", map); 
        
        // 输液事件
        searchBean.setSubType(null);
        List<RegOptOperIoeventFormBean> infusionAllList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
        List<RegOptOperIoeventFormBean> infusionList = new ArrayList<RegOptOperIoeventFormBean>();
        if (null != infusionAllList && infusionAllList.size() > 0 && null != searchBean.getInfusionNum())
        {
            for (int i = searchBean.getInfusionNum(); i < infusionAllList.size(); i++)
            {
                RegOptOperIoeventFormBean ioeventFormBean = infusionAllList.get(i);
                List<SearchOptOperIoevent> ioeventList = ioeventFormBean.getIoeventList();
                if (null != ioeventList && ioeventList.size() > 0)
                {
                    for (int k = ioeventList.size() - 1; k >=0; k--)
                    {
                        SearchOptOperIoevent ioevent = ioeventList.get(k);
                        Long time = ioevent.getStartTime().getTime();
                        if (time < startTime || time > endTime)
                        {
                            ioeventList.remove(k);
                        }
                    }
                }
                if (null != ioeventList && ioeventList.size() > 0)
                {
                    infusionAllList.get(i).setIoeventList(ioeventList);
                    infusionList.add(infusionAllList.get(i));
                }
            }
        }
        resp.put("infusionList", infusionList);
        
        // 出量事件
        List<RegOptOperEgressFormBean> egressAllList = evtEgressService.searchEgressGroupByDefIdList(searchBean);
        List<RegOptOperEgressFormBean> egressList = new ArrayList<RegOptOperEgressFormBean>();
        if (null != egressAllList && egressAllList.size() > 0 && null != searchBean.getEgressNum())
        {
            for (int i = searchBean.getEgressNum(); i < egressAllList.size(); i++)
            {
                RegOptOperEgressFormBean egress = egressAllList.get(i);
                List<SearchOptOperEgress> egressEventList = egress.getEgressList();
                if (null != egressEventList && egressEventList.size() > 0)
                {
                    for (int k = egressEventList.size() - 1; k >=0; k--)
                    {
                        SearchOptOperEgress egressEvent = egressEventList.get(k);
                        Long time = egressEvent.getStartTime().getTime();
                        if (time < startTime || time > endTime)
                        {
                            egressEventList.remove(k);
                        }
                    }
                }
                if (null != egressEventList && egressEventList.size() > 0)
                {
                    egressAllList.get(i).setEgressList(egressEventList);
                    egressList.add(egressAllList.get(i));
                }
            }
        }
        resp.put("egress", egressList);
        
        logger.info("end searchAllEventList");
        return resp.getJsonStr();
    }
	
	@RequestMapping(value = "/searchAllEventListHBGZB")
	@ResponseBody
	@ApiOperation(value = "查询所有的事件", httpMethod = "POST", notes = "查询所有的事件")
	public String searchAllEventListHBGZB(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
		logger.info("begin searchAllEventListHBGZB");
		ResponseValue resp = new ResponseValue();
		// 麻醉事件
		searchBean.setCode(null);
		List<EvtAnaesEvent> anaeseventList = evtAnaesEventService.searchAnaeseventList(searchBean);
		resp.put("anaesevent", anaeseventList);

		 //		 检测事件
		 List<EvtCheckEvent> checkeventList = evtCheckEventService.serarchCheckeventHBGZB(searchBean);
		 resp.put("checkeventList", checkeventList);

		// 其他事件
		List<EvtOtherEvent> othereventList = evtOtherEventService.searchOthereventList(searchBean);
		resp.put("otherevent", othereventList);

		// 呼吸事件
		List<EvtCtlBreath> ctlBreathList = evtCtlBreathService.searchCtlBreathList(searchBean);
		resp.put("ctlBreath", ctlBreathList);

		// 抢救事件
		List<EvtRescueevent> rescueeventList = evtRescueeventService.searchRescueeventList(searchBean);
		if (rescueeventList != null && rescueeventList.size() > 0) {
			for (int i = 0; i < rescueeventList.size(); i++) {
//				String date = DateUtils.strToStr(DateUtils.DateToString(rescueeventList.get(i).getStartTime()), "yyyy-MM-dd HH:mm:ss", "HH:mm");
				rescueeventList.get(i).setStartTime(rescueeventList.get(i).getStartTime());
			}
		}
		resp.put("rescueevent", rescueeventList);

		// 交换班事件
		List<EvtShiftChange> shiftChangeList = evtShiftChangeService.searchShiftChangeList(searchBean);
//		if (shiftChangeList != null && shiftChangeList.size() > 0) {
//			for (int i = 0; i < shiftChangeList.size(); i++) {
//				String date = DateUtils.strToStr(shiftChangeList.get(i).getShiftChangeTime(), "yyyy-MM-dd HH:mm:ss", "HH:mm");
//				shiftChangeList.get(i).setShiftChangeTime(date);
//			}
//		}
		resp.put("shiftChange", shiftChangeList);

	    // 用药事件
        searchBean.setType("01");
        List<RegOptOperMedicaleventFormBean> list = evtMedicaleventService.searchMedicaleventGroupByCodeListNHFE(searchBean);
        List<RegOptOperMedicaleventFormBean> medicaleventList = new ArrayList<RegOptOperMedicaleventFormBean>();
        if (list != null && list.size() > 0 && null != searchBean.getMedEventNum()) {
            for (int i = searchBean.getMedEventNum(); i < list.size(); i++) {
                medicaleventList.add(list.get(i));
            }
        }
        resp.put("medicalevent", medicaleventList);
        
        //麻醉用药事件
//        searchBean.setType("02");
//        List<RegOptOperMedicaleventFormBean> list2 = evtMedicaleventService.searchMedicaleventGroupByCodeListNHFE(searchBean);
//        List<RegOptOperMedicaleventFormBean> anaesMedeventList = new ArrayList<RegOptOperMedicaleventFormBean>();
//        if (list2 != null && list2.size() > 0 && null != searchBean.getAnaesMedNum())
//        {
//            for (int i = searchBean.getAnaesMedNum(); i < list2.size(); i++)
//            {
//                anaesMedeventList.add(list2.get(i));
//            }
//        }
//        resp.put("anaesMedevent", anaesMedeventList);
	    
		// 输液事件
		searchBean.setSubType("1");
		searchBean.setCode(null);
		// List<RegOptOperIoeventFormBean> infusionAllList =
		// evtInEventService.searchIoeventGroupByDefIdList(searchBean);

		// if (null != infusionAllList && infusionAllList.size() > 0 && null !=
		// searchBean.getInfusionNum()) {
		// for (int i = searchBean.getInfusionNum(); i < infusionAllList.size();
		// i++) {
		// infusionList.add(infusionAllList.get(i));
		// }
		// }
		List<SearchOptOperIoevent> infusionAllList = evtInEventService
				.searchIoeventList(searchBean);
		List<SearchOptOperIoevent> infusionList = new ArrayList<SearchOptOperIoevent>();

		if (null != infusionAllList && infusionAllList.size() > 0
				&& null != searchBean.getInfusionNum()) {
			for (int i = searchBean.getInfusionNum(); i < infusionAllList
					.size(); i++) {
				infusionList.add(infusionAllList.get(i));
			}
		}

		resp.put("infusionList", infusionList);

		// 输血事件
		searchBean.setSubType("2");
		// List<RegOptOperIoeventFormBean> bloodAllList =
		// evtInEventService.searchIoeventGroupByDefIdList(searchBean);
		// List<RegOptOperIoeventFormBean> bloodList = new
		// ArrayList<RegOptOperIoeventFormBean>();
		// if (null != bloodAllList && bloodAllList.size() > 0 && null !=
		// searchBean.getBloodNum()) {
		// for (int i = searchBean.getBloodNum(); i < bloodAllList.size(); i++)
		// {
		// bloodList.add(bloodAllList.get(i));
		// }
		// }
		List<SearchOptOperIoevent> bloodAllList = evtInEventService
				.searchIoeventList(searchBean);

		List<SearchOptOperIoevent> bloodList = new ArrayList<SearchOptOperIoevent>();
		if (null != bloodAllList && bloodAllList.size() > 0
				&& null != searchBean.getBloodNum()) {
			for (int i = searchBean.getBloodNum(); i < bloodAllList.size(); i++) {
				bloodList.add(bloodAllList.get(i));
			}
		}
		resp.put("bloodList", bloodList);
		
		// 出量事件
		List<RegOptOperEgressFormBean> egressAllList = evtEgressService.searchEgressGroupByDefIdList(searchBean);
		List<RegOptOperEgressFormBean> egressList = new ArrayList<RegOptOperEgressFormBean>();
		if (null != egressAllList && egressAllList.size() > 0 && null != searchBean.getEgressNum()) {
			for (int i = searchBean.getEgressNum(); i < egressAllList.size(); i++) {
				egressList.add(egressAllList.get(i));
			}
		}
		resp.put("egress", egressList);

		logger.info("end searchAllEventListHBGZB");
		return resp.getJsonStr();
	}
	
	
	@RequestMapping(value = "/searchAllEventListXYCD")
    @ResponseBody
    @ApiOperation(value = "查询所有的事件", httpMethod = "POST", notes = "查询所有的事件")
    public String searchAllEventListXYCD(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
        logger.info("begin searchAllEventList");
        ResponseValue resp = new ResponseValue();
        Map map = new HashMap();
        DocAnaesRecord anaesRecord = docAnaesRecordService.searchAnaesRecordById(searchBean.getDocId());
        map.put("analgesicMethod", anaesRecord.getAnalgesicMethod());
        map.put("flow1", anaesRecord.getFlow1());
        map.put("flowUnit1", anaesRecord.getFlowUnit1());
        map.put("flow2", anaesRecord.getFlow2());
        map.put("flowUnit2", anaesRecord.getFlowUnit2());
        Long startTime = Long.parseLong(searchBean.getStartTime()); 
        Long endTime = Long.parseLong(searchBean.getEndTime());
        
        
        // 麻醉事件
        searchBean.setCode(null);
        List<EvtAnaesEvent> anaeseventList = evtAnaesEventService.searchAnaeseventListQNZ(searchBean);
        if (null != anaeseventList && anaeseventList.size() > 0 && null != searchBean.getAnaesEvtNum())
        {
            int anaesEvtNum = searchBean.getAnaesEvtNum();
            for (int i = anaeseventList.size() - 1; i >= 0; i--)
            {
                EvtAnaesEvent evtAnaesEvent = anaeseventList.get(i);
                Long time = evtAnaesEvent.getOccurTime().getTime();
                if ((evtAnaesEvent.getCode() <= anaesEvtNum && 8 != anaesEvtNum) || time < startTime || time > endTime)
                {
                    anaeseventList.remove(evtAnaesEvent);
                }
            }
        }
        resp.put("anaesevent", anaeseventList); 

         //检测事件
         List<EvtCheckEvent> checkeventList = evtCheckEventService.serarchCheckeventHBGZB(searchBean);
         if (null != checkeventList && checkeventList.size() > 0)
         {
             for (int i = checkeventList.size() - 1; i >=0; i--)
             {
                 Long time = checkeventList.get(i).getOccurTime().getTime();
                 if (time < startTime || time > endTime)
                 {
                     checkeventList.remove(i);
                 }
             }
         }
         resp.put("checkeventList", checkeventList);

        // 其他事件
        List<EvtOtherEvent> othereventList = evtOtherEventService.searchOthereventList(searchBean);
        if (null != othereventList && othereventList.size() > 0)
        {
            for (int i = othereventList.size() - 1; i >=0; i--)
            {
                Long time = othereventList.get(i).getStartTime().getTime();
                if (time < startTime || time > endTime)
                {
                    othereventList.remove(i);
                }
            }
        }
        resp.put("otherevent", othereventList);

        // 呼吸事件
        List<EvtCtlBreath> ctlBreathList = evtCtlBreathService.searchCtlBreathList(searchBean);
        if (null != ctlBreathList && ctlBreathList.size() > 0)
        {
            for (int i = ctlBreathList.size() - 1; i >=0; i--)
            {
                Long time = ctlBreathList.get(i).getStartTime().getTime();
                if (time < startTime || time > endTime)
                {
                    ctlBreathList.remove(i);
                }
            }
        }
        resp.put("ctlBreath", ctlBreathList);

        // 抢救事件
        List<EvtRescueevent> rescueeventList = evtRescueeventService.searchRescueeventList(searchBean);
        if (null != rescueeventList && rescueeventList.size() > 0)
        {
            for (int i = rescueeventList.size() - 1; i >=0; i--)
            {
                Long time = rescueeventList.get(i).getStartTime().getTime();
                if (time < startTime || time > endTime)
                {
                    rescueeventList.remove(i);
                }
            }
        }
        resp.put("rescueevent", rescueeventList);

        // 交换班事件
        List<EvtShiftChange> shiftChangeList = evtShiftChangeService.searchShiftChangeList(searchBean);
        if (null != shiftChangeList && shiftChangeList.size() > 0)
        {
            for (int i = shiftChangeList.size() - 1; i >=0; i--)
            {
                Long time = shiftChangeList.get(i).getShiftChangeTime().getTime();
                if (time < startTime || time > endTime)
                {
                    shiftChangeList.remove(i);
                }
            }
        }
        resp.put("shiftChange", shiftChangeList);
        
        // 用药事件
        searchBean.setType("01");
        searchBean.setOrder("a.durable desc,a.medEventId");
        List<RegOptOperMedicaleventFormBean> list =
            evtMedicaleventService.searchMedicaleventGroupByCodeListQNZ(searchBean);
        List<RegOptOperMedicaleventFormBean> medicaleventList = new ArrayList<RegOptOperMedicaleventFormBean>();
        if (list != null && list.size() > 0 && null != searchBean.getMedEventNum())
        {
            for (int i = searchBean.getMedEventNum(); i < list.size(); i++)
            {
                RegOptOperMedicaleventFormBean medicalEventFormBean = list.get(i);
                List<SearchOptOperMedicalevent> medicalEventList = medicalEventFormBean.getMedicalEventList();
                if (null != medicalEventList && medicalEventList.size() > 0)
                {
                    for (int k = medicalEventList.size() - 1; k >=0; k--)
                    {
                        SearchOptOperMedicalevent medicalevent = medicalEventList.get(k);
                        Long eventStartTime = medicalevent.getStartTime().getTime();
                        Long eventEndTime = medicalevent.getStartTime().getTime();
                        if ("1".equals(medicalevent.getDurable()))
                        {
                            eventEndTime = medicalevent.getEndTime().getTime();
                        }
                        if (eventStartTime > endTime || eventEndTime < startTime)
                        {
                            medicalEventList.remove(k);
                        }
                    }
                }
                if (null != medicalEventList && medicalEventList.size() > 0)
                {
                    list.get(i).setMedicalEventList(medicalEventList); 
                    medicaleventList.add(list.get(i));
                }
            }
        }
        resp.put("medicalevent", medicaleventList);
        
        // 镇痛用药事件
        searchBean.setType("03");
        List<RegOptOperMedicaleventFormBean> list3 =
            evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
        map.put("analgesicMedEvtList", list3);
        resp.put("analgesicMedEvt", map); 
        
        // 输液事件
        searchBean.setSubType(null);
        searchBean.setOrder("a.inEventId");
        List<RegOptOperIoeventFormBean> infusionAllList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
        List<RegOptOperIoeventFormBean> infusionList = new ArrayList<RegOptOperIoeventFormBean>();
        if (null != infusionAllList && infusionAllList.size() > 0 && null != searchBean.getInfusionNum())
        {
            for (int i = searchBean.getInfusionNum(); i < infusionAllList.size(); i++)
            {
                RegOptOperIoeventFormBean ioeventFormBean = infusionAllList.get(i);
                List<SearchOptOperIoevent> ioeventList = ioeventFormBean.getIoeventList();
                if (null != ioeventList && ioeventList.size() > 0)
                {
                    for (int k = ioeventList.size() - 1; k >=0; k--)
                    {
                        SearchOptOperIoevent ioevent = ioeventList.get(k);
                        Long time = ioevent.getStartTime().getTime();
                        if (time < startTime || time > endTime)
                        {
                            ioeventList.remove(k);
                        }
                    }
                }
                if (null != ioeventList && ioeventList.size() > 0)
                {
                    infusionAllList.get(i).setIoeventList(ioeventList);
                    infusionList.add(infusionAllList.get(i));
                }
            }
        }
        resp.put("infusionList", infusionList);
        
        // 出量事件
        searchBean.setOrder("a.egressId");
        List<RegOptOperEgressFormBean> egressAllList = evtEgressService.searchEgressGroupByDefIdList(searchBean);
        List<RegOptOperEgressFormBean> egressList = new ArrayList<RegOptOperEgressFormBean>();
        if (null != egressAllList && egressAllList.size() > 0 && null != searchBean.getEgressNum())
        {
            for (int i = searchBean.getEgressNum(); i < egressAllList.size(); i++)
            {
                RegOptOperEgressFormBean egress = egressAllList.get(i);
                List<SearchOptOperEgress> egressEventList = egress.getEgressList();
                if (null != egressEventList && egressEventList.size() > 0)
                {
                    for (int k = egressEventList.size() - 1; k >=0; k--)
                    {
                        SearchOptOperEgress egressEvent = egressEventList.get(k);
                        Long time = egressEvent.getStartTime().getTime();
                        if (time < startTime || time > endTime)
                        {
                            egressEventList.remove(k);
                        }
                    }
                }
                if (null != egressEventList && egressEventList.size() > 0)
                {
                    egressAllList.get(i).setEgressList(egressEventList);
                    egressList.add(egressAllList.get(i));
                }
            }
        }
        resp.put("egress", egressList);
        
		DocAnaesPacuRec docAnaesPacuRec = docAnaesPacuRecService.getAnaesPacuRecByRegOptId(anaesRecord.getRegOptId());
		if(null!=docAnaesPacuRec){
			if(null!=docAnaesPacuRec.getEnterTime()){
				Long time = docAnaesPacuRec.getEnterTime().getTime();
		        if (time >= startTime && time <= endTime)
		        {
		        	resp.put("docAnaesPacuRec", docAnaesPacuRec);
		        }
			}
		}
        logger.info("end searchAllEventList");
        return resp.getJsonStr();
    }	
	
	
	

	/**
	 * 查询麻醉时长和手术时长 <功能详细描述>
	 * 
	 * @param searchBean
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequestMapping(value = "/searchTimesByCode")
	@ResponseBody
	@ApiOperation(value = "查询麻醉时长和手术时长", httpMethod = "POST", notes = "查询麻醉时长和手术时长")
	public String searchTimesByCode(@ApiParam(name = "searchBean", value = "系统查询参数") @RequestBody SearchFormBean searchBean) {
		logger.info("begin searchTimesByCode");
		ResponseValue resp = new ResponseValue();
		resp = evtAnaesEventService.searchTimesByCode(searchBean);
		resp.put("resultMessage", "查询手术时长和麻醉时长成功!");
		logger.info("end searchTimesByCode");
		return resp.getJsonStr();
	}
}
