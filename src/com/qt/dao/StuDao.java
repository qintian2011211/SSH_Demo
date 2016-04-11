package com.qt.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qt.bean.StuBean;
import com.qt.util.JdbcTemplate;

public class StuDao {
			private JdbcTemplate jdbc=new JdbcTemplate();
			
			public StuBean login(String name,String password){
				
				String sql="select * from stu where name=? and password =?";
				List params=new ArrayList<>();
				params.add(name);
				params.add(password);
				ResultSet rs=jdbc.query(sql, params);
				StuBean sb=null;
				try {
					if(rs.next()){
							sb=new StuBean(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return sb;
			}
}
