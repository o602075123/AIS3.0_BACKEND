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

import com.digihealth.anesthesia.common.config.Constants;
import com.digihealth.anesthesia.common.config.Global;
import com.digihealth.anesthesia.common.utils.DateUtils;
import com.digihealth.anesthesia.common.utils.Exceptions;
import com.digihealth.anesthesia.common.utils.StringUtils;

/**
 * 移除30天以前的数据
 */
public class DelMessageWorkstationDateTask
{
	private final static Logger logger = Logger.getLogger(DelMessageWorkstationDateTask.class);

	public final static String model = Global.getConfig(Constants.MASTERORSLAVE_STR).toString();
	public static String queues = Global.getConfig(Constants.QUEUES_STR).toString();
	public final static String curQueue = Global.getConfig(Constants.CURQUEUE_STR).toString();
	
	
	public void job()
	{
		deteleMessageWorkstationByTimerTask();
	}

	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private int taskTotal = 0;
	private List<String> idList = new ArrayList<String>();
	
	@Transactional(readOnly = false)
	public void deteleMessageWorkstationByTimerTask(){
	    try {
    		String deleteDay = DateUtils.DateToString(DateUtils.addDays(new Date(), -30));
			//删除message主表数据
			//getDateForDel("bas_message_workstation2",deleteDay);
			
			if (model.equals(Constants.MASTER)) {
				if (StringUtils.isNotBlank(queues)) {
					String[] ques = queues.split(",");
					for (int i = 0; i < ques.length; i++) { 
						String receiver = ques[i];
						String table = "bas_message" + Constants.LINK + receiver;
						getDateForDel(table,deleteDay);
					}
				}
				//删除bas_message数据
				getDateForDel("bas_message",deleteDay);
			}else{
				String table = "bas_message" + Constants.LINK + curQueue;
				getDateForDel(table,deleteDay);
			}
			
			
	        logger.info("DelMessageWorkstationDateTask=============while end===============");

	    } catch (Exception e) {
	    	logger.info("DelMessageWorkstationDateTask Exception ====="+Exceptions.getStackTraceAsString(e));
	    }
	}
	
	private void getDateForDel(String tableName ,String deleteDay)
	{
		logger.info("DelMessageWorkstationDateTask getDateForDel is start.");
		goDataBytime(tableName,deleteDay);
        this.taskTotal = 0;
		logger.info("DelMessageWorkstationDateTask getDateForDel is end.");
	}

	
	private void goDataBytime(String tableName,String deleteDay)
	{
		boolean flag = false;
		//从dat_monitor_data 查询出500条数据
		idList = selectMessageDateList(tableName,deleteDay);
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
			deleteMessageData(tableName,ids);
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
	private List<String> selectMessageDateList(String tableName,String deleteDay) {
		
		List<String> mdidList = new ArrayList<String>();
		List<Map<String, Object>> mapList = jdbcTemplate.queryForList("SELECT id FROM "+tableName+"  WHERE enable = 0 and time < ? limit 1000", new Object[]{deleteDay});
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
	private int deleteMessageData(String tableName,String ids) {
		return jdbcTemplate.update("delete from "+tableName+" where id in ("+ids+") ");
	}
	
}
