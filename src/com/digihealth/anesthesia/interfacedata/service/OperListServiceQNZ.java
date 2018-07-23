/**     
 * @discription 在此输入一句话描述此文件的作用
 * @author chengwang       
 * @created 2015-10-10 下午5:32:33    
 * tags     
 * see_to_target     
 */

package com.digihealth.anesthesia.interfacedata.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tempuri.AIMSService;
import org.tempuri.AIMSServiceSoap;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.formbean.DispatchFormbean;
import com.digihealth.anesthesia.basedata.po.BasAnaesMethod;
import com.digihealth.anesthesia.basedata.po.BasChargeItem;
import com.digihealth.anesthesia.basedata.po.BasDiagnosedef;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasMedicine;
import com.digihealth.anesthesia.basedata.po.BasOperationPeople;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.BasRegion;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.BeanHelper;
import com.digihealth.anesthesia.common.utils.ConnectionManager;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.PingYinUtil;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.doc.po.DocAccede;
import com.digihealth.anesthesia.doc.po.DocAnaesBeforeSafeCheck;
import com.digihealth.anesthesia.doc.po.DocAnaesPlan;
import com.digihealth.anesthesia.doc.po.DocAnaesPostop;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.doc.po.DocAnaesSummary;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAllergicReaction;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAppendixCanal;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAppendixGen;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryAppendixVisit;
import com.digihealth.anesthesia.doc.po.DocAnaesSummaryVenipuncture;
import com.digihealth.anesthesia.doc.po.DocDocordRecord;
import com.digihealth.anesthesia.doc.po.DocExitOperSafeCheck;
import com.digihealth.anesthesia.doc.po.DocInsuredPatAgree;
import com.digihealth.anesthesia.doc.po.DocNurseInterviewRecord;
import com.digihealth.anesthesia.doc.po.DocOperBeforeSafeCheck;
import com.digihealth.anesthesia.doc.po.DocOptCareRecord;
import com.digihealth.anesthesia.doc.po.DocOptNurse;
import com.digihealth.anesthesia.doc.po.DocOptRiskEvaluation;
import com.digihealth.anesthesia.doc.po.DocPatCheckItem;
import com.digihealth.anesthesia.doc.po.DocPatCheckRecord;
import com.digihealth.anesthesia.doc.po.DocPatInspectItem;
import com.digihealth.anesthesia.doc.po.DocPatInspectRecord;
import com.digihealth.anesthesia.doc.po.DocPatOutRangeAgree;
import com.digihealth.anesthesia.doc.po.DocPatShuttleTransfer;
import com.digihealth.anesthesia.doc.po.DocPlacentaHandleAgree;
import com.digihealth.anesthesia.doc.po.DocPostFollowRecord;
import com.digihealth.anesthesia.doc.po.DocPostOperRegard;
import com.digihealth.anesthesia.doc.po.DocPreOperVisit;
import com.digihealth.anesthesia.doc.po.DocPrePostVisit;
import com.digihealth.anesthesia.doc.po.DocPreVisit;
import com.digihealth.anesthesia.doc.po.DocSafeCheck;
import com.digihealth.anesthesia.doc.po.DocTransferConnectRecord;
import com.digihealth.anesthesia.interfacedata.formbean.DoctorExcutFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.DoctorOrderFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.EmergencyOperFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.HisCancleOptFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisDispatchFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.po.ChargItem;
import com.digihealth.anesthesia.interfacedata.po.ChargResult;
import com.digihealth.anesthesia.interfacedata.po.HisChargItem;
import com.digihealth.anesthesia.interfacedata.po.HisDispatch;
import com.digihealth.anesthesia.interfacedata.po.HisOptChargResponse;
import com.digihealth.anesthesia.interfacedata.po.HisOptChargeRequest;
import com.digihealth.anesthesia.interfacedata.po.HisRegOpt;
import com.digihealth.anesthesia.interfacedata.po.HisReportDetailReq;
import com.digihealth.anesthesia.interfacedata.po.HisReportDetailResp;
import com.digihealth.anesthesia.interfacedata.po.HisReportReq;
import com.digihealth.anesthesia.interfacedata.po.HisReportResp;
import com.digihealth.anesthesia.interfacedata.po.OperList;
import com.digihealth.anesthesia.interfacedata.po.Report;
import com.digihealth.anesthesia.interfacedata.po.ReportItem;

/**
 * Title: OperListService.java Description: 描述
 * 
 * @author chengwang
 * @created 2015-10-10 下午5:32:33
 */
@Service
public class OperListServiceQNZ extends BaseService {

	/**
     *急诊手术
     */
    public HisResponse sendEmergencyOperation(BasRegOpt regOpt,BasDispatch record)
    {
        logger.info("正在同步"+regOpt.getName()+"的急诊患者基础信息!!!");
        
        HisRegOpt hisRegOpt = new HisRegOpt();
        BeanHelper.copyProperties(regOpt, hisRegOpt); 
        hisRegOpt.setOperDate(regOpt.getOperaDate());
        hisRegOpt.setOperType(1);
        hisRegOpt.setAnaesType(String.valueOf(regOpt.getIsLocalAnaes()));
        hisRegOpt.setDragAllergy(regOpt.getHyperSusceptiBility());
        hisRegOpt.setDeptId(null == regOpt.getDeptId() ? null : Integer.parseInt(regOpt.getDeptId()));
        hisRegOpt.setRegionId(null == regOpt.getRegionId() ? null : Integer.parseInt(regOpt.getRegionId()));
        hisRegOpt.setOperLevel(regOpt.getOptLevel());
        hisRegOpt.setIncisionLevel(null == regOpt.getCutLevel() ? null : regOpt.getCutLevel()+"");
        hisRegOpt.setFrontOperSpecialCase(regOpt.getFrontOperSpecialCase());
        hisRegOpt.setFrontOperForbidTake(regOpt.getFrontOperForbidTake());
        hisRegOpt.setCreateUser(regOpt.getCreateUser());
        //切口等级
        if(null != regOpt.getCutLevel()){
            String incisionlevel = "";
            if("1".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅰ";
            }
            if("2".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅱ";
            }
            if("3".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅲ";
            }
            if("4".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅳ";
            }
            hisRegOpt.setIncisionLevel(incisionlevel);
        }
        
        String age = "";
        if(null!=regOpt.getAge() && regOpt.getAge() > 0){
            age = regOpt.getAge()+"岁";
        }
        if(null!=regOpt.getAgeMon() && regOpt.getAgeMon() > 0){
            age += regOpt.getAgeMon()+"月";
        }
        if(null!=regOpt.getAgeDay() && regOpt.getAgeDay() > 0){
            age += regOpt.getAgeDay()+"天";
        }
        hisRegOpt.setAge(age);
        
        //诊断名称
        if (StringUtils.isNotBlank(regOpt.getDiagnosisCode()))
        {
            String diagCode = "";
            String[] diags = regOpt.getDiagnosisCode().split(",");
            for (String s : diags)
            {
                BasDiagnosedef diagnosedef = basDiagnosedefDao.searchDiagnosedefById(s);
                if (null != diagnosedef)
                {
                    String code = diagnosedef.getCode();
                    if ("".equals(diagCode))
                    {
                        diagCode = null == code ? "" : code;
                    }
                    else
                    {
                        diagCode = diagCode + "," + code;
                    }
                }
            }
            hisRegOpt.setDiagCode(diagCode);
        }
        hisRegOpt.setDiagName(regOpt.getDiagnosisName());
        
        //手术名称
        if (StringUtils.isNotBlank(regOpt.getDesignedOptCode()))
        {
            String operCode = "";
            String[] designedOptCodes = regOpt.getDesignedOptCode().split(",");
            for (String s : designedOptCodes)
            {
                BasOperdef operDef = basOperdefDao.queryOperdefById(s);
                if (null != operDef)
                {
                    String code = operDef.getCode();
                    if ("".equals(operCode))
                    {
                        operCode = null == code ? "" : code;
                    }
                    else
                    {
                        operCode = operCode + "," + code;
                    }
                }
            }
            hisRegOpt.setOperCode(operCode);
        }
        hisRegOpt.setOperName(regOpt.getDesignedOptName());
        
        
        
        //拟实施麻醉方法 将原id转成his对应的code
        String anaesMethodCode = "";
        if (StringUtils.isNotBlank(regOpt.getDesignedAnaesMethodCode()))
        {
            String[] anaesMethodCodeList = regOpt.getDesignedAnaesMethodCode().split(",");
            for (int i = 0; i < anaesMethodCodeList.length; i++)
            {
                BasAnaesMethod def = basAnaesMethodDao.searchAnaesMethodById(anaesMethodCodeList[i]);
                if (null != def)
                {
                    String code = def.getCode();
                    if ("".equals(anaesMethodCode))
                    {
                        anaesMethodCode = null == code ? "" : code;
                    }
                    else
                    {
                        anaesMethodCode = anaesMethodCode + "," + code;
                    }
                }
            }
            hisRegOpt.setAnaesID(anaesMethodCode);
        }
        hisRegOpt.setAnaesName(regOpt.getDesignedAnaesMethodName());
        
        //手术开始时间
        //hisRegOpt.setOperstarttime(regOpt.getStartTime());
        
        //获取主刀医生code 将原id转成his对应的code
        String operationPeopleCode = regOpt.getOperatorId(); 
        if(StringUtils.isNotBlank(operationPeopleCode)){
            BasOperationPeople ope = basOperationPeopleDao.queryOperationPeopleById(operationPeopleCode);
            if(null!=ope){
                hisRegOpt.setSurgeryDoctorId(ope.getCode());
            }
        }
        hisRegOpt.setSurgeryDoctorName(regOpt.getOperatorName());
        
        //助手医生处理
        String assistantId = "";
        if (StringUtils.isNotBlank(regOpt.getAssistantId()))
        {
            String[] assistantsList = regOpt.getAssistantId().split(",");
            for (String id : assistantsList)
            {
                BasOperationPeople ope = basOperationPeopleDao.queryOperationPeopleById(id);
                if (null != ope)
                {
                    String code = ope.getCode();
                    if ("".equals(assistantId))
                    {
                        assistantId = null == code ? "" : code;
                    }
                    else
                    {
                        assistantId = assistantId + "," + code;
                    }
                }
            }
            hisRegOpt.setAssistantId(assistantId);
        }
        hisRegOpt.setAssistantName(regOpt.getAssistantName());
        
        logger.info("正在同步"+regOpt.getName()+"的急诊患者排班信息!!!");
        
        HisDispatch dis = new HisDispatch();
        BeanHelper.copyProperties(record, dis);
        String stime = record.getStartTime();
//        if(StringUtils.isNotEmpty(record.getStartTime())){
//            Integer st = new Integer(record.getStartTime());
//            if(st.intValue()<=9){
//                stime = "0"+st;
//            }else{
//                stime = st+"";
//            }
//            stime += ":00";
//        }
        stime = regOpt.getOperaDate()+" "+stime;
        dis.setStartTime(stime);
        String respMsg = "";
        HisResponse response = null;
        try {
            EmergencyOperFormbean emg = new EmergencyOperFormbean();
            emg.setDispatch(dis);
            emg.setRegopt(hisRegOpt);
            
            String asXml = getObjectToXml(emg);
            logger.info("sendEmergencyOperation请求参数为=========="+asXml);
            respMsg = getAIMSServiceSoap().createUrgentOperation(asXml);
            
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {   
                     if(!"0".equals(response.getResultCode())){
                         throw new RuntimeException(response.getResultMessage()); 
                     }
                }else{
                    logger.info("sendEmergencyOperation时his端无响应");
                }
            }
            
            logger.info("sendEmergencyOperation响应参数为=========="+respMsg);
        } catch (Exception e) {
            logger.info("同步"+regOpt.getName()+"的急诊信息异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("完成同步"+regOpt.getName()+"的急诊信息!!!");
        return response;    
    }
	
	
	
	/**
     *手术排班信息回传HIS
     */
    public String sendDispatchInfo(BasDispatch record,BasRegOpt regOpt)
    {
        logger.info("正在同步"+regOpt.getName()+"的排班信息!!!");
        
        if (StringUtils.isBlank(regOpt.getPreengagementnumber()))
        {
            return null;
        }
        
        HisDispatchFormbean dis = new HisDispatchFormbean();
        BeanHelper.copyProperties(record, dis);
        String beid = getBeid();
        dis.setAnesthetistId(basUserDao.selectHisIdByUserName(record.getAnesthetistId(), beid));
        dis.setCircunurseId1(basUserDao.selectHisIdByUserName(record.getCircunurseId1(), beid));
        dis.setCircunurseId2(basUserDao.selectHisIdByUserName(record.getCircunurseId2(), beid));
        dis.setInstrnurseId1(basUserDao.selectHisIdByUserName(record.getInstrnurseId1(), beid));
        dis.setInstrnurseId2(basUserDao.selectHisIdByUserName(record.getInstrnurseId2(), beid));
        dis.setReservenumber(regOpt.getPreengagementnumber());
        String stime = "";
        if(StringUtils.isNotEmpty(record.getStartTime())){
            Integer st = new Integer(record.getStartTime());
            if(st.intValue()<=9){
                stime = "0"+st;
            }else{
                stime = st+"";
            }
            stime += ":00";
        }
        stime = regOpt.getOperaDate()+" "+stime;
        dis.setStartTime(stime);
        String respMsg = "";
        try {
            String asXml = getObjectToXml(dis);
            logger.info("sendDispatchInfo请求参数为=========="+asXml);

            respMsg = getAIMSServiceSoap().updateOperationStage(asXml);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                HisResponse response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {
                     if(!"0".equals(response.getResultCode())){
                         throw new RuntimeException(response.getResultMessage()); 
                     }
                }else{
                    logger.info("sendDispatchInfo时his端无响应");
                }
            }
            
            logger.info("sendDispatchInfo响应参数为=========="+respMsg);
        } catch (Exception e) {
            logger.info("同步"+regOpt.getName()+"的排班异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("完成同步"+regOpt.getName()+"的排班信息!!!");
        return respMsg; 
    }
    
    /**
     *手术费用同步HIS
     */
    @Transactional
    public void sendOptCost(String regOptId, ResponseValue resp)
    {
        logger.info("sendOptCost==手术费用正在同步!!!");
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        String beid = getBeid();
        String anaesCode = "";
        //String nurseCode = "";
        String operatorCode = basUserDao.selectHisIdByUserName(getUserName(), beid);
        DispatchFormbean dispatchFormbean = basDispatchDao.getDispatchOperByRegOptId(regOptId, beid);
        if (null != dispatchFormbean)
        {
            anaesCode = basUserDao.selectHisIdByUserName(dispatchFormbean.getAnesthetistId(), beid);
            //nurseCode = basUserDao.selectHisIdByUserName(dispatchFormbean.getCircunurseId1(), beid);
        }
        
        HisOptChargeRequest req = new HisOptChargeRequest();
        req.setHid(regOpt.getHid());
        req.setOperSource(regOpt.getOperSource()+"");
        req.setExecDept("6096");
        req.setRecipeDept("6096");
        req.setRecipeDoctorCode(anaesCode);
        req.setChargeOperCode(operatorCode);
        req.setUserID("F1001");
        HisChargItem hisChargItem = new HisChargItem();
        List<ChargItem> items = new ArrayList<ChargItem>();
        
        List<ChargItem> medItem = docEventBillingDao.queryUnChargMedByRegOptId(regOptId);
        if (null != medItem && medItem.size() > 0)
        {
            req.setExecDept("6052");
        }
        items.addAll(medItem);
        
        List<ChargItem> chargItem = docPackagesItemDao.queryUnChargItemByRegOptId(regOptId);
        items.addAll(chargItem);
        hisChargItem.setItem(items);
        req.setHisChargItem(hisChargItem);
        String respMsg = "";
        HisOptChargResponse response = new HisOptChargResponse();
        try
        {
            String asXml = getObjectToXml(req);
            logger.info("sendOptCost请求参数为=========="+asXml);
            
            respMsg = getAIMSServiceSoap().charge(asXml);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisOptChargResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                response = (HisOptChargResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {
                     if(!"0".equals(response.getCode())){
                         throw new RuntimeException(response.getMessage()); 
                     }
                }else{
                    logger.info("sendOptCost时his端无响应");
                }
            }
            logger.info("sendOptCost响应参数为=========="+respMsg);
        }
        catch (Exception e)
        {
            logger.info("同步"+regOpt.getName()+"的计费信息失败:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        List<String> succList = new ArrayList<String>();
        List<String> failList = new ArrayList<String>();
        
        try
        {
            if (null != response && null != response.getHisOptChargResult())
            {
                List<ChargResult> results = response.getHisOptChargResult().getResult();
                if (null != results && results.size() > 0)
                {
                    for (ChargResult result : results)
                    {
                        if ("0".equals(result.getResultCode()))
                        {
                            succList.add(result.getItemName());
                            updateChargeState(result);
                        }
                        else
                        {
                            if ("该项目已计费！".equals(result.getResultMessage()))
                            {
                                updateChargeState(result);
                            }
                            failList.add(result.getItemName() + ":" + result.getResultMessage());
                        }
                    }
                }
            }
            resp.put("succList", StringUtils.getStringByList(succList));
            resp.put("failList", StringUtils.getStringByList(failList));
        }
        catch (Exception e)
        {
            resp.setResultCode("1000000000");
            resp.setResultMessage("术中药品费用同步异常！");
        }
        
    }
    
    private void updateChargeState(ChargResult result)
    {
        String itemId = result.getRecipeNo();
        docEventBillingDao.updateChargeState(itemId);
        docPackagesItemDao.updateChargeState(itemId);
    }
    
    /**
     *实例化HIS webService接口
     */
    private AIMSServiceSoap getAIMSServiceSoap()
    {
        AIMSService service = new AIMSService();
        AIMSServiceSoap soap = service.getAIMSServiceSoap();
        return soap;
    }
    
    //创建getObjectToXml方法（将对象转换成XML格式的文件）
    public static <T> String getObjectToXml(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            // 将对象转变为xml Object------XML
            // 指定对应的xml文件
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);// 是否格式化生成的xml串
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);// 是否省略xml头信息

            // 将对象转换为对应的XML文件
            marshaller.marshal(object, byteArrayOutputStream);
        } catch (JAXBException e) {

            e.printStackTrace();
        }
        // 转化为字符串返回
        String xmlContent = new String(byteArrayOutputStream.toByteArray(),
                "UTF-8");
        return xmlContent;
    }
    
    
    
    public void closeConnection(PreparedStatement pstmt, ResultSet rs,Connection conn) throws SQLException
    {
        if (null != pstmt)
        {
            pstmt.close();
        }
        
        if (null != rs)
        {
            rs.close();
        }
        //关闭conn
        ConnectionManager.closeConnection(conn);
    }
    
    
    /** 
     * 将修改的手术信息同步到his
     * <功能详细描述>
     * @param regOpt
     * @see [类、类#方法、类#成员]
     */
    public void updateRegOptToHis(BasRegOpt regOpt)
    {
        logger.info("---------------------begin updateRegOptToHis------------------------");
        OperList operList = new OperList();
        
        BeanUtils.copyProperties(regOpt, operList);
        operList.setReservenumber(regOpt.getPreengagementnumber());
        //诊断
        operList.setDiagCode(idToCode(regOpt.getDiagnosisCode(), "diagnosis"));
        operList.setDiagName(regOpt.getDiagnosisName());
        
        //手术名称
        operList.setOperCode(idToCode(regOpt.getDesignedOptCode(), "operdef"));
        operList.setOperName(regOpt.getDesignedOptName());
        
        //手术医生
        operList.setSurgeryDoctorId(idToCode(regOpt.getOperatorId(), "operationPeople"));
        operList.setSurgeryDoctorName(regOpt.getOperatorName());
        
        //助手医生
        operList.setAssistantId(idToCode(regOpt.getAssistantId(), "operationPeople"));
        operList.setAssistantName(regOpt.getAssistantName());
        
        //麻醉方法
        operList.setAnaesId(idToCode(regOpt.getDesignedAnaesMethodCode(), "anaesMethod"));
        operList.setAnaesName(regOpt.getDesignedAnaesMethodName());
        
        //麻醉类型
        operList.setAnaesType(String.valueOf(regOpt.getIsLocalAnaes()));
        operList.setCreateUser(regOpt.getCreateUser());
        
        operList.setOperType(regOpt.getEmergency());
        operList.setCredNumber(regOpt.getIdentityNo());
        operList.setOperDate(regOpt.getOperaDate());
       
        DocAnaesRecord anaesRecord = docAnaesRecordDao.searchAnaesRecordByRegOptId(regOpt.getRegOptId());
        if (null != anaesRecord)
        {
            String startTime =anaesRecord.getOperStartTime();
            String endTime = anaesRecord.getOperEndTime();
            if (StringUtils.isNotBlank(regOpt.getStartTime()))
            {
                startTime = regOpt.getStartTime();
            }
            if (StringUtils.isNotBlank(regOpt.getEndTime()))
            {
                endTime = regOpt.getEndTime();
            }
            
            operList.setOperStartTime(StringUtils.isNotBlank(startTime) ? DateUtils.strToStr(startTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm") : null);
            operList.setOperEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.strToStr(endTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm") : null);
        }
        
        operList.setDragAllergy(regOpt.getHyperSusceptiBility());
        String optLevel = regOpt.getOptLevel();
        if (StringUtils.isNotBlank(optLevel))
        {
            String operLevel = null;
            if ("一级".equals(optLevel))
            {
                operLevel = "1";
            }
            else if ("二级".equals(optLevel))
            {
                operLevel = "2";
            }
            else if ("三级".equals(optLevel))
            {
                operLevel = "3";
            }
            else if ("四级".equals(optLevel))
            {
                operLevel = "4";
            }
            operList.setOperLevel(operLevel);
        }
        
        //切口等级
        if(null != regOpt.getCutLevel()){
            String incisionlevel = "";
            if("1".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅰ";
            }
            if("2".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅱ";
            }
            if("3".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅲ";
            }
            if("4".equals(regOpt.getCutLevel()+"")){
                incisionlevel = "Ⅳ";
            }
            operList.setIncisionLevel(incisionlevel);
        }
        operList.setOperSource(regOpt.getOperSource());
        
        String respMsg = "";
        try {
            String asXml = getObjectToXml(operList);
            logger.info("updateRegOptToHis请求参数为=========="+asXml);

            respMsg = getAIMSServiceSoap().updateOperationApply(asXml);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                HisResponse response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {
                     if(!"0".equals(response.getResultCode())){
                         throw new RuntimeException(response.getResultMessage()); 
                     }
                }else{
                    logger.info("updateRegOptToHis时his端无响应");
                }
            }
            
            logger.info("updateRegOptToHis响应参数为=========="+respMsg);
        } catch (Exception e) {
            logger.info("同步"+regOpt.getName()+"的修改信息异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("---------------------end updateRegOptToHis------------------------");
    }
    
    private String idToCode(String ids, String type)
    {
        String codes = "";
        if (StringUtils.isNotBlank(ids))
        {
            String[] idAry = ids.split(",");
            if (type.equals("diagnosis"))
            {
                for (String diagDefId : idAry)
                {
                    BasDiagnosedef diagnosedef = basDiagnosedefDao.searchDiagnosedefById(diagDefId);
                    if (null != diagnosedef && null != diagnosedef.getCode())
                    {
                        codes = codes + diagnosedef.getCode() + ",";
                    }
                }
            }
            if (type.equals("operdef"))
            {
                for (String operId : idAry)
                {
                    BasOperdef operdef = basOperdefDao.queryOperdefById(operId);
                    if (null != operdef && null != operdef.getCode())
                    {
                        codes = codes + operdef.getCode() + ",";
                    }
                }
            }
            if (type.equals("operationPeople"))
            {
                for (String operatorId : idAry)
                {
                    BasOperationPeople operator = basOperationPeopleDao.queryOperationPeopleById(operatorId);
                    if (null != operator && null != operator.getCode())
                    {
                        codes = codes + operator.getCode() + ",";
                    }
                }
            }
            if (type.equals("anaesMethod"))
            {
                for (String anaesMethodId : idAry)
                {
                    BasAnaesMethod anaesMethod = basAnaesMethodDao.searchAnaesMethodById(anaesMethodId);
                    if (null != anaesMethod && null != anaesMethod.getCode())
                    {
                        codes = codes + anaesMethod.getCode() + ",";
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(codes))
        {
            codes = codes.substring(0, codes.length() - 1);
        }
        return codes;
    }
    
    
    
    
    /**
     * 同步HIS手术通知数据
     */
    @Transactional(readOnly =false)
    public void synHisOperList(){
        logger.info("---------------------begin synHisOperList------------------------");
        Connection conn = ConnectionManager.getNHFEHisConnection();
        
        logger.info("synHisOperList=============conn = "+conn+"===============");
        //获取基础数据表里最大的手术预约号
        Long maxPre = basRegOptDao.selectHisToRegOptNHFE();
        if(maxPre==null){
            maxPre = 0L;    
        }
        logger.info("synHisOperList=============maxPre = "+maxPre+"===============");
        
        String sql = "select * from BSSA.view_operation_request where reservenumber > "+maxPre;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            
            logger.info("synHisOperList=============pstmt =  "+pstmt+"===============");
            
            rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    logger.info("rs.getString ==== "+rs.getString(i)+" " + "\t");
                }
                String regOptId = GenerateSequenceUtil.generateSequenceNo();
                BasRegOpt regOpt = new BasRegOpt();
                regOpt.setRegOptId(regOptId);
                
                regOpt.setPreengagementnumber(rs.getString("reservenumber"));
                regOpt.setName(rs.getString("name"));
                
                String age = rs.getString("age");
                
                if(StringUtils.isNotEmpty(age) && age.indexOf("岁")!=-1){
                    regOpt.setAge(Integer.parseInt(age.substring(0, age.trim().indexOf("岁"))));
                }
                if(StringUtils.isNotEmpty(age) && age.indexOf("月")!=-1){
                    int sub = 2;
                    if(age.indexOf("月")<2){
                        sub = 1;
                    }
                    regOpt.setAgeMon(Integer.parseInt(OperBaseDataService.StringNumberFilter(age.substring(age.trim().indexOf("月") - sub, age.trim().indexOf("月")))));
                }
                if(StringUtils.isNotEmpty(age) && age.indexOf("天")!=-1){
                    int sub = 2;
                    if(age.indexOf("天")<2){
                        sub = 1;
                    }
                    regOpt.setAgeDay(Integer.parseInt(OperBaseDataService.StringNumberFilter(age.substring(age.trim().indexOf("天") - sub, age.trim().indexOf("天")))));
                }
                
                if(StringUtils.isNotEmpty(rs.getString("birthday"))){
                    regOpt.setBirthday(DateUtils.strToStr(rs.getString("birthday"), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
                }
                regOpt.setSex(rs.getString("sex"));
                regOpt.setMedicalType(rs.getString("medicaltype"));
                regOpt.setIdentityNo(rs.getString("crednumber"));
                regOpt.setHid(rs.getString("hid"));
                regOpt.setCid(rs.getString("cid"));
                if(!StringUtils.isEmpty(rs.getString("regionid"))){
                    regOpt.setRegionId(rs.getString("regionid"));
                    BasRegion region = basRegionDao.searchRegionById(regOpt.getRegionId(),getBeid());
                    if(region!=null){
                        regOpt.setRegionName(region.getName());
                    }
                }
                regOpt.setDeptName(rs.getString("deptname"));
                if(!StringUtils.isEmpty(rs.getString("deptid"))){
                    regOpt.setDeptId(rs.getString("deptid"));
                }
                regOpt.setBed(rs.getString("bed"));
                
                /**
                 * 拟实施诊断
                 * 这里现在code跟name在基础表中对应不上，需要his确认
                 */
                String diagnosisCode = "";
                String diagnosisName = rs.getString("diagname");
                if(StringUtils.isNotBlank(diagnosisName)){
                    
                    diagnosisName = OperBaseDataService.StringFilter(diagnosisName);
                    
                    List<BasDiagnosedef> diagnosisList = basDiagnosedefDao.selectByName(diagnosisName, getBeid());
                    if(null!=diagnosisList && diagnosisList.size()>0){
                        diagnosisCode = diagnosisList.get(0).getDiagDefId()+"";
                    }else{
                        BasDiagnosedef diagnosedef = new BasDiagnosedef();
                        String diagDefId = GenerateSequenceUtil.generateSequenceNo(GenerateSequenceUtil.getRoomId());
                        diagnosedef.setDiagDefId(diagDefId);
                        diagnosedef.setName(diagnosisName);
                        diagnosedef.setPinYin(PingYinUtil.getFirstSpell(diagnosisName));
                        diagnosedef.setEnable(1);
                        basDiagnosedefDao.insert(diagnosedef);
                        diagnosisCode = diagDefId;
                    }
                }
                regOpt.setDiagnosisCode(diagnosisCode);
                regOpt.setDiagnosisName(diagnosisName);
                
                //拟施手术
                String designedOptName = rs.getString("opername");
                String designedOptCode="";
                if(null != designedOptName && StringUtils.isNotBlank(designedOptName)){
                    
                    designedOptName = OperBaseDataService.StringFilter(designedOptName);
                    
                    List<BasOperdef> operdefList = basOperdefDao.selectByName(designedOptName, getBeid());
                    //这里his给到的麻醉方法是code，我们用到的是id所以在保存的时候需要做下转换
                    if(null!=operdefList && operdefList.size()>0){
                        designedOptCode = operdefList.get(0).getOperdefId()+"";
                    }else{
                        BasOperdef operdef = new BasOperdef();
                        String operdefId = GenerateSequenceUtil.generateSequenceNo(GenerateSequenceUtil.getRoomId());
                        operdef.setOperdefId(operdefId);
                        operdef.setName(designedOptName);
                        operdef.setPinYin(PingYinUtil.getFirstSpell(designedOptName));
                        operdef.setEnable(1);
                        basOperdefDao.insert(operdef);
                        designedOptCode = operdefId;
                    }
                }
                regOpt.setDesignedOptCode(designedOptCode);
                regOpt.setDesignedOptName(designedOptName);
                
                //获取主刀医生
                String operationPeopleCode = rs.getString("surgerydoctorid"); //获取主刀医生code
                if(StringUtils.isNotBlank(operationPeopleCode)){
                    List<BasOperationPeople> ope = basOperationPeopleDao.selectByCode(operationPeopleCode, getBeid());
                    if(null!=ope && ope.size()>0){
                        regOpt.setOperatorId(ope.get(0).getOperatorId()+"");
                    }
                }
                regOpt.setOperatorName(rs.getString("surgerydoctorname"));

                //助手医生处理
                regOpt.setAssistantId("");
                regOpt.setAssistantName("");
                //助手医生id是逗号分割的
                String assistants = rs.getString("assistantid");
                if(null != assistants && StringUtils.isNotBlank(assistants)){
                    String[] assistantsList = assistants.split(",");
                    if(null != assistantsList && assistantsList.length>0){
                        for (String code : assistantsList) {
                            List<BasOperationPeople> operPerList = basOperationPeopleDao.selectByCode(code, getBeid());
                            regOpt.setAssistantId( regOpt.getAssistantId()+((operPerList != null && operPerList.size()>0)?operPerList.get(0).getOperatorId()+",":""));
                            regOpt.setAssistantName(regOpt.getAssistantName()+((operPerList != null && operPerList.size()>0)?operPerList.get(0).getName()+",":""));
                        }
                    }
                }
                //助手医生code和name去掉最后一个逗号
                String assistantId = regOpt.getAssistantId();
                if(null != assistantId && StringUtils.isNotBlank(assistantId)){
                    assistantId = assistantId.substring(0, assistantId.length()-1);
                    regOpt.setAssistantId(assistantId);
                }
                String assistantName = regOpt.getAssistantName();
                if(null != assistantName && StringUtils.isNotBlank(assistantName)){
                    assistantName = assistantName.substring(0, assistantName.length()-1);
                    regOpt.setAssistantName(assistantName);
                }
                
                if(!StringUtils.isEmpty(rs.getString("weight"))){
                    regOpt.setWeight(Float.parseFloat(rs.getString("weight").trim()));
                }
                if(!StringUtils.isEmpty(rs.getString("height"))){
                    regOpt.setHeight(Float.parseFloat(rs.getString("height").trim()));
                }
                regOpt.setHbsag(rs.getString("hbsag"));
                regOpt.setHcv(rs.getString("hcv"));
                regOpt.setHiv(rs.getString("hiv"));
                regOpt.setHp(rs.getString("hp"));
                
                String operDate = DateUtils.strToStr(rs.getString("operdate"), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
                
                regOpt.setOperaDate(DateUtils.strToStr(operDate, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
                regOpt.setStartTime(DateUtils.strToStr(operDate, "yyyy-MM-dd HH:mm:ss", "HH:mm"));
//              String startTime = DateUtils.strToStr(operDate, "yyyy-MM-dd HH:mm:ss", "HH:mm:ss");
//              if(null!=startTime && !startTime.equals("")){
//                  regOpt.setStartTime(startTime.substring(0, 2));
//              }
                regOpt.setHyperSusceptiBility(rs.getString("dragallergy"));             
                regOpt.setOptLevel(rs.getString("operlevel"));              
                if(StringUtils.isNotBlank(rs.getString("incisionlevel"))){
                    Integer cutLevel = null;
                    if("Ⅰ".equals(rs.getString("incisionlevel")))
                        cutLevel = 1;
                    if("Ⅱ".equals(rs.getString("incisionlevel")))
                        cutLevel = 2;
                    if("Ⅲ".equals(rs.getString("incisionlevel")))
                        cutLevel = 3;
                    if("Ⅳ".equals(rs.getString("incisionlevel")))
                        cutLevel = 4;
                    regOpt.setCutLevel(cutLevel);
                }
                
                if(StringUtils.isNotBlank(rs.getString("opesource"))){
                    regOpt.setOperSource(new Integer(rs.getString("opesource")));
                }

                if(!StringUtils.isEmpty(rs.getString("opertype"))){
                    regOpt.setEmergency(Integer.parseInt(rs.getString("opertype").trim()));
                }
                
                //麻醉方法
                String designedAnaesMethodName = rs.getString("anaesname");
                String designedAnaesMethodCode = "";
                if(null != designedAnaesMethodName && StringUtils.isNotBlank(designedAnaesMethodName)){
                    String[] designedAnaesMethodNameList = designedAnaesMethodName.split(",");
                    if(null != designedAnaesMethodNameList && designedAnaesMethodNameList.length>0){
                        for (String name : designedAnaesMethodNameList) {
                            List<BasAnaesMethod> anaesMethodList = basAnaesMethodDao.selectByName(name, getBeid());
                            //这里his给到的麻醉方法是code，我们用到的是id所以在保存的时候需要做下转换
                            if(null!=anaesMethodList && anaesMethodList.size()>0){
                                designedAnaesMethodCode +=anaesMethodList.get(0).getAnaMedId()+",";
                            }
                        }
                    }
                }
                // 麻醉方法code去掉最后一个逗号
                if(null != designedAnaesMethodCode && StringUtils.isNotBlank(designedAnaesMethodCode)){
                    designedAnaesMethodCode = designedAnaesMethodCode.substring(0, designedAnaesMethodCode.length()-1);
                    regOpt.setDesignedAnaesMethodCode(designedAnaesMethodCode);
                }
                
                //全麻局麻控制
                if("7".equals(regOpt.getDesignedAnaesMethodCode())|| "29".equals(regOpt.getDesignedAnaesMethodCode())){
                    regOpt.setIsLocalAnaes(1);
                }else{
                    regOpt.setIsLocalAnaes(0);
                }
                
                
                regOpt.setDesignedAnaesMethodName(designedAnaesMethodName);
                regOpt.setCreateUser(rs.getString("createuser"));
                if(StringUtils.isNotBlank(rs.getString("frontoperforbidtake"))){
                    regOpt.setFrontOperForbidTake(new Integer(rs.getString("frontoperforbidtake")));
                }
                regOpt.setFrontOperSpecialCase(rs.getString("front_oper_special_case"));
                regOpt.setCreateTime(DateUtils.formatDateTime(new Date()));
                regOpt.setState(OperationState.NO_SCHEDULING);
                this.checkOperation(regOpt);
                int resultInsert = basRegOptDao.insert(regOpt);
                logger.info("insert==========="+resultInsert);
            }
            logger.info("synHisOperList=============while end===============");
        } catch (Exception e) {
            logger.info("synHisOperList Exception ====="+Exceptions.getStackTraceAsString(e));
            e.printStackTrace();
        }
        finally
        {
            try
            {
                closeConnection(pstmt, rs,conn);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        logger.info("------------------end synHisOperList-------------------------");
    }
    
    /*
     *  checkType 1B超 2 内窥镜 3 PACS影像
     */
    @Transactional
    public void synCheckDataList(String regOptId,String checkType)
    {
        logger.info("---------------------begin synCheckDataList------------------------");
        //获取基础数据表里最大的手术预约号
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        Connection conn = null;
        if("1".equals(checkType)){
            conn = ConnectionManager.getBcConnection();
        }else if("2".equals(checkType)){
            conn = ConnectionManager.getNjConnection();
        }else{
            //conn = ConnectionManager.get;
        }
        
        logger.info("synCheckDataList=="+checkType+"===========conn =  "+conn+"===============");
        
        String sql = "select * from v_report_sm where zyhm = '"+regOpt.getHid()+"'";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            logger.info("synCheckDataList=="+checkType+"===========pstmt =  "+pstmt+"===============");
            rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            
            List<DocPatCheckRecord> patRecordList = new ArrayList<DocPatCheckRecord>();
            
            while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    logger.info("rs.getString ==== "+rs.getString(i)+" " + "\t");
                }
                /**
                 * 报告状态 inspectStatus   N   1：未出报告 2：已出报告，未审核 3：已出报告，已审核
                 */
                DocPatCheckRecord rec = new DocPatCheckRecord();
                rec.setType(checkType);
                rec.setCheckId(rs.getString("checkid"));//送检ID
                if(StringUtils.isNotEmpty(rs.getString("checktime")))
                    rec.setCheckTime(DateUtils.getParseTime(rs.getString("checktime")));//送检时间
                rec.setState(rs.getString("checkstatus"));//状态
                if(StringUtils.isNotEmpty(rs.getString("reporttime")))
                    rec.setReportDate(DateUtils.getParseTime(rs.getString("reporttime")));//报告日期
                
                rec.setDep(rs.getString("deptname"));//送检科室
                rec.setExeDept(rs.getString("exedeptname"));//执行科室
                rec.setReqDoctorId(rs.getString("doctorname"));//送检医生
                rec.setReporter(rs.getString("reporter"));//报告
                rec.setCheckName(rs.getString("checkname"));//检查名称
                rec.setRegOptId(regOptId);
                rec.setAuditor(rs.getString("auditor"));//审核人
                patRecordList.add(rec);
            }
            if(patRecordList != null && patRecordList.size()>0){
                for (DocPatCheckRecord record : patRecordList) {

                    //查询一个检查是否已经存在了，如果不存在插入；否则更新状态和详情。
                    String checkId = record.getCheckId();
                    String status = record.getState();
                    
                    DocPatCheckRecord patCheckRecord = null;
                    patCheckRecord = docPatCheckRecordDao.selectRecordByCheckId(regOptId,checkId);
                    if(null != patCheckRecord)
                    {
                        //判断状态是否发生变化，变化了更新状态
                        if(!patCheckRecord.getState().equals(status))
                        {
                            patCheckRecord.setState(status);
                            docPatCheckRecordDao.updateByPrimaryKeySelective(patCheckRecord);
                        }
                        
                        List<DocPatCheckItem> patiItemList = docPatCheckItemDao.queryRecordByCheckId(patCheckRecord.getRegOptId(),patCheckRecord.getId());
                        if(null == patiItemList || patiItemList.size()==0)
                        {
                            List<DocPatCheckItem> itemList = synCheckDetailDataList(patCheckRecord.getCheckId(),patCheckRecord.getId(),checkType);
                            if(null != itemList && itemList.size()>0)
                            {
                                for(DocPatCheckItem patCheckItem : itemList)
                                {
                                    String itId = GenerateSequenceUtil.generateSequenceNo();
                                    patCheckItem.setId(itId);
                                    patCheckItem.setRegOptId(regOptId);
                                    docPatCheckItemDao.insertSelective(patCheckItem);
                                }
                            }
                        }
                    }
                    else
                    {
                        patCheckRecord = new DocPatCheckRecord();
                        String id = GenerateSequenceUtil.generateSequenceNo();
                        patCheckRecord.setId(id);
                        patCheckRecord.setType(checkType);
                        patCheckRecord.setRegOptId(record.getRegOptId());
                        patCheckRecord.setCheckId(record.getCheckId());
                        patCheckRecord.setState(record.getState());
                        patCheckRecord.setCheckTime(record.getCheckTime());
                        patCheckRecord.setReportDate(record.getReportDate());
                        patCheckRecord.setExeDept(record.getExeDept());
                        patCheckRecord.setReporter(record.getReporter());
                        patCheckRecord.setDep(record.getDep());
                        patCheckRecord.setReqDoctorId(record.getReqDoctorId());
                        patCheckRecord.setCheckName(record.getCheckName());
                        patCheckRecord.setAuditor(record.getAuditor());
                        docPatCheckRecordDao.insertSelective(patCheckRecord);
                        
                        List<DocPatCheckItem> itemList = synCheckDetailDataList(patCheckRecord.getCheckId(),patCheckRecord.getId(),checkType);
                        if(null != itemList && itemList.size()>0)
                        {
                            for(DocPatCheckItem patCheckItem : itemList)
                            {
                                String itId = GenerateSequenceUtil.generateSequenceNo();
                                patCheckItem.setId(itId);
                                patCheckItem.setRegOptId(regOptId);
                                docPatCheckItemDao.insertSelective(patCheckItem);
                            }
                        }
                    }
                }
            }   
            
            logger.info("synCheckDataList====="+checkType+"========while end===============");
        } catch (Exception e) {
            logger.info("synCheckDataList "+checkType+" Exception ====="+Exceptions.getStackTraceAsString(e));
            e.printStackTrace();
        }
        finally
        {
            try
            {
                closeConnection(pstmt, rs ,conn);
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    
    private void checkOperation(BasRegOpt regOpt)
    {
        List<String> tables = basDocumentDao.searchAllTables(getBeid());
        if (tables.contains("doc_pre_visit"))
        {
            DocPreVisit preVisit = new DocPreVisit();
            preVisit.setPreVisitId(GenerateSequenceUtil.generateSequenceNo());
            preVisit.setRegOptId(regOpt.getRegOptId());
            preVisit.setProcessState("NO_END");
            docPreVisitDao.insert(preVisit);
        }
        
        if (tables.contains("doc_pre_oper_visit"))
        {
            // 创建术前访视单
            DocPreOperVisit docPreOperVisit = new DocPreOperVisit();
            docPreOperVisit.setId(GenerateSequenceUtil.generateSequenceNo());
            docPreOperVisit.setRegOptId(regOpt.getRegOptId());
            docPreOperVisit.setProcessState("NO_END");
            docPreOperVisitDao.insert(docPreOperVisit);
        }
        
        if (tables.contains("doc_accede"))
        {
            // 创建麻醉同意书
            DocAccede accede = new DocAccede();
            accede.setAccedeId(GenerateSequenceUtil.generateSequenceNo());
            accede.setRegOptId(regOpt.getRegOptId());
            accede.setFlag("1");
            accede.setProcessState("NO_END");
            docAccedeDao.insert(accede);
        }
        
        if (tables.contains("doc_anaes_plan"))
        {
            //麻醉计划单
            DocAnaesPlan anaesPlan = new DocAnaesPlan();
            anaesPlan.setRegOptId(regOpt.getRegOptId());
            anaesPlan.setProcessState("NO_END");
            anaesPlan.setId(GenerateSequenceUtil.generateSequenceNo());
            docAnaesPlanDao.insert(anaesPlan);
        }
        
        if (tables.contains("doc_pat_out_range_agree"))
        {
            //医疗保险病人超范围用药同意书
            DocPatOutRangeAgree patOutRangeAgree = new DocPatOutRangeAgree();
            patOutRangeAgree.setRegOptId(regOpt.getRegOptId());
            patOutRangeAgree.setProcessState("NO_END");
            patOutRangeAgree.setId(GenerateSequenceUtil.generateSequenceNo());
            docPatOutRangeAgreeDao.insert(patOutRangeAgree);
        }
        
        if (tables.contains("doc_pre_post_visit"))
        {
            //手术病人术前术后访问记录单
            DocPrePostVisit prePostVisit = new DocPrePostVisit();
            prePostVisit.setRegOptId(regOpt.getRegOptId());
            prePostVisit.setProcessState("NO_END");
            prePostVisit.setId(GenerateSequenceUtil.generateSequenceNo());
            docPrePostVisitDao.insert(prePostVisit);
        }
        
        if (tables.contains("doc_pat_shuttle_transfer"))
        {
            //手术患者接送交接单
            DocPatShuttleTransfer patShuttleTransfer = new DocPatShuttleTransfer();
            patShuttleTransfer.setRegOptId(regOpt.getRegOptId());
            patShuttleTransfer.setProcessState("NO_END");
            patShuttleTransfer.setId(GenerateSequenceUtil.generateSequenceNo());
            docPatShuttleTransferDao.insert(patShuttleTransfer);
        }
        
        if (tables.contains("doc_opt_risk_evaluation"))
        {
            //创建手术风险评估单 
            DocOptRiskEvaluation optRiskEvaluatio = new DocOptRiskEvaluation();
            optRiskEvaluatio.setRegOptId(regOpt.getRegOptId());
            optRiskEvaluatio.setOptRiskEvaluationId(GenerateSequenceUtil.generateSequenceNo());
            optRiskEvaluatio.setProcessState("NO_END");
            optRiskEvaluatio.setFlag("1");
            docOptRiskEvaluationDao.insert(optRiskEvaluatio);
        }
        
        if (tables.contains("doc_anaes_record"))
        {
            //创建麻醉记录单
            DocAnaesRecord anaesRecord = new DocAnaesRecord();
            anaesRecord.setAnaRecordId(GenerateSequenceUtil.generateSequenceNo());
            anaesRecord.setOther("O2L/min");
            anaesRecord.setProcessState("NO_END");
            anaesRecord.setRegOptId(regOpt.getRegOptId());
            docAnaesRecordDao.insert(anaesRecord);
        }
        
        if (tables.contains("doc_anaes_summary"))
        {
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
            //并发症
            DocAnaesSummaryAllergicReaction anaesSummaryAllergicReaction = new DocAnaesSummaryAllergicReaction();
            anaesSummaryAllergicReaction.setAnaSumId(anaesSummary.getAnaSumId());
            anaesSummaryAllergicReaction.setAnaSumAllReaId(GenerateSequenceUtil.generateSequenceNo());
            docAnaesSummaryAllergicReactionDao.insert(anaesSummaryAllergicReaction);
            //中心静脉穿刺
            DocAnaesSummaryVenipuncture  anaesSummaryVenipuncture = new DocAnaesSummaryVenipuncture();
            anaesSummaryVenipuncture.setAnaSumId(anaesSummary.getAnaSumId());
            anaesSummaryVenipuncture.setAnesSumVenId(GenerateSequenceUtil.generateSequenceNo());
            docAnaesSummaryVenipunctureDao.insert(anaesSummaryVenipuncture);
        }
        
        if (tables.contains("doc_opt_care_record"))
        {
            //创建手术护理记录文书
            DocOptCareRecord optCareRecord = new DocOptCareRecord();
            optCareRecord.setRegOptId(regOpt.getRegOptId());
            optCareRecord.setId(GenerateSequenceUtil.generateSequenceNo());
            optCareRecord.setProcessState("NO_END");
            docOptCareRecordDao.insert(optCareRecord);
        }
        
        if (tables.contains("doc_opt_nurse"))
        {
            //创建手术清点记录
            DocOptNurse optNurse = new DocOptNurse();
            optNurse.setRegOptId(regOpt.getRegOptId());
            optNurse.setOptNurseId(GenerateSequenceUtil.generateSequenceNo());
            optNurse.setProcessState("NO_END");
            docOptNurseDao.insert(optNurse);
        }
        
        if (tables.contains("doc_safe_check"))
        {
            //创建手术核查单
            DocSafeCheck safeCheck = new DocSafeCheck();
            safeCheck.setRegOptId(regOpt.getRegOptId());
            safeCheck.setProcessState("NO_END");
            safeCheck.setSafCheckId(GenerateSequenceUtil.generateSequenceNo());
            docSafeCheckDao.insert(safeCheck);
            DocAnaesBeforeSafeCheck anaesBeforeSafeCheck = new DocAnaesBeforeSafeCheck();
            anaesBeforeSafeCheck.setRegOptId(regOpt.getRegOptId());
            anaesBeforeSafeCheck.setAnesBeforeId(GenerateSequenceUtil.generateSequenceNo());
            anaesBeforeSafeCheck.setProcessState("NO_END");
            docAnaesBeforeSafeCheckDao.insert(anaesBeforeSafeCheck);
            DocOperBeforeSafeCheck operBeforeSafeCheck = new DocOperBeforeSafeCheck();
            operBeforeSafeCheck.setRegOptId(regOpt.getRegOptId());
            operBeforeSafeCheck.setOperBeforeId(GenerateSequenceUtil.generateSequenceNo());
            operBeforeSafeCheck.setProcessState("NO_END");
            docOperBeforeSafeCheckDao.insert(operBeforeSafeCheck);
            DocExitOperSafeCheck exitOperSafeCheck = new DocExitOperSafeCheck();
            exitOperSafeCheck.setRegOptId(regOpt.getRegOptId());
            exitOperSafeCheck.setProcessState("NO_END");
            exitOperSafeCheck.setExitOperId(GenerateSequenceUtil.generateSequenceNo());
            docExitOperSafeCheckDao.insert(exitOperSafeCheck);
        }
        
        if (tables.contains("doc_post_follow_record"))
        {
            //术后随访记录单
            DocPostFollowRecord postFollowRecord = new DocPostFollowRecord();
            postFollowRecord.setRegOptId(regOpt.getRegOptId());
            postFollowRecord.setProcessState("NO_END");
            postFollowRecord.setPostFollowId(GenerateSequenceUtil.generateSequenceNo());
            docPostFollowRecordDao.insert(postFollowRecord);
        }
        
        if (tables.contains("doc_insured_pat_agree"))
        {
            //参保患者特殊用药、卫材知情单
            DocInsuredPatAgree insuredPatAgree = new DocInsuredPatAgree();
            insuredPatAgree.setRegOptId(regOpt.getRegOptId());
            insuredPatAgree.setProcessState("NO_END");
            insuredPatAgree.setId(GenerateSequenceUtil.generateSequenceNo());
            docInsuredPatAgreeDao.insert(insuredPatAgree);
        }
        
        if (tables.contains("doc_transfer_connect_record"))
        {
            // 手术病人转运交接记录单
            DocTransferConnectRecord transferConnectRecord = new DocTransferConnectRecord();
            transferConnectRecord.setRegOptId(regOpt.getRegOptId());
            transferConnectRecord.setProcessState("NO_END");
            transferConnectRecord.setId(GenerateSequenceUtil.generateSequenceNo());
            docTransferConnectRecordDao.insert(transferConnectRecord);
        }
        
        if (tables.contains("doc_placenta_handle_agree"))
        {
            // 胎盘处置知情同意书
            DocPlacentaHandleAgree placentaHandleAgree = new DocPlacentaHandleAgree();
            placentaHandleAgree.setRegOptId(regOpt.getRegOptId());
            placentaHandleAgree.setProcessState("NO_END");
            placentaHandleAgree.setId(GenerateSequenceUtil.generateSequenceNo());
            docPlacentaHandleAgreeDao.insert(placentaHandleAgree);
        }
        
        if (tables.contains("doc_nurse_interview_record"))
        {
            //手术室护理工作访视记录
            DocNurseInterviewRecord nurseInterviewRecord = new DocNurseInterviewRecord();
            nurseInterviewRecord.setRegOptId(regOpt.getRegOptId());
            nurseInterviewRecord.setProcessState("NO_END");
            nurseInterviewRecord.setId(GenerateSequenceUtil.generateSequenceNo());
            docNurseInterviewRecordDao.insert(nurseInterviewRecord);
        }
        
        if (tables.contains("doc_post_oper_regard"))
        {
            // 术后回视
            DocPostOperRegard docPostOperRegard = new DocPostOperRegard();
            docPostOperRegard.setRegOptId(regOpt.getRegOptId());
            docPostOperRegard.setProcessState("NO_END");
            docPostOperRegard.setId(GenerateSequenceUtil.generateSequenceNo());
            docPostOperRegardDao.insert(docPostOperRegard);
        }
        
        if (tables.contains("doc_anaes_postop"))
        {
            //麻醉后访视记录单
            DocAnaesPostop docAnaesPostop = new DocAnaesPostop();
            docAnaesPostop.setRegOptId(regOpt.getRegOptId());
            docAnaesPostop.setProcessState("NO_END");
            docAnaesPostop.setAnaPostopId(GenerateSequenceUtil.generateSequenceNo());
            docAnaesPostopDao.insert(docAnaesPostop);
        }
        
        //在审核的时候  生成排程信息记录 
        int dispatchCount = basDispatchDao.searchDistchByRegOptId(regOpt.getRegOptId());
        if(dispatchCount<1){
            BasDispatch dispatch = new BasDispatch();
            dispatch.setRegOptId(regOpt.getRegOptId());
            dispatch.setBeid(getBeid());
            basDispatchDao.insert(dispatch);
        }
    }
    
    /**
     * 检查报告详细信息
     */
    public List<DocPatCheckItem> synCheckDetailDataList(String checkId,String recordId,String checkType)
    {
        logger.info("---------------------begin synCheckDetailDataList--"+checkType+"----------------------");
        //获取基础数据表里最大的手术预约号
        Connection conn = null;
       
        
        String sql = "select * from v_report_sm_mx where checkid = '"+checkId+"'";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if("1".equals(checkType)){
                conn = ConnectionManager.getBcConnection();
            }else if("2".equals(checkType)){
                conn = ConnectionManager.getNjConnection();
            }else{
                //conn = ConnectionManager.get;
            }
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            logger.info("synCheckDetailDataList=="+checkType+"===========pstmt =  "+pstmt+"===============");
            rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            
            List<DocPatCheckItem> patCheckItemList = new ArrayList<DocPatCheckItem>();
            
            while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    logger.info("rs.getString ==== "+rs.getString(i)+" " + "\t");
                }
                DocPatCheckItem rec = new DocPatCheckItem();
                rec.setRecId(recordId);
                rec.setOption(rs.getString("option"));
                rec.setAdvice(rs.getString("advice"));
                rec.setCheckMethod(rs.getString("checkmethod"));
                rec.setCheckPart(rs.getString("checkpart"));
                rec.setCheckSituation(rs.getString("checksituation"));
                patCheckItemList.add(rec);
            }
            logger.info("synCheckDetailDataList="+checkType+"============while end===============");
            return patCheckItemList;
        } catch (Exception e) {
            logger.info("synCheckDetailDataList "+checkType+" Exception ====="+Exceptions.getStackTraceAsString(e));
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                closeConnection(pstmt, rs ,conn);
            }
            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 同步HIS临时医嘱数据
     */
    @Transactional
    public void synDoctorOrderList(BasRegOpt regOpt,ResponseValue resp){
        logger.info("---------------------begin synDoctorOrderList------------------------");
        
        Connection conn = null;
        
        logger.info("synDoctorOrderList=============conn = "+conn+"===============");
        //获取基础数据
        String sql = "select * from BSSA.VIEW_OPERATION_ADVICE where zyhm = '"+regOpt.getHid()+"' order by doctortime desc ";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            //conn = ConnectionManager.getNHFEHisConnection();
            
            List<DocDocordRecord> resultList = new ArrayList<DocDocordRecord>();
            
            //pstmt = (PreparedStatement)conn.prepareStatement(sql);
            
            logger.info("synDoctorOrderList=============pstmt =  "+pstmt+"===============");
            
            //rs = pstmt.executeQuery();
            //int col = rs.getMetaData().getColumnCount();
            
           /* while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    logger.info("rs.getString ==== "+rs.getString(i)+" " + "\t");
                }
                DocDocordRecord formbean = new DocDocordRecord();
                formbean.setRegOptId(regOpt.getRegOptId());
                formbean.setZyhm(regOpt.getHid());
                formbean.setGroupId(rs.getString("groupid"));
                formbean.setGroupName(rs.getString("groupname"));
                formbean.setType(rs.getString("type"));
                formbean.setSubType(rs.getString("subtype"));
                formbean.setMedId(rs.getInt("medid"));
                formbean.setName(rs.getString("name"));
                formbean.setMethod(rs.getString("method"));
                formbean.setDoctorContent(rs.getString("doctorcontent"));
                formbean.setFreq(rs.getString("freq"));
                formbean.setNumber1(new Float(rs.getString("number1")));
                formbean.setTimes(rs.getInt("times"));
                formbean.setDosage(new Float(rs.getString("dosage")));
                formbean.setUnit(rs.getString("unit"));
                formbean.setSpeed(rs.getString("speed"));
                formbean.setDoctor(rs.getString("doctor"));
                formbean.setJlxh(rs.getString("jlxh"));
                if(StringUtils.isNotEmpty(rs.getString("doctortime"))){
                    formbean.setDoctorTime(DateUtils.getParseTime(rs.getString("doctortime")));//医嘱下发时间
                }
                if(StringUtils.isNotEmpty(rs.getString("starttime"))){
                    formbean.setStartTime(DateUtils.getParseTime(rs.getString("starttime")));
                }
                if(StringUtils.isNotEmpty(rs.getString("endtime"))){
                    formbean.setEndTime(DateUtils.getParseTime(rs.getString("endtime")));
                }
                
                List<DocDocordRecord> ls = docDocordRecordDao.queryListByHidGroupId(regOpt.getHid(), formbean.getGroupId(),formbean.getJlxh());
                
                if(null==ls || ls.size()==0){
                    formbean.setDocRecordId(GenerateSequenceUtil.generateSequenceNo(GenerateSequenceUtil.getRoomId()));
                    docDocordRecordDao.insertSelective(formbean);
                }
                
            }*/
            
            resultList = docDocordRecordDao.queryDocordRecordListByRegOptId(regOpt.getRegOptId());
            resp.put("resultList", resultList);
            
            logger.info("synDoctorOrderList=============while end===============");
        } catch (Exception e) {
            logger.info("synDoctorOrderList Exception ====="+Exceptions.getStackTraceAsString(e));
            e.printStackTrace();
        }
        finally
        {
            try
            {
                closeConnection(pstmt, rs,conn);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    
    /**
     *  同步临时医嘱执行信息
     * @param orderList
     * @return
     */
    
    @Transactional
    public String sendDoctorExcuteList(List<DoctorOrderFormbean> orderList)
    {
        logger.info("正在同步临时医嘱执行信息!!!");
        String result="";
        String respMsg = "";
        try {
            DoctorExcutFormbean record  = new DoctorExcutFormbean();
            record.setDoctorExcutList(orderList);
            String asXml = getObjectToXml(record);
            logger.info("sendDoctorExcuteList请求参数为=========="+asXml);

            //respMsg = getAIMSServiceSoap().queryPatientOrder(asXml);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                HisResponse response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {
                     if(!"0".equals(response.getResultCode())){
                         throw new RuntimeException(response.getResultMessage()); 
                     }
                     //成功后修改临时医嘱表的数据
                     if("1".equals(response.getResultCode())){
                         result = "succ";
                     }
                }else{
                    logger.info("sendDoctorExcuteList时his端无响应");
                     throw new RuntimeException("sendDoctorExcuteList时his端无响应"); 
                }
            }
            
            logger.info("sendDoctorExcuteList响应参数为=========="+respMsg);
        } catch (Exception e) {
            logger.info("同步临时医嘱执行信息异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        logger.info("完成同步临时医嘱执行信息!!!");
        return result;  
    }
    
    
    /**
     *手术取消时信息回传HIS
     */
    public String sendCancleRegOpt(String regOptId, String state)
    {
        logger.info("sendCancleRegOpt="+regOptId+"==手术取消正在同步!!!");
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        
        if (StringUtils.isBlank(regOpt.getPreengagementnumber()))
        {
            return null;
        }
        
        HisCancleOptFormBean formbean = new HisCancleOptFormBean();
        formbean.setReservenumber(regOpt.getPreengagementnumber());
        formbean.setState(state);
        String respMsg = "";
        try {
            String asXml = getObjectToXml(formbean);
            logger.info("sendCancleRegOpt请求参数为=========="+asXml);
            
//          String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
//          if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                respMsg = getAIMSServiceSoap().updateOperationState(asXml);
//          }
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                HisResponse response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {
                     if(!"0".equals(response.getResultCode())){
                         throw new RuntimeException(response.getResultMessage()); 
                     }
                }else{
                    logger.info("sendCancleRegOpt时his端无响应");
                }
            }
            
            logger.info("sendCancleRegOpt响应参数为=========="+respMsg);
        } catch (Exception e) {
            logger.info("sendCancleRegOpt==="+regOpt.getName()+"取消手术同步时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("sendCancleRegOpt==="+regOpt.getName()+"的手术取消同步完成!!!");
        return respMsg; 
    }
    
    /**
     * 检验报告
     */
    @Transactional
    public void synLisDataList(String regOptId)
    {
        logger.info("---------------------begin synLisDataList------------------------");
        
        //获取基础数据表里最大的手术预约号
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        if (null == regOpt || StringUtils.isBlank(regOpt.getHid()))
        {
            return;
        }
        
        HisReportReq req = new HisReportReq();
        HisReportResp resp = new HisReportResp();
        String respMsg = "";
        
        req.setHid(regOpt.getHid());
        req.setOperSource(null != regOpt.getOperSource() ? regOpt.getOperSource()+"" : null);
        
        try
        {
            //String asXml = "<Request><InpatientNo>" +hid+"</InpatientNo><operSource>"+operSource+"</operSource></Request>";
            
            String asXml = getObjectToXml(req);
            logger.info("synLisDataList请求参数为=========="+asXml);
            respMsg = getAIMSServiceSoap().queryPatientLisReport(asXml);
            
            logger.info("synLisDataList响应参数为=========="+respMsg);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisReportResp.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                resp = (HisReportResp)unmarshaller.unmarshal(new StringReader(respMsg));
                if(null != resp)
                {
                     if(!"0".equals(resp.getResultCode())){
                         throw new RuntimeException(resp.getResultMessage());  
                     }
                }else{
                    logger.info("synLisDataList时his端无响应");
                    throw new RuntimeException("synLisDataList时his端无响应");
                }
            }
            
        } catch (Exception e) {
            logger.info("synLisDataList===查询检验报告时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        List<DocPatInspectRecord> patRecordList = new ArrayList<DocPatInspectRecord>();
        if (null != resp && null != resp.getHisReport())
        {
            List<Report> list = resp.getHisReport().getReport();
            if (null != list && list.size() > 0)
            {
                for (Report rs : list)
                {
                    DocPatInspectRecord rec = new DocPatInspectRecord();
                    rec.setInspectId(rs.getInspectId());
                    if (StringUtils.isNotBlank(rs.getInspectTime()))
                    {
                        rec.setDate(DateUtils.getParseTime(DateUtils.strToStr(rs.getInspectTime(), "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss")));
                    }
                    rec.setState(rs.getInspectStatus());
                    if (StringUtils.isNotBlank(rs.getReportTime()))
                    {
                        rec.setReportDate(DateUtils.getParseTime(DateUtils.strToStr(rs.getReportTime(), "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss")));
                    }
                    rec.setDep(rs.getDeptName());
                    rec.setReqDoctorId(rs.getDoctorName());
                    rec.setInstruction(rs.getInspectName());
                    rec.setRegOptId(regOptId);
                    patRecordList.add(rec);
                }
            }
        }
        if (patRecordList.size() > 0)
        {
            for (DocPatInspectRecord record : patRecordList) 
            {
                //查询一个检查是否已经存在了，如果不存在插入；否则更新状态和详情。
                String inspectId = record.getInspectId();
                String status = record.getState();
                
                DocPatInspectRecord patInspectRecord = null;
                patInspectRecord = docPatInspectRecordDao.selectRecordByInspectId(regOptId,inspectId);
                if(null != patInspectRecord)
                {
                    //String remark = patInspectRecord.getState();
                    //判断状态是否发生变化，变化了更新状态
                    if(!patInspectRecord.getState().equals(status))
                    {
                        patInspectRecord.setState(status);
                        docPatInspectRecordDao.updateByPrimaryKeySelective(patInspectRecord);
                    }
                    
                    List<DocPatInspectItem> patiItemList = docPatInspectItemDao.queryRecordByInspectId(patInspectRecord.getRegOptId(),patInspectRecord.getId());
                    if(null == patiItemList || patiItemList.size()==0)
                    {
                        List<DocPatInspectItem> itemList = synLisDetailDataList(record.getInspectId(),patInspectRecord.getId());
                        if(null != itemList && itemList.size()>0)
                        {
                            for(DocPatInspectItem patInspectItem : itemList)
                            {
                                String itId = GenerateSequenceUtil.generateSequenceNo();
                                patInspectItem.setId(itId);
                                patInspectItem.setRegOptId(regOptId);
                                docPatInspectItemDao.insertSelective(patInspectItem);
                            }
                        }
                    }
                }
                else
                {
                    patInspectRecord = new DocPatInspectRecord();
                    String id = GenerateSequenceUtil.generateSequenceNo();
                    patInspectRecord.setId(id);
                    patInspectRecord.setRegOptId(record.getRegOptId());
                    patInspectRecord.setInspectId(record.getInspectId());
                    patInspectRecord.setState(record.getState());
                    patInspectRecord.setDate(record.getDate());
                    patInspectRecord.setReportDate(record.getReportDate());
                    patInspectRecord.setDep(record.getDep());
                    patInspectRecord.setReqDoctorId(record.getReqDoctorId());
                    patInspectRecord.setInstruction(record.getInstruction());//inspectName
                    docPatInspectRecordDao.insertSelective(patInspectRecord);
                    
                    List<DocPatInspectItem> itemList = synLisDetailDataList(patInspectRecord.getInspectId(),patInspectRecord.getId());
                    if(null != itemList && itemList.size()>0)
                    {
                        for(DocPatInspectItem patInspectItem : itemList)
                        {
                            String itId = GenerateSequenceUtil.generateSequenceNo();
                            patInspectItem.setId(itId);
                            patInspectItem.setRegOptId(regOptId);
                            docPatInspectItemDao.insertSelective(patInspectItem);
                        }
                    }
                }
            }
        } 
        logger.info("---------------------begin synLisDataList------------------------");
    }
    
    /**
     * 检验检查报告详细信息
     */
    public List<DocPatInspectItem> synLisDetailDataList(String inspectId,String recordId)
    {
        logger.info("---------------------begin synLisDetailDataList------------------------");
        HisReportDetailResp resp = null;
        List<DocPatInspectItem> patInspectItemList = new ArrayList<DocPatInspectItem>();
        HisReportDetailReq req = new HisReportDetailReq();
        req.setInspectId(inspectId);
        String respMsg = "";
        try
        {
            String asXml = getObjectToXml(req);
            logger.info("synLisDetailDataList请求参数为=========="+asXml);
            respMsg = getAIMSServiceSoap().queryPatientListResult(asXml);
            
            logger.info("synLisDetailDataList响应参数为=========="+respMsg);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisReportDetailResp.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                resp = (HisReportDetailResp)unmarshaller.unmarshal(new StringReader(respMsg));
                if(null != resp)
                {
                     if(!"0".equals(resp.getResultCode()))
                     {
                         throw new RuntimeException(resp.getResultMessage());  
                     }
                }
                else
                {
                    logger.info("synLisDetailDataList时his端无响应");
                    throw new RuntimeException("synLisDetailDataList时his端无响应");
                }
            }
            
            if (null != resp.getHisReportItem())
            {
                List<ReportItem> list = resp.getHisReportItem().getList();
                if (null != list && list.size() > 0)
                {
                    for (ReportItem item : list)
                    {
                        DocPatInspectItem rec = new DocPatInspectItem();
                        rec.setRecId(recordId);
                        rec.setName(item.getItemName());
                        rec.setRefVal(item.getRefRange());
                        rec.setVal(item.getResult());
                        rec.setResult(item.getAbnormal());
                        rec.setUnit(item.getUnit());
                        patInspectItemList.add(rec);
                    }
                }
                
            }
        }
        catch (Exception e)
        {
            logger.info("检验检查报告详细信息异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        return patInspectItemList;
    }
    
    
   /* private String readXMLFile()
    {
        StringBuffer sBuffer = new StringBuffer();
        //FileReader f = null;
        BufferedReader buf = null;

        String fileName = "D:\\0919.txt";
            try
            {
                buf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"GBK"));
                String s;
                while ((s = buf.readLine()) != null)
                {
                    sBuffer.append(s);
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                if (buf != null)
                {
                    try
                    {
                        buf.close(); // 关闭
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        return sBuffer.toString();
    }*/
}
