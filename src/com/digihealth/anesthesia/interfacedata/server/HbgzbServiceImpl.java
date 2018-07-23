package com.digihealth.anesthesia.interfacedata.server;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.po.BasAnaesMethod;
import com.digihealth.anesthesia.basedata.po.BasDiagnosedef;
import com.digihealth.anesthesia.basedata.po.BasOperationPeople;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.Controller;
import com.digihealth.anesthesia.basedata.utils.BasRegOptUtils;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.BeanHelper;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.PingYinUtil;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.digihealth.anesthesia.interfacedata.formbean.Request;
import com.digihealth.anesthesia.interfacedata.formbean.Response;
import com.digihealth.anesthesia.interfacedata.po.OperList;

@WebService
@Component
public class HbgzbServiceImpl extends BaseService implements HbgzbService {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String GETNIS_OperationBbyPatientID = "getNIS_OperationBbyPatientID";
	private static final String GETNIS_OperationEventbyPatientID = "getNIS_OperationEventbyPatientID";

	@Override
	@Transactional
	public HisResponse getHisOperateNotice(OperList operList) {
		logger.info("hbgzb----begin getHisOperateNotice------");

		HisResponse resp = new HisResponse();
		if (operList != null) {
			BasRegOpt regOpt = new BasRegOpt();
			String beid = getBeid();
			BeanHelper.copyProperties(operList, regOpt);

			// 诊断名称
			if (StringUtils.isNotEmpty(operList.getDiagCode())) {
				String diagDef = "";
				String diagName = "";
				String[] diagCode = operList.getDiagCode().split(",");
				String[] diagNames = operList.getDiagName().split(",");
				for (int i = 0; i < diagCode.length; i++) {
					List<BasDiagnosedef> diagnosedefs = basDiagnosedefDao.selectByCode(diagCode[i], beid);
					if (null != diagnosedefs && diagnosedefs.size() > 0) {
						diagDef = diagDef + diagnosedefs.get(0).getDiagDefId() + ",";
						diagName = diagName + diagnosedefs.get(0).getName() + ",";
					} else {
						BasDiagnosedef diagnosedef = new BasDiagnosedef();
						diagnosedef.setDiagDefId(GenerateSequenceUtil.generateSequenceNo());
						diagnosedef.setCode(diagCode[i]);
						diagnosedef.setName(diagNames[i]);
						diagnosedef.setEnable(1);
						diagnosedef.setPinYin(PingYinUtil.getFirstSpell(diagNames[i]));
						diagnosedef.setBeid(beid);
						basDiagnosedefDao.insert(diagnosedef);

						diagDef = diagDef + diagnosedef.getDiagDefId() + ",";
						diagName = diagName + diagNames[i] + ",";
					}
				}
				regOpt.setDiagnosisCode(StringUtils.isNotBlank(diagDef) ? diagDef.substring(0, diagDef.length() - 1) : "");
			}
			regOpt.setDiagnosisName(operList.getDiagName());

			// 手术名称
			if (StringUtils.isNotEmpty(operList.getOperCode())) {
				String operId = "";
				String operName = "";
				String[] operCodes = operList.getOperCode().split(",");
				String[] operNames = operList.getOperName().split(",");
				for (int i = 0; i < operCodes.length; i++) {
					List<BasOperdef> opers = basOperdefDao.selectByCode(operCodes[i], beid);
					if (null != opers && opers.size() > 0) {
						operId = operId + opers.get(0).getOperdefId() + ",";
						operName = operName + opers.get(0).getName() + ",";
					} else {
						BasOperdef operdef = new BasOperdef();
						operdef.setOperdefId(GenerateSequenceUtil.generateSequenceNo());
						operdef.setCode(operCodes[i]);
						operdef.setName(operNames[i]);
						operdef.setEnable(1);
						operdef.setPinYin(PingYinUtil.getFirstSpell(operNames[i]));
						operdef.setBeid(beid);
						basOperdefDao.insert(operdef);

						operId = operId + operdef.getOperdefId() + ",";
						operName = operName + operNames[i] + ",";
					}
				}
				regOpt.setDesignedOptCode(StringUtils.isNotBlank(operId) ? operId.substring(0, operId.length() - 1) : "");
			}
			regOpt.setDesignedOptName(operList.getOperName());

			// 手术医生
			if (StringUtils.isNotEmpty(operList.getSurgeryDoctorId())) {
				String operatorId = "";
				String operatorName = "";
				String[] operatorCodes = operList.getSurgeryDoctorId().split(",");
				String[] operatorNames = operList.getSurgeryDoctorName().split(",");
				for (int i = 0; i < operatorCodes.length; i++) {
					List<BasOperationPeople> operators = basOperationPeopleDao.selectByCode(operatorCodes[i], beid);
					if (null != operators && operators.size() > 0) {
						operatorId = operatorId + operators.get(0).getOperatorId() + ",";
						operatorName = operatorName + operators.get(0).getName() + ",";
					} else {
						BasOperationPeople operationPeople = new BasOperationPeople();
						operationPeople.setOperatorId(GenerateSequenceUtil.generateSequenceNo());
						operationPeople.setCode(operatorCodes[i]);
						operationPeople.setName(operatorNames[i]);
						operationPeople.setEnable(1);
						operationPeople.setPinYin(PingYinUtil.getFirstSpell(operatorNames[i]));
						operationPeople.setBeid(beid);
						basOperationPeopleDao.insert(operationPeople);

						operatorId = operatorId + operationPeople.getOperatorId() + ",";
						operatorName = operatorName + operatorNames[i] + ",";
					}
				}
				regOpt.setOperatorId(StringUtils.isNotBlank(operatorId) ? operatorId.substring(0, operatorId.length() - 1) : "");
			}
			regOpt.setOperatorName(operList.getSurgeryDoctorName());

			// 助手医生处理
			if (StringUtils.isNotEmpty(operList.getAssistantId())) {
				String operatorId = "";
				String operatorName = "";
				String[] operatorCodes = operList.getAssistantId().split(",");
				String[] operatorNames = operList.getAssistantName().split(",");
				for (int i = 0; i < operatorCodes.length; i++) {
					List<BasOperationPeople> operators = basOperationPeopleDao.selectByCode(operatorCodes[i], beid);
					if (null != operators && operators.size() > 0) {
						operatorId = operatorId + operators.get(0).getOperatorId() + ",";
						operatorName = operatorName + operators.get(0).getName() + ",";
					} else {
						BasOperationPeople operationPeople = new BasOperationPeople();
						operationPeople.setOperatorId(GenerateSequenceUtil.generateSequenceNo());
						operationPeople.setCode(operatorCodes[i]);
						operationPeople.setName(operatorNames[i]);
						operationPeople.setEnable(1);
						operationPeople.setPinYin(PingYinUtil.getFirstSpell(operatorNames[i]));
						operationPeople.setBeid(beid);
						basOperationPeopleDao.insert(operationPeople);

						operatorId = operatorId + operationPeople.getOperatorId() + ",";
						operatorName = operatorName + operatorNames[i] + ",";
					}
				}
				regOpt.setAssistantId(StringUtils.isNotBlank(operatorId) ? operatorId.substring(0, operatorId.length() - 1) : "");
			}
			regOpt.setAssistantName(operList.getAssistantName());

			// 麻醉方法
			String designedAnaesMethodName = operList.getAnaesName();
			String designedAnaesMethodCode = operList.getAnaesId();

			List<BasAnaesMethod> anaesMethods = basAnaesMethodDao.selectByCode(designedAnaesMethodCode, beid);
			if (null != anaesMethods && anaesMethods.size() > 0) {
				regOpt.setDesignedAnaesMethodCode(anaesMethods.get(0).getAnaMedId() + "");
				regOpt.setDesignedAnaesMethodName(anaesMethods.get(0).getName());
			} else { // 如果没查到，则重新判断
				BasAnaesMethod anaesMethod = new BasAnaesMethod();
				anaesMethod.setAnaMedId(GenerateSequenceUtil.generateSequenceNo());
				anaesMethod.setCode(designedAnaesMethodCode);
				anaesMethod.setName(designedAnaesMethodName);
				anaesMethod.setIsValid(1);
				anaesMethod.setPinYin(StringUtils.isNotBlank(designedAnaesMethodName) ? PingYinUtil.getFirstSpell(designedAnaesMethodName) : null);
				if(StringUtils.isNotBlank(designedAnaesMethodCode)){
					if("302".equals(designedAnaesMethodCode) || "03".equals(designedAnaesMethodCode)){ //局部浸润麻醉 or局部麻醉  为局麻，其他都有麻醉医生参与
						anaesMethod.setIsLocalAnaes(1); //1是局麻
					}else{
						anaesMethod.setIsLocalAnaes(0); //全麻
					}
				}else{
					anaesMethod.setIsLocalAnaes(0);
				}
				anaesMethod.setBeid(beid);
				basAnaesMethodDao.insert(anaesMethod);

				regOpt.setDesignedAnaesMethodCode(anaesMethod.getAnaMedId());
				regOpt.setDesignedAnaesMethodName(designedAnaesMethodName);
			}

			if (null == regOpt.getIsLocalAnaes()) {
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
				regOpt.setOptLevel(operLevel);
			}
			
			// 切口等级
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
			
			//手术来源
			Integer opeSource = operList.getOperSource();
			if(null != opeSource ){
				regOpt.setOperSource(opeSource);
			}
			
			String reservenumber = operList.getReservenumber();
			if (StringUtils.isNotBlank(reservenumber)) {
				// 判断患者是否已经存在，如果存在则更新基础数据
				BasRegOpt regPo = basRegOptDao.searchRegOptByReservenumber(reservenumber, beid);
				regOpt.setPreengagementnumber(reservenumber); //设置his预约号
				if (regPo == null) {
					String regOptId = GenerateSequenceUtil.generateSequenceNo();
					regOpt.setRegOptId(regOptId);
					regOpt.setCreateTime(DateUtils.formatDateTime(new Date()));
					basRegOptDao.insert(regOpt);
					
					Controller controller = new Controller();
					controller.setRegOptId(regOpt.getRegOptId());
					controller.setCostsettlementState("0");
					controller.setState(OperationState.NOT_REVIEWED);
					controllerDao.update(controller);
				} else {
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
				}
			}

		}
		resp.setResultCode("0");
		resp.setResultMessage("创建手术成功");
		logger.info("hbgzb----end getHisOperateNotice-----");

		return resp;
	}

	@Override
	public Response process(Request request) {
		String method = request.getMethod();
		if(method.equals(GETNIS_OperationBbyPatientID)){ // 获取指定ID列表手麻手术数据
			//住院患者指定ID列表
			//			<request>
			//			<method>GETNIS_OPERATIONBBYPATIENTID</method>
			//			<para>
			//				<patientids>
			//					<element>
			//		            		<PATIENT_ID>123456</PATIENT_ID>
			//		<VISIT_ID>2</VISIT_ID>
			//		        		</element>
			//		       		<element>
			//		            		<PATIENT_ID>1234509</PATIENT_ID>
			//		<VISIT_ID>2</VISIT_ID>
			//		        		</element>
			//				</patientids>
			//			</para>
			//		</request>
			
			
			

		}else if(method.equals(GETNIS_OperationEventbyPatientID)){ //获取指定id列表手麻事件列表 
			
		}
		return null;
	}
	
}
