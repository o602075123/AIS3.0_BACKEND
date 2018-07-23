package com.digihealth.anesthesia.doc.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digihealth.anesthesia.basedata.po.BasBloodDefination;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasPatInspectItem;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.common.web.BaseController;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.doc.po.DocBloodTransItem;
import com.digihealth.anesthesia.doc.po.DocBloodTransRecord;
import com.digihealth.anesthesia.doc.po.DocPatInspectItem;
import com.digihealth.anesthesia.evt.formbean.SearchFormBean;
import com.digihealth.anesthesia.evt.po.EvtShiftChange;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 术中护士输血申请单
 * @author dell
 *
 */
@Controller
@RequestMapping(value = "/document")
@Api(value="DocBloodTransRecordController",description="术中护士输血申请单处理类")
public class DocBloodTransRecordController extends BaseController {

	/** 
	 * 查询输血申请单
	 * <功能详细描述>
	 * @param map
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequestMapping(value = "/searchOptBloodTransRecordByRegOptId")
	@ResponseBody
	@ApiOperation(value="查询输血申请单",httpMethod="POST",notes="查询输血申请单")
	public String searchOptBloodTransRecordByRegOptId(@ApiParam(name="map", value ="查询参数") @RequestBody Map map) {
		ResponseValue resp = new ResponseValue();
		String regOptId = map.get("regOptId").toString();
		DocBloodTransRecord bloodTransRecord = docBloodTransRecordService.searchOptBloodTransRecordByRegOptId(regOptId);
		List<DocBloodTransItem> bloodTransItem = docBloodTransItemService.getBloodTransItemList(bloodTransRecord.getBloodTransId());
		BasRegOpt regOpt = basRegOptService.searchRegOptById(regOptId);
		BasDispatch dispatch = basDispatchService.getDispatchOper(regOptId);
		
		if ("NO_END".equals(bloodTransRecord.getProcessState()))
        {
            DocAnaesRecord ansRecord = docAnaesRecordService.searchAnaesRecordByRegOptId(regOptId);
            String docId = ansRecord.getAnaRecordId();
            SearchFormBean searchBean = new SearchFormBean();
            searchBean.setDocId(docId);
            List<EvtShiftChange> shiftChangeList = evtShiftChangeService.searchShiftChangeList(searchBean);
            if (null != shiftChangeList && shiftChangeList.size() > 0)
            {
                bloodTransRecord.setAnaestheitistId(shiftChangeList.get(shiftChangeList.size() - 1).getShiftChangePeopleId());
            }
        }
		
		resp.put("bloodTransRecord", bloodTransRecord);
		resp.put("bloodTransItem", bloodTransItem);
		resp.put("regOpt", regOpt);
		resp.put("dispatch", dispatch);
		return resp.getJsonStr();
	}
	
	
	/** 
	 * 查询输血申请单
	 * <功能详细描述>
	 * @param map
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequestMapping(value = "/searchOptBloodTransRecordByRegOptIdNHFE")
	@ResponseBody
	@ApiOperation(value="查询输血申请单",httpMethod="POST",notes="查询输血申请单")
	public String searchOptBloodTransRecordByRegOptIdNHFE(@ApiParam(name="map", value ="查询参数") @RequestBody Map map) {
		ResponseValue resp = new ResponseValue();
		String regOptId = map.get("regOptId").toString();
		DocBloodTransRecord bloodTransRecord = docBloodTransRecordService.searchOptBloodTransRecordByRegOptId(regOptId);
		
		List<DocBloodTransItem> bloodTransItem = docBloodTransItemService.getBloodTransItemList(bloodTransRecord.getBloodTransId());
		
		Boolean insertFlag = false; //是否插入数据
			
		if(null==bloodTransItem || bloodTransItem.size()==0){
			insertFlag = true;
		}
		
		//如果输血单中的血型字段都为空，则去获取检验表的数据
		if(StringUtils.isBlank(bloodTransRecord.getBloodType()) && StringUtils.isBlank(bloodTransRecord.getRhType()) 
				&& StringUtils.isBlank(bloodTransRecord.getAntScr()) && StringUtils.isBlank(bloodTransRecord.getAntGlo())){
			//先从本地的检验表的获取数据
			List<DocPatInspectItem> bloodList = patInspectItemService.queryBloodTypeByRegOptId(regOptId);
			if(null==bloodList || bloodList.size()<1){
				//如果检验表里没有，同步HIS最新的检验数据
				operListService.synLisDataList(map.get("regOptId").toString());
				//同步完成后再获取本地的检验数据
				bloodList = patInspectItemService.queryBloodTypeByRegOptId(regOptId);
			}
			
			if(null!=bloodList && bloodList.size()>0){
				for (DocPatInspectItem patInspectItem : bloodList) {
					if(patInspectItem.getName().equals("ABO血型")){
						bloodTransRecord.setBloodType(StringUtils.StringFilter(patInspectItem.getVal(),"[^A-Z]"));
						continue;
					}
					if(patInspectItem.getName().equals("Rh(D)血型")){
						bloodTransRecord.setRhType(patInspectItem.getVal());
						continue;
					}
					if(patInspectItem.getName().equals("血型单特异性抗体筛查")){
						bloodTransRecord.setAntScr(patInspectItem.getVal());
						continue;
					}
					if(patInspectItem.getName().equals("Rh血型系统E抗原")){
						bloodTransRecord.setAntGlo(patInspectItem.getVal());
						continue;
					}
				}
				docBloodTransRecordService.updateBloodTransRecord(bloodTransRecord);
			}else{
				resp.setResultCode("10000002");
				resp.setResultMessage("在检验报告中未获取患者的血型信息！");
			}
		}
		
		if(insertFlag){
			//如果输血明细表里没有数据，则默认生成5条输血数据
			List<BasBloodDefination> baseBlood = basBloodDefinationService.queryAllList();
			for (BasBloodDefination bloodDefination : baseBlood) {
				DocBloodTransItem item = new DocBloodTransItem();
				item.setBloodTransId(bloodTransRecord.getBloodTransId());
				item.setBloodName(bloodDefination.getName());
				item.setBloodType(bloodTransRecord.getBloodType());
				item.setBloodUnit(bloodDefination.getDosageUnit());
				item.setDate(new Date());
				item.setBloodId(bloodDefination.getBloodId());
				item.setId(GenerateSequenceUtil.generateSequenceNo());
				item.setStatus("1");
				bloodTransItem.add(item);
				docBloodTransItemService.insertBloodTransItem(item);
			}
		}
		
		BasRegOpt regOpt = basRegOptService.searchRegOptById(regOptId);
		BasDispatch dispatch = basDispatchService.getDispatchOper(regOptId);
		resp.put("bloodTransRecord", bloodTransRecord);
		resp.put("bloodTransItem", bloodTransItem);
		resp.put("regOpt", regOpt);
		resp.put("dispatch", dispatch);
		return resp.getJsonStr();
	}
	
	
    /** 
     * 保存输血申请单
     * <功能详细描述>
     * @param optCareRecord
     * @return
     * @throws ParseException 
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/updateBloodTransRecord")
    @ResponseBody
    @ApiOperation(value="保存输血申请单",httpMethod="POST",notes="保存输血申请单")
    public String updateBloodTransRecord(@ApiParam(name="record", value ="输血申请单对象") @RequestBody DocBloodTransRecord record)
    {
        logger.info("----------start updateBloodTransRecord---------------");
        ResponseValue resp = new ResponseValue();
        docBloodTransRecordService.updateBloodTransRecord(record);
        logger.info("----------end updateBloodTransRecord---------------");
        return resp.getJsonStr();
    }
    
    /**
     * 保存输血详情信息接口
     * @param record
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/saveBloodTransItem")
    @ResponseBody
    @ApiOperation(value="保存输血详情信息接口",httpMethod="POST",notes="保存输血详情信息接口")
    public String saveBloodTransItem(@ApiParam(name="record", value ="输血详情信息对象") @RequestBody DocBloodTransItem record)
    {
        logger.info("----------start saveBloodTransItem---------------");
        ResponseValue resp = new ResponseValue();
        docBloodTransItemService.saveBloodTransItem(record);
        logger.info("----------end saveBloodTransItem---------------");
        return resp.getJsonStr();
    }
    
    
    /**
     * 删除输血详情信息接口
     * @param record
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/deteleBloodTransItem")
    @ResponseBody
    @ApiOperation(value="删除输血详情信息接口",httpMethod="POST",notes="删除输血详情信息接口")
    public String deteleBloodTransItem(@ApiParam(name="record", value ="输血详情信息对象") @RequestBody DocBloodTransItem record)
    {
        logger.info("----------start deteleBloodTransItem---------------");
        ResponseValue resp = new ResponseValue();
        docBloodTransItemService.deleteBloodTransItem(record);
        logger.info("----------end deteleBloodTransItem---------------");
        return resp.getJsonStr();
    }
}
