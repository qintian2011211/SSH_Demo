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

	
	//  �������ݿ����
	private static String  driver;
	private static String  url;
	private static String  userName;
	private static String  userPs;
	//  �������ݿ�������
	private  Connection conn;
	private   Statement  st;
	private  PreparedStatement ps;
	private  ResultSet  rs;
	
	//  ��̬  ���� ������Ϣ   ��������  
	static{
		// �õ�һ�� �ļ�������
		InputStream  is  = JdbcTemplate.class.getClassLoader().getResourceAsStream("jdbcParam.properties");
		Properties  pro  = new  Properties();
		try {
			pro.load(is);
			//  �� ��̬ ���� ��ֵ
			driver  = pro.getProperty("driver");
			url   = pro.getProperty("url");
			userName = pro.getProperty("userName");
			userPs  = pro.getProperty("userPs");
		   //  ��������
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("����û�ҵ�");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** ��ʼ�� �������캯��
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
		//  ��������
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("����û�ҵ�");
		}
	}
	
	/**
	 *  �õ����ӵķ���
	 */
	public   Connection  getConnection(){
		 try {
			conn =   DriverManager.getConnection(url,userName,userPs);
			conn.setAutoCommit(false);
		 } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�õ�����ʧ��!");
		 }
		 return conn;
	}
	/**
	 *  �õ�״̬ͨ������
	 */
	private   void   getStatement(){
		this.getConnection();
		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�õ�״̬ͨ��ʧ��");
		}
	}
	/**
	 * �õ�Ԥ״̬ͨ���ķ���
	 * @param  sql   ?????
	 */
	private  void  getPstatement(String sql){
		this.getConnection();
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�õ�Ԥ״̬ͨ��ʧ��");
		}
	}
	/**
	 * ����״̬ͨ�������ݲ���   insert  update  delete'
	 * @param  sql
	 * @return  boolean
	 */
	public  boolean  updateData(String sql){
		  boolean  isok = false;
		  this.getStatement();
		  try {
			int   result =  st.executeUpdate(sql);
			//  ��Ӱ������
			if(result>0){
				this.myCommit();
				isok = true;
			}
		 } catch (SQLException e) {
			e.printStackTrace();
			this.myRollback();
			System.out.println("ɾ��ʧ��");
		 }
		 return  isok;
		  
	}
	/**
	 * ����Ԥ״̬ͨ���µ����ݲ���     insert  ��������
	 * @param  sql   ģ�����
	 * @param  params  �ʺò���ֵ
	 * @return boolean
	 */
	public  boolean  updateData(String sql,List params){
		   boolean  isok = false;
		   this.getPstatement(sql);
			  try {
				//  �󶨲���
				this.bindParam(params);
				int   result =  ps.executeUpdate();
				//  ��Ӱ������
				if(result>0){
					this.myCommit();
					isok = true;
				}
			 } catch (SQLException e) {
				e.printStackTrace();
				this.myRollback();
				System.out.println("ɾ��ʧ��");
			 }
			 return  isok;
	}
	/**
	 * ����״̬ͨ���µĲ�ѯ    select    resultSet
	 */
	public  ResultSet    query(String sql){
		  this.getStatement();
		  try {
			rs = st.executeQuery(sql);
		  } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��״̬ͨ���²�ѯʧ��");
		  }
		  return  rs;
		  
	}
	/**
	 * �󶨲���
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
	 * ����Ԥ״̬ͨ���µĲ�ѯ    select    resultSet   ���������  ��������
	 */
	public    ResultSet  query(String sql,List  params){
		this.getPstatement(sql);
		  try {
			  //  �󶨲���
			  this.bindParam(params);
			  //
			rs = ps.executeQuery();
		  } catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��״̬ͨ���²�ѯʧ��");
		  }
		  return  rs;
	}
	/**
	 * ����״̬ͨ���µ�������    update  insert  delete
	 * @param  sqls  
	 */
	public   boolean  executeBatch(List  sqls){
		   boolean isok = false;
		   this.getStatement();
		   //  sql��ӵ� ����������
		   try {
			   for (int i = 0; i < sqls.size(); i++) {
					st.addBatch(sqls.get(i).toString());
			   }
			   //  ִ��������
			   st.executeBatch();
			   this.myCommit();
			   isok = true;
		   } catch (SQLException e) {
			   e.printStackTrace();
			   this.myRollback();
			   System.out.println("��״̬ͨ���� ִ��������ʧ��");
		   }
		   return  isok;
	}
	/**
	 * ����Ԥ״̬ͨ���µ�������    update  insert  delete   ����������
	 */
	public    boolean   executeBatch(String sql,String [][] params){
		   boolean  isok = false;
		   this.getPstatement(sql);
		    try {
			
			   //  �����󶨲���  �ڰ�sql ������������
			   for (int i = 0; i < params.length; i++) {
				   //  �󶨣�����
				   for (int  j = 0;  j< params[i].length;j++) {
					      ps.setString(j+1, params[i][j]);
				   }
				   // ����ڻ���
				   ps.addBatch();
			   }
			   //  ִ��������
			   ps.executeBatch();
			   this.myCommit();
			   isok = true;
			} catch (Exception e) {
				e.printStackTrace();
				this.myRollback();
				System.out.println("��Ԥ״̬ͨ���� ִ��������ʧ��!");
			}
		    return isok;
	}
	/**
	 * �ͷ���Դ
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
	 * �����ύ
	 */
	public   void  myCommit(){
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ����Ļع�
	 */
	public  void  myRollback(){
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * �ۺϺ�����ѯ
	 * @param   ������һ�� �ۺϺ�����ѯ  max  min  avg....
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
			System.out.println("�ۺϺ�����ѯʧ��!");
		}
	    return number;
	}
	
}
