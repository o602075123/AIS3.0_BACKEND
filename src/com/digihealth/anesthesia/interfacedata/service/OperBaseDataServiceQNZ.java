package com.digihealth.anesthesia.interfacedata.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.basedata.formbean.BaseInfoQuery;
import com.digihealth.anesthesia.basedata.po.BasAnaesMethod;
import com.digihealth.anesthesia.basedata.po.BasChargeItem;
import com.digihealth.anesthesia.basedata.po.BasDept;
import com.digihealth.anesthesia.basedata.po.BasDiagnosedef;
import com.digihealth.anesthesia.basedata.po.BasInstrument;
import com.digihealth.anesthesia.basedata.po.BasMedicine;
import com.digihealth.anesthesia.basedata.po.BasOperationPeople;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.basedata.po.BasPrice;
import com.digihealth.anesthesia.basedata.po.BasRegion;
import com.digihealth.anesthesia.basedata.utils.ImageUpload;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.ConnectionManager;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.PingYinUtil;
import com.digihealth.anesthesia.common.utils.StringUtils;

@Service
public class OperBaseDataServiceQNZ extends BaseService{
	
	
	/**
	 * 获取外部系统视图VIEW_OPERATION_NAME，并插入当前数据库
	 * 手术名称数据同步  his不提供code值 
	 */
	@Transactional
	public void synHisOperNameList(){
		logger.info("-------start synHisOperNameList-----------");
		String sql = "select * from SMXT.VIEW_OPERATION_NAME";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if(!StringUtils.isEmpty(rs.getString("name")))
                {
                    //手术名称为空，则不需插入到数据库中
                    String code = rs.getString("code");
                    String name = StringFilter(rs.getString("name")).trim(); //过滤特殊字符
                    Integer enable = rs.getInt("enable");
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    BaseInfoQuery baseQuery =new BaseInfoQuery();
                    baseQuery.setCode(code);
                    List<BasOperdef> operdefList = basOperdefDao.selectByCode(code, getBeid());
                    if (null != operdefList && operdefList.size() > 0)
                    {
                        for (BasOperdef operDefFormBean : operdefList)
                        {
                            BasOperdef operdef = basOperdefDao.queryOperdefById(operDefFormBean.getOperdefId());
                            if (checkData(operdef.getName(),name) || operdef.getEnable() != enable)
                            {
                                operdef.setName(name);
                                operdef.setEnable(enable);
                                operdef.setPinYin(pinyin);
                                basOperdefDao.update(operdef);
                            }
                        }
                    }
                    else
                    {
                        BasOperdef odef = new BasOperdef();
                        odef.setOperdefId(GenerateSequenceUtil.generateSequenceNo(GenerateSequenceUtil.getRoomId()));
                        odef.setCode(code);
                        odef.setName(name);
                        odef.setPinYin(pinyin);
                        odef.setEnable(enable);
                        odef.setBeid(getBeid());
                        basOperdefDao.insert(odef);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisOperNameList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisOperNameList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		logger.info("-------end synHisOperNameList-----------");
	}
	
	/**
	 * 获取手术医生
	 * 视图名称：VIEW_OPERATION_DOCTOR
	 */
	@Transactional
	public void synHisOperDoctorList(){
		logger.info("-------start synHisOperDoctorList-----------");
		
		String sql = "select * from SMXT.VIEW_OPERATION_DOCTOR";
		
//		String httpPath = Global.getConfig("imageUpload.operation.ip");
//		
//		String imgPath = Global.getConfig("imageUpload.operation.path");
//		
//		String filePath = "/upload/operate/";
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            System.out.println("============================");
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String code = rs.getString("code");
                    String name = rs.getString("name");
                    Integer region = rs.getInt("region");
                    Integer enable = rs.getInt("enable");
                    //byte[] dzqm = rs.getBytes("dzqm");
                    //String yggh = rs.getString("yggh");
                    
                    //手术医生名字为空，则不进行插入操作
                    if (StringUtils.isBlank(name))
                    {
                        continue;
                    }
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    
                    List<BasOperationPeople> operationPeoples = basOperationPeopleDao.selectByCode(code, getBeid());
                    if (null != operationPeoples && operationPeoples.size() > 0)
                    {
                        for (BasOperationPeople opd : operationPeoples)
                        {
                           if (checkData(opd.getName(),name.trim()) || opd.getEnable() != enable || !opd.getRegion().equals(region))
                            {
                        	   opd.setOperatorId(opd.getOperatorId());
                        	   opd.setCode(code);
                        	   opd.setName(name.trim());
                        	   opd.setEnable(enable);
                        	   opd.setRegion(region);
                        	   opd.setPinYin(pinyin);
                        	   //opd.setYggh(yggh);
                        	   
//                        	   	String fileName = opd.getOperatorId()+".png";
//                                opd.setPicPath("http://" + httpPath + filePath	+ fileName);
//                                ImageUpload.saveToImgByByte(dzqm, imgPath+filePath, fileName);
                        	   
                               basOperationPeopleDao.update(opd);
                            }
                        }
                    }
                    else
                    {
                        BasOperationPeople opd = new BasOperationPeople();
                        opd.setOperatorId(GenerateSequenceUtil.generateSequenceNo());
                        opd.setCode(code);
                        opd.setName(name.trim());
                        opd.setEnable(enable);
                        opd.setRegion(region);
                        opd.setPinYin(pinyin);
                        opd.setBeid(getBeid());
                        //opd.setYggh(yggh);
                 	   
//                 	   	String fileName = opd.getOperatorId()+".png";
//                        opd.setPicPath("http://" + httpPath + filePath	+ fileName);
//                        ImageUpload.saveToImgByByte(dzqm, imgPath+filePath, fileName);
                        
                        basOperationPeopleDao.insert(opd);
                    }
                }
            }
            System.out.println("============================");
        }
        catch (Exception e)
        {
            logger.error("synHisOperDoctorList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn,pstmt,rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisOperDoctorList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		logger.info("-------end synHisOperDoctorList-----------");
	}
	
	/**
	 * 获取麻醉方法列表
	 * 视图名称：VIEW_ANAES_METHOD
	 */
	@Transactional
	public void synHisAnaesMethodList(){
		logger.info("-------start synHisAnaesMethodList-----------");
		String sql = "select * from SMXT.VIEW_ANAES_METHOD";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
        {
		    conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String name = rs.getString("name");
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    Integer isValid = rs.getInt("isvalid");
                    String code = rs.getString("code");
                    
                    List<BasAnaesMethod> anaesMethods = basAnaesMethodDao.selectByCode(code, getBeid());
                    if (StringUtils.isBlank(name))
                    {
                        continue;
                    }
                    if (null != anaesMethods && anaesMethods.size() > 0)
                    {
                        for (BasAnaesMethod anaesMethod : anaesMethods)
                        {
                            if (checkData(anaesMethod.getCode(), code) || checkData(anaesMethod.getName(), name.trim()) || anaesMethod.getIsValid() != isValid)
                            {
                            	anaesMethod.setAnaMedId(anaesMethod.getAnaMedId());
                            	anaesMethod.setName(name.trim());
                            	anaesMethod.setIsValid(isValid);
                            	anaesMethod.setPinYin(pinyin);
                                basAnaesMethodDao.update(anaesMethod);
                            }
                        }
                    }
                    else
                    {
                        BasAnaesMethod anaesMethod = new BasAnaesMethod();
                        anaesMethod.setAnaMedId(GenerateSequenceUtil.generateSequenceNo());
                        anaesMethod.setCode(code);
                        anaesMethod.setName(name.trim());
                        anaesMethod.setIsValid(isValid);
                        anaesMethod.setPinYin(pinyin);
                        anaesMethod.setBeid(getBeid());
                        basAnaesMethodDao.insert(anaesMethod);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisAnaesMethodList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisAnaesMethodList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		logger.info("-------end synHisAnaesMethodList-----------");
	}
	
	/**
	 * 获取诊断名称列表
	 * 视图名称：VIEW_DIAG_NAME
	 */
	@Transactional
	public void synHisDiagNameList(){
		logger.info("-------start synHisDiagNameList-----------");
		
		String sql = "select * from SMXT.VIEW_DIAG_NAME";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String code = rs.getString("code");
                    String name = StringFilter(rs.getString("name"));
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    Integer enable = rs.getInt("enable");
                    
                    List<BasDiagnosedef> diagnosedefs = basDiagnosedefDao.selectByCode(code, getBeid());
                    if (StringUtils.isBlank(name))
                    {
                        continue;
                    }
                    
                    if (null != diagnosedefs && diagnosedefs.size() > 0)
                    {
                        for (BasDiagnosedef diagnosedef : diagnosedefs)
                        {
                            if (checkData(diagnosedef.getName(), name) || diagnosedef.getEnable() != enable)
                            {
                                diagnosedef.setName(name);
                                diagnosedef.setEnable(enable);
                                diagnosedef.setPinYin(pinyin);
                                basDiagnosedefDao.update(diagnosedef);
                            }
                        }
                    }
                    else
                    {
                        BasDiagnosedef diagnosedef = new BasDiagnosedef();
            			diagnosedef.setDiagDefId(GenerateSequenceUtil.generateSequenceNo(GenerateSequenceUtil.getRoomId()));
                        diagnosedef.setCode(code);
                        diagnosedef.setName(name);
                        diagnosedef.setEnable(enable);
                        diagnosedef.setPinYin(pinyin);
                        diagnosedef.setBeid(getBeid());
                        basDiagnosedefDao.insert(diagnosedef);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisDiagNameList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisDiagNameList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		logger.info("-------end synHisDiagNameList-----------");
	}
	
	/**
	 * 获取科室列表
	 * 视图名称：VIEW_DEPT_ROOM
	 */
	
	@Transactional
	public void synHisDeptRoomList(){
		logger.info("-------start synHisDeptRoomList-----------");
		String sql = "select * from SMXT.VIEW_DEPT_ROOM";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            String beid = getBeid();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String name = rs.getString("name");
                    Integer enable = rs.getInt("enable");
                    String id = rs.getString("deptId");
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    if (StringUtils.isBlank(name))
                    {
                        continue;
                    }
                    BasDept dt = basDeptDao.searchDeptById(id,beid);
                    
                    if (dt!=null && (checkData(dt.getName(),name.trim()) || dt.getEnable() != enable))
                    {
                        dt.setName(name.trim());
                        dt.setEnable(enable);
                        dt.setPinYin(pinyin);
                        basDeptDao.update(dt);
                    }
                    
                    if(null == dt){
                        BasDept dept = new BasDept();
                        dept.setDeptId(id);
                        dept.setName(name.trim());
                        dept.setEnable(enable);
                        dept.setPinYin(pinyin);
                        dept.setBeid(beid);
                        basDeptDao.insert(dept);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisDeptRoomList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisDeptRoomList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		logger.info("-------end synHisDeptRoomList-----------");
	}
	
	/**
	 * 获取病区列表
	 * 视图名称：VIEW_REGION
	 */
	@Transactional
	public void synHisRegionList(){
		logger.info("-------start synHisRegionList-----------");
		
		Connection conn = null;
		String sql = "select * from SMXT.VIEW_REGION";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            String beid = getBeid();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    //科室名字为空，则没必要插入到数据库中
                    String name = rs.getString("name");
                    String regionId = rs.getString("regionId");
                    Integer enable = rs.getInt("enable");
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    BasRegion re = basRegionDao.searchRegionById(regionId,beid);
                    if (null == name)
                    {
                        continue;
                    }
                    
                    //根据id查询科室信息，不为空则更新，为空则新增
                    if (null != re && (checkData(re.getName(),name.trim()) || re.getEnable() != enable))
                    {
                        re.setName(name.trim());
                        re.setEnable(enable);
                        re.setPinYin(pinyin);
                        basRegionDao.update(re);
                    }
                    
                    if(null == re)
                    {
                        BasRegion region = new BasRegion();
                        region.setRegionId(regionId);
                        region.setName(name.trim());
                        region.setEnable(enable);
                        region.setPinYin(pinyin);
                        region.setBeid(beid);
                        basRegionDao.insert(region);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisRegionList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisRegionList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		
		logger.info("-------end synHisRegionList-----------");
	}
	
	/**
	 * 检验项目(只需要血气检查的)
	 * 视图名称：VIEW_INSPECT_ITEM 接口暂不做
	 */
	@Transactional
	public void synHisInspectItemList(){
		logger.info("-------start synHisInspectItemList-----------");
		
		logger.info("-------end synHisInspectItemList-----------");
	}
	
	/**
	 * 获取器械列表
	 * 视图名称：VIEW_INSTRUMENT  his不提供表，待需求确认
	 */
	@Transactional
	public void synHisInstrumentList(){
		logger.info("-------start synHisInstrumentList-----------");
        Connection conn = null;
        String sql = "select * from SMXT.VIEW_INSTRUMENT";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String code = rs.getString("code");
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    String pinyin = rs.getString("pinyin");
                    Integer enable = rs.getInt("enable");
                    String beid = getBeid();
                    List<BasInstrument> instruments = basInstrumentDao.selectByCode(code, beid);
                    if (null != instruments && instruments.size() > 0)
                    {
                        for (BasInstrument instrument : instruments)
                        {
                            if (checkData(instrument.getType(), type) || instrument.getEnable() != enable || checkData(instrument.getName(), name))
                            {
                                instrument.setCode(code);
                                instrument.setEnable(enable);
                                instrument.setName(name);
                                instrument.setType(type);
                                instrument.setPinYin(StringUtils.isBlank(pinyin) ? PingYinUtil.getFirstSpell(name) : pinyin); 
                                basInstrumentDao.update(instrument);
                            }
                            
                        }
                    }
                    else
                    {
                        BasInstrument instrument = new BasInstrument();
                        instrument.setInstrumentId(GenerateSequenceUtil.generateSequenceNo());
                        instrument.setEnable(enable);
                        instrument.setName(name);
                        instrument.setType(type);
                        instrument.setCode(code);
                        instrument.setPinYin(pinyin);
                        instrument.setBeid(beid);
                        basInstrumentDao.insert(instrument);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisInstrumentList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisInstrumentList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
        
		
		logger.info("-------end synHisInstrumentList-----------");
	}
	
	/**
	 * 获取药品库信息
	 * 视图名称：VIEW_DRUG_STORE 批次没有
	 */
	@Transactional
	public void synHisDrugStoreList(){
		logger.info("-------start synHisDrugStoreList-----------");
		
		Connection conn = null;
		String sql = "select * from SMXT.VIEW_DRUG_STORE";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String type = rs.getString("type");
                    Integer enable = rs.getInt("enable");
                    String name = rs.getString("name");
                    String pinyin = PingYinUtil.getFirstSpell(name);
                    String briefName = rs.getString("briefName");
                    Float packageDosageAmout = rs.getFloat("packageDosageAmount");
                    String dosageUnit = rs.getString("dosageUnit");
                    String spec = rs.getString("spec");
                    String code = rs.getString("code");
                    List<BasMedicine> medicines = basMedicineDao.selectByCode(code, getBeid());
                    if (null != medicines && medicines.size() > 0)
                    {
                        for (BasMedicine medicine : medicines)
                        {
                            if (checkData(medicine.getBriefName(),briefName)
                                || checkData(medicine.getDosageUnit(),dosageUnit) || medicine.getEnable() != enable
                                || checkData(medicine.getName(),name)
                                || medicine.getPackageDosageAmount() != packageDosageAmout
                                || checkData(medicine.getSpec(),spec) || checkData(medicine.getType(),type))
                            {
                                medicine.setType(type);
                                medicine.setEnable(enable);
                                medicine.setPinYin(pinyin);
                                medicine.setName(name);
                                medicine.setBriefName(briefName);
                                medicine.setPackageDosageAmount(packageDosageAmout);
                                medicine.setDosageUnit(dosageUnit);
                                medicine.setSpec(spec);
                                basMedicineDao.update(medicine);
                            }
                            
                        }
                    }
                    else
                    {
                        BasMedicine medicine = new BasMedicine();
                        medicine.setMedicineId(GenerateSequenceUtil.generateSequenceNo());
                        medicine.setCode(code);
                        medicine.setType(type);
                        medicine.setEnable(enable);
                        medicine.setPinYin(pinyin);
                        medicine.setName(name);
                        medicine.setBriefName(briefName);
                        medicine.setPackageDosageAmount(packageDosageAmout);
                        medicine.setDosageUnit(dosageUnit);
                        medicine.setSpec(spec);
                        medicine.setBeid(getBeid());
                        basMedicineDao.insert(medicine);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisDrugStoreList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisDrugStoreList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		
		logger.info("-------end synHisDrugStoreList-----------");
	}
	
	/**
	 * 获取药品价格列表
	 * 视图：VIEW_MEDICINE_PRICE
	 */
	@Transactional
	public void synHisMedicinePriceList(){
		logger.info("-------start synHisMedicinePriceList-----------");
		Connection conn = null;
		String sql = "select * from SMXT.VIEW_MEDICINE_PRICE";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (null == rs)
            {
                return;
            }
            //basPriceDao.updateEnable();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String code = rs.getString("code");
                    String spec = rs.getString("spec");
                    String firm = rs.getString("firm");
                    String batch = rs.getString("batch");
                    String minPackageUnit = rs.getString("minPackageUnit");
                    Float priceMinPackage = rs.getFloat("priceMinPackage");
                    Integer enable = rs.getInt("enable");
                    List<BasPrice> prices = basPriceDao.selectByCode(code,getBeid());
                    if (null != prices && prices.size() > 0)
                    {
                        for (BasPrice price : prices)
                        {
                            if (checkData(price.getSpec(),spec) 
                            	|| checkData(price.getFirm(),firm)
                                || checkData(price.getBatch(),batch) || checkData(price.getMinPackageUnit(),minPackageUnit)
                                || price.getPriceMinPackage() != priceMinPackage
                                || price.getEnable() != enable)
                            {
                                price.setSpec(spec);
                                price.setFirm(firm);
                                price.setBatch(batch);
                                price.setMinPackageUnit(minPackageUnit);
                                price.setPriceMinPackage(priceMinPackage);
                                price.setEnable(enable);
                                basPriceDao.update(price);
                            }
                        }
                    }
                    else
                    {
                        BasPrice price = new BasPrice();
                        price.setPriceId(GenerateSequenceUtil.generateSequenceNo());
                        price.setCode(code);
                        price.setSpec(spec);
                        price.setFirm(firm);
                        price.setBatch(batch);
                        price.setMinPackageUnit(minPackageUnit);
                        price.setPriceMinPackage(priceMinPackage);
                        price.setEnable(enable);
                        price.setBeid(getBeid());
                        basPriceDao.insert(price);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisMedicinePriceList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisMedicinePriceList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
		
		logger.info("-------end synHisMedicinePriceList-----------");
	}
	
	
	/**
	 * 获取收费项目列表
	 * 视图：VIEW_CHARGE_ITEM
	 */
	@Transactional
	public void synHisChargeItemList(){
		logger.info("-------start synHisChargeItemList-----------");
		Connection conn = null;
		String sql = "select * from SMXT.VIEW_CHARGE_ITEM";
		ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = ConnectionManager.getQNZHisConnection();
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                if (!StringUtils.isEmpty(rs.getString(1)))
                {
                    String code = rs.getString("code");
                    List<BasChargeItem> chargeItems = basChargeItemDao.selectByCode(code, getBeid());
                    String spec = rs.getString("spec");

                    String chargeItemName = rs.getString("name");
                    
                    if(StringUtils.isNotBlank(chargeItemName)){
                    	chargeItemName = StringFilter(chargeItemName); //chargeItemName.replaceAll("[^a-zA-Z_\u4e00-\u9fa5]", "");
                    }
                    logger.info("----------------------------"+chargeItemName+"-----------------------------");

                    String pinyin = rs.getString("pinyin");
                    if(StringUtils.isNotBlank(pinyin)){
                    	pinyin = pinyin.replaceAll("[^a-zA-Z]", "");
                    }

                    String unit = rs.getString("unit");
                    if(StringUtils.isNotBlank(unit)){
                    	unit = unit.trim();
                    }
                    float basicUnitAmout = rs.getFloat("basicUnitAmount");
                    float price = rs.getFloat("price");
                    float basicUnitPrice = rs.getFloat("basicUnitPrice");
                    String type = rs.getString("type");
                    String chargeType = rs.getString("chargeType");
                    Integer enable = rs.getInt("enable");
                    if (null != chargeItems && chargeItems.size() > 0)
                    {
                        for (BasChargeItem chargeItem : chargeItems)
                        {
                        	if(checkData(chargeItem.getChargeItemName(), chargeItemName) || checkData(chargeItem.getSpec(),spec) ||
                        			checkData(chargeItem.getUnit(),unit) || 
                        			Math.abs(chargeItem.getBasicUnitAmount()-basicUnitAmout)> 0 ||
                        			Math.abs(chargeItem.getPrice() - price)> 0 ||
                        			Math.abs(chargeItem.getBasicUnitPrice() - basicUnitPrice) >0 ||
                        			checkData(chargeItem.getType(),type) || checkData(chargeItem.getChargeType(),chargeType) ||
                        			chargeItem.getEnable() != enable){
                                chargeItem.setChargeItemName(chargeItemName);
                                chargeItem.setSpec(spec);
                                chargeItem.setPinYin(pinyin);
                                chargeItem.setUnit(unit);
                                chargeItem.setBasicUnitAmount(basicUnitAmout);
                                chargeItem.setPrice(price);
                                chargeItem.setBasicUnitPrice(basicUnitPrice);
                                chargeItem.setType(type);
                                chargeItem.setChargeType(chargeType);
                                chargeItem.setEnable(enable);
                                basChargeItemDao.updateByPrimaryKeySelective(chargeItem);
                            }
                        }
                    }
                    else
                    {
                        BasChargeItem chargeItem = new BasChargeItem();
                        chargeItem.setChargeItemId(GenerateSequenceUtil.generateSequenceNo());
                        chargeItem.setChargeItemCode(code);
                        chargeItem.setChargeItemName(chargeItemName);
                        chargeItem.setSpec(spec);
                        chargeItem.setPinYin(pinyin);
                        chargeItem.setUnit(unit);
                        chargeItem.setBasicUnitAmount(basicUnitAmout);
                        chargeItem.setPrice(price);
                        chargeItem.setBasicUnitPrice(basicUnitPrice);
                        chargeItem.setType(type);
                        chargeItem.setChargeType(chargeType);
                        chargeItem.setEnable(enable);
                        chargeItem.setBeid(getBeid());
                        basChargeItemDao.insert(chargeItem);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("synHisChargeItemList--------------"+Exceptions.getStackTraceAsString(e));
        }
        finally
        {
            try
            {
                close(conn, pstmt, rs);
            }
            catch (SQLException e)
            {
                logger.error("synHisChargeItemList--------------"+Exceptions.getStackTraceAsString(e));
            }
        }
        
		logger.info("-------end synHisChargeItemList-----------");
	}
	
	public static String StringFilter(String str) throws PatternSyntaxException {
		if(StringUtils.isBlank(str)){
			return str;
		}
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "['?？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	
	public static String StringNumberFilter(String str) throws PatternSyntaxException {
		if(StringUtils.isBlank(str)){
			return str;
		}
		// 只允许字母和数字
		 String regEx = "[^0-9]";
		// 清除掉所有特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return  m.replaceAll("").trim();
	}
	
	
	public boolean checkData(String changeValue,String sourceValue){
		if(changeValue == null){
			changeValue = "";
		}
		if(sourceValue == null){
			sourceValue = "";
		}
		if(!changeValue.equals(sourceValue)){
			return true;
		}
		return false;
	}
	
	private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) throws SQLException
    {
        ConnectionManager.closeConnection();
        
        if (null != pstmt)
        {
            pstmt.close();
        }
        
        if (null != rs)
        {
            rs.close();
        }
    }

}
