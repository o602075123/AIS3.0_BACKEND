package com.digihealth.anesthesia.common.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;

/**
 * 移除一天前的采集数据（秒表），保证表的数据不至于太大
 */
public class DelDatMonitorDateTask
{
	private final static Logger logger = Logger.getLogger(DelDatMonitorDateTask.class);

	public void job()
	{
		deteleObsByTimerTask();
	}

	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private int taskTotal = 0;
	private List<String> idList = new ArrayList<String>();
	
	@Transactional(readOnly = false)
	public void deteleObsByTimerTask(){
	    try {
    		String deleteDay = DateUtils.DateToString(DateUtils.addDays(new Date(), -7));
			//删除message主表数据
			getDateForDel("bas_observe_data",deleteDay);
			
	        logger.info("deteleObsByTimerTask=============while end===============");

	    } catch (Exception e) {
	    	logger.info("deteleObsByTimerTask Exception ====="+Exceptions.getStackTraceAsString(e));
	    }
	}
	
	private void getDateForDel(String tableName ,String deleteDay)
	{
		logger.info("deteleMessageByTimerTask getDateForDel is start.");
		goDataBytime(tableName,deleteDay);
        this.taskTotal = 0;
		logger.info("deteleMessageByTimerTask getDateForDel is end.");
	}

	
	private void goDataBytime(String tableName,String deleteDay)
	{
		boolean flag = false;
		//从dat_monitor_data 查询出500条数据
		idList = selectObsDateList(tableName,deleteDay);
		if(null != idList && idList.size()>0)
		{
			StringBuffer sb = new StringBuffer();
			for(int i =0; i<idList.size() ; i++)
				{
					String id = idList.get(i);
					if(i == idList.size() -1)
					{
						sb.append("'"+id+"'");
					}else
					{
						sb.append("'"+id+"'").append(",");
					}
				
			}
			String ids = sb.toString();
			//将这些数据从dat_monitor_data表删除
			deleteObserveData(tableName,ids);
			flag = true;
			idList.clear();
		}
		
		taskTotal ++;
		//还能从数据库里查到记录并且循环次数不能超过100万,超过了停下来，起到保护作用。
		if(flag && taskTotal<1000000)
		{
			goDataBytime(tableName,deleteDay);
		}
	}
	
	/**
	 * 根据时间查询500条采集数据
	 */
	private List<String> selectObsDateList(String tableName,String deleteDay) {
		
		List<String> mdidList = new ArrayList<String>();
		List<Map<String, Object>> mapList = jdbcTemplate.queryForList("SELECT id FROM "+tableName+"  WHERE time < ? limit 1000", new Object[]{deleteDay});
		if(null != mapList && mapList.size()>0)
		{
			 Iterator<Map<String, Object>> mapIte = mapList.iterator();
             while (mapIte.hasNext()) {
                 Map<String, Object> map = mapIte.next();
                 String id = String.valueOf(map.get("id"));
                 mdidList.add(id);
             }
		}
		return mdidList;
	}
	
	@SuppressWarnings("unused")
	private int deleteObserveData(String tableName,String ids) {
		return jdbcTemplate.update("delete from "+tableName+" where id in ("+ids+") ");
	}
	
}
