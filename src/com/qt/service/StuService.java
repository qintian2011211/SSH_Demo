package com.qt.service;

import com.qt.bean.StuBean;
import com.qt.dao.StuDao;

public class StuService {
		private StuDao sd;
		public StuBean  logins(String name,String password) {
			StuBean sb=sd.login(name, password);
			return sb;
		}
		public StuDao getSd() {
			return sd;
		}
		public void setSd(StuDao sd) {
			this.sd = sd;
		}
		
		
		
}
