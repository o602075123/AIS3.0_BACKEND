/**     
 * @discription 在此输入一句话描述此文件的作用
 * @author chengwang       
 * @created 2015-10-10 下午5:34:19    
 * tags     
 * see_to_target     
 */

package com.digihealth.anesthesia.interfacedata.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digihealth.anesthesia.basedata.formbean.MedicineFormBean;
import com.digihealth.anesthesia.basedata.po.BasChargeItem;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.web.BaseController;
import com.digihealth.anesthesia.doc.po.DocBloodTransRecord;
import com.digihealth.anesthesia.interfacedata.formbean.DoctorOrderFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.HisBloodInfoFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisOptcostFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.TakeMedRecordFormbean;
import com.digihealth.anesthesia.interfacedata.po.Blood;
import com.digihealth.anesthesia.interfacedata.po.BloodList;
import com.digihealth.anesthesia.interfacedata.po.OperCost;
import com.digihealth.anesthesia.interfacedata.po.OperCostList;

/**
 * Title: OperListController.java Description: 描述
 * 
 * @author chengwang
 * @created 2015-10-10 下午5:34:19
 */
@Controller
@RequestMapping(value = "/interfacedata")
public class OperListController extends BaseController {

	@Autowired  
	private  HttpServletRequest request;  
	
	/*@RequestMapping(value = "/hisToRegOpt")
	@ResponseBody
	public String hisToRegOpt(@RequestBody HisToRegOptFormBean bean){
		ResponseValue req = new ResponseValue();
		ValidatorBean validatorBean = beanValidator(bean);
		if(!(validatorBean.isValidator())){
			req.setResultCode("10000001");
			req.setResultMessage(validatorBean.getMessage());
		}
		ValidatorBean validatorBeanOperList = beanValidator(bean.getOperList());
		if(!(validatorBeanOperList.isValidator())){
			req.setResultCode("10000001");
			req.setResultMessage(validatorBeanOperList.getMessage());
		}
		InterfaceOauth iOauth = interfaceOauthService.findByKey(bean.getAppkey());
		if(iOauth==null){
			req.setResultCode("90000001");
		}else {
			try {
				String appkey = new String(RSACoder.decryptByPrivateKey(RSACoder.String2byte(bean.getToken()),iOauth.getPrivateKey()));
				if(bean.getAppkey().equals(appkey)){
					String requestUrl = request.getRequestURI();
					if(requestUrl.indexOf("ais")!=-1) {
						int index = requestUrl.indexOf("ais");
						requestUrl = requestUrl.substring(index+3, requestUrl.length()).trim();
					}
					if(iOauth.getMethod().indexOf(requestUrl)==-1){
						req.setResultCode("90000003");
					}else{
						int result = operListService.hisToRegOpt(bean.getOperList());
						if(result == 0){
							req.setResultCode("90000004");
						}else if(result == 1){
							req.setResultCode("90000000");
						}
					}
				}else{
					req.setResultCode("90000002");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(logger.isErrorEnabled()){
					logger.error(Exceptions.getStackTraceAsString(e));
				}
				req.setResultCode("10000000");
			}
		}
		
		
		return req.getJsonStr();
	}*/
	
	/**
	 * 深圳市龙岗区第二人民医院
	 * @return
	 */
	@RequestMapping(value = "/hisToRegOpt")
	@ResponseBody
	public String hisToRegOpt(){
		logger.info("begin hisToRegOpt");
		ResponseValue req = new ResponseValue();
		
		operListService.synHisOperList();
		
		logger.info("end hisToRegOpt");
		return req.getJsonStr();
	}
	
	/**
     *获得患者检验结果
     */
    @RequestMapping("/sendOperDispatchData")
    @ResponseBody
    public String sendOperDispatchData()
    {
        ResponseValue resp = new ResponseValue();
        try
        {
            BasDispatch disp = basDispatchService.getDispatchOper("201611101616144409");
            BasRegOpt opt = basRegOptService.searchRegOptById("201611101616144409");
            operListService.sendDispatchInfo(disp,opt);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resp.setResultCode("20000001");
            resp.setResultMessage("系统异常，请联系管理员！");
        }
        
        return resp.getJsonStr();
    }
    
    
    /**
     * 将手术费用信息同步至his
     * @return
     */
    public static final String CHARGE_TYPE_MEDICINE = "1";
    public static final String CHARGE_TYPE_PACKAGE = "2";
    
    @RequestMapping("/sendOperCostDataToHis")
    @ResponseBody
    public String sendOperCostDataToHis(@RequestBody Map map)
    {
        logger.info("sendOperCostDataToHis======费用开始同步!!!");
        ResponseValue resp = new ResponseValue();
        String regOptId = map.get("regOptId").toString();
        String costType = map.get("costType").toString();
        HisOptcostFormBean formbean = new HisOptcostFormBean();
        formbean.setCostType(map.get("costType").toString());
        formbean.setCreateUser(map.get("createUser").toString());
        //formbean.setRegDate(DateUtils.getDateTime());
        OperCostList  costList = new OperCostList();
        
        logger.info("sendOperCostDataToHis======术中收费项目开始同步!!!");
        
        List<OperCost> operCost = docPackagesItemService.queryUnCostListByRegOptId(regOptId,costType);
        if(null != operCost && operCost.size()>0){
            costList.setOperCost(operCost);
            formbean.setOperCostSum(costList);
            try {
                String msg = operListService.sendOptCost(formbean, regOptId ,CHARGE_TYPE_PACKAGE);
                if(msg.equals("succ")){
                    docPackagesItemService.updateChargeState(formbean.getOperCostSum().getOperCost());
                }
            } catch (Exception e) {
                resp.setResultCode("1000000000");
                resp.setResultMessage("单项收费项目费用同步异常："+Exceptions.getStackTraceAsString(e));
                return resp.getJsonStr();
            }
        }
        
        logger.info("sendOperCostDataToHis======术中药品费用开始同步!!!");
        
        List<OperCost> operMedCost = docEventBillingService.queryUnCostListByRegOptId(regOptId,costType);
        if(null != operMedCost && operMedCost.size()>0){
            costList.setOperCost(operMedCost);
            formbean.setOperCostSum(costList);
            try {
                String msg = "succ";
                //是否调用his接口
                String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
                if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                    msg = operListService.sendOptCost(formbean, regOptId , CHARGE_TYPE_MEDICINE);
                }
                if(msg.equals("succ")){
                    docEventBillingService.updateChargeState(formbean.getOperCostSum().getOperCost());
                }
            } catch (Exception e) {
                resp.setResultCode("1000000000");
                resp.setResultMessage("术中药品费用同步异常："+Exceptions.getStackTraceAsString(e));
                return resp.getJsonStr();
            }
        }
        logger.info("sendOperCostDataToHis======费用同步完成!!!");
        return resp.getJsonStr();
    }
    
    
    /**
     * 将手术输血信息同步至his
     * @return
     */
    @RequestMapping("/sendOperBloodDataToHis")
    @ResponseBody
    public String sendOperBloodDataToHis(@RequestBody Map map)
    {
        logger.info("sendOperBloodDataToHis======输血信息开始同步!!!");
        ResponseValue resp = new ResponseValue();
        String regOptId = map.get("regOptId").toString();
        BasRegOpt regOpt = basRegOptService.searchRegOptById(regOptId);
        DocBloodTransRecord bloodTransRecord = docBloodTransRecordService.searchOptBloodTransRecordByRegOptId(regOptId);
        List<Blood> bloodList = docBloodTransItemService.queryNotSendBloodItemListByRegoptId(bloodTransRecord.getBloodTransId(), "1");
        HisBloodInfoFormBean formbean = new HisBloodInfoFormBean();
        formbean.setAntGlo(bloodTransRecord.getAntGlo());
        formbean.setAntScr(bloodTransRecord.getAntScr());
        formbean.setRhType(bloodTransRecord.getRhType());
        formbean.setBloodType(bloodTransRecord.getBloodType());
        formbean.setReservenumber(regOpt.getPreengagementnumber());
        formbean.setUseDate(DateUtils.formatDateTime(new Date()));
        formbean.setDeptId(regOpt.getDeptId()+"");
        formbean.setDeptName(regOpt.getDeptName());
        BloodList blood_sum = new BloodList();
        formbean.setBlood_sum(blood_sum);
        if(null != bloodList && bloodList.size()>0){
            blood_sum.setBlood(bloodList);//获取未同步的输血记录
            //是否调用his接口
            String msg = "succ";
            String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
            if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                msg = operListService.sendBloodList(formbean,regOpt.getName());
            }
            if(msg.equals("succ")){
                docBloodTransItemService.updateBloodItemState(formbean.getBlood_sum().getBlood());
            }
            
        }
        logger.info("sendOperBloodDataToHis======输血信息同步完成!!!");
        return resp.getJsonStr();
    }
    
    
    
    /**
     *获得患者检验结果
     */
    @RequestMapping("/queryCheckListMaster")
    @ResponseBody
    public String queryCheckListMaster(@RequestBody Map map)
    {
        logger.info("queryCheckListMaster======检验数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                operListService.synLisDataList(map.get("regOptId").toString());
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("queryCheckListMaster====检验数据请求参数错误");
            }
        }
        logger.info("queryCheckListMaster======检验数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    public static final String CHECK_TYPE_BC = "1";//B超
    public static final String CHECK_TYPE_NKJ = "2";//内窥镜
    public static final String CHECK_TYPE_PACS = "3";//PACS影像
    
    /**
     *获得患者B超检查结果
     */
    @RequestMapping("/queryCheckBcMaster")
    @ResponseBody
    public String queryCheckBcMaster(@RequestBody Map map)
    {
        logger.info("queryCheckBcMaster======B超检查数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                operListService.synCheckDataList(map.get("regOptId").toString(),CHECK_TYPE_BC);
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("queryCheckBcMaster====B超检查数据请求参数错误");
            }
        }
        logger.info("queryCheckBcMaster======B超检查数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    /**
     *获得患者内窥镜检查结果
     */
    @RequestMapping("/queryCheckNkjMaster")
    @ResponseBody
    public String queryCheckNkjMaster(@RequestBody Map map)
    {
        logger.info("queryCheckNkjMaster======内窥镜检查数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                operListService.synCheckDataList(map.get("regOptId").toString(),CHECK_TYPE_NKJ);
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("queryCheckNkjMaster====内窥镜检查请求参数错误");
            }
        }
        logger.info("queryCheckNkjMaster======内窥镜检查数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    /**
     *获得患者PACS检查结果
     */
    @RequestMapping("/queryCheckPacsMaster")
    @ResponseBody
    public String queryCheckPacsMaster(@RequestBody Map map)
    {
        logger.info("queryCheckPacsMaster======PACS检查数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                operListService.synCheckDataList(map.get("regOptId").toString(),CHECK_TYPE_PACS);
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("queryCheckPacsMaster====PACS检查请求参数错误");
            }
        }
        logger.info("queryCheckPacsMaster======PACS检查数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    
    /**
     *获得患者临时医嘱信息
     */
    @RequestMapping("/queryDoctorOrderList")
    @ResponseBody
    public String queryDoctorOrderList(@RequestBody Map map)
    {
        logger.info("queryDoctorOrderList======临时医嘱数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                BasRegOpt regOpt = basRegOptService.searchRegOptById(map.get("regOptId").toString());
                operListService.synDoctorOrderList(regOpt,resp);
                resp.put("regOpt", regOpt); 
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("queryDoctorOrderList====临时医嘱请求参数错误");
            }
        }
        logger.info("queryDoctorOrderList======临时医嘱数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    
    
    /**
     *同步执行的医嘱信息
     */
    @RequestMapping("/sendDoctorExcuteList")
    @ResponseBody
    public String sendDoctorExcuteList(@RequestBody List<DoctorOrderFormbean> orderList)
    {
        logger.info("sendDoctorExcuteList======临时医嘱执行记录开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=orderList && orderList.size()>0){
            
            String msg = "succ";
            String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
            if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                msg = operListService.sendDoctorExcuteList(orderList);
            }
            if(msg.equals("succ")){
                docDocordRecordService.updateDoctorOrder(orderList);
            }
        }
        logger.info("queryDoctorOrderList======临时医嘱执行数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    
    
    /**
     *获得患者取药记录
     */
    @RequestMapping("/queryTakeMedRecordList")
    @ResponseBody
    public String queryTakeMedRecordList(@RequestBody Map map)
    {
        logger.info("queryTakeMedRecordList======患者取药记录数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                BasRegOpt regOpt = basRegOptService.searchRegOptById(map.get("regOptId").toString());
                List<TakeMedRecordFormbean> ls = new ArrayList<TakeMedRecordFormbean>();
                        //operListService.synDoctorOrderList(regOpt.getHid());
                resp.put("medList", ls);
                resp.put("total", ls.size());
                resp.put("regOpt", regOpt);
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("queryTakeMedRecordList====患者取药记录请求参数错误");
            }
        }
        logger.info("queryTakeMedRecordList======患者取药记录数据同步完成!!!");
        return resp.getJsonStr();
    }
    
	/**
	 *根据传入的参数获取his端的数据
	 */
	@RequestMapping("/queryHisChargeItemList")
	@ResponseBody
	public String queryHisChargeItemList(@RequestBody Map map)
	{
		logger.info("queryHisChargeItemList======开始数据获取!!!");
		ResponseValue resp = new ResponseValue();
		if(null !=map){
			if(null != map.get("pinyin")){
				String whereSql = " where pinyin like UPPER('%"+map.get("pinyin")+"%') or CHARGE_ITEM_NAME like '%"+map.get("pinyin")+"%'";
				List<BasChargeItem> chargeList = operBaseDataService.queryHisChargeItemListByWhereSql(whereSql);
				resp.put("rsList", chargeList);
			}else{
				resp.setResultCode("10000000");
				resp.setResultMessage("queryHisChargeItemList====请求错误!");
			}
		}
		logger.info("queryHisChargeItemList======数据获取完成!!!");
		return resp.getJsonStr();
	}
	
	
	/**
	 *根据传入的参数获取his端的数据
	 */
	@RequestMapping("/queryHisMedicinePriceDetailList")
	@ResponseBody
	public String queryHisMedicinePriceDetailList(@RequestBody Map map)
	{
		
		logger.info("queryHisMedicinePriceDetailList======开始数据获取!!!");
		ResponseValue resp = new ResponseValue();
		if(null !=map){
			if(null != map.get("name")){
				String whereSql = " and name like '%"+map.get("name")+"%'";
				List<MedicineFormBean> medLs= operBaseDataService.queryHisMedicinePriceListByWhereSql(whereSql);
				resp.put("rsList", medLs);
			}else{
				resp.setResultCode("10000000");
				resp.setResultMessage("queryHisMedicinePriceDetailList====请求错误!");
			}
		}
		logger.info("queryHisMedicinePriceDetailList======数据获取完成!!!");
		return resp.getJsonStr();
	}
    
	//===============================================黔南州接口开始==============================================================
	/**
     *获得患者检验结果
     */
    @RequestMapping("/queryCheckListMasterQNZ")
    @ResponseBody
    public String queryCheckListMasterQNZ(@RequestBody Map map)
    {
        logger.info("queryCheckListMasterQNZ======检验数据开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=map){
            if(null != map.get("regOptId")){
                operListServiceQNZ.synLisDataList(map.get("regOptId").toString());
            }else{
                resp.setResultCode("10000000");
                resp.setResultMessage("手术ID为空");
            }
        }
        logger.info("queryCheckListMasterQNZ======检验数据同步完成!!!");
        return resp.getJsonStr();
    }
    
    /** 
     * 费用同步到his
     * <功能详细描述>
     * @param map
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping("/sendOperCostDataToHisQNZ")
    @ResponseBody
    public String sendOperCostDataToHisQNZ(@RequestBody Map map)
    {
        logger.info("sendOperCostDataToHis======费用开始同步!!!");
        ResponseValue resp = new ResponseValue();
        String regOptId = map.get("regOptId").toString();
        operListServiceQNZ.sendOptCost(regOptId, resp);
        logger.info("sendOperCostDataToHis======费用同步结束!!!");
        return resp.getJsonStr();
    }
    
    /**
     *同步执行的医嘱信息
     */
    @RequestMapping("/sendDoctorExcuteListQNZ")
    @ResponseBody
    public String sendDoctorExcuteListQNZ(@RequestBody List<DoctorOrderFormbean> orderList)
    {
        logger.info("sendDoctorExcuteList======临时医嘱执行记录开始同步!!!");
        ResponseValue resp = new ResponseValue();
        if(null !=orderList && orderList.size()>0){
            
            String msg = "succ";
            String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
            if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                msg = operListServiceQNZ.sendDoctorExcuteList(orderList);
            }
            if(msg.equals("succ")){
                docDocordRecordService.updateDoctorOrder(orderList);
            }
        }
        logger.info("queryDoctorOrderList======临时医嘱执行数据同步完成!!!");
        return resp.getJsonStr();
    }
    
  //===============================================黔南州接口结束==============================================================
}
