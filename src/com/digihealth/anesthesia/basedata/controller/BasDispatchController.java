package com.digihealth.anesthesia.basedata.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digihealth.anesthesia.basedata.formbean.BaseInfoQuery;
import com.digihealth.anesthesia.basedata.formbean.DispatchOperationFormBean;
import com.digihealth.anesthesia.basedata.formbean.EmgencyOperationFormBean;
import com.digihealth.anesthesia.basedata.formbean.PrintNoticeFormBean;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.Controller;
import com.digihealth.anesthesia.basedata.utils.BasRegOptUtils;
import com.digihealth.anesthesia.common.beanvalidator.ValidatorBean;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.utils.SpringContextHolder;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.common.web.BaseController;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.service.OperListServiceQNZ;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;


/**
 * 
     * Title: DispatchController.java    
     * Description: 手术排程Controller
     * @author liukui       
     * @created 2015-9-15 下午1:56:40
 */
@org.springframework.stereotype.Controller
@RequestMapping(value = "/basedata")
@Api(value="BasDispatchController",description="手术排程处理类")
public class BasDispatchController extends BaseController{
	/**
	 * 手术排程查询接口
	 * @param baseQuery
	 * @return
	 */
	@RequestMapping(value = "/getDispatchItem")
	@ResponseBody
	@ApiOperation(value="查询手术排程信息列表",httpMethod="POST",notes="查询手术排程信息列表")
	public String getDispatchItem(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery) {
		logger.info("begin getDispatchItem");
		ResponseValue resp = new ResponseValue();
		resp.put("resultList", basDispatchService.findList(baseQuery));
		logger.info("end getDispatchItem");
		return resp.getJsonStr();
	}
	
	/**
	 * 打印手术通知单
	 * @param baseQuery
	 * @return
	 */
	@RequestMapping(value = "/printOperNoticeindList")
	@ResponseBody
	@ApiOperation(value="打印手术通知单",httpMethod="POST",notes="打印手术通知单")
	public String printOperNoticeindList(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery) {
		logger.info("begin printOperNoticeindList");
		ResponseValue resp = new ResponseValue();
		resp.put("resultList", basDispatchService.printOperNoticeindList(baseQuery));
		logger.info("end printOperNoticeindList");
		return resp.getJsonStr();
	}
	
	
	/**
	 * 打印排班
	 * @param baseQuery
	 * @return
	 */
	@RequestMapping(value = "/printDispatchItem")
	@ResponseBody
	@ApiOperation(value="打印排班",httpMethod="POST",notes="打印排班")
	public String printDispatchItem(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery) {
		logger.info("begin printDispatchItem");
		ResponseValue resp = new ResponseValue();
		resp.put("resultList", basDispatchService.printDispatchItem(baseQuery));
		logger.info("end printDispatchItem");
		return resp.getJsonStr();
	}
	
	/**
     * 打印排班
     * @param baseQuery
     * @return
     */
    @RequestMapping(value = "/printDispatchItemHBGZB")
    @ResponseBody
    @ApiOperation(value="打印排班",httpMethod="POST",notes="打印排班")
    public String printDispatchItemHBGZB(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery) {
        logger.info("begin printDispatchItem");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.printDispatchItemHBGZB(baseQuery));
        logger.info("end printDispatchItem");
        return resp.getJsonStr();
    }
    
    /**
     * 打印排班
     * @param baseQuery
     * @return
     */
    @RequestMapping(value = "/printDispatchItemQNZ")
    @ResponseBody
    @ApiOperation(value="打印排班",httpMethod="POST",notes="打印排班")
    public String printDispatchItemQNZ(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery) {
        logger.info("begin printDispatchItem");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.printDispatchItemQNZ(baseQuery));
        logger.info("end printDispatchItem");
        return resp.getJsonStr();
    }
    
    /**
     * 打印排班
     * @param baseQuery
     * @return
     */
    @RequestMapping(value = "/printDispatchItemXYCD")
    @ResponseBody
    @ApiOperation(value="打印排班",httpMethod="POST",notes="打印排班")
    public String printDispatchItemXYCD(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery) {
        logger.info("begin printDispatchItem");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.printDispatchItemXYCD(baseQuery));
        logger.info("end printDispatchItem");
        return resp.getJsonStr();
    }
	
	/**
	 * 创建紧急手术排程
	 * @param BasDispatch
	 * @return
	 */
	@RequestMapping(value = "/createEmergencyOperation")
	@ResponseBody
	@ApiOperation(value="创建紧急手术排程",httpMethod="POST",notes="创建紧急手术排程")
	public String createEmergencyOperation(@ApiParam(name="emgencyOperationFormBean", value ="急诊参数对象") @RequestBody EmgencyOperationFormBean emgencyOperationFormBean){
        logger.info("begin createEmergencyOperation");
        ResponseValue respValue = new ResponseValue();
        BasRegOpt regOpt = emgencyOperationFormBean.getRegOpt();
        BasDispatch dispatch = emgencyOperationFormBean.getDispatch();
        ValidatorBean validatorBean = beanValidator(regOpt);
        if (!(validatorBean.isValidator())) {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        if (regOpt.getAge() == null && regOpt.getAgeMon() == null && regOpt.getAgeDay() == null) {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天必填一项");
            return respValue.getJsonStr();
        }
        int age = regOpt.getAge() == null ? 0 : regOpt.getAge();
        int ageMon = regOpt.getAgeMon() == null ? 0 : regOpt.getAgeMon();
        int ageDay = regOpt.getAgeDay() == null ? 0 : regOpt.getAgeDay();
        if (age < 1 && ageMon < 1 && ageDay < 1) {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天其中一项必须要大于0");
            return respValue.getJsonStr();
        }
        ValidatorBean validatorBean1 = beanValidator(dispatch);
        if (!(validatorBean1.isValidator())) {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        // 检查手术时间是否被占用
        Integer total = basDispatchService.checkOperateTimeBeUse(dispatch);
        if (total > 0) {
            respValue.setResultCode("10000001");
            respValue.setResultCode("手术时间被占用请重新选择!");
            return respValue.getJsonStr();
        }
        String beid = regOpt.getBeid();
        if (StringUtils.isBlank(beid)) {
			beid = getBeid();
			regOpt.setBeid(beid);
		}
        BasRegOptUtils.getOtherInfo(regOpt);
        BasRegOptUtils.IsLocalAnaesSet(regOpt);
        if (StringUtils.isEmpty(dispatch.getAnesthetistId()) && regOpt.getIsLocalAnaes() == 0) {
            respValue.setResultCode("10000001");
            respValue.setResultMessage("全麻手术必填麻醉医生");
            return respValue.getJsonStr();
        }
        basDispatchService.createEmergencyOperation(regOpt, dispatch, respValue);
        logger.info("end createEmergencyOperation");
        return respValue.getJsonStr();
	}	
	
	/**
     * 创建紧急手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/createEmergencyOperationSYBX")
    @ResponseBody
    @ApiOperation(value="创建紧急手术排程",httpMethod="POST",notes="创建紧急手术排程")
    public String createEmergencyOperationSYBX(@ApiParam(name="emgencyOperationFormBean", value ="急诊参数对象") @RequestBody EmgencyOperationFormBean emgencyOperationFormBean){
        logger.info("begin createEmergencyOperation");
        ResponseValue respValue = new ResponseValue();
        BasRegOpt regOpt = emgencyOperationFormBean.getRegOpt();
        BasDispatch dispatch = emgencyOperationFormBean.getDispatch();
        ValidatorBean validatorBean = beanValidator(regOpt);
        if (!(validatorBean.isValidator())) {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        if (regOpt.getAge() == null && regOpt.getAgeMon() == null && regOpt.getAgeDay() == null) {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天必填一项");
            return respValue.getJsonStr();
        }
        int age = regOpt.getAge() == null ? 0 : regOpt.getAge();
        int ageMon = regOpt.getAgeMon() == null ? 0 : regOpt.getAgeMon();
        int ageDay = regOpt.getAgeDay() == null ? 0 : regOpt.getAgeDay();
        if (age < 1 && ageMon < 1 && ageDay < 1) {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天其中一项必须要大于0");
            return respValue.getJsonStr();
        }
        ValidatorBean validatorBean1 = beanValidator(dispatch);
        if (!(validatorBean1.isValidator())) {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        // 检查手术时间是否被占用
        Integer total = basDispatchService.checkOperateTimeBeUse(dispatch);
        if (total > 0) {
            respValue.setResultCode("10000001");
            respValue.setResultCode("手术时间被占用请重新选择!");
            return respValue.getJsonStr();
        }
        String beid = regOpt.getBeid();
        if (StringUtils.isBlank(beid)) {
            beid = getBeid();
            regOpt.setBeid(beid);
        }
        BasRegOptUtils.getOtherInfo(regOpt);
        BasRegOptUtils.IsLocalAnaesSet(regOpt);
        if (StringUtils.isEmpty(dispatch.getAnesthetistId()) && regOpt.getIsLocalAnaes() == 0) {
            respValue.setResultCode("10000001");
            respValue.setResultMessage("全麻手术必填麻醉医生");
            return respValue.getJsonStr();
        }
        basDispatchService.createEmergencyOperationSYBX(regOpt, dispatch, respValue);
        logger.info("end createEmergencyOperation");
        return respValue.getJsonStr();
    }   
	
    /**
     * 创建紧急手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/createEmergencyOperationNHFE")
    @ResponseBody
    @ApiOperation(value="创建紧急手术排程",httpMethod="POST",notes="创建紧急手术排程")
    public String createEmergencyOperationNHFE(
        @ApiParam(name = "emgencyOperationFormBean", value = "急诊参数对象") @RequestBody EmgencyOperationFormBean emgencyOperationFormBean)
    {
        logger.info("begin createEmergencyOperation");
        
        ResponseValue respValue = new ResponseValue();
        BasRegOpt regOpt = emgencyOperationFormBean.getRegOpt();
        BasDispatch dispatch = emgencyOperationFormBean.getDispatch();
        ValidatorBean validatorBean = beanValidator(regOpt);
        if (!(validatorBean.isValidator()))
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        if (regOpt.getAge() == null && regOpt.getAgeMon() == null && regOpt.getAgeDay() == null)
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天必填一项");
            return respValue.getJsonStr();
        }
        int age = regOpt.getAge() == null ? 0 : regOpt.getAge();
        int ageMon = regOpt.getAgeMon() == null ? 0 : regOpt.getAgeMon();
        int ageDay = regOpt.getAgeDay() == null ? 0 : regOpt.getAgeDay();
        if (age < 1 && ageMon < 1 && ageDay < 1)
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天其中一项必须要大于0");
            return respValue.getJsonStr();
        }
        
        
        ValidatorBean validatorBean1 = beanValidator(dispatch);
        if (!(validatorBean1.isValidator()))
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        String beid = regOpt.getBeid();
        if (StringUtils.isBlank(beid)) {
            beid = getBeid();
            regOpt.setBeid(beid);
        }
        BasRegOptUtils.IsLocalAnaesSet(regOpt);
        BasRegOptUtils.getOtherInfo(regOpt);
        basDispatchService.createEmergencyOperationNHFE(regOpt, dispatch, respValue);
        
        // 将急诊信息同步到his,并对his回传的预约号进行保存
        HisResponse hisResponse = operListService.sendEmergencyOperation(regOpt, dispatch);
        if (null != hisResponse && "0".equals(hisResponse.getResultCode()))
        {
            if (StringUtils.isNotBlank(hisResponse.getReservenumber()))
            {
                regOpt.setPreengagementnumber(hisResponse.getReservenumber());
                basRegOptService.updateRegOpt(regOpt);
            }
        }
        
        logger.info("end createEmergencyOperation");
        return respValue.getJsonStr();
    }
    
    /**
     * 创建紧急手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/createEmergencyOperationQNZ")
    @ResponseBody
    @ApiOperation(value="创建紧急手术排程",httpMethod="POST",notes="创建紧急手术排程")
    public String createEmergencyOperationQNZ(
        @ApiParam(name = "emgencyOperationFormBean", value = "急诊参数对象") @RequestBody EmgencyOperationFormBean emgencyOperationFormBean)
    {
        logger.info("begin createEmergencyOperationQNZ");
        
        ResponseValue respValue = new ResponseValue();
        BasRegOpt regOpt = emgencyOperationFormBean.getRegOpt();
        BasDispatch dispatch = emgencyOperationFormBean.getDispatch();
        ValidatorBean validatorBean = beanValidator(regOpt);
        if (!(validatorBean.isValidator()))
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        if (regOpt.getAge() == null && regOpt.getAgeMon() == null && regOpt.getAgeDay() == null)
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天必填一项");
            return respValue.getJsonStr();
        }
        int age = regOpt.getAge() == null ? 0 : regOpt.getAge();
        int ageMon = regOpt.getAgeMon() == null ? 0 : regOpt.getAgeMon();
        int ageDay = regOpt.getAgeDay() == null ? 0 : regOpt.getAgeDay();
        if (age < 1 && ageMon < 1 && ageDay < 1)
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode("年、月、天其中一项必须要大于0");
            return respValue.getJsonStr();
        }
        
        
        ValidatorBean validatorBean1 = beanValidator(dispatch);
        if (!(validatorBean1.isValidator()))
        {
            respValue.setResultCode("10000001");
            respValue.setResultCode(validatorBean.getMessage());
            return respValue.getJsonStr();
        }
        String beid = regOpt.getBeid();
        if (StringUtils.isBlank(beid)) {
            beid = getBeid();
            regOpt.setBeid(beid);
        }
        BasRegOptUtils.IsLocalAnaesSet(regOpt);
        BasRegOptUtils.getOtherInfo(regOpt);
        basDispatchService.createEmergencyOperationQNZ(regOpt, dispatch, respValue);
        
        /*String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
        if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag))
        {
            // 将急诊信息同步到his,并对his回传的预约号进行保存
            OperListServiceQNZ operListServiceQNZ = SpringContextHolder.getBean(OperListServiceQNZ.class);
            HisResponse hisResponse = operListServiceQNZ.sendEmergencyOperation(regOpt, dispatch);
            if (null != hisResponse && "0".equals(hisResponse.getResultCode()))
            {
                if (StringUtils.isNotBlank(hisResponse.getReservenumber()))
                {
                    regOpt.setPreengagementnumber(hisResponse.getReservenumber());
                    basRegOptService.updateRegOptByHis(regOpt);
                }
            }
        }*/
        
        logger.info("end createEmergencyOperationQNZ");
        return respValue.getJsonStr();
    }
    
    
    
	/**
	 * 根据传入的手术室、手术日期获取手术时间列表
	 * @param BasDispatch
	 * @return
	 */
    @RequestMapping(value = "/getStartTimeList")
    @ResponseBody
    @ApiOperation(value = "根据传入的手术室、手术日期获取手术时间列表", httpMethod = "POST", notes = "根据传入的手术室、手术日期获取手术时间列表")
    public String getStartTimeList(@ApiParam(name = "dispatch", value = "排程信息对象") @RequestBody BasDispatch dispatch)
    {
        logger.info("begin getStartTimeList");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.getNotUseTimeList(dispatch));
        logger.info("end getStartTimeList");
        return resp.getJsonStr();
    }
	
    @RequestMapping("/getNoUsePcsList")
    @ResponseBody
    @ApiOperation(value = "查询未使用的台次", httpMethod = "POST", notes = "查询未使用的台次")
    public String getNoUsePcsList(@ApiParam(name = "dispatch", value = "排程信息对象") @RequestBody BasDispatch dispatch)
    {
        logger.info("begin getNoUsePcsList");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.getNoUsePcsList(dispatch));
        logger.info("end getNoUsePcsList");
        return resp.getJsonStr();
    }
	
	/**
	 * 创建手术排程
	 * @param BasDispatch
	 * @return
	 */
    @RequestMapping(value = "/dispatchOperation")
    @ResponseBody
    @ApiOperation(value = "创建手术排程", httpMethod = "POST", notes = "创建手术排程")
    public String dispatchOperation(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin dispatchOperation");
        ResponseValue resp = new ResponseValue();
        basDispatchService.saveDispatch(dispatchFormBean, resp);
        logger.info("end dispatchOperation");
        return resp.getJsonStr();
    }
    
    /**
     * 创建手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/dispatchOperationSYBX")
    @ResponseBody
    @ApiOperation(value = "创建手术排程", httpMethod = "POST", notes = "创建手术排程")
    public String dispatchOperationSYBX(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin dispatchOperation");
        ResponseValue resp = new ResponseValue();
        basDispatchService.saveDispatchSYBX(dispatchFormBean, resp);
        logger.info("end dispatchOperation");
        return resp.getJsonStr();
    }
    
    /**
     * 创建手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/dispatchOperationNHFE")
    @ResponseBody
    @ApiOperation(value = "创建手术排程", httpMethod = "POST", notes = "创建手术排程")
    public String dispatchOperationNHFE(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin dispatchOperation");
        ResponseValue resp = new ResponseValue();
        basDispatchService.saveDispatchNHFE(dispatchFormBean, resp);
        logger.info("end dispatchOperation");
        return resp.getJsonStr();
    }
    
    /**
     * 创建手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/dispatchOperationQNZ")
    @ResponseBody
    @ApiOperation(value = "创建手术排程", httpMethod = "POST", notes = "创建手术排程")
    public String dispatchOperationQNZ(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin dispatchOperation");
        ResponseValue resp = new ResponseValue();
        basDispatchService.saveDispatchQNZ(dispatchFormBean, resp);
        logger.info("end dispatchOperation");
        return resp.getJsonStr();
    }
    
    /**
     * 创建手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/dispatchOperationXYCD")
    @ResponseBody
    @ApiOperation(value = "创建手术排程", httpMethod = "POST", notes = "创建手术排程")
    public String dispatchOperationXYCD(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin dispatchOperation");
        ResponseValue resp = new ResponseValue();
        basDispatchService.saveDispatchXYCD(dispatchFormBean, resp);
        logger.info("end dispatchOperation");
        return resp.getJsonStr();
    }
    
    /**
     * 创建手术排程
     * @param BasDispatch
     * @return
     */
    @RequestMapping(value = "/dispatchOperationYXRM")
    @ResponseBody
    @ApiOperation(value = "创建手术排程", httpMethod = "POST", notes = "创建手术排程")
    public String dispatchOperationYXRM(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin dispatchOperation");
        ResponseValue resp = new ResponseValue();
        basDispatchService.saveDispatchYXRM(dispatchFormBean, resp);
        logger.info("end dispatchOperation");
        return resp.getJsonStr();
    }
    
    
    @RequestMapping(value = "/changeOperRoom")
    @ResponseBody
    @ApiOperation(value = "更改手术室", httpMethod = "POST", notes = "更改手术室")
    public String changeOperRoom(@ApiParam(name = "dispatchFormBean", value = "排程信息操作对象") @RequestBody DispatchOperationFormBean dispatchFormBean)
    {
        logger.info("begin changeOperRoom");
        ResponseValue resp = new ResponseValue();
        basDispatchService.changeOperRoom(dispatchFormBean, resp);
        logger.info("end changeOperRoom");
        return resp.getJsonStr();
    }
	
	/**
	 * 手术排程取消接口
	 * @param BasDispatch
	 * @return
	 */
	@RequestMapping(value = "/cancelDispatchItem")
	@ResponseBody
	@ApiOperation(value="取消手术排程",httpMethod="POST",notes="取消手术排程")
    public String cancelDispatchItem(@ApiParam(name = "regOptIdList", value = "手术Id列表") @RequestBody List<String> regOptIdList)
    {
        logger.info("begin cancelDispatchItem");
        ResponseValue resp = new ResponseValue();
        basDispatchService.cancelDispatchItem(regOptIdList);
        logger.info("end cancelDispatchItem");
        return resp.getJsonStr();
    }
	
	/**
	 * 批量重排手术排程
	 * @param BasDispatch
	 * @return
	 */
	@RequestMapping(value = "/redispatchItem")
	@ResponseBody
	@ApiOperation(value="批量重排手术排程",httpMethod="POST",notes="批量重排手术排程")
    public String redispatchItem(@ApiParam(name = "map", value = "参数") @RequestBody Map map)
    {
        logger.info("begin redispatchItem");
        ResponseValue respValue = new ResponseValue();
        basDispatchService.redispatchItem(map);
        logger.info("end redispatchItem");
        return respValue.getJsonStr();
    }
	
	/**
	 * 查询当前人员的状态
	 * @param BasDispatch
	 * @return
	 */
	@RequestMapping(value = "/getPersonCurrState")
	@ResponseBody
	@ApiOperation(value="查询当前人员的状态",httpMethod="POST",notes="查询当前人员的状态")
    public String getPersonCurrState(@ApiParam(name = "basDispatch", value = "排程信息对象") @RequestBody BasDispatch basDispatch)
    {
        logger.info("begin getPersonCurrState");
        ResponseValue respValue = new ResponseValue();
        Controller controller = controllerService.getControllerById(basDispatch.getRegOptId() + "");
        respValue.put("state", controller.getState());
        logger.info("end getPersonCurrState");
        return respValue.getJsonStr();
    }

	
	
	/**
	 * 根据手术日期、时间、手术室id，获取该时间段的排班数
	 * @param operDate
	 * @param startTime
	 * @param roomId
	 * @return
	 */
	@RequestMapping(value = "/searchPersonTotalInOperRoomByStartTime")
	@ResponseBody
	@ApiOperation(value="根据手术日期、时间、手术室id，获取该时间段的排班数",httpMethod="POST",notes="根据手术日期、时间、手术室id，获取该时间段的排班数")
	public String searchPersonTotalInOperRoomByStartTime(@ApiParam(name = "basDispatch", value = "排程信息对象") @RequestBody BasDispatch basDispatch)
	{
        logger.info("begin searchPersonTotalInOperRoomByStartTime");
        ResponseValue respValue = new ResponseValue();
        Integer total = basDispatchService.searchPersonTotalInOperRoomByStartTime(basDispatch);
        respValue.put("total", total);
        logger.info("end searchPersonTotalInOperRoomByStartTime");
        return respValue.getJsonStr();
	}
	
	@RequestMapping(value = "/getOperateInfoByInsideScreen")
	@ResponseBody
	@ApiOperation(value="获取内屏手术信息",httpMethod="POST",notes="获取内屏手术信息")
    public String getOperateInfoByInsideScreen()
    {
        logger.info("begin getOperateInfoByInsideScreen");
        ResponseValue resp = new ResponseValue();
        Integer total = basDispatchService.getOperateInfoByInsideScreen().size();
        resp.put("resultList", basDispatchService.getOperateInfoByInsideScreen());
        resp.put("total", total);
        basTitleStyleService.getStyleList(resp, 1);
        logger.info("end getOperateInfoByInsideScreen");
        return resp.getJsonStr();
    }
	
	@RequestMapping(value = "/getOperateInfoByInsideScreenQNZ")
    @ResponseBody
    @ApiOperation(value="获取内屏手术信息",httpMethod="POST",notes="获取内屏手术信息")
    public String getOperateInfoByInsideScreenQNZ()
    {
        logger.info("begin getOperateInfoByInsideScreenQNZ");
        ResponseValue resp = new ResponseValue();
        List<PrintNoticeFormBean> list = basDispatchService.getOperateInfoByInsideScreenQNZ();
        Integer total = null != list ? list.size() : null;
        resp.put("resultList", list);
        resp.put("total", total);
        basTitleStyleService.getStyleList(resp, 1);
        logger.info("end getOperateInfoByInsideScreenQNZ");
        return resp.getJsonStr();
    }
	
	
	@RequestMapping(value = "/getOperateInfoByOutsideScreen")
	@ResponseBody
	@ApiOperation(value="获取外屏手术信息",httpMethod="POST",notes="获取外屏手术信息")
    public String getOperateInfoByOutsideScreen()
    {
        logger.info("begin getOperateInfoByOutsideScreen");
        ResponseValue resp = new ResponseValue();
        Integer total = basDispatchService.getOperateInfoByOutsideScreen().size();
        resp.put("resultList", basDispatchService.getOperateInfoByOutsideScreen());
        resp.put("total", total);
        basTitleStyleService.getStyleList(resp, 2);
        logger.info("end getOperateInfoByOutsideScreen");
        return resp.getJsonStr();
    }
	
	/**
     * 根据条件查询未排班的的列表  列表排班
     * @return
     */
    @RequestMapping(value = "/searchNoEndListSchedule")
    @ResponseBody
    @ApiOperation(value="根据条件查询未排班的的列表 ",httpMethod="POST",notes="根据条件查询未排班的的列表 ")
    public String searchNoEndListSchedule(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery){
        logger.info("begin searchNoEndListSchedule");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.searchAllDispatchList(baseQuery));
        resp.put("total", basDispatchService.searchAllDispatchListTotal(baseQuery));
        logger.info("end searchNoEndListSchedule");
        return resp.getJsonStr();
    }
    
    /**
     * 根据条件查询未排班的的列表  列表排班
     * @return
     */
    @RequestMapping(value = "/searchNoEndListScheduleSYBX")
    @ResponseBody
    @ApiOperation(value="根据条件查询未排班的的列表 ",httpMethod="POST",notes="根据条件查询未排班的的列表 ")
    public String searchNoEndListScheduleSYBX(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery){
        logger.info("begin searchNoEndListSchedule");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.searchAllDispatchListSYBX(baseQuery));
        logger.info("end searchNoEndListSchedule");
        return resp.getJsonStr();
    }
    
    
    /**
     * 根据条件查询未排班的的列表  列表排班
     * @return
     */
    @RequestMapping(value = "/searchNoEndListScheduleNHFE")
    @ResponseBody
    @ApiOperation(value="根据条件查询未排班的的列表 ",httpMethod="POST",notes="根据条件查询未排班的的列表 ")
    public String searchNoEndListScheduleNHFE(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery){
        logger.info("begin searchNoEndListSchedule");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.searchAllDispatchListNHFE(baseQuery));
        logger.info("end searchNoEndListSchedule");
        return resp.getJsonStr();
    }
    
    /**
     * 根据条件查询未排班的的列表  列表排班
     * @return
     */
    @RequestMapping(value = "/searchNoEndListScheduleQNZ")
    @ResponseBody
    @ApiOperation(value="根据条件查询未排班的的列表 ",httpMethod="POST",notes="根据条件查询未排班的的列表 ")
    public String searchNoEndListScheduleQNZ(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery){
        logger.info("begin searchNoEndListSchedule");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.searchAllDispatchListQNZ(baseQuery));
        logger.info("end searchNoEndListSchedule");
        return resp.getJsonStr();
    }
    
    /**
     * 根据条件查询未排班的的列表  列表排班
     * @return
     */
    @RequestMapping(value = "/searchNoEndListScheduleYXRM")
    @ResponseBody
    @ApiOperation(value="根据条件查询未排班的的列表 ",httpMethod="POST",notes="根据条件查询未排班的的列表 ")
    public String searchNoEndListScheduleYXRM(@ApiParam(name="baseQuery", value ="系统查询参数") @RequestBody BaseInfoQuery baseQuery){
        logger.info("begin searchNoEndListScheduleYXRM");
        ResponseValue resp = new ResponseValue();
        resp.put("resultList", basDispatchService.searchAllDispatchListYXRM(baseQuery));
        logger.info("end searchNoEndListScheduleYXRM");
        return resp.getJsonStr();
    }
    
    /**
     * 护士将排班未完成的数据推送给麻醉医生排班
     * @return
     */
    @RequestMapping(value = "/dispatchDataPush")
    @ResponseBody
    public String dispatchDataPush(@RequestBody List<String> regOptList){
        logger.info("begin dispatchDataPush");
        ResponseValue resp = new ResponseValue();
        basDispatchService.dispatchDataPush(regOptList);
        logger.info("end dispatchDataPush");
        return resp.getJsonStr();
    }
	
    /**
     * 手术室安排取消
     * @return
     */
    @RequestMapping(value = "/cancelOperroomDispatch")
    @ResponseBody
    public String cancelOperroomDispatch(@RequestBody BasRegOpt regOpt){
        logger.info("begin cancelOperroomDispatch");
        ResponseValue resp = new ResponseValue();
        
        basDispatchService.cancelOperroomDispatch(regOpt.getRegOptId(),resp);
        
        logger.info("end cancelOperroomDispatch");
        return resp.getJsonStr();
    }
    
    /**
     * 手术室安排取消
     * @return
     */
    @RequestMapping(value = "/cancelOperroomDispatchNHFE")
    @ResponseBody
    public String cancelOperroomDispatchNHFE(@RequestBody BasRegOpt regOpt){
        logger.info("begin cancelOperroomDispatchNHFE");
        ResponseValue resp = new ResponseValue();
        
        basDispatchService.cancelOperroomDispatchNHFE(regOpt.getRegOptId(),resp);
        
        logger.info("end cancelOperroomDispatchNHFE");
        return resp.getJsonStr();
    }
    
    /**
     * 手术室安排取消
     * @return
     */
    @RequestMapping(value = "/cancelOperroomDispatchQNZ")
    @ResponseBody
    public String cancelOperroomDispatchQNZ(@RequestBody BasRegOpt regOpt){
        logger.info("begin cancelOperroomDispatchQNZ");
        ResponseValue resp = new ResponseValue();
        
        basDispatchService.cancelOperroomDispatchQNZ(regOpt.getRegOptId(),resp);
        
        logger.info("end cancelOperroomDispatchQNZ");
        return resp.getJsonStr();
    }
}
