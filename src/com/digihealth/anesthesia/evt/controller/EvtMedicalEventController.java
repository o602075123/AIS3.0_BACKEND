package com.digihealth.anesthesia.evt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digihealth.anesthesia.common.beanvalidator.ValidatorBean;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.web.BaseController;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.evt.formbean.MedicalDetailFormbean;
import com.digihealth.anesthesia.evt.formbean.RegOptOperEgressFormBean;
import com.digihealth.anesthesia.evt.formbean.RegOptOperIoeventFormBean;
import com.digihealth.anesthesia.evt.formbean.RegOptOperMedicaleventFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchOptOperMedicalevent;
import com.digihealth.anesthesia.evt.po.EvtEgress;
import com.digihealth.anesthesia.evt.po.EvtInEvent;
import com.digihealth.anesthesia.evt.po.EvtMedicalEvent;
import com.digihealth.anesthesia.evt.po.EvtMedicalEventDetail;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value = "/operation")
public class EvtMedicalEventController extends BaseController {

	@RequestMapping(value = "/serarchMedicaleventList")
	@ResponseBody
	@ApiOperation(value = "查询用药事件", httpMethod = "POST", notes = "查询用药事件")
	public String searchMedicaleventList(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
		logger.info("begin serarchMedicaleventList");
		ResponseValue resp = new ResponseValue();
		if(null == searchBean)
		{
			resp.setResultCode("10000001");
            resp.setResultMessage("查询对象不能为空");
		}else
		{
			List<SearchOptOperMedicalevent> resultList = new ArrayList<SearchOptOperMedicalevent>();
			
			//如果没有传文书ID，传了regOptId,通过regOptId得到文书ID
			if(StringUtils.isBlank(searchBean.getDocId()))
			{
				String regOptid = searchBean.getRegOptId();
				if(StringUtils.isNotBlank(regOptid))
				{
					DocAnaesRecord docAnaesRecord = docAnaesRecordService.searchAnaesRecordByRegOptId(regOptid);
					if(null != docAnaesRecord)
					{
						searchBean.setDocId(docAnaesRecord.getAnaRecordId());
						resultList = evtMedicaleventService.searchMedicaleventList(searchBean);
					}
				}
			}else{
				resultList = evtMedicaleventService.searchMedicaleventList(searchBean);
			}
			float totalAmout = 0;
			if (resultList.size() > 0)
			{
			    for (SearchOptOperMedicalevent medicalevent : resultList)
			    {
			        totalAmout = totalAmout + (null != medicalevent.getAmout() ? medicalevent.getAmout().floatValue() : 0);
			    }
			}
			
			resp.put("totalAmout", totalAmout);
			resp.put("resultList", resultList);
		}
		
		logger.info("end serarchMedicaleventList");
		return resp.getJsonStr();
	}

	@RequestMapping(value = "/searchMedicaleventGroupByCodeList")
	@ResponseBody
	@ApiOperation(value = "分组获取相同用药list", httpMethod = "POST", notes = "分组获取相同用药list")
	public String searchMedicaleventGroupByCodeList(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
		logger.info("begin searchMedicaleventGroupByCodeList");
		ResponseValue resp = new ResponseValue();
		List<RegOptOperMedicaleventFormBean> resultList = evtMedicaleventService.searchMedicaleventGroupByCodeList(searchBean);
		resp.put("resultList", resultList);
		logger.info("end searchMedicaleventGroupByCodeList");
		return resp.getJsonStr();
	}
	
	@RequestMapping(value = "/searchMedicaleventGroupByCodeListNHFE")
    @ResponseBody
    @ApiOperation(value = "分组获取相同用药list", httpMethod = "POST", notes = "分组获取相同用药list")
    public String searchMedicaleventGroupByCodeListNHFE(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
        logger.info("begin searchMedicaleventGroupByCodeList");
        ResponseValue resp = new ResponseValue();
        List<RegOptOperMedicaleventFormBean> resultList = evtMedicaleventService.searchMedicaleventGroupByCodeListNHFE(searchBean);
        resp.put("resultList", resultList);
        logger.info("end searchMedicaleventGroupByCodeList");
        return resp.getJsonStr();
    }
	
	@RequestMapping(value = "/searchMedicaleventGroupByCodeListQNZ")
    @ResponseBody
    @ApiOperation(value = "分组获取相同用药list", httpMethod = "POST", notes = "分组获取相同用药list")
    public String searchMedicaleventGroupByCodeListQNZ(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
        logger.info("begin searchMedicaleventGroupByCodeList");
        ResponseValue resp = new ResponseValue();
        List<RegOptOperMedicaleventFormBean> resultList = evtMedicaleventService.searchMedicaleventGroupByCodeListQNZ(searchBean);
        resp.put("resultList", resultList);
        logger.info("end searchMedicaleventGroupByCodeList");
        return resp.getJsonStr();
    }

	@RequestMapping(value = "/saveMedicalevent")
	@ResponseBody
	@ApiOperation(value = "保存用药事件", httpMethod = "POST", notes = "保存用药事件")
	public String saveMedicalevent(@ApiParam(name = "medicalevent", value = "参数")@RequestBody EvtMedicalEvent medicalevent) {
		logger.info("begin saveMedicalevent");
		ResponseValue value = new ResponseValue();
		ValidatorBean validatorBean = beanValidator(medicalevent);
		if (!(validatorBean.isValidator())) {
			value.setResultCode("10000001");
			value.setResultMessage(validatorBean.getMessage());
			return value.getJsonStr();
		}
		evtMedicaleventService.saveMedicalevent(medicalevent, value);
		logger.info("end saveMedicalevent");
		return value.getJsonStr();
	}
	
	@RequestMapping(value = "/saveMedicaleventNHFE")
    @ResponseBody
    @ApiOperation(value = "保存用药事件", httpMethod = "POST", notes = "保存用药事件")
    public String saveMedicaleventNHFE(@ApiParam(name = "medicalevent", value = "参数")@RequestBody EvtMedicalEvent medicalevent) {
        logger.info("begin saveMedicalevent");
        ResponseValue value = new ResponseValue();
        ValidatorBean validatorBean = beanValidator(medicalevent);
        if (!(validatorBean.isValidator())) {
            value.setResultCode("10000001");
            value.setResultMessage(validatorBean.getMessage());
            return value.getJsonStr();
        }
        evtMedicaleventService.saveMedicaleventNHFE(medicalevent, value);
        logger.info("end saveMedicalevent");
        return value.getJsonStr();
    }
	
	@RequestMapping(value = "/saveMedicaleventQNZ")
    @ResponseBody
    @ApiOperation(value = "保存用药事件", httpMethod = "POST", notes = "保存用药事件")
    public String saveMedicaleventQNZ(@ApiParam(name = "medicalevent", value = "参数")@RequestBody EvtMedicalEvent medicalevent) {
        logger.info("begin saveMedicalevent");
        ResponseValue value = new ResponseValue();
        ValidatorBean validatorBean = beanValidator(medicalevent);
        if(medicalevent.getEndTime()!=null){
        if(medicalevent.getEndTime().getTime() < medicalevent.getStartTime().getTime()){
        	value.setResultCode("10000001");
            value.setResultMessage("该药品的开始时间：" + DateUtils.formatDateTime(medicalevent.getStartTime()) + "大于结束时间：" + DateUtils.formatDateTime(medicalevent.getEndTime()) + ", 请修改后添加!");
            return value.getJsonStr();
        }
        }
        if (!(validatorBean.isValidator())) {
            value.setResultCode("10000001");
            value.setResultMessage(validatorBean.getMessage());
            return value.getJsonStr();
        }
        evtMedicaleventService.saveMedicaleventQNZ(medicalevent, value);
        logger.info("end saveMedicalevent");
        return value.getJsonStr();
    }

	@RequestMapping("/saveMedicalEventDetail")
	@ResponseBody
	@ApiOperation(value = "保存用药事件详情", httpMethod = "POST", notes = "保存用药事件详情")
	public String saveMedicalEventDetail(@ApiParam(name = "medicalevent", value = "参数")@RequestBody MedicalDetailFormbean bean) {
		logger.info("begin saveMedicalEventDetail");
		ResponseValue value = new ResponseValue();
		ValidatorBean validatorBean = beanValidator(bean);
		if (!(validatorBean.isValidator())) {
			value.setResultCode("10000001");
			value.setResultMessage(validatorBean.getMessage());
			return value.getJsonStr();
		}
		evtMedicaleventService.saveMedicalEventDetail(bean, value);
		logger.info("end saveMedicalEventDetail");
		return value.getJsonStr();
	}

	@RequestMapping("/deleteMedicalEventDetail")
	@ResponseBody
	@ApiOperation(value = "删除用药事件详情", httpMethod = "POST", notes = "删除用药事件详情")
	public String deleteMedicalEventDetail(@ApiParam(name = "medicalevent", value = "参数")@RequestBody MedicalDetailFormbean bean) {
		logger.info("begin deleteMedicalEventDetail");
		ResponseValue value = new ResponseValue();
		evtMedicaleventService.deleteMedicalEventDetail(bean, value);
		logger.info("end deleteMedicalEventDetail");
		return value.getJsonStr();
	}

	@RequestMapping(value = "/batchSaveMedicalevent")
	@ResponseBody
	@ApiOperation(value = "批量保存用药事件", httpMethod = "POST", notes = "批量保存用药事件")
	public String batchSaveMedicalevent(@ApiParam(name = "medicalevent", value = "参数")@RequestBody List<EvtMedicalEvent> medicalevents) {
		logger.info("begin batchSaveMedicalevent");
		ResponseValue value = new ResponseValue();
		evtMedicaleventService.batchSaveMedicalevent(medicalevents, value);
		logger.info("end batchSaveMedicalevent");
		return value.getJsonStr();
	}
	
	@RequestMapping(value = "/batchSaveMedicaleventNHFE")
    @ResponseBody
    @ApiOperation(value = "批量保存用药事件", httpMethod = "POST", notes = "批量保存用药事件")
    public String batchSaveMedicaleventNHFE(@ApiParam(name = "medicalevent", value = "参数")@RequestBody List<EvtMedicalEvent> medicalevents) {
        logger.info("begin batchSaveMedicaleventNHFE");
        ResponseValue value = new ResponseValue();
        evtMedicaleventService.batchSaveMedicaleventNHFE(medicalevents, value);
        logger.info("end batchSaveMedicaleventNHFE");
        return value.getJsonStr();
    }
	
	@RequestMapping(value = "/batchSaveMedicaleventQNZ")
    @ResponseBody
    @ApiOperation(value = "批量保存用药事件", httpMethod = "POST", notes = "批量保存用药事件")
    public String batchSaveMedicaleventQNZ(@ApiParam(name = "medicalevent", value = "参数")@RequestBody List<EvtMedicalEvent> medicalevents) {
        logger.info("begin batchSaveMedicaleventQNZ");
        ResponseValue value = new ResponseValue();
        evtMedicaleventService.batchSaveMedicaleventQNZ(medicalevents, value);
        logger.info("end batchSaveMedicaleventQNZ");
        return value.getJsonStr();
    }

	@RequestMapping(value = "/deleteMedicalevent")
	@ResponseBody
	@ApiOperation(value = "删除用药事件", httpMethod = "POST", notes = "删除用药事件")
	public String deleteMedicalevent(@ApiParam(name = "medicalevent", value = "参数")@RequestBody EvtMedicalEvent medicalevent) {
		logger.info("begin deleteMedicalevent");
		ResponseValue value = new ResponseValue();
		evtMedicaleventService.deleteMedicalevent(medicalevent);
		logger.info("end deleteMedicalevent");
		return value.getJsonStr();
	}
	
	@RequestMapping(value = "/searchNoEndTimeList")
    @ResponseBody
    @ApiOperation(value = "查询没有填写结束时间的用药事件", httpMethod = "POST", notes = "查询没有填写结束时间的用药事件")
    public String searchNoEndTimeList(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean) {
        logger.info("begin searchNoEndTimeList");
        ResponseValue value = new ResponseValue();
        String name = evtMedicaleventService.searchNoEndTimeList(searchBean);
        value.put("name", name);
        logger.info("end searchNoEndTimeList");
        return value.getJsonStr();
    }
	
	@RequestMapping(value = "/updateEventTime")
    @ResponseBody
    @ApiOperation(value = "修改事件时间", httpMethod = "POST", notes = "修改事件时间")
	public String updateEventTime(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean)
	{
	    logger.info("begin updateEventTime");
        ResponseValue value = new ResponseValue();
        String startTime = searchBean.getStartTime();
        String endTime = searchBean.getEndTime(); 
        boolean isModify = true;
        if (StringUtils.isNotBlank(endTime))
        {
            if (startTime.compareTo(endTime) == 0)
            {
                value.setResultCode("10000001");
                value.setResultMessage("开始时间和结束时间不能相等");
                isModify = false;
            }
            else if (startTime.compareTo(endTime) > 0)
            {
                startTime = searchBean.getEndTime();
                endTime = searchBean.getStartTime();
            }
        }
        if ("1".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            if (isModify)
            {
                searchBean.setStartTime(startTime);
                searchBean.setEndTime(endTime);
                evtMedicaleventService.updateMedicalEventTime(searchBean, value);
            }
            
            // 治疗药事件明细   用药 
            List<RegOptOperMedicaleventFormBean> treatMedEvtList = evtMedicaleventService.searchMedicaleventGroupByCodeListQNZ(searchBean);
            value.put("resultList", treatMedEvtList); 
        }
        else if ("I".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            EvtInEvent ioevent = evtInEventService.selectById(searchBean.getId());
            if (null == ioevent)
            {
                value.setResultCode("10000001");
                value.setResultMessage("入量事件不存在");
            }
            if (null != startTime && isModify) 
            {
                ioevent.setStartTime(DateUtils.getParseTime(startTime));
                ioevent.setEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.getParseTime(endTime) : null);
                evtInEventService.saveIoevent(ioevent, value);
            }
            searchBean.setId(null);
            // 入量事件
            List<RegOptOperIoeventFormBean> inIoeventList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
            value.put("resultList", inIoeventList);
        }
        else if ("O".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            List<EvtEgress> egressList = evtEgressService.queryEgressListById(searchBean);
            if (null == egressList || egressList.size() == 0)
            {
                value.setResultCode("10000001");
                value.setResultMessage("出量事件不存在");
            }
            EvtEgress evtEgress = egressList.get(0);
            evtEgress.setStartTime(DateUtils.getParseTime(startTime));
            evtEgressService.saveEgress(evtEgress);
            // 出量事件
            searchBean.setId(null);
            List<RegOptOperEgressFormBean> allEgressList = evtEgressService.searchEgressGroupByDefIdList(searchBean);
            value.put("resultList", allEgressList);
        }
        
        logger.info("end updateEventTime");
        return value.getJsonStr();
	}
	
	
	@RequestMapping(value = "/updateEventTimeHBGZB")
    @ResponseBody
    @ApiOperation(value = "修改事件时间", httpMethod = "POST", notes = "修改事件时间")
	public String updateEventTimeHBGZB(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean)
	{
	    logger.info("begin updateEventTimeHBGZB");
        ResponseValue value = new ResponseValue();
        String startTime = searchBean.getStartTime();
        String endTime = searchBean.getEndTime(); 
        boolean isModify = true;
        if (StringUtils.isNotBlank(endTime))
        {
            if (startTime.compareTo(endTime) == 0)
            {
                value.setResultCode("10000001");
                value.setResultMessage("开始时间和结束时间不能相等");
                isModify = false;
            }
            else if (startTime.compareTo(endTime) > 0)
            {
                startTime = searchBean.getEndTime();
                endTime = searchBean.getStartTime();
            }
        }
        if ("1".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            if (isModify)
            {
                searchBean.setStartTime(startTime);
                searchBean.setEndTime(endTime);
                
                //修改用药时间需增加判断持续用药明细的时间是否包含在修改后的时间内
	        	List<EvtMedicalEvent> evtList = evtMedicaleventService.queryMedicaleventListById(searchBean);
	        	if(evtList.size()>0){
	        		EvtMedicalEvent evt = evtList.get(0);
	        		if (null != evt.getDurable() && 1 == evt.getDurable()){
	        			List<EvtMedicalEventDetail> mdList = evtMedicaleventService.getMedEventDetailByEvtId(evt.getMedEventId()); // 根据时间排序
	        			if(null!=mdList && mdList.size()>1){
	        				//获取第二个点的开始时间来比较
	        				Date st = mdList.get(1).getStartTime();
	        				Date et = mdList.get(mdList.size()-1).getStartTime();
	    
	        				if(DateUtils.getParseTime(startTime).getTime() >= st.getTime()){
	        					value.setResultCode("10000001");
	        	                value.setResultMessage("修改后的用药开始时间:"+startTime+",不能晚于:"+DateUtils.formatLongTime(st.getTime()));
	        					return value.getJsonStr();
	        				}
	        				if(DateUtils.getParseTime(endTime).getTime() <= et.getTime()){
	        					value.setResultCode("10000001");
	        					value.setResultMessage("修改后的用药结束时间:"+endTime+",不能早于:"+DateUtils.formatLongTime(et.getTime()));
	        					return value.getJsonStr();
	        				}
	        			}
	        		}
	        		EvtMedicalEvent medicalevent = evtList.get(0);
	        		medicalevent.setStartTime(DateUtils.getParseTime(startTime));
	        		medicalevent.setEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.getParseTime(endTime) : null);
	        		evtMedicaleventService.saveMedicaleventNHFE(medicalevent, value);
	        	}
            }
        }
        else if ("I".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            EvtInEvent ioevent = evtInEventService.selectById(searchBean.getId());
            if (null == ioevent)
            {
                value.setResultCode("10000001");
                value.setResultMessage("入量事件不存在");
            }
            if (null != startTime && isModify) 
            {
                ioevent.setStartTime(DateUtils.getParseTime(startTime));
                ioevent.setEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.getParseTime(endTime) : null);
                evtInEventService.saveIoeventGZB(ioevent, value);
            }
        }
        else if ("O".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            List<EvtEgress> egressList = evtEgressService.queryEgressListById(searchBean);
            if (null == egressList || egressList.size() == 0)
            {
                value.setResultCode("10000001");
                value.setResultMessage("出量事件不存在");
            }
            EvtEgress evtEgress = egressList.get(0);
            evtEgress.setStartTime(DateUtils.getParseTime(startTime));
            evtEgressService.saveEgress(evtEgress);
        }
        
        logger.info("end updateEventTimeHBGZB");
        return value.getJsonStr();
	}
	
	
	
	@RequestMapping(value = "/updateEventTimeXYCD")
    @ResponseBody
    @ApiOperation(value = "修改事件时间", httpMethod = "POST", notes = "修改事件时间")
	public String updateEventTimeXYCD(@ApiParam(name = "searchBean", value = "参数")@RequestBody SearchFormBean searchBean)
	{
	    logger.info("begin updateEventTimeXYCD");
        ResponseValue value = new ResponseValue();
        String startTime = searchBean.getStartTime();
        String endTime = searchBean.getEndTime(); 
        boolean isModify = true;
        if (StringUtils.isNotBlank(endTime))
        {
            if (startTime.compareTo(endTime) == 0)
            {
                value.setResultCode("10000001");
                value.setResultMessage("开始时间和结束时间不能相等");
                isModify = false;
            }
            else if (startTime.compareTo(endTime) > 0)
            {
                startTime = searchBean.getEndTime();
                endTime = searchBean.getStartTime();
            }
        }
        if ("1,2".contains(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            if (isModify)
            {
                searchBean.setStartTime(startTime);
                searchBean.setEndTime(endTime);
                
                //修改用药时间需增加判断持续用药明细的时间是否包含在修改后的时间内
	        	List<EvtMedicalEvent> evtList = evtMedicaleventService.queryMedicaleventListById(searchBean);
	        	if(evtList.size()>0){
	        		EvtMedicalEvent evt = evtList.get(0);
	        		if (null != evt.getDurable() && 1 == evt.getDurable()){
	        			List<EvtMedicalEventDetail> mdList = evtMedicaleventService.getMedEventDetailByEvtId(evt.getMedEventId()); // 根据时间排序
	        			if(null!=mdList && mdList.size()>1){
	        				//获取第二个点的开始时间来比较
	        				Date st = mdList.get(1).getStartTime();
	        				Date et = mdList.get(mdList.size()-1).getStartTime();
	    
	        				if(DateUtils.getParseTime(startTime).getTime() >= st.getTime()){
	        					value.setResultCode("10000001");
	        	                value.setResultMessage("修改后的用药开始时间:"+startTime+",不能晚于:"+DateUtils.formatLongTime(st.getTime()));
	        					return value.getJsonStr();
	        				}
	        				if(DateUtils.getParseTime(endTime).getTime() <= et.getTime()){
	        					value.setResultCode("10000001");
	        					value.setResultMessage("修改后的用药结束时间:"+endTime+",不能早于:"+DateUtils.formatLongTime(et.getTime()));
	        					return value.getJsonStr();
	        				}
	        			}
	        		}
	        		EvtMedicalEvent medicalevent = evtList.get(0);
	        		medicalevent.setStartTime(DateUtils.getParseTime(startTime));
	        		medicalevent.setEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.getParseTime(endTime) : null);
	        		evtMedicaleventService.saveMedicaleventNHFE(medicalevent, value);
	        	}
            }
            //用药事件明细
            if("1".equals(searchBean.getType())){
            	 searchBean.setType("01");
            }else{
            	searchBean.setType("02");
            }
            searchBean.setOrder("a.durable desc,a.medEventId");
            List<RegOptOperMedicaleventFormBean> treatMedEvtList = evtMedicaleventService.searchMedicaleventGroupByCodeListQNZ(searchBean);
            value.put("resultList", treatMedEvtList);
            
        }
        else if ("I".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            EvtInEvent ioevent = evtInEventService.selectById(searchBean.getId());
            if (null == ioevent)
            {
                value.setResultCode("10000001");
                value.setResultMessage("入量事件不存在");
            }
            if (null != startTime && isModify) 
            {
                ioevent.setStartTime(DateUtils.getParseTime(startTime));
                ioevent.setEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.getParseTime(endTime) : null);
                evtInEventService.saveIoevent(ioevent, value);
            }
            searchBean.setId(null);
            searchBean.setOrder("a.inEventId");
            // 入量事件
            List<RegOptOperIoeventFormBean> inIoeventList = evtInEventService.searchIoeventGroupByDefIdList(searchBean);
            value.put("resultList", inIoeventList);
            
        }
        else if ("O".equals(searchBean.getType()) && StringUtils.isNotBlank(startTime))
        {
            List<EvtEgress> egressList = evtEgressService.queryEgressListById(searchBean);
            if (null == egressList || egressList.size() == 0)
            {
                value.setResultCode("10000001");
                value.setResultMessage("出量事件不存在");
            }
            EvtEgress evtEgress = egressList.get(0);
            evtEgress.setStartTime(DateUtils.getParseTime(startTime));
            evtEgressService.saveEgress(evtEgress);
            
            //出量事件
            searchBean.setId(null);
            searchBean.setOrder("a.egressId");
            List<RegOptOperEgressFormBean> allEgressList = evtEgressService.searchEgressGroupByDefIdList(searchBean);
            value.put("resultList", allEgressList);
            
        }
        
        logger.info("end updateEventTimeXYCD");
        return value.getJsonStr();
	}
	
	
	
}
