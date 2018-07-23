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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tempurl.NSmxtjk;
import org.tempurl.NSmxtjkSoap;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.po.BasAnaesMethod;
import com.digihealth.anesthesia.basedata.po.BasDiagnosedef;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasOperationPeople;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.BasRegion;
import com.digihealth.anesthesia.common.config.Global;
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
import com.digihealth.anesthesia.interfacedata.controller.OperListController;
import com.digihealth.anesthesia.interfacedata.formbean.DoctorExcutFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.DoctorOrderFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.EmergencyOperFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.HisBloodInfoFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisCancleOptFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisDispatchFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.HisOptcostFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.po.HisDispatch;
import com.digihealth.anesthesia.interfacedata.po.HisRegOpt;

/**
 * Title: OperListService.java Description: 描述
 * 
 * @author chengwang
 * @created 2015-10-10 下午5:32:33
 */
@Service
@Transactional(readOnly = true)
public class OperListService extends BaseService {

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
        hisRegOpt.setDeptName(regOpt.getDeptName());
        hisRegOpt.setBed(regOpt.getBed());
        hisRegOpt.setRegionId(null == regOpt.getRegionId() ? null : Integer.parseInt(regOpt.getRegionId()));
        hisRegOpt.setRegionName(regOpt.getRegionName());
        hisRegOpt.setMedicalType(regOpt.getMedicalType());
        String age = "";
        if(null!=regOpt.getAge()){
            age = regOpt.getAge()+"岁";
        }
        if(null!=regOpt.getAgeMon()){
            age += regOpt.getAgeMon()+"月";
        }
        if(null!=regOpt.getAgeDay()){
            age += regOpt.getAgeDay()+"天";
        }
        hisRegOpt.setAge(age);
        
        hisRegOpt.setDiagName(regOpt.getDiagnosisName());
        
        hisRegOpt.setOperName(regOpt.getDesignedOptName());
        
        //拟实施麻醉方法 将原id转成his对应的code
        String anaesMethodCode = "";
        if(StringUtils.isNotBlank(regOpt.getDesignedAnaesMethodCode())){
            String[] anaesMethodCodeList = regOpt.getDesignedAnaesMethodCode().split(",");
            for (int i = 0; i < anaesMethodCodeList.length; i++) {
                BasAnaesMethod def = basAnaesMethodDao.searchAnaesMethodById(anaesMethodCodeList[i]);
                if(null!=def){
                    anaesMethodCode += def.getCode()+",";
                }
            }
            if(StringUtils.isNotBlank(anaesMethodCode)){
                hisRegOpt.setAnaesID(anaesMethodCode.substring(0, anaesMethodCode.length()-1));
            }
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
        String assistants = regOpt.getAssistantId();
        if(null != regOpt.getAssistantId() && StringUtils.isNotBlank(regOpt.getAssistantId())){
            String[] assistantsList = regOpt.getAssistantId().split(",");
            if(null != assistantsList && assistantsList.length>0){
                for (String id : assistantsList) {
                    BasOperationPeople ope = basOperationPeopleDao.queryOperationPeopleById(id);
                    if(null!=ope){
                        assistants +=ope.getCode()+",";
                    }
                }
            }
            if(StringUtils.isNotBlank(assistants)){
                hisRegOpt.setAssistantId(assistants.substring(0, assistants.length()-1));
            }
        }
        hisRegOpt.setAssistantName(regOpt.getAssistantName());
        
        
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
        hisRegOpt.setFrontOperSpecialCase(regOpt.getFrontOperSpecialCase());
        hisRegOpt.setFrontOperForbidTake(regOpt.getFrontOperForbidTake());
        
        
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
            String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
            if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                /**
                 * 暂时缺少急诊接口
                 */
                //respMsg = getNSmxtjkSoap().opreationArrangement(asXml);
            }
            
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
        
        HisDispatchFormbean dis = new HisDispatchFormbean();
        BeanHelper.copyProperties(record, dis);
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

            respMsg = getNSmxtjkSoap().opreationArrangement(asXml);
            
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
    public String sendOptCost(HisOptcostFormBean formbean,String regOptId,String chargeType)
    {
        logger.info("sendOptCost==手术费用正在同步!!!");
        String result="";
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        
        formbean.setReservenumber(regOpt.getPreengagementnumber());
        formbean.setRegDate(DateUtils.formatDateTime(new Date()));
        String respMsg = "";
        try {
            String asXml = getObjectToXml(formbean);
            logger.info("sendOptCost手术费用请求参数为=========="+asXml);
            //药品费用信息同步HIS
            if(OperListController.CHARGE_TYPE_MEDICINE.equals(chargeType)){
                respMsg = getNSmxtjkSoap().operationInseryp(asXml);
            }else{
                //收费项目信息同步HIS
                respMsg = getNSmxtjkSoap().operationInser(asXml);   
            }
            logger.info("sendOptCost响应参数为=========="+respMsg);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                HisResponse response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                if(null != response)
                {
                     if("0".equals(response.getResultCode())){
                         result = "succ";
                     }else{
                         throw new RuntimeException(response.getResultMessage());  
                     }
                }else{
                    logger.info("sendOptCost时his端无响应");
                    throw new RuntimeException("sendOptCost时his端无响应");
                }
            }
            
        } catch (Exception e) {
            logger.info("sendOptCost==="+regOpt.getName()+"手术费用同步时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("sendOptCost==="+regOpt.getName()+"的手术费用同步完成!!!");
        
        return result;
    }
    
    /**
     *术中输血同步HIS
     */
    public String sendBloodList(HisBloodInfoFormBean formbean,String name)
    {
        logger.info("sendBloodList==术中输血信息用正在同步!!!");
        String result="";
        
        String respMsg = "";
        try {
            String asXml = getObjectToXml(formbean);
            logger.info("sendBloodList术中输血信息请求参数为=========="+asXml);
                
//          String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
//          if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                respMsg = getNSmxtjkSoap().operationBlood(asXml);   
//          }else{
//              result = "succ";
//          }
            
            logger.info("sendBloodList响应参数为=========="+respMsg);
            
            if(null != respMsg && !"".equals(respMsg))
            {
                JAXBContext context = JAXBContext.newInstance(HisResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();  
                HisResponse response = (HisResponse)unmarshaller.unmarshal(new StringReader(respMsg));
                    
                if(null != response)
                {
                     if("0".equals(response.getResultCode())){
                         result = "succ";
                         
                     }else{
                         throw new RuntimeException(response.getResultMessage());  
                     }
                }else{
                    logger.info("sendBloodList时his端无响应");
                    throw new RuntimeException("sendBloodList时his端无响应");
                }
            }
            
        } catch (Exception e) {
            logger.info("sendBloodList==="+name+",术中输血信息同步时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("sendBloodList==="+name+",术中输血信息同步完成!!!");
        
        return result;
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
        Connection conn = ConnectionManager.getNHFELisConnection();
        
        logger.info("synLisDataList=============conn =  "+conn+"===============");
        
        String sql = "select * from zhlis.v_report_sm where zyhm = '"+regOpt.getHid()+"'";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            logger.info("synLisDataList=============pstmt =  "+pstmt+"===============");
            rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            
            List<DocPatInspectRecord> patRecordList = new ArrayList<DocPatInspectRecord>();
            
            while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    logger.info("rs.getString ==== "+rs.getString(i)+" " + "\t");
                }
                /**
                 * 报告状态 inspectStatus   N   1：未出报告 2：已出报告，未审核 3：已出报告，已审核
                 */
                DocPatInspectRecord rec = new DocPatInspectRecord();
                
                rec.setInspectId(rs.getString("inspectid"));
                if(StringUtils.isNotEmpty(rs.getString("inspecttime")))
                    rec.setDate(DateUtils.getParseTime(rs.getString("inspecttime")));
                rec.setState(rs.getString("inspectstatus"));
                if(StringUtils.isNotEmpty(rs.getString("reporttime")))
                    rec.setReportDate(DateUtils.getParseTime(rs.getString("reporttime")));
                
                rec.setDep(rs.getString("deptname"));
                rec.setReqDoctorId(rs.getString("doctorname"));
                rec.setInstruction(rs.getString("inspectname"));//inspectName
                rec.setRegOptId(regOptId);
                patRecordList.add(rec);
            }
            if(patRecordList != null && patRecordList.size()>0){
                for (DocPatInspectRecord record : patRecordList) {

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
            
            logger.info("synLisDataList=============while end===============");
        } catch (Exception e) {
            logger.info("synLisDataList Exception ====="+Exceptions.getStackTraceAsString(e));
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
    
    /**
     *实例化HIS webService接口
     */
    private NSmxtjkSoap getNSmxtjkSoap()
    {
        NSmxtjk service = new NSmxtjk();
        NSmxtjkSoap soap = service.getNSmxtjkSoap();
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
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xml头信息

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
    
    /**
     * 检验检查报告详细信息
     */
    public List<DocPatInspectItem> synLisDetailDataList(String inspectId,String recordId)
    {
        logger.info("---------------------begin synLisDetailDataList------------------------");
        //获取基础数据表里最大的手术预约号
        Connection conn = ConnectionManager.getNHFELisConnection();
        String sql = "select * from zhlis.v_report_sm_mx where inspectid = '"+inspectId+"'";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            logger.info("synLisDetailDataList=============pstmt =  "+pstmt+"===============");
            rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            
            List<DocPatInspectItem> patInspectItemList = new ArrayList<DocPatInspectItem>();
            
            while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    logger.info("rs.getString ==== "+rs.getString(i)+" " + "\t");
                }
                DocPatInspectItem rec = new DocPatInspectItem();
                rec.setRecId(recordId);
                rec.setName(rs.getString("itemname"));
                rec.setRefVal(rs.getString("refrange"));
                rec.setVal(rs.getString("result"));
                if(StringUtils.isNotEmpty(rs.getString("reporttime")))
                    rec.setDate(DateUtils.getParseTime(rs.getString("reporttime")));
                
                /**
                 * 0：正常 1：偏高 2：偏低
                 */
                String abnormal = rs.getString("abnormal");
                if(StringUtils.isNotEmpty(abnormal)){
                    if(abnormal.equals("0")){
                        rec.setResult("正常");
                    }
                    if(abnormal.equals("1")){
                        rec.setResult("偏高");
                    }
                    if(abnormal.equals("2")){
                        rec.setResult("偏低");
                    }
                }
                patInspectItemList.add(rec);
            }
            logger.info("synLisDetailDataList=============while end===============");
            return patInspectItemList;
        } catch (Exception e) {
            logger.info("synLisDetailDataList Exception ====="+Exceptions.getStackTraceAsString(e));
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
                if("7".equals(regOpt.getDesignedAnaesMethodCode())|| "29".equals(regOpt.getDesignedAnaesMethodCode()) || "6".equals(regOpt.getDesignedAnaesMethodCode())){
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
    @Transactional(readOnly =false)
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
    
    @Transactional(readOnly =false)
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

            //respMsg = getNSmxtjkSoap().opreationArrangement(asXml);
            
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
    public String sendCancleRegOpt(String regOptId)
    {
        logger.info("sendCancleRegOpt="+regOptId+"==手术取消正在同步!!!");
        BasRegOpt regOpt = basRegOptDao.searchRegOptById(regOptId);
        
        HisCancleOptFormBean formbean = new HisCancleOptFormBean();
        formbean.setReservenumber(regOpt.getPreengagementnumber());
        formbean.setState("08");//取消手术
        String respMsg = "";
        try {
            String asXml = getObjectToXml(formbean);
            logger.info("sendCancleRegOpt请求参数为=========="+asXml);
            
//          String isConnectionFlag = Global.getConfig("isConnectionHis").trim();
//          if(StringUtils.isEmpty(isConnectionFlag) || "true".equals(isConnectionFlag)){
                respMsg = getNSmxtjkSoap().opreationModify(asXml);
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
}
