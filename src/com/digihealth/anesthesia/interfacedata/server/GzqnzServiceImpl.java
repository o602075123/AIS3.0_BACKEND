package com.digihealth.anesthesia.interfacedata.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.po.BasAnaesMethod;
import com.digihealth.anesthesia.basedata.po.BasDept;
import com.digihealth.anesthesia.basedata.po.BasDiagnosedef;
import com.digihealth.anesthesia.basedata.po.BasOperationPeople;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.Controller;
import com.digihealth.anesthesia.basedata.utils.BasRegOptUtils;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.BeanHelper;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.PingYinUtil;
import com.digihealth.anesthesia.common.utils.SpringContextHolder;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.evt.formbean.SearchFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.Data;
import com.digihealth.anesthesia.interfacedata.formbean.HisCancleOptFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.formbean.StewardScoData;
import com.digihealth.anesthesia.interfacedata.formbean.StewardScoRequest;
import com.digihealth.anesthesia.interfacedata.formbean.StewardScoResponse;
import com.digihealth.anesthesia.interfacedata.po.OperList;
import com.digihealth.anesthesia.research.formbean.SearchStewardScoFormBean;
import com.digihealth.anesthesia.research.service.StatisticsService;

@WebService
@Component
public class GzqnzServiceImpl extends BaseService implements GzqnzService
{
    @Override
    @Transactional
    public String cancleRegOpt(String request)
    {
        logger.info("begin cancleRegOpt");
        String response = "";
        HisResponse resp = new HisResponse();
        try
        {
            logger.info("-------------------------cancleRegOpt Request----------------------------:" + request);
            JAXBContext context = JAXBContext.newInstance(HisCancleOptFormBean.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            HisCancleOptFormBean hisCancleOptFormBean = (HisCancleOptFormBean)unmarshaller.unmarshal(new StringReader(request));
            
            if (StringUtils.isBlank(hisCancleOptFormBean.getReservenumber()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("手术预约号不能为空");
                return getObjectToXml(resp);
            }
            
            if (StringUtils.isBlank(hisCancleOptFormBean.getState()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("手术状态不能为空");
                return getObjectToXml(resp);
            }
            
            BasRegOpt regOpt =
                basRegOptDao.searchRegOptByReservenumber(hisCancleOptFormBean.getReservenumber(), getBeid());
            if (null != regOpt)
            {
                Controller controller = new Controller();
                controller.setRegOptId(regOpt.getRegOptId());
                controller.setCostsettlementState("0");
                controller.setState(hisCancleOptFormBean.getState());
                controllerDao.update(controller);
                resp.setResultCode("0");
                resp.setResultMessage("更新手术状态成功");
            }
            else
            {
                resp.setResultCode("1");
                resp.setResultMessage("没有找到预约号为" + hisCancleOptFormBean.getReservenumber() + "的手术");
            }
            response = getObjectToXml(resp);
            logger.info("-------------------------cancleRegOpt Response----------------------------:" + response);
        }
        catch (Exception e)
        {
            logger.info("更改手术状态时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("end cancleRegOpt");
        return response;
    }
    
    /**
     * 重载方法
     * @param request
     * @return
     */
    @Override
    @Transactional
    public String getHisOperateNotice(String request)
    {
        logger.info("begin getHisOperateNotice");
        String response = "";
        HisResponse resp = new HisResponse();
        try
        {
            logger.info("-------------------------getHisOperateNotice Request----------------------------:" + request);
            JAXBContext context = JAXBContext.newInstance(OperList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            OperList operList = (OperList)unmarshaller.unmarshal(new StringReader(request));
            resp = getRegOpt(operList);
            response = getObjectToXml(resp);
            logger.info("-------------------------getHisOperateNotice Response----------------------------:" + response);
        }
        catch (Exception e)
        {
            logger.info("手术信息同步时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        
        logger.info("end getHisOperateNotice");
        
        return response;
    }

    private HisResponse getRegOpt(OperList operList)
    {
        HisResponse resp = new HisResponse();
        if (operList != null)
        {
            BasRegOpt regOpt = new BasRegOpt();
            String beid = getBeid();
            BeanHelper.copyProperties(operList, regOpt);
            
            if (StringUtils.isBlank(operList.getReservenumber()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("手术预约号不能为空");
                return resp;
            }
            
            // 判断患者是否已经存在，如果存在则更新基础数据
            BasRegOpt regPo = basRegOptDao.searchRegOptByReservenumber(operList.getReservenumber(), beid);
            
            //当前his同步手术信息流程为：his创建手术并且提交后，才会将手术信息同步到手麻系统，his提交手术后，手术信息不能再进行修改，因此，his不会调用手麻系统修改手术信息
            if (null != regPo)
            {
                resp.setResultCode("1");
                resp.setResultMessage("手术信息已存在,无需重复提交");
                return resp;
            }
            
            if (StringUtils.isBlank(operList.getHid()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("住院号不能为空");
                return resp;
            }
            
            if (StringUtils.isBlank(operList.getName()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("姓名不能为空");
                return resp;
            }
            
            if (StringUtils.isBlank(operList.getOperDate()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("手术日期不能为空");
                return resp;
            }
            
            if (StringUtils.isBlank(operList.getSurgeryDoctorId()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("主刀医生不能为空");
                return resp;
            }
            
            if (StringUtils.isBlank(operList.getDiagCode()))
            {
                resp.setResultCode("1");
                resp.setResultMessage("术前诊断不能为空");
                return resp;
            }
            
            
            // 诊断名称
            String diagDef = "";
            String diagName = "";
            String[] diagCode = null;
            if (StringUtils.isNotEmpty(operList.getDiagCode()))
            {
                diagCode = operList.getDiagCode().split(",");
                for (int i = 0; i < diagCode.length; i++)
                {
                    List<BasDiagnosedef> diagnosedefs = basDiagnosedefDao.selectByCode(diagCode[i], beid);
                    if (null != diagnosedefs && diagnosedefs.size() > 0)
                    {
                        diagDef = diagDef + diagnosedefs.get(0).getDiagDefId() + ",";
                        diagName = diagName + diagnosedefs.get(0).getName() + ",";
                    }
                }
            }
            //diagName为空说明his传过来的diagCode为空，或者是his传过来的diagCode不为空但是还未同步到手麻系统
            if ("".equals(diagName) && StringUtils.isNotBlank(operList.getDiagName())) 
            {
                String[] diagNames = operList.getDiagName().split(",");
                for (int i = 0; i < diagNames.length; i++)
                {
                    String name = diagNames[i];
                    String code = null != diagCode ? diagCode[i] : null;
                    List<BasDiagnosedef> diagnosedefs = basDiagnosedefDao.selectByCode(name, beid);
                    if (null != diagnosedefs && diagnosedefs.size() > 0)
                    {
                        diagDef = diagDef + diagnosedefs.get(0).getDiagDefId() + ",";
                        diagName = diagName + diagnosedefs.get(0).getName() + ",";
                    }
                    else
                    {
                        BasDiagnosedef diagnosedef = new BasDiagnosedef();
                        diagnosedef.setDiagDefId(GenerateSequenceUtil.generateSequenceNo());
                        diagnosedef.setCode(code);
                        diagnosedef.setName(name);
                        diagnosedef.setEnable(1);
                        diagnosedef.setPinYin(PingYinUtil.getFirstSpell(name));
                        diagnosedef.setBeid(beid);
                        basDiagnosedefDao.insert(diagnosedef);
                        
                        diagDef = diagDef + diagnosedef.getDiagDefId() + ",";
                        diagName = diagName + name + ",";
                    }
                }
            }
            diagDef = StringUtils.isNotBlank(diagDef) ? diagDef.substring(0, diagDef.length() - 1) : "";
            diagName = StringUtils.isNotBlank(diagName) ? diagName.substring(0, diagName.length() - 1) : "";
            regOpt.setDiagnosisCode(diagDef);
            regOpt.setDiagnosisName(diagName);
            
            // 手术名称
            String operId = "";
            String operName = "";
            String[] operCodes = null;
            if (StringUtils.isNotEmpty(operList.getOperCode()))
            {
                operCodes = operList.getOperCode().split(",");
                for (int i = 0; i < operCodes.length; i++)
                {
                    List<BasOperdef> opers = basOperdefDao.selectByCode(operCodes[i], beid);
                    if (null != opers && opers.size() > 0)
                    {
                        operId = operId + opers.get(0).getOperdefId() + ",";
                        operName = operName + opers.get(0).getName() + ",";
                    }
                }
            }
            if ("".equals(operName) && StringUtils.isNotBlank(operList.getOperName()))
            {
                String[] operNames = operList.getOperName().split(",");
                for (int i = 0; i < operNames.length; i++)
                {
                    String name = operNames[i];
                    String code = null != operCodes ? operCodes[i] : null;
                    List<BasOperdef> opers = basOperdefDao.selectByName(name, beid);
                    if (null != opers && opers.size() > 0)
                    {
                        operId = operId + opers.get(0).getOperdefId() + ",";
                        operName = operName + opers.get(0).getName() + ",";
                    }
                    else
                    {
                        BasOperdef operdef = new BasOperdef();
                        operdef.setOperdefId(GenerateSequenceUtil.generateSequenceNo());
                        operdef.setName(name);
                        operdef.setCode(code);
                        operdef.setEnable(1);
                        operdef.setPinYin(PingYinUtil.getFirstSpell(name));
                        operdef.setBeid(beid);
                        basOperdefDao.insert(operdef);
                        
                        operId = operId + operdef.getOperdefId() + ",";
                        operName = operName + name + ",";
                    }
                }
            }
            operId = StringUtils.isNotBlank(operId) ? operId.substring(0, operId.length() - 1) : "";
            operName = StringUtils.isNotBlank(operName) ? operName.substring(0, operName.length() - 1) : "";
                
            regOpt.setDesignedOptCode(operId);
            regOpt.setDesignedOptName(operName);
            
            // 手术医生
            String operatorId = "";
            String operatorName = "";
            String[] operatorCodes = null;
            if (StringUtils.isNotEmpty(operList.getSurgeryDoctorId()))
            {
                operatorCodes = operList.getSurgeryDoctorId().split(",");
                for (int i = 0; i < operatorCodes.length; i++)
                {
                    List<BasOperationPeople> operators =
                        basOperationPeopleDao.selectByCode(operatorCodes[i], beid);
                    if (null != operators && operators.size() > 0)
                    {
                        operatorId = operatorId + operators.get(0).getOperatorId() + ",";
                        operatorName = operatorName + operators.get(0).getName() + ",";
                    }
                }
            }
            if ("".equals(operatorName) && StringUtils.isNotBlank(operList.getSurgeryDoctorName()))
            {
                String[] operatorNames = operList.getSurgeryDoctorName().split(",");
                
                for (int i = 0; i < operatorNames.length; i++)
                {
                    String name = operatorNames[i];
                    String code = null != operatorCodes ? operatorCodes[i] : null;
                    List<BasOperationPeople> operators =
                        basOperationPeopleDao.selectByName(name, beid);
                    if (null != operators && operators.size() > 0)
                    {
                        operatorId = operatorId + operators.get(0).getOperatorId() + ",";
                        operatorName = operatorName + operators.get(0).getName() + ",";
                    }
                    else
                    {
                        BasOperationPeople operationPeople = new BasOperationPeople();
                        operationPeople.setOperatorId(GenerateSequenceUtil.generateSequenceNo());
                        operationPeople.setName(name);
                        operationPeople.setCode(code);
                        operationPeople.setEnable(1);
                        operationPeople.setPinYin(PingYinUtil.getFirstSpell(name));
                        operationPeople.setBeid(beid);
                        basOperationPeopleDao.insert(operationPeople);
                        
                        operatorId = operatorId + operationPeople.getOperatorId() + ",";
                        operatorName = operatorName + name + ",";
                    }
                }
            }
            operatorId = StringUtils.isNotBlank(operatorId) ? operatorId.substring(0, operatorId.length() - 1) : "";    
            operatorName = StringUtils.isNotBlank(operatorName) ? operatorName.substring(0, operatorName.length() - 1) : ""; 
            regOpt.setOperatorName(operatorName);
            regOpt.setOperatorId(operatorId);
            
            // 助手医生处理
            String assistantId = "";
            String assistantName = "";
            String[] assistantCodes = null;
            if (StringUtils.isNotEmpty(operList.getAssistantId()))
            {
                assistantCodes = operList.getAssistantId().split(",");
                for (int i = 0; i < assistantCodes.length; i++)
                {
                    List<BasOperationPeople> operators =
                        basOperationPeopleDao.selectByCode(assistantCodes[i], beid);
                    if (null != operators && operators.size() > 0)
                    {
                        assistantId = assistantId + operators.get(0).getOperatorId() + ",";
                        assistantName = assistantName + operators.get(0).getName() + ",";
                    }
                }
            }
            if ("".equals(assistantName) && StringUtils.isNotBlank(operList.getAssistantName()))
            {
                String[] operatorNames = operList.getAssistantName().split(",");
                for (int i = 0; i < operatorNames.length; i++)
                {
                    String name = operatorNames[i];
                    String code = null != assistantCodes ? assistantCodes[i] : null;
                    
                    List<BasOperationPeople> operators =
                        basOperationPeopleDao.selectByName(name, beid);
                    if (null != operators && operators.size() > 0)
                    {
                        assistantId = assistantId + operators.get(0).getOperatorId() + ",";
                        assistantName = assistantName + operators.get(0).getName() + ",";
                    }
                    else
                    {
                        BasOperationPeople operationPeople = new BasOperationPeople();
                        operationPeople.setOperatorId(GenerateSequenceUtil.generateSequenceNo());
                        operationPeople.setName(name);
                        operationPeople.setCode(code);
                        operationPeople.setEnable(1);
                        operationPeople.setPinYin(PingYinUtil.getFirstSpell(name));
                        operationPeople.setBeid(beid);
                        basOperationPeopleDao.insert(operationPeople);
                        
                        assistantId = assistantId + operationPeople.getOperatorId() + ",";
                        assistantName = assistantName + name + ",";
                    }
                }
            }
            assistantId = StringUtils.isNotBlank(assistantId) ? assistantId.substring(0, assistantId.length() - 1) : "";
            assistantName = StringUtils.isNotBlank(assistantName) ? assistantName.substring(0, assistantName.length() - 1) : "";
            regOpt.setAssistantId(assistantId);
            regOpt.setAssistantName(assistantName);
            
            // 麻醉方法
            String designedAnaesMethodName = "";
            String designedAnaesMethodCode = "";
            String[] anaesCodes = null;
            
            if (StringUtils.isNotBlank(operList.getAnaesId()))
            {
                anaesCodes = operList.getAnaesId().split(",");
                
                for (String anaesId : anaesCodes)
                {
                    List<BasAnaesMethod> anaesMethods = basAnaesMethodDao.selectByCode(anaesId, beid);
                    if (null != anaesMethods && anaesMethods.size() > 0)
                    {
                        designedAnaesMethodCode = designedAnaesMethodCode + anaesMethods.get(0).getAnaMedId() + ",";
                        designedAnaesMethodName = designedAnaesMethodName + anaesMethods.get(0).getName() + ",";
                    }
                }
            }
            if ("".equals(designedAnaesMethodName) && StringUtils.isNotBlank(operList.getAnaesName()))
            {
                String[] anaesNames = operList.getAnaesName().split(",");
                for (int i = 0; i < anaesNames.length; i++)
                {
                    String name = anaesNames[i];
                    String code = null != anaesCodes ? anaesCodes[i] : null;
                    List<BasAnaesMethod> anaesMethods = basAnaesMethodDao.selectByName(name, beid);
                    if (null != anaesMethods && anaesMethods.size() > 0)
                    {
                        designedAnaesMethodCode = designedAnaesMethodCode + anaesMethods.get(0).getAnaMedId() + ",";
                        designedAnaesMethodName = designedAnaesMethodName + anaesMethods.get(0).getName() + ",";
                    }
                    else
                    {
                        BasAnaesMethod anaesMethod = new BasAnaesMethod();
                        anaesMethod.setAnaMedId(GenerateSequenceUtil.generateSequenceNo());
                        anaesMethod.setCode(code);
                        anaesMethod.setName(name);
                        anaesMethod.setIsValid(1);
                        anaesMethod.setPinYin(PingYinUtil.getFirstSpell(name));
                        anaesMethod.setBeid(beid);
                        basAnaesMethodDao.insert(anaesMethod); 
                        
                        designedAnaesMethodCode = designedAnaesMethodCode + anaesMethod.getAnaMedId() + ",";
                        designedAnaesMethodName = designedAnaesMethodName + name + ",";
                    }
                }
            }
            designedAnaesMethodCode = StringUtils.isNotBlank(designedAnaesMethodCode) ? designedAnaesMethodCode.substring(0, designedAnaesMethodCode.length() - 1) : "";
            designedAnaesMethodName = StringUtils.isNotBlank(designedAnaesMethodName) ? designedAnaesMethodName.substring(0, designedAnaesMethodName.length() - 1) : "";
            regOpt.setDesignedAnaesMethodCode(designedAnaesMethodCode);
            regOpt.setDesignedAnaesMethodName(designedAnaesMethodName);
            regOpt.setDesignedAnaesMethodCodes(StringUtils.getListByString(designedAnaesMethodCode));
            
            if (StringUtils.isBlank(regOpt.getDeptName()))
            {
                BasDept dept = basDeptDao.searchDeptById(regOpt.getDeptId(), beid);
                regOpt.setDeptName(null != dept ? dept.getName() : "");
            }
            
            if (null == regOpt.getIsLocalAnaes())
            {
                BasRegOptUtils.IsLocalAnaesSet(regOpt);
            }
            regOpt.setBeid(beid);
            
            // 急诊 or 择期
            Integer operType = operList.getOperType();
            if(null != operType){
                regOpt.setEmergency(operType);
            }
            //身份证号
            String credNumber = operList.getCredNumber();
            if(StringUtils.isNotBlank(credNumber)){
                regOpt.setIdentityNo(credNumber);
            }
            // 手术日期
            String operDate = operList.getOperDate();
            if(StringUtils.isNotBlank(operDate)){
                regOpt.setOperaDate(operDate);
            }
            // 开始时间 结束时间
            String operStartTime = operList.getOperStartTime();
            String operEndTime = operList.getOperEndTime();
            if(StringUtils.isNotBlank(operStartTime)){
                regOpt.setStartTime(operStartTime);
            }
            if(StringUtils.isNotBlank(operEndTime)){
                regOpt.setEndTime(operEndTime);
            }
            //药物过敏
            String hyperSusceptiBility = operList.getDragAllergy();
            if(StringUtils.isNotBlank(hyperSusceptiBility)){
                regOpt.setHyperSusceptiBility(hyperSusceptiBility );
            }
            //手术级别
            String operLevel = operList.getOperLevel();
            if(StringUtils.isNotBlank(operLevel)){
                String optLevel = null;
                if ("1".equals(operLevel))
                {
                    optLevel = "一级";
                }
                else if ("2".equals(operLevel))
                {
                    optLevel = "二级";
                }
                else if ("3".equals(operLevel))
                {
                    optLevel = "三级";
                }
                else if ("4".equals(operLevel))
                {
                    optLevel = "四级";
                }
                regOpt.setOptLevel(optLevel);
            }
            
            //手术创建者
            String createUser = operList.getCreateUser();
            if (StringUtils.isNotBlank(createUser))
            {
                regOpt.setCreateUser(createUser);
            }
            
            // 切口等级
            /*Integer incisionLevel = operList.getIncisionLevel();
            if(incisionLevel != null){
                regOpt.setCutLevel(incisionLevel);
            }*/
            if(null != operList.getIncisionLevel()){ 
                Integer cutLevel = null;
                if("Ⅰ".equals(operList.getIncisionLevel()))
                    cutLevel = 1;
                if("Ⅱ".equals(operList.getIncisionLevel()))
                    cutLevel = 2;
                if("Ⅲ".equals(operList.getIncisionLevel()))
                    cutLevel = 3;
                if("Ⅳ".equals(operList.getIncisionLevel()))
                    cutLevel = 4;
                regOpt.setCutLevel(cutLevel);
            }
            
            //手术来源
            Integer operSource = operList.getOperSource();
            if(null != operSource ){
                regOpt.setOperSource(operSource);
            }
            
            String regOptId = GenerateSequenceUtil.generateSequenceNo();
            regOpt.setRegOptId(regOptId);
            regOpt.setCreateTime(DateUtils.formatDateTime(new Date()));
            regOpt.setPreengagementnumber(operList.getReservenumber());
            regOpt.setIsUseAntiDrug(operList.getIsUseAntiDrug());
            regOpt.setUseAntiDrugName(operList.getUseAntiDrugName());
            regOpt.setMedicineIndications(operList.getMedicineIndications());
            regOpt.setHp(operList.getHp());
            regOpt.setFrontOperForbidTake(operList.getFrontOperForbidTake());
            basRegOptDao.insert(regOpt);
            
            Controller controller = new Controller();
            controller.setRegOptId(regOpt.getRegOptId());
            controller.setCostsettlementState("0");
            
            //如果是急诊手术，则直接将手术状态变为未排班，并且创建手术文书
            if (0 != regOpt.getEmergency())
            {
                controller.setState(OperationState.NO_SCHEDULING);
                creatDocument(regOpt);
            }
            else
            {
                controller.setState(OperationState.NOT_REVIEWED);
            }
            controllerDao.update(controller);
            
            resp.setResultCode("0");
            resp.setResultMessage("创建手术成功");
                /*else
                {
                    //比对手术名称，诊断，麻醉方法有变化的时候，才改
                    String hisDiagnosisCode = regOpt.getDiagnosisCode();
                    String hisDesignedOptCode = regOpt.getDesignedOptCode(); 
                    String hisDesignedAnaesMethodCode = regOpt.getDesignedAnaesMethodCode();
                    
                    String myDiagnosisCode = regPo.getDiagnosisCode();
                    String myDesignedOptCode = regPo.getDesignedOptCode();
                    String myDesignedAnaesMethodCode = regPo.getDesignedAnaesMethodCode();
                    
                    BasRegOpt bro = new BasRegOpt();
                    bro.setRegOptId(regPo.getRegOptId());
                    boolean bool = false;
                    
                    if(!hisDiagnosisCode.equals(myDiagnosisCode)){//修改诊断名称
                        bro.setDiagnosisCode(hisDiagnosisCode);
                        bool = true;
                    }
                    if(!hisDesignedOptCode.equals(myDesignedOptCode)){
                        bro.setDesignedOptCode(hisDesignedOptCode); //修改手术名称 
                        bool = true;
                    }
                    if(!hisDesignedAnaesMethodCode.equals(myDesignedAnaesMethodCode)){
                        bro.setDesignedAnaesMethodCode(hisDesignedAnaesMethodCode); //修改麻醉方法
                        bool = true;
                    }
                    
                    if(bool){ //只有手术名称、麻醉方法、诊断被更改了，才修改
                        basRegOptDao.updateByPrimaryKeySelective(bro);
                    }
                }*/
        }
        return resp;
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

    /**
     * 麻醉复苏（Steward苏醒评分）管理例数
     * @param request
     * @return
     */
    @Override
    public String searchStewardSco(String request)
    {
        logger.info("begin searchStewardSco");
        String response = "";
        StewardScoResponse resp = new StewardScoResponse();
        
        try
        {
            JAXBContext context = JAXBContext.newInstance(StewardScoRequest.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            StewardScoRequest stewardScoRequest = (StewardScoRequest)unmarshaller.unmarshal(new StringReader(request));
            
            String startTime = stewardScoRequest.getStartTime();
            String endTime = stewardScoRequest.getEndTime();
            
            if (!valiDateTimeWithLongFormat(startTime))
            {
                resp.setResultCode("1");
                resp.setResultMessage("开始时间格式不正确");
                response = getObjectToXml(resp);
                return response;
            }
            if (!valiDateTimeWithLongFormat(endTime))
            {
                resp.setResultCode("1");
                resp.setResultMessage("结束时间格式不正确");
                response = getObjectToXml(resp);
                return response;
            }
            
            SearchFormBean searchFormBean = new SearchFormBean();
            searchFormBean.setStartTime(startTime);
            searchFormBean.setEndTime(endTime);
            StatisticsService statisticsService = SpringContextHolder.getBean("statisticsService");
            List<SearchStewardScoFormBean> tableList= statisticsService.searchStewardScoByPacu(searchFormBean);
            
            StewardScoData stewardScoData = new StewardScoData();
            List<Data> datas = new ArrayList<Data>();
            if (null != tableList && tableList.size() > 0)
            {
                for (SearchStewardScoFormBean searchStewardScoFormBean : tableList) 
                {
                    Data data = new Data();
                    data.setInPacuNum(null != searchStewardScoFormBean.getPacuTotal() ? searchStewardScoFormBean.getPacuTotal() + "" : null);
                    data.setStewardThanFour(null != searchStewardScoFormBean.getStewardTotal() ? searchStewardScoFormBean.getStewardTotal() + "" : null);
                    data.setDate(searchStewardScoFormBean.getEnterTime());
                    datas.add(data);
                }
            }
            stewardScoData.setData(datas);
            resp.setDatas(stewardScoData);
            resp.setResultCode("0");
            resp.setResultMessage("查询成功");
            response = getObjectToXml(resp);
        }
        catch (Exception e)
        {
            logger.info("更改Steward苏醒评分时出现异常:"+Exceptions.getStackTraceAsString(e));
            throw new RuntimeException(Exceptions.getStackTraceAsString(e));
        }
        return response;
    }
    
    
    public boolean valiDateTimeWithLongFormat(String timeStr) {
        String format = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(timeStr);
        if (matcher.matches()) {
            pattern = Pattern.compile("(\\d{4})-(\\d+)-(\\d+).*");
            matcher = pattern.matcher(timeStr);
            if (matcher.matches()) {
                int y = Integer.valueOf(matcher.group(1));
                int m = Integer.valueOf(matcher.group(2));
                int d = Integer.valueOf(matcher.group(3));
                if (d > 28) {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m-1, 1);
                    int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    return (lastDay >= d);
                }
            }
            return true;
        }
        return false;
    }
}
