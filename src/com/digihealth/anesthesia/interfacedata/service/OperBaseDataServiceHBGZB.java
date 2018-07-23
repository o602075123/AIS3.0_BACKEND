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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.winning.WebInterface;
import cn.com.winning.WebInterfaceSoap;

import com.digihealth.anesthesia.basedata.config.OperationState;
import com.digihealth.anesthesia.basedata.formbean.SysCodeFormbean;
import com.digihealth.anesthesia.basedata.po.BasAnaesMethod;
import com.digihealth.anesthesia.basedata.po.BasChargeItem;
import com.digihealth.anesthesia.basedata.po.BasDept;
import com.digihealth.anesthesia.basedata.po.BasDiagnosedef;
import com.digihealth.anesthesia.basedata.po.BasDispatch;
import com.digihealth.anesthesia.basedata.po.BasInstrument;
import com.digihealth.anesthesia.basedata.po.BasMedicine;
import com.digihealth.anesthesia.basedata.po.BasOperationPeople;
import com.digihealth.anesthesia.basedata.po.BasOperdef;
import com.digihealth.anesthesia.basedata.po.BasPrice;
import com.digihealth.anesthesia.basedata.po.BasRegOpt;
import com.digihealth.anesthesia.basedata.po.BasRegion;
import com.digihealth.anesthesia.basedata.po.Controller;
import com.digihealth.anesthesia.basedata.service.BasSysCodeService;
import com.digihealth.anesthesia.basedata.utils.BasRegOptUtils;
import com.digihealth.anesthesia.common.service.BaseService;
import com.digihealth.anesthesia.common.utils.BeanHelper;
import com.digihealth.anesthesia.common.utils.CacheUtils;
import com.digihealth.anesthesia.common.utils.ConnectionManager;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.GenerateSequenceUtil;
import com.digihealth.anesthesia.common.utils.PingYinUtil;
import com.digihealth.anesthesia.common.utils.StringUtils;
import com.digihealth.anesthesia.common.utils.SysUtil;
import com.digihealth.anesthesia.interfacedata.formbean.HisCancleOptFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisDispatchFormbean;
import com.digihealth.anesthesia.interfacedata.formbean.HisGetOperationFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisGetOperationResponse;
import com.digihealth.anesthesia.interfacedata.formbean.HisOperationFormBean;
import com.digihealth.anesthesia.interfacedata.formbean.HisResponse;
import com.google.common.base.Objects;

@Service
public class OperBaseDataServiceHBGZB extends BaseService {
    
	// 手术名称(VIEW_OPERATION_NAME)
	@Transactional
	public void synHisOperNameList() {
		logger.info("-------start hbgzb synHisOperNameList-----------");

		Connection conn = null;
		String sql = "select * from VIEW_OPERATION_NAME";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String beid = getBeid();
			while (rs.next()) {
				// 手术名称为空，则不需插入到数据库中
				String code = rs.getString("code");
				String name = rs.getString("name");
				Integer enable = rs.getInt("enable");
				String pinyin = PingYinUtil.getFirstSpell(name);

				List<BasOperdef> operdefList = basOperdefDao.selectByCode(code, beid);
				if (null != operdefList && operdefList.size() > 0) {
					for (BasOperdef operDefFormBean : operdefList) {
						String operdefId = operDefFormBean.getOperdefId();
						BasOperdef operdef = basOperdefDao.queryOperdefById(operdefId);
						if (isNeedUpdate(operdef.getName(), name) || operdef.getEnable() != enable) {
							operdef.setName(name);
							operdef.setEnable(enable);
							operdef.setCode(code);
							operdef.setPinYin(pinyin);
							operdef.setBeid(beid);
							basOperdefDao.update(operdef);
						}
					}
				} else {
					BasOperdef odef = new BasOperdef();
					odef.setOperdefId(GenerateSequenceUtil.generateSequenceNo());
					odef.setCode(code);
					odef.setName(name);
					odef.setPinYin(pinyin);
					odef.setEnable(enable);
					odef.setBeid(beid);
					basOperdefDao.insert(odef);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisOperNameList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisOperNameList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisOperNameList-----------");
	}

	// 手术医生(VIEW_OPERATION_DOCTOR)
	@Transactional
	public void synHisOperDoctorList() {
		logger.info("-------start hbgzb synHisOperDoctorList-----------");

		Connection conn = null;
		String sql = "select * from VIEW_OPERATION_DOCTOR";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String beid = getBeid();
			while (rs.next()) {
				String code = rs.getString("code");
				String name = rs.getString("name");
				Integer region = rs.getInt("region");
				Integer enable = rs.getInt("enable");
				String pinyin = PingYinUtil.getFirstSpell(name);

				List<BasOperationPeople> operationPeoples = basOperationPeopleDao.selectByCode(code, beid);
				if (null != operationPeoples && operationPeoples.size() > 0) {
					for (BasOperationPeople opd : operationPeoples) {
						if (isNeedUpdate(opd.getName(), name) || opd.getEnable() != enable || opd.getRegion() != region) {
							opd.setCode(code);
							opd.setName(name);
							opd.setEnable(enable);
							opd.setRegion(region);
							opd.setPinYin(pinyin);
							opd.setBeid(beid);
							basOperationPeopleDao.update(opd);
						}
					}
				} else {
					BasOperationPeople opd = new BasOperationPeople();
					opd.setOperatorId(GenerateSequenceUtil.generateSequenceNo());
					opd.setCode(code);
					opd.setName(name);
					opd.setEnable(enable);
					opd.setRegion(region);
					opd.setPinYin(pinyin);
					opd.setBeid(beid);
					basOperationPeopleDao.insert(opd);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisOperDoctorList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisOperDoctorList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}

		logger.info("-------end synHisOperDoctorList-----------");
	}

	// 麻醉方法(VIEW_ANAES_METHOD)
	@Transactional
	public void synHisAnaesMethodList() {
		logger.info("-------start hbgzb synHisAnaesMethodList-----------");
		Connection conn = null;
		String sql = "select * from VIEW_ANAES_METHOD";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String beid = getBeid();
			while (rs.next()) {
				String name = rs.getString("name");
				String pinyin = PingYinUtil.getFirstSpell(name);
				Integer isValid = rs.getInt("is_valid");
				String code = rs.getString("code");
				List<BasAnaesMethod> anaesMethods = basAnaesMethodDao.selectByCode(code, beid);
				if (StringUtils.isBlank(name)) {// name如果为null，则直接不处理
					continue;
				}
				if (null != anaesMethods && anaesMethods.size() > 0) {
					for (BasAnaesMethod anaesMethod : anaesMethods) {
						if (isNeedUpdate(anaesMethod.getName(), name) || anaesMethod.getIsValid() != isValid) {
							anaesMethod.setName(name);
							anaesMethod.setIsValid(isValid);
							anaesMethod.setPinYin(pinyin);
							anaesMethod.setCode(code);
							if (StringUtils.isNotBlank(code)) {
								if ("302".equals(code) || "03".equals(code)) { // 局部浸润麻醉 or 局部麻醉 
															// 为局麻，其他都有麻醉医生参与
									anaesMethod.setIsLocalAnaes(1); // 1是局麻
								} else {
									anaesMethod.setIsLocalAnaes(0); // 全麻
								}
							} else {
								anaesMethod.setIsLocalAnaes(0);
							}
							anaesMethod.setBeid(beid);
							basAnaesMethodDao.update(anaesMethod);
						}
					}
				} else {
					BasAnaesMethod anaesMethod = new BasAnaesMethod();
					anaesMethod.setAnaMedId(GenerateSequenceUtil.generateSequenceNo());
					anaesMethod.setCode(code);
					anaesMethod.setName(name);
					anaesMethod.setIsValid(isValid);
					anaesMethod.setPinYin(pinyin);
					if (StringUtils.isNotBlank(code)) {
						if ("302".equals(code)|| "03".equals(code)) { // 局部浸润麻醉or局部麻醉  为局麻，其他都有麻醉医生参与
							anaesMethod.setIsLocalAnaes(1); // 1是局麻
						} else {
							anaesMethod.setIsLocalAnaes(0); // 全麻
						}
					} else {
						anaesMethod.setIsLocalAnaes(0);
					}
					anaesMethod.setBeid(beid);
					basAnaesMethodDao.insert(anaesMethod);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisAnaesMethodList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisAnaesMethodList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisAnaesMethodList-----------");
	}

	// 诊断名称(VIEW_DIAG_NAME)
	@Transactional
	public void synHisDiagNameList() {
		logger.info("-------start hbgzb synHisDiagNameList-----------");

		Connection conn = null;
		String sql = "select * from VIEW_DIAG_NAME";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String beid = getBeid();
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String code = rs.getString("code");
				String name = rs.getString("name");
				String pinyin = PingYinUtil.getFirstSpell(name);
				Integer enable = rs.getInt("enable");

				List<BasDiagnosedef> diagnosedefs = basDiagnosedefDao.selectByCode(code, beid);
				if (StringUtils.isBlank(name)) {
					continue;
				}

				if (null != diagnosedefs && diagnosedefs.size() > 0) {
					for (BasDiagnosedef diagnosedef : diagnosedefs) {
						if (isNeedUpdate(diagnosedef.getName(), name) || diagnosedef.getEnable() != enable) {
							diagnosedef.setName(name);
							diagnosedef.setEnable(enable);
							diagnosedef.setPinYin(pinyin);
							diagnosedef.setCode(code);
							basDiagnosedefDao.update(diagnosedef);
						}
					}
				} else {
					BasDiagnosedef diagnosedef = new BasDiagnosedef();
					diagnosedef.setDiagDefId(GenerateSequenceUtil.generateSequenceNo());
					diagnosedef.setCode(code);
					diagnosedef.setName(name);
					diagnosedef.setEnable(enable);
					diagnosedef.setPinYin(pinyin);
					diagnosedef.setBeid(beid);
					basDiagnosedefDao.insert(diagnosedef);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisDiagNameList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisDiagNameList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisDiagNameList-----------");
	}

	// 科室(VIEW_DEPT_ROOM)
	@Transactional
	public void synHisDeptRoomList() {
		logger.info("-------start hbgzb synHisDeptRoomList-----------");
		Connection conn = null;
		String sql = "select * from VIEW_DEPT_ROOM";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String beid = getBeid();
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				Integer enable = rs.getInt("enable");
				String id = rs.getString("deptId");
				if (StringUtils.isBlank(name)) {
					continue;
				}
				BasDept dt = basDeptDao.searchDeptById(id, beid);

				if (null != dt && (isNeedUpdate(dt.getName(), name) || dt.getEnable() != enable)) {
					dt.setName(name);
					dt.setEnable(enable);
					basDeptDao.update(dt);
				}

				if (null == dt) {
					BasDept dept = new BasDept();
					dept.setDeptId(id);
					dept.setName(name);
					dept.setEnable(enable);
					dept.setBeid(beid);
					basDeptDao.insert(dept);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisDeptRoomList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisDeptRoomList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisDeptRoomList-----------");
	}

	// 病区(VIEW_REGION)
	@Transactional
	public void synHisRegionList() {
		logger.info("-------start hbgzb synHisRegionList-----------");

		Connection conn = null;
		String sql = "select * from VIEW_REGION";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String beid = getBeid();
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String regionId = rs.getString("regionId");
				Integer enable = rs.getInt("enable");
				BasRegion re = basRegionDao.searchRegionById(regionId, beid);
				if (null == name) {
					continue;
				}

				// 根据id查询科室信息，不为空则更新，为空则新增
				if (null != re && (isNeedUpdate(re.getName(), name) || re.getEnable() != enable)) {
					re.setName(name);
					re.setEnable(enable);
					basRegionDao.update(re);
				}

				if (null == re) {
					BasRegion region = new BasRegion();
					region.setRegionId(regionId);
					region.setName(name);
					region.setEnable(enable);
					region.setBeid(beid);
					basRegionDao.insert(region);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisRegionList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisRegionList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisRegionList-----------");
	}

	// 器械(VIEW_INSTRUMENT)
	@Transactional
	public void synHisInstrumentList() {
		logger.info("-------start hbgzb synHisInstrumentList-----------");
		Connection conn = null;
		String sql = "select * from VIEW_INSTRUMENT";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String beid = getBeid();
			while (rs.next()) {
				String code = rs.getString("code");
				String name = rs.getString("name");
				String type = rs.getString("type");
				String pinyin = rs.getString("pinyin");
				Integer enable = rs.getInt("enable");
				List<BasInstrument> instruments = basInstrumentDao.selectByCode(code, beid);
				if (null != instruments && instruments.size() > 0) {
					for (BasInstrument instrument : instruments) {
						if (isNeedUpdate(instrument.getType(), type) || instrument.getEnable() != enable || isNeedUpdate(instrument.getName(), name)) {
							instrument.setCode(code);
							instrument.setEnable(enable);
							instrument.setName(name);
							instrument.setType(type);
							instrument.setPinYin(StringUtils.isBlank(pinyin) ? PingYinUtil.getFirstSpell(name) : pinyin);
							basInstrumentDao.update(instrument);
						}

					}
				} else {
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
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisInstrumentList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisInstrumentList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}

		logger.info("-------end hbgzb synHisInstrumentList-----------");
	}

	// 药品库(VIEW_DRUG_STORE)
	@Transactional
	public void synHisDrugStoreList() {
		logger.info("-------start hbgzb synHisDrugStoreList-----------");

		Connection conn = null;
		String sql = "select * from VIEW_DRUG_STORE";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String beid = getBeid();
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String code = rs.getString("code");
				String type = rs.getString("type");
				Integer enable = rs.getInt("enable");
				String name = rs.getString("name");
				String pinyin = PingYinUtil.getFirstSpell(name);
				String briefName = rs.getString("brief_name");
				Float packageDosageAmout = rs.getFloat("package_dosage_amount");
				String dosageUnit = rs.getString("dosage_unit");
				String spec = rs.getString("spec");
				logger.info("synHisDrugStoreList---code="+code+",dosageUnit="+dosageUnit);
				List<BasMedicine> medicines = basMedicineDao.selectByCode(code, beid);
				if (null != medicines && medicines.size() > 0) {
					for (BasMedicine medicine : medicines) {
						if (isNeedUpdate(medicine.getBriefName(), briefName) || isNeedUpdate(medicine.getDosageUnit(), dosageUnit) || medicine.getEnable() != enable || isNeedUpdate(medicine.getName(), name) || !medicine.getPackageDosageAmount().equals(packageDosageAmout) || isNeedUpdate(medicine.getSpec(), spec) || isNeedUpdate(medicine.getType(), type)) {
							medicine.setCode(code);
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
				} else {
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
					medicine.setBeid(beid);
					basMedicineDao.insert(medicine);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisDrugStoreList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisDrugStoreList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisDrugStoreList-----------");
	}

	// 药品价格(VIEW_MEDICINE_PRICE)
	@Transactional
	public void synHisMedicinePriceList() {
		logger.info("-------start hbgzb synHisMedicinePriceList-----------");
		Connection conn = null;
		String sql = "select * from VIEW_MEDICINE_PRICE";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String beid = getBeid();
			while (rs.next()) {
				String code = rs.getString("code");
				String spec = rs.getString("spec");
				String firm = rs.getString("firm");
				String batch = rs.getString("batch");
				String minPackageUnit = rs.getString("min_package_unit");
				Float priceMinPackage = rs.getFloat("price_min_package");
				Integer enable = rs.getInt("enable");
				List<BasPrice> prices = basPriceDao.selectByCode(code, beid);
				if (null != prices && prices.size() > 0) {
					for (BasPrice price : prices) {
						if (isNeedUpdate(price.getSpec(), spec) || isNeedUpdate(price.getFirm(), firm) || isNeedUpdate(price.getBatch(), batch) || isNeedUpdate(price.getMinPackageUnit(), minPackageUnit) || !price.getPriceMinPackage().equals(priceMinPackage) || price.getEnable() != enable) {
							price.setSpec(spec);
							price.setFirm(firm);
							price.setBatch(batch);
							price.setMinPackageUnit(minPackageUnit);
							price.setPriceMinPackage(priceMinPackage);
							price.setEnable(enable);
							price.setCode(code);
							basPriceDao.update(price);
						}
					}
				} else {
					BasPrice price = new BasPrice();
					price.setPriceId(GenerateSequenceUtil.generateSequenceNo());
					price.setCode(code);
					price.setSpec(spec);
					price.setFirm(firm);
					price.setBatch(batch);
					price.setMinPackageUnit(minPackageUnit);
					price.setPriceMinPackage(priceMinPackage);
					price.setEnable(enable);
					price.setBeid(beid);
					basPriceDao.insert(price);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisMedicinePriceList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisMedicinePriceList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisMedicinePriceList-----------");
	}

	// 收费项目(VIEW_CHARGE_ITEM)
	@Transactional
	public void synHisChargeItemList() {
		logger.info("-------start hbgzb synHisChargeItemList-----------");
		Connection conn = null;
		String sql = "select * from VIEW_CHARGE_ITEM";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getHBGZBHisConnection();
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String beid = getBeid();
			while (rs.next()) {
				String code = rs.getString("charge_item_code");
				List<BasChargeItem> chargeItems = basChargeItemDao.selectByCode(code, beid);

				String spec = rs.getString("spec");

				String chargeItemName = rs.getString("charge_item_name");
				if (StringUtils.isNotBlank(chargeItemName)) {
					chargeItemName = StringFilter(chargeItemName); // 过滤掉？
				}

				String pinyin = rs.getString("pinyin");
				if (StringUtils.isNotBlank(pinyin)) {
					pinyin = pinyin.replaceAll("[^a-zA-Z]", "");
				}

				String unit = rs.getString("unit");
				float basicUnitAmout = rs.getFloat("basic_unit_amount");
				float price = rs.getFloat("price");
				float basicUnitPrice = rs.getFloat("basic_unit_price");
				String type = rs.getString("type");
				String chargeType = rs.getString("charge_type");
				Integer enable = rs.getInt("enable");
				if (null != chargeItems && chargeItems.size() > 0) {
					for (BasChargeItem chargeItem : chargeItems) {
						if (isNeedUpdate(chargeItem.getChargeItemName(), chargeItemName) || isNeedUpdate(chargeItem.getSpec(), spec) || isNeedUpdate(chargeItem.getUnit(), unit) || chargeItem.getBasicUnitAmount() != basicUnitAmout || chargeItem.getPrice() != price || chargeItem.getBasicUnitPrice() != basicUnitPrice || isNeedUpdate(chargeItem.getType(), type) || isNeedUpdate(chargeItem.getChargeType(), chargeType) || chargeItem.getEnable() != enable) {
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
							chargeItem.setChargeItemCode(code);
							basChargeItemDao.updateByPrimaryKeySelective(chargeItem);
						}
					}
				} else {
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
					chargeItem.setBeid(beid);
					basChargeItemDao.insert(chargeItem);
				}
			}
		} catch (Exception e) {
			logger.error("OperBaseDataServiceHBGZB:synHisChargeItemList--------------" + Exceptions.getStackTraceAsString(e));
		} finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				logger.error("OperBaseDataServiceHBGZB:synHisChargeItemList--------------" + Exceptions.getStackTraceAsString(e));
			}
		}
		logger.info("-------end hbgzb synHisChargeItemList-----------");
	}

	public static String StringFilter(String str) throws PatternSyntaxException {
		if (StringUtils.isBlank(str)) {
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

	/**
	 * 关闭连接相关对象
	 * 
	 * @param conn
	 * @param pstmt
	 * @param rs
	 * @throws SQLException
	 */
	private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) throws SQLException {
		ConnectionManager.closeConnection();

		if (null != pstmt) {
			pstmt.close();
		}

		if (null != rs) {
			rs.close();
		}
	}

	private boolean isNeedUpdate(String value, String hisValue) {
		if ((null == value && null != hisValue) || (null != value && !value.equals(hisValue))) {
			return true;
		}

		return false;
	}

	/******************************** 调用his的webservice接口 start *************************************/

	public String synHisOperList() {
		logger.info("synHisOperList：获取手术通知单的手术信息--------------");
		HisGetOperationFormBean bean = new HisGetOperationFormBean();
		bean.setSyxh("0");
		String kssj = SysUtil.getDateFormat(new Date(), "yyyyMMdd");
		bean.setKssj(kssj);
		bean.setJssj("");
		String respMsg = null;
		try {
			String request = getObjectToXml(bean);
			logger.debug("request--------------------:" + request);
			respMsg = getWebInterface().getPatientOrder(request);
			if (StringUtils.isNotBlank(respMsg)) {
				JAXBContext context = JAXBContext.newInstance(HisGetOperationResponse.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				HisGetOperationResponse response = (HisGetOperationResponse) unmarshaller.unmarshal(new StringReader(respMsg));
				if (null != response) {
					if (!"0".equals(response.getResultCode())) {
						throw new RuntimeException(response.getResultMessage());
					}
					// 数据操作
					List<HisOperationFormBean> operList = response.getOperList().getItem();
					if (null != operList && operList.size() > 0) {
						for (int i = 0; i < operList.size(); i++) {
							HisOperationFormBean item = operList.get(i);
							logger.info("synHisOperList:item-->" + item.toString());
							getHisOperateNotice(item);
						}
					}

				} else {
					logger.info("synHisOperList时his端无响应");
				}
			}
		} catch (Exception e) {
			logger.info("synHisOperList 调用异常:" + Exceptions.getStackTraceAsString(e));
			throw new RuntimeException(Exceptions.getStackTraceAsString(e));
		}

		return respMsg;

	}
	

	public HisResponse getHisOperateNotice(HisOperationFormBean operList) {
		logger.info("hbgzb--getHisOperateNotice--his--存入手术信息------");

		HisResponse resp = new HisResponse();
		if (operList != null) {
			BasRegOpt regOpt = new BasRegOpt();
			String beid = getBeid();
			BeanHelper.copyProperties(operList, regOpt);
			regOpt.setFrontOperForbidTake(new Integer(1));

			// 诊断名称
            String diagDef = "";
            String diagName = "";
            String[] diagCode = null;
//            if (StringUtils.isNotEmpty(operList.getDiagCode()))
//            {
//                diagCode = operList.getDiagCode().split(",");
//                for (int i = 0; i < diagCode.length; i++)
//                {
//                    List<BasDiagnosedef> diagnosedefs = basDiagnosedefDao.selectByCode(diagCode[i], beid);
//                    if (null != diagnosedefs && diagnosedefs.size() > 0)
//                    {
//                        diagDef = diagDef + diagnosedefs.get(0).getDiagDefId() + ",";
//                        diagName = diagName + diagnosedefs.get(0).getName() + ",";
//                    }
//                }
//            }
            
            //diagName为空说明his传过来的diagCode为空，或者是his传过来的diagCode不为空但是还未同步到手麻系统
            if (StringUtils.isNotBlank(operList.getDiagName())) 
            {
                String[] diagNames = operList.getDiagName().split(",");
                for (int i = 0; i < diagNames.length; i++)
                {
                	String name = diagNames[i];
                    String code = null ;
                	
                	if (StringUtils.isNotEmpty(operList.getDiagCode())){
                		diagCode = operList.getDiagCode().split(",");
                	}
                    
                    if(null!=diagCode && Objects.equal(diagCode.length, diagNames.length)){
                    	code = diagCode[i];
                    }
                    
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
            if (StringUtils.isNotBlank(operList.getOperName()))
            {
            	if (StringUtils.isNotEmpty(operList.getOperCode())){
            		operCodes = operList.getOperCode().split(",");
            	}
                String[] operNames = operList.getOperName().split(",");
                
                for (int i = 0; i < operNames.length; i++)
                {
                    String name = operNames[i];
                    String code = null;
                    if(null!=operCodes && Objects.equal(operCodes.length, operNames.length)){
                    	code = operCodes[i];
                    }
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
            
            
//            String[] operCodes = null;
//            if (StringUtils.isNotEmpty(operList.getOperCode()))
//            {
//                operCodes = operList.getOperCode().split(",");
//                for (int i = 0; i < operCodes.length; i++)
//                {
//                    List<BasOperdef> opers = basOperdefDao.selectByCode(operCodes[i], beid);
//                    if (null != opers && opers.size() > 0)
//                    {
//                        operId = operId + opers.get(0).getOperdefId() + ",";
//                        operName = operName + opers.get(0).getName() + ",";
//                    }
//                }
//            }
//            if ("".equals(operName) && StringUtils.isNotBlank(operList.getOperName()))
//            {
//                String[] operNames = operList.getOperName().split(",");
//                for (int i = 0; i < operNames.length; i++)
//                {
//                    String name = operNames[i];
//                    String code = null != operCodes ? operCodes[i] : null;
//                    List<BasOperdef> opers = basOperdefDao.selectByName(name, beid);
//                    if (null != opers && opers.size() > 0)
//                    {
//                        operId = operId + opers.get(0).getOperdefId() + ",";
//                        operName = operName + opers.get(0).getName() + ",";
//                    }
//                    else
//                    {
//                        BasOperdef operdef = new BasOperdef();
//                        operdef.setOperdefId(GenerateSequenceUtil.generateSequenceNo());
//                        operdef.setName(name);
//                        operdef.setCode(code);
//                        operdef.setEnable(1);
//                        operdef.setPinYin(PingYinUtil.getFirstSpell(name));
//                        operdef.setBeid(beid);
//                        basOperdefDao.insert(operdef);
//                        
//                        operId = operId + operdef.getOperdefId() + ",";
//                        operName = operName + name + ",";
//                    }
//                }
//            }
//            operId = StringUtils.isNotBlank(operId) ? operId.substring(0, operId.length() - 1) : "";
//            operName = StringUtils.isNotBlank(operName) ? operName.substring(0, operName.length() - 1) : "";
//                
//            regOpt.setDesignedOptCode(operId);
//            regOpt.setDesignedOptName(operName);

            
            
            
			
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
			String designedAnaesMethodCode = operList.getAnaesID();
			
			List<String> designedAnaesMethodCodes = new ArrayList<String>();
			
			List<BasAnaesMethod> anaesMethods = basAnaesMethodDao.selectByCode(designedAnaesMethodCode, beid);
			if (null != anaesMethods && anaesMethods.size() > 0) {
				regOpt.setDesignedAnaesMethodCode(anaesMethods.get(0).getAnaMedId() + "");
				regOpt.setDesignedAnaesMethodName(anaesMethods.get(0).getName());
				
				designedAnaesMethodCodes.add(anaesMethods.get(0).getAnaMedId());
				
			} else { // 如果没查到，则重新判断
				BasAnaesMethod anaesMethod = new BasAnaesMethod();
				anaesMethod.setAnaMedId(GenerateSequenceUtil.generateSequenceNo());
				anaesMethod.setCode(designedAnaesMethodCode);
				anaesMethod.setName(designedAnaesMethodName);
				anaesMethod.setIsValid(1);
				anaesMethod.setPinYin(StringUtils.isNotBlank(designedAnaesMethodName) ? PingYinUtil.getFirstSpell(designedAnaesMethodName) : null);
				if (StringUtils.isNotBlank(designedAnaesMethodCode)) {
					if ("302".equals(designedAnaesMethodCode) || "03".equals(designedAnaesMethodCode)) { // 局部浸润麻醉 or局部麻醉
																	// 为局麻，其他都有麻醉医生参与
						anaesMethod.setIsLocalAnaes(1); // 1是局麻
					} else {
						anaesMethod.setIsLocalAnaes(0); // 全麻
					}
				} else {
					anaesMethod.setIsLocalAnaes(0);
				}
				anaesMethod.setBeid(beid);
				basAnaesMethodDao.insert(anaesMethod);

				regOpt.setDesignedAnaesMethodCode(anaesMethod.getAnaMedId());
				regOpt.setDesignedAnaesMethodName(designedAnaesMethodName);
				
				designedAnaesMethodCodes.add(anaesMethod.getAnaMedId());
			}

			regOpt.setDesignedAnaesMethodCodes(designedAnaesMethodCodes);
			
			if (null == regOpt.getIsLocalAnaes()) {
				
				BasRegOptUtils.IsLocalAnaesSet(regOpt);
				
			}
			regOpt.setBeid(beid);

			String operTypeStr = operList.getOperType();
			if (StringUtils.isNotBlank(operTypeStr)) {
				// 急诊 or 择期
				Integer operType = Integer.valueOf(operTypeStr);
				regOpt.setEmergency(operType);
			}

			// 身份证号
			String credNumber = operList.getCredNumber();
			if (StringUtils.isNotBlank(credNumber)) {
				regOpt.setIdentityNo(credNumber);
			}
			// 手术日期
			String operDate = operList.getOperDate();
			if (StringUtils.isNotBlank(operDate)) {
				regOpt.setOperaDate(operDate);
			}
			// 开始时间 结束时间
			String operStartTime = operList.getOperStartTime();
			String operEndTime = operList.getOperEndTime();
			if (StringUtils.isNotBlank(operStartTime)) {
				regOpt.setStartTime(operStartTime);
			}
			if (StringUtils.isNotBlank(operEndTime)) {
				regOpt.setEndTime(operEndTime);
			}
			// 药物过敏
			String hyperSusceptiBility = operList.getDragAllergy();
			if (StringUtils.isNotBlank(hyperSusceptiBility)) {
				regOpt.setHyperSusceptiBility(hyperSusceptiBility);
			}
			// 手术级别 his传入为数字1，2，3，4需要转换成中文
			String operLevel = operList.getOperLevel();
			if (StringUtils.isNotBlank(operLevel)) {
				List<SysCodeFormbean> levelList = basSysCodeDao.searchSysCodeByGroupIdAndCodeValue("operat_level", operLevel, beid);
				if(null!=levelList && levelList.size()>0){
					regOpt.setOptLevel(levelList.get(0).getCodeName());
				}
				
			}

			// 切口等级

			String incisionLevelStr = operList.getIncisionLevel();
			if (StringUtils.isNotBlank(incisionLevelStr)) {
				Integer incisionLevel = Integer.valueOf(incisionLevelStr);
				regOpt.setCutLevel(incisionLevel);
			}

			// 手术来源
			String opeSourceStr = operList.getOpeSource();
			if (StringUtils.isNotBlank(opeSourceStr)) {
				Integer opeSource = Integer.valueOf(opeSourceStr);
				regOpt.setOperSource(opeSource);
			}

			String reservenumber = operList.getReservenumber();
			if (StringUtils.isNotBlank(reservenumber)) {
				// 判断患者是否已经存在，如果存在则更新基础数据
				BasRegOpt regPo = basRegOptDao.searchRegOptByReservenumber(reservenumber, beid);
				regOpt.setPreengagementnumber(reservenumber); // 设置his预约号
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

	// 手麻手术排班信息发送给his系统
	public String sendDispatchInfo(BasDispatch dispatch, BasRegOpt regOpt) {
		logger.info("正在发送his" + regOpt.getName() + "的排班信息!!!");
		HisDispatchFormbean hisDispatchFormbean = new HisDispatchFormbean();
		BeanUtils.copyProperties(dispatch, hisDispatchFormbean);
		hisDispatchFormbean.setReservenumber(regOpt.getPreengagementnumber()); // 预约号
		String stime = "";
		stime = regOpt.getOperaDate() + " " + dispatch.getStartTime();
		hisDispatchFormbean.setStartTime(stime);
		String respMsg = null;
		try {
			String requestXml = getObjectToXml(hisDispatchFormbean);
			respMsg = getWebInterface().updateYs(requestXml);
			if (StringUtils.isNotBlank(respMsg)) {
				JAXBContext context = JAXBContext.newInstance(HisResponse.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				HisResponse response = (HisResponse) unmarshaller.unmarshal(new StringReader(respMsg));

				if (null != response) {
					if (!"0".equals(response.getResultCode())) {
						throw new RuntimeException(response.getResultMessage());
					}
				} else {
					logger.info("sendDispatchInfo时his端无响应");
				}
			}

		} catch (Exception e) {
			logger.info("同步" + regOpt.getName() + "的排班异常:" + Exceptions.getStackTraceAsString(e));
			throw new RuntimeException(Exceptions.getStackTraceAsString(e));
		}

		return respMsg;
	}

	// 手术状态修改
	public String sendOperStateUpdate(BasRegOpt regOpt) {
		HisCancleOptFormBean formBean = new HisCancleOptFormBean();
		formBean.setReservenumber(regOpt.getPreengagementnumber());
		formBean.setState(regOpt.getState());
		String respMsg = null;
		try {
			String requestXml = getObjectToXml(formBean);
			respMsg = getWebInterface().updateZt(requestXml);
			if (StringUtils.isBlank(respMsg)) {
				JAXBContext context = JAXBContext.newInstance(HisResponse.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				HisResponse response = (HisResponse) unmarshaller.unmarshal(new StringReader(respMsg));
				if (null != response) {
					if (!"0".equals(response.getResultCode())) {
						throw new RuntimeException(response.getResultMessage());
					}
				} else {
					logger.info("sendOperStateUpdate时his端无响应");
				}
			}
		} catch (Exception e) {
			logger.info("修改" + regOpt.getName() + "的排班状态异常:" + Exceptions.getStackTraceAsString(e));
			throw new RuntimeException(Exceptions.getStackTraceAsString(e));
		}
		return respMsg;
	}
	
	
	/**
     *实例化HIS webService接口
     */
    private WebInterfaceSoap getWebInterface()
    {
        WebInterface webInterface = new WebInterface();
        WebInterfaceSoap soap = webInterface.getWebInterfaceSoap();
        return soap;
    }

	/**
	 * 实例化HIS webService接口
	 */
	/*private WebInterfaceHttpPost getWebInterface() {
		WebInterface service = new WebInterface();
		WebInterfaceHttpPost webInterfaceHttpPost = service.getWebInterfaceHttpPost();
		return webInterfaceHttpPost;
	}*/

	// 创建getObjectToXml方法（将对象转换成XML格式的文件）
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
		String xmlContent = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
		return xmlContent;
	}

	/******************************** 调用his的webservice接口 end *************************************/

}
