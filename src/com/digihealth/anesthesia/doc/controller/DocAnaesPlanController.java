package com.digihealth.anesthesia.doc.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digihealth.anesthesia.basedata.formbean.DispatchFormbean;
import com.digihealth.anesthesia.basedata.formbean.SysCodeFormbean;
import com.digihealth.anesthesia.common.entity.ResponseValue;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.common.web.BaseController;
import com.digihealth.anesthesia.doc.formbean.AnalgesicPumpFormbean;
import com.digihealth.anesthesia.doc.po.DocAnaesPlan;
import com.digihealth.anesthesia.doc.po.DocAnaesRecord;
import com.digihealth.anesthesia.evt.formbean.SearchFormBean;
import com.digihealth.anesthesia.evt.formbean.SearchRegOptByIdFormBean;
import com.digihealth.anesthesia.evt.po.EvtShiftChange;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value = "/document")
@Api(value="DocAnaesPlanController",description="麻醉计划处理类")
public class DocAnaesPlanController extends BaseController
{
    /** 
     * 查询麻醉计划
     * <功能详细描述>
     * @param map
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/searchAnaesPlan")
    @ResponseBody
	@ApiOperation(value="查询麻醉计划",httpMethod="POST",notes="查询麻醉计划")
    public String searchAnaesPlan(@ApiParam(name="map", value ="查询参数") @RequestBody Map<String, Object> map) {
        String regOptId = null != map.get("regOptId") ? map.get("regOptId").toString() : "";
        logger.debug("------------------searchAnaesPlan begin------------------");
        ResponseValue resp = new ResponseValue();
        DocAnaesPlan anaesPlan = docAnaesPlanService.searchAnaesPlan(regOptId);
        if (null == anaesPlan) {
            resp.setResultCode("80000001");
            resp.setResultMessage("麻醉计划单不存在!");
            return resp.getJsonStr();
        }
        SearchRegOptByIdFormBean searchRegOptByIdFormBean = basRegOptService.searchApplicationById(regOptId);
        
        if (null == anaesPlan.getAnaesDoctorName()) {
            anaesPlan.setAnaesDoctorName(searchRegOptByIdFormBean.getAnesthetistName());
        }
        
        List<SysCodeFormbean> puncturePointList =  basSysCodeService.searchSysCodeByGroupId("puncture_point", null);
        List<SysCodeFormbean> laryngealMaskList =  basSysCodeService.searchSysCodeByGroupId("laryngeal_mask_model", null);
        List<SysCodeFormbean> microPumpList = basSysCodeService.searchSysCodeByGroupId("micro_pump", null);
        List<SysCodeFormbean> catherIdList = basSysCodeService.searchSysCodeByGroupId("trachea_cather_id", null);

        getMedicalList(anaesPlan); 
        
        if (null == anaesPlan.getDate())
        {
            anaesPlan.setDate(new Date());
        }
        
        if (null == anaesPlan.getProblemAndMethod())
        {
            anaesPlan.setAnaesInstrument("1,2,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
            anaesPlan.setProblemAndMethod("1.备齐抢救物品及药品\n2.严密监测生命体征，麻醉前开放通畅的输液通路，维持循环稳定\n3.术前积极纠正各项病理状态，术中维持呼吸循环稳定\n4.注意术中出血量，控制适当液体输入量，必要时输血");
        }
        
        //获取到麻醉医生名字
        if (null == anaesPlan.getAnaesDoctorId())
        {
            DispatchFormbean dispatchPeople =
                basDispatchService.getDispatchOperByRegOptId(map.get("regOptId").toString());
            if (dispatchPeople != null)
            {
                anaesPlan.setAnaesDoctorId(dispatchPeople.getAnesthetistId() != null ? dispatchPeople.getAnesthetistId() : "");
            }
        }
        
        if ("NO_END".equals(anaesPlan.getProcessState()))
        {
            DocAnaesRecord ansRecord = docAnaesRecordService.searchAnaesRecordByRegOptId(regOptId);
            String docId = ansRecord.getAnaRecordId();
            SearchFormBean searchBean = new SearchFormBean();
            searchBean.setDocId(docId);
            List<EvtShiftChange> shiftChangeList = evtShiftChangeService.searchShiftChangeList(searchBean);
            if (null != shiftChangeList && shiftChangeList.size() > 0)
            {
                anaesPlan.setAnaesDoctorId(shiftChangeList.get(shiftChangeList.size() - 1).getShiftChangePeopleId());
            }
        }
        
        resp.put("anaesPlan", anaesPlan);
        resp.put("searchRegOptByIdFormBean", searchRegOptByIdFormBean);
        resp.put("puncturePointList", puncturePointList);//穿刺点
        resp.put("laryngealMaskList", laryngealMaskList);//喉罩型号
        resp.put("microPumpList", microPumpList);//微量泵
        resp.put("catherIdList", catherIdList);//气管导管ID
        resp.setResultCode("1");
        resp.setResultMessage("麻醉计划单查询成功!");
        logger.debug("------------------searchAnaesPlan end------------------");
        return resp.getJsonStr();
    }
    
    private void getMedicalList(DocAnaesPlan anaesPlan)
    {
        anaesPlan.setLocalAnestList(StringUtils.getListByString(anaesPlan.getLocalAnest()));
        anaesPlan.setSedativesList(StringUtils.getListByString(anaesPlan.getSedatives()));
        anaesPlan.setAnalgesicsList(StringUtils.getListByString(anaesPlan.getAnalgesics()));
        anaesPlan.setMuscleRelaxantList(StringUtils.getListByString(anaesPlan.getMuscleRelaxant()));
        anaesPlan.setIntravenousAnestList(StringUtils.getListByString(anaesPlan.getIntravenousAnest()));
        anaesPlan.setInhalationAnestList(StringUtils.getListByString(anaesPlan.getInhalationAnest()));
        anaesPlan.setAidMedicationList(StringUtils.getListByString(anaesPlan.getAidMedication()));
        anaesPlan.setBeforeAnaesMedicalList(StringUtils.getListByString(anaesPlan.getBeforeAnaesMedical()));
        anaesPlan.setInfusionList(StringUtils.getListByString(anaesPlan.getInfusion()));
        
        if (StringUtils.isNotBlank(anaesPlan.getAnalgesicPumpMethod()))
        {
            List<AnalgesicPumpFormbean> analgesicPumpMethodList = new ArrayList<AnalgesicPumpFormbean>();
            JSONArray jsonArray = JSONArray.fromObject(anaesPlan.getAnalgesicPumpMethod());
            analgesicPumpMethodList = (List<AnalgesicPumpFormbean>)JSONArray.toCollection(jsonArray,AnalgesicPumpFormbean.class);
            anaesPlan.setAnalgesicPumpMethodList(analgesicPumpMethodList);
        }
    }
    
    private void getMedicalStr(DocAnaesPlan anaesPlan)
    {
        anaesPlan.setLocalAnest(StringUtils.getStringByList(anaesPlan.getLocalAnestList()));
        anaesPlan.setSedatives(StringUtils.getStringByList(anaesPlan.getSedativesList()));
        anaesPlan.setAnalgesics(StringUtils.getStringByList(anaesPlan.getAnalgesicsList()));
        anaesPlan.setMuscleRelaxant(StringUtils.getStringByList(anaesPlan.getMuscleRelaxantList()));
        anaesPlan.setIntravenousAnest(StringUtils.getStringByList(anaesPlan.getIntravenousAnestList()));
        anaesPlan.setInhalationAnest(StringUtils.getStringByList(anaesPlan.getInhalationAnestList()));
        anaesPlan.setAidMedication(StringUtils.getStringByList(anaesPlan.getAidMedicationList()));
        anaesPlan.setBeforeAnaesMedical(StringUtils.getStringByList(anaesPlan.getBeforeAnaesMedicalList()));
        anaesPlan.setInfusion(StringUtils.getStringByList(anaesPlan.getInfusionList()));
        
        if (null != anaesPlan.getAnalgesicPumpMethodList())
        {
            JSONArray listArray=JSONArray.fromObject(anaesPlan.getAnalgesicPumpMethodList());
            anaesPlan.setAnalgesicPumpMethod(listArray.toString());
        }
    }

    /** 
     * 更新保存麻醉计划
     * <功能详细描述>
     * @param anaesPlan
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/updateAnaesPlan")
    @ResponseBody
	@ApiOperation(value="更新保存麻醉计划",httpMethod="POST",notes="更新保存麻醉计划")
    public String updateAnaesPlan(@ApiParam(name="anaesPlan", value ="麻醉计划参数") @RequestBody DocAnaesPlan anaesPlan) {
        logger.debug("------------------updateAnaesPlan begin------------------");
        ResponseValue resp = new ResponseValue();
        if (null == anaesPlan) {
            resp.setResultCode("80000001");
            resp.setResultMessage("麻醉计划单不存在!");
            return resp.getJsonStr();
        }
        getMedicalStr(anaesPlan);
        
        docAnaesPlanService.updateAnaesPlan(anaesPlan);
        resp.setResultCode("1");
        resp.setResultMessage("麻醉计划单更新成功!");
        logger.debug("------------------updateAnaesPlan end------------------");
        return resp.getJsonStr();
    }
}
