package com.qt.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class JdbcTemplate {

	
	//  链接数据库参数
	private static String  driver;
	private static String  url;
	private static String  userName;
	private static String  userPs;
	//  链接数据库对象参数
	private  Connection conn;
	private   Statement  st;
	private  PreparedStatement ps;
	private  ResultSet  rs;
	
	//  静态  加载 参数信息   加载驱动  
	static{
		// 得到一个 文件流对象
		InputStream  is  = JdbcTemplate.class.getClassLoader().getResourceAsStream("jdbcParam.properties");
		Properties  pro  = new  Properties();
		try {
			pro.load(is);
			//  给 静态 变量 赋值
			driver  = pro.getProperty("driver");
			url   = pro.getProperty("url");
			userName = pro.getProperty("userName");
			userPs  = pro.getProperty("userPs");
		   //  加载驱动
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("驱动没找到");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 初始化 参数构造函数
	 * 
	 */
	public  JdbcTemplate(){
		
	}
	public JdbcTemplate(String driver, String url, String userName,
			String userPs) {
		this.driver = driver;
		this.url = url;
		this.userName = userName;
		this.userPs = userPs;
		//  加载驱动
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("驱动没找到");
		}
	}
	
	/**
	 *  得到链接的方法
	 */
	public   Connection  getConnection(){
		 try {
			conn =   DriverManager.getConnection(url,userName,userPs);
			conn.setAutoCommit(false);
		 } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("得到链接失败!");
		 }
		 return conn;
	}
	/**
	 *  得到状态通道方法
	 */
	private   void   getStatement(){
		this.getConnection();
		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("得到状态通道失败");
		}
	}
	/**
	 * 得到预状态通道的方法
	 * @param  sql   ?????
	 */
	private  void  getPstatement(String sql){
		this.getConnection();
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("得到预状态通道失败");
		}
	}
	/**
	 * 基于状态通道下数据操作   insert  update  delete'
	 * @param  sql
	 * @return  boolean
	 */
	public  boolean  updateData(String sql){
		  boolean  isok = false;
		  this.getStatement();
		  try {
			int   result =  st.executeUpdate(sql);
			//  受影响行数
			if(result>0){
				this.myCommit();
				isok = true;
			}
		 } catch (SQLException e) {
			e.printStackTrace();
			this.myRollback();
			System.out.println("删除失败");
		 }
		 return  isok;
		  
	}
	/**
	 * 基于预状态通道下的数据操作     insert  ？？？？
	 * @param  sql   模板语句
	 * @param  params  问好参数值
	 * @return boolean
	 */
	public  boolean  updateData(String sql,List params){
		   boolean  isok = false;
		   this.getPstatement(sql);
			  try {
				//  绑定参数
				this.bindParam(params);
				int   result =  ps.executeUpdate();
				//  受影响行数
				if(result>0){
					this.myCommit();
					isok = true;
				}
			 } catch (SQLException e) {
				e.printStackTrace();
				this.myRollback();
				System.out.println("删除失败");
			 }
			 return  isok;
	}
	/**
	 * 基于状态通道下的查询    select    resultSet
	 */
	public  ResultSet    query(String sql){
		  this.getStatement();
		  try {
			rs = st.executeQuery(sql);
		  } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("在状态通道下查询失败");
		  }
		  return  rs;
		  
	}
	/**
	 * 绑定参数
	 * @throws SQLException 
	 */
	private  void  bindParam(List params) throws SQLException{
		if(params != null && !params.isEmpty()){
			for (int i = 0; i <params.size(); i++) {
				ps.setString(i+1,params.get(i).toString());
			}
		}
	}
	/**
	 * 基于预状态通道下的查询    select    resultSet   如果有条件  ？？？？
	 */
	public    ResultSet  query(String sql,List  params){
		this.getPstatement(sql);
		  try {
			  //  绑定参数
			  this.bindParam(params);
			  //
			rs = ps.executeQuery();
		  } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("在状态通道下查询失败");
		  }
		  return  rs;
	}
	/**
	 * 基于状态通道下的批处理    update  insert  delete
	 * @param  sqls  
	 */
	public   boolean  executeBatch(List  sqls){
		   boolean isok = false;
		   this.getStatement();
		   //  sql添加到 批处理缓存中
		   try {
			   for (int i = 0; i < sqls.size(); i++) {
					st.addBatch(sqls.get(i).toString());
			   }
			   //  执行批处理
			   st.executeBatch();
			   this.myCommit();
			   isok = true;
		   } catch (SQLException e) {
			   e.printStackTrace();
			   this.myRollback();
			   System.out.println("在状态通道下 执行批处理失败");
		   }
		   return  isok;
	}
	/**
	 * 基于预状态通道下的批处理    update  insert  delete   ？？？？？
	 */
	public    boolean   executeBatch(String sql,String [][] params){
		   boolean  isok = false;
		   this.getPstatement(sql);
		    try {
			
			   //  把语句绑定参数  在把sql 放在批处理缓存
			   for (int i = 0; i < params.length; i++) {
				   //  绑定？参数
				   for (int  j = 0;  j< params[i].length;j++) {
					      ps.setString(j+1, params[i][j]);
				   }
				   // 添加在缓存
				   ps.addBatch();
			   }
			   //  执行批处理
			   ps.executeBatch();
			   this.myCommit();
			   isok = true;
			} catch (Exception e) {
				e.printStackTrace();
				this.myRollback();
				System.out.println("在预状态通道下 执行批处理失败!");
			}
		    return isok;
	}
	/**
	 * 释放资源
	 */
	public  void   closeRes(){
		try {
			
			if(rs != null){
				rs.close();
			}
			if(st != null){
				st.close();
			}
			if(ps != null){
				ps.close();
			}
			if(conn != null){
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 事物提交
	 */
	public   void  myCommit(){
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 事物的回滚
	 */
	public  void  myRollback(){
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 聚合函数查询
	 * @param   必须是一个 聚合函数查询  max  min  avg....
	 */
	public    int   getNumber(String  sql){
		 rs =  this.query(sql);
		 int  number = 0;
		 try {
			if(rs.next()){
				 number = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("聚合函数查询失败!");
		}
	    return number;
	}
	
}
