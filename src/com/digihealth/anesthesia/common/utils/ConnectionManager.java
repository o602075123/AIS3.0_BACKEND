package com.digihealth.anesthesia.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.digihealth.anesthesia.common.config.Global;

public class ConnectionManager {
	private static Logger logger = Logger.getLogger(ConnectionManager.class);
	
	private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();
	
	private static String SYBX_JDBC_SQLSERVER_DRIVER_CLASS = Global.getJdbcConfig("sybx.jdbc.driver.sqlserver");
    private static String SYBX_JDBC_SQLSERVER_URL = Global.getJdbcConfig("sybx.jdbc.url.sqlserver");
    private static String SYBX_JDBC_SQLSERVER_USERNAME = Global.getJdbcConfig("sybx.jdbc.username.sqlserver");
    private static String SYBX_JDBC_SQLSERVER_PASSWORD = Global.getJdbcConfig("sybx.jdbc.password.sqlserver");
    
    private static String SYBX_JDBC_YY_SQLSERVER_DRIVER_CLASS = Global.getJdbcConfig("sybx.jdbc.yy.driver.sqlserver");
    private static String SYBX_JDBC_YY_SQLSERVER_URL = Global.getJdbcConfig("sybx.jdbc.yy.url.sqlserver");
    private static String SYBX_JDBC_YY_SQLSERVER_USERNAME = Global.getJdbcConfig("sybx.jdbc.yy.username.sqlserver");
    private static String SYBX_JDBC_YY_SQLSERVER_PASSWORD = Global.getJdbcConfig("sybx.jdbc.yy.password.sqlserver");
    
    /**
     * LIS
     */
    private static String NHFE_LIS_JDBC_DRIVER_CLASS = Global.getJdbcConfig("nhfe.lisJdbc.driver.oracle");
    private static String NHFE_LIS_JDBC_URL = Global.getJdbcConfig("nhfe.lisJdbc.url.oracle");
    private static String NHFE_LIS_JDBC_USERNAME = Global.getJdbcConfig("nhfe.lisJdbc.username.oracle");
    private static String NHFE_LIS_JDBC_PASSWORD = Global.getJdbcConfig("nhfe.lisJdbc.password.oracle");
	
    /**
     * 南华手术通知单
     */
    private static String NHFE_HIS_JDBC_DRIVER_CLASS = Global.getJdbcConfig("nhfe.jdbc.driver.oracle");
    private static String NHFE_HIS_JDBC_URL = Global.getJdbcConfig("nhfe.jdbc.url.oracle");
    private static String NHFE_HIS_JDBC_USERNAME = Global.getJdbcConfig("nhfe.jdbc.username.oracle");
    private static String NHFE_HIS_JDBC_PASSWORD = Global.getJdbcConfig("nhfe.jdbc.password.oracle");
    
    /**
     *SQLServer BC
     */
    private static String NHFE_BC_JDBC_DRIVER_CLASS = Global.getJdbcConfig("nhfe.bcJdbc.driver.SQLServer");
    private static String NHFE_BC_JDBC_URL = Global.getJdbcConfig("nhfe.bcJdbc.url.SQLServer");
    private static String NHFE_BC_JDBC_USERNAME = Global.getJdbcConfig("nhfe.bcJdbc.username.SQLServer");
    private static String NHFE_BC_JDBC_PASSWORD = Global.getJdbcConfig("nhfe.bcJdbc.password.SQLServer");
    
    /**
     * SQLServer NJ
     */
    private static String NHFE_NJ_JDBC_DRIVER_CLASS = Global.getJdbcConfig("nhfe.njJdbc.driver.SQLServer");
    private static String NHFE_NJ_JDBC_URL = Global.getJdbcConfig("nhfe.njJdbc.url.SQLServer");
    private static String NHFE_NJ_JDBC_USERNAME = Global.getJdbcConfig("nhfe.njJdbc.username.SQLServer");
    private static String NHFE_NJ_JDBC_PASSWORD = Global.getJdbcConfig("nhfe.njJdbc.password.SQLServer");
    
    /**
     * hbgzb配置信息
     */
    private static String HBGZB_JDBC_DRIVER_CLASS = Global.getJdbcConfig("hbgzb_jdbc.driver.SQLServer");
    private static String HBGZB_JDBC_URL = Global.getJdbcConfig("hbgzb_jdbc.url.SQLServer");
    private static String HBGZB_JDBC_USERNAME = Global.getJdbcConfig("hbgzb_jdbc.username.SQLServer");
    private static String HBGZB_JDBC_PASSWORD = Global.getJdbcConfig("hbgzb_jdbc.password.SQLServer");
    
    /**
     * 黔南州his
     */
    private static String QNZ_HIS_JDBC_DRIVER_CLASS = Global.getJdbcConfig("qnz.jdbc.driver.oracle");
    private static String QNZ_HIS_JDBC_URL = Global.getJdbcConfig("qnz.jdbc.url.oracle");
    private static String QNZ_HIS_JDBC_USERNAME = Global.getJdbcConfig("qnz.jdbc.username.oracle");
    private static String QNZ_HIS_JDBC_PASSWORD = Global.getJdbcConfig("qnz.jdbc.password.oracle");

	/**
	 * 从连接池拿Connection
	 * 
	 * getConnection和connectionHolder.get()的区别
	 * connectionHolder.get()是尝试从ThreadLocal中获取Connection,如果没有,返回null,如果有,直接返回.
	 * getConnection也是尝试从ThreadLocal中获取Connection,如果没有,则创建一个,然后返回,如果有,直接返回.
	 */
	public static Connection getConnection(String jdbcDriveClass,String jdbcUrl,String jdbcUserName,String jdbcPassword) {

		Connection connection = connectionHolder.get();

		if (connection == null) {
			// 1.连接池可以理解是一个java类,必须实现接口DateSource
			// 2.DBCP连接池也是一个java类,Tomcat为其new了对象并注册到JNDI,同时Tomcat实现了JavaEE规范之一的JNDI
			// 3.Context接口的lookup()可以从JNDI获取对象,通过名值对的形式;下面语句获取连接池对象(DateSource类型)
			try {
				Class.forName(jdbcDriveClass); // classLoader,加载对应驱动
				connection = (Connection) DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("ConnectionManager--getConnection(SQLException):"+e.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				logger.error("ConnectionManager--getConnection(ClassNotFoundException):"+e.getMessage());
			}
			// 直接从连接池拿连接,不是此时才让Oracle去创建连接
			connectionHolder.set(connection);
		}
		return connection;
	}
	
	/**
	 * 获取湖北葛洲坝的连接
	 * @return
	 */
	public static Connection getHBGZBHisConnection(){
		logger.info("HBGZB_JDBC_DRIVER_CLASS = " +HBGZB_JDBC_DRIVER_CLASS);
        return getConnection(HBGZB_JDBC_DRIVER_CLASS,HBGZB_JDBC_URL,HBGZB_JDBC_USERNAME,HBGZB_JDBC_PASSWORD);
	}
	
	//--------------------------------------------------沈阳本溪--------------------------------------------------------------
	/**
     * 连接沈阳本溪his系统数据库
     * 从连接池拿Connection
     * 
     * getConnection和connectionHolder.get()的区别
     * connectionHolder.get()是尝试从ThreadLocal中获取Connection,如果没有,返回null,如果有,直接返回.
     * getConnection也是尝试从ThreadLocal中获取Connection,如果没有,则创建一个,然后返回,如果有,直接返回.
     */
    public static Connection getSYBXHisConnection() {
        logger.info("LIS_JDBC_DRIVER_CLASS = " +SYBX_JDBC_SQLSERVER_DRIVER_CLASS);
        return getConnection(SYBX_JDBC_SQLSERVER_DRIVER_CLASS,SYBX_JDBC_SQLSERVER_URL,SYBX_JDBC_SQLSERVER_USERNAME,SYBX_JDBC_SQLSERVER_PASSWORD);
    }
    
    /**
     * 连接用友系统数据库
     * 从连接池拿Connection
     * 
     * getConnection和connectionHolder.get()的区别
     * connectionHolder.get()是尝试从ThreadLocal中获取Connection,如果没有,返回null,如果有,直接返回.
     * getConnection也是尝试从ThreadLocal中获取Connection,如果没有,则创建一个,然后返回,如果有,直接返回.
     */
    public static Connection getSYBXYYConnection() {
        logger.info("LIS_JDBC_DRIVER_CLASS = " +SYBX_JDBC_YY_SQLSERVER_DRIVER_CLASS);
        return getConnection(SYBX_JDBC_YY_SQLSERVER_DRIVER_CLASS,SYBX_JDBC_YY_SQLSERVER_URL,SYBX_JDBC_YY_SQLSERVER_USERNAME,SYBX_JDBC_YY_SQLSERVER_PASSWORD);
    }
    //--------------------------------------------------沈阳本溪--------------------------------------------------------------
    
    //--------------------------------------------------南华附二--------------------------------------------------------------
    public static Connection getNHFEHisConnection(){
        logger.info("LIS_JDBC_DRIVER_CLASS = " +NHFE_HIS_JDBC_DRIVER_CLASS);
        return getConnection(NHFE_HIS_JDBC_DRIVER_CLASS, NHFE_HIS_JDBC_URL, NHFE_HIS_JDBC_USERNAME, NHFE_HIS_JDBC_PASSWORD);
    }
    
    public static Connection getNHFELisConnection(){
        logger.info("LIS_JDBC_DRIVER_CLASS = " +NHFE_LIS_JDBC_DRIVER_CLASS);
        return getConnection(NHFE_LIS_JDBC_DRIVER_CLASS, NHFE_LIS_JDBC_URL, NHFE_LIS_JDBC_USERNAME, NHFE_LIS_JDBC_PASSWORD);
    }
	
    public static Connection getBcConnection(){
        logger.info("LIS_JDBC_DRIVER_CLASS = " +NHFE_BC_JDBC_DRIVER_CLASS);
        return getConnection(NHFE_BC_JDBC_DRIVER_CLASS, NHFE_BC_JDBC_URL, NHFE_BC_JDBC_USERNAME, NHFE_BC_JDBC_PASSWORD);
    }
    
    public static Connection getNjConnection(){
        logger.info("LIS_JDBC_DRIVER_CLASS = " +NHFE_NJ_JDBC_DRIVER_CLASS);
        return getConnection(NHFE_NJ_JDBC_DRIVER_CLASS, NHFE_NJ_JDBC_URL, NHFE_NJ_JDBC_USERNAME, NHFE_NJ_JDBC_PASSWORD);
    }
    //--------------------------------------------------南华附二--------------------------------------------------------------
    
    //--------------------------------------------------黔南州--------------------------------------------------------------
    /**
     * 获取湖北葛洲坝的连接
     * @return
     */
    public static Connection getQNZHisConnection(){
        logger.info("QNZ_HIS_JDBC_DRIVER_CLASS = " +QNZ_HIS_JDBC_DRIVER_CLASS);
        return getConnection(QNZ_HIS_JDBC_DRIVER_CLASS,QNZ_HIS_JDBC_URL,QNZ_HIS_JDBC_USERNAME,QNZ_HIS_JDBC_PASSWORD);
    }
    //--------------------------------------------------黔南州--------------------------------------------------------------
    
    /**
	 * Connection使用完毕,关闭
	 * 此处的Connection是从连接池中拿出来的,关闭Connection实质上是让Connection恢复空闲状态
	 */
	public static void closeConnection() {
		// 尝试从ThreadLocal获取Connection,如果没有,关闭Connection失去意义.
		Connection connection = connectionHolder.get();

		if (connection != null) {
			try {
				connection.close();
				connectionHolder.remove();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("ConnectionManager--closeConnection(SQLException):"+e.getMessage());
			}
		}
	}
	
	/**
     * Connection使用完毕,关闭
     * 此处的Connection是从连接池中拿出来的,关闭Connection实质上是让Connection恢复空闲状态
     */
    public static void closeConnection(Connection connection) {
        // 尝试从ThreadLocal获取Connection,如果没有,关闭Connection失去意义.
        //Connection connection = connectionHolder.get();

        if (connection != null) {
            try {
                connection.close();
                //connectionHolder.remove();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("ConnectionManager--closeConnection(SQLException):"+Exceptions.getStackTraceAsString(e));
            }
        }
    }
}
