package com.qt.bean;

public class StuBean {
		
		private int id;
		private String name;
		private int age;
		private String address;
		private String password;
		
		public StuBean() {
			// TODO Auto-generated constructor stub
		}

		public StuBean(int id, String name, int age, String address,
				String password) {
			super();
			this.id = id;
			this.name = name;
			this.age = age;
			this.address = address;
			this.password = password;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
}
