package com.digihealth.anesthesia.common.interceptor;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
//import org.apache.log4j.Logger;



import com.digihealth.anesthesia.common.utils.SpringContextHolder;
//import com.digihealth.anesthesia.core.SysUtil;
import com.digihealth.anesthesia.common.task.MsgProcessHolder;

/**
 * 
     * Title: MybatisInterceptor.java    
     * Description: 
     * @author chenyong       
     * @created 2017年6月27日 下午2:02:23
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = {
        MappedStatement.class, Object.class }) })
public class MybatisInterceptor implements Interceptor {
	
	//private static Logger logger = Logger.getLogger(MybatisInterceptor.class);
	
	private MsgProcessHolder msgHolder;
    
    @SuppressWarnings("unused")
    private Properties properties;

    public Object intercept(Invocation invocation) throws Throwable {
        if (msgHolder == null) {
        	msgHolder = (MsgProcessHolder) SpringContextHolder
                    .getBean(MsgProcessHolder.class);
        }
        
        Object returnValue = null;
        MappedStatement mappedStatement = (MappedStatement) invocation
                .getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        returnValue = invocation.proceed();
        String sql = getSql(configuration, boundSql);
        //logger.info("拦截的sql语句========"+sql);
        msgHolder.saveMsg(sql);
        return returnValue;
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj == null) {
            value = "null";
        } else {
            if (obj instanceof String) {
                value = "'" + obj.toString() + "'";
            } else if (obj instanceof Date) {
                DateFormat formatter = DateFormat.getDateTimeInstance(
                        DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
                value = "'" + formatter.format(obj) + "'";
                //logger.info("Date类型的obj的值为============"+value+",当前时间==="+ SysUtil.getTimeFormat(new Date()) );
            } else {
                value = obj.toString();
            }
        }
        return value;
    }

    public static String getSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        
        Map<String,String> specMap = new HashMap<String, String>();
        
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration
                    .getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?",
                        getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration
                        .newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        String objValue = getParameterValue(obj);
                        sql = replaceSpecialChar(sql, objValue,specMap);
                        
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql
                                .getAdditionalParameter(propertyName);
                        String objValue = getParameterValue(obj);
                        sql = replaceSpecialChar(sql, objValue,specMap);
                    }
                }
            }
        }
        
        /**
         * 还原被中文问号替换的英文问号
         */
		for (Map.Entry<String, String> entry : specMap
		.entrySet()) {
			sql = sql.replace(entry.getValue(), entry.getKey());
		}
        
        return sql;
    }
    /**
     * 将objValue中的英文问号全部替换成中文，这样在update语句拼接sql时就不会出现占位的问题
     * 并将替换前的内容保存到specMap中
     * @param sql
     * @param objValue
     * @return
     */
    public static String replaceSpecialChar(String sql, String objValue, Map<String,String> specMap){
    	int index = 0;
    	if(StringUtils.isNotBlank(objValue)){
    		index = objValue.indexOf("?");
    		if(index > 0){
    			specMap.put(objValue,  objValue.replace("?","？"));
    			
    			objValue = objValue.replace("?","？");
    		}
    	}
    	return sql.replaceFirst("\\?", objValue);
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties0) {
        this.properties = properties0;
    }
    
  
}
