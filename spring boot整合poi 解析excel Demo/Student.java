package com.unicom.entity;

import java.io.Serializable;

public class Student implements Serializable {
    private String sno;

    private String sname;

    private String major;

    private String cls;
    
    private String sex;

    private String nation;

    private String tel;

    private String qq;
    
    private String mail;

    private static final long serialVersionUID = 1L;

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "Student [sno=" + sno + ", sname=" + sname + ", major=" + major
				+ ", cls=" + cls + ", sex=" + sex + ", nation=" + nation
				+ ", tel=" + tel + ", qq=" + qq + ", mail=" + mail + "]";
	}

    
}