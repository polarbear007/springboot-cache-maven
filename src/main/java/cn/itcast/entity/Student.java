package cn.itcast.entity;

import java.io.Serializable;

public class Student implements Serializable{
	private static final long serialVersionUID = 7118151581722691693L;

	private Integer sid;

    private String sname;

    private Integer age;

    private String gender;
    
    public Student(String sname, Integer age, String gender) {
		super();
		this.sname = sname;
		this.age = age;
		this.gender = gender;
	}

	public Student() {
		super();
	}

	public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname == null ? null : sname.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

	@Override
	public String toString() {
		return "Student [sid=" + sid + ", sname=" + sname + ", age=" + age + ", gender=" + gender + "]";
	}
    
}