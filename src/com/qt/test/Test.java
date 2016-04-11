package com.qt.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qt.bean.StuBean;
import com.qt.service.StuService;

public class Test {
	
		public static void main(String[] args) {
				
				ApplicationContext ac=new ClassPathXmlApplicationContext("applicationContext.xml");
				StuService ss=(StuService) ac.getBean("stuS");
				StuBean sb=ss.logins("ÇØÌì","123");
				System.out.println(sb.getId());
		}
}
