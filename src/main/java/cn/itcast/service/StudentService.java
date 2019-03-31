package cn.itcast.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.entity.Student;
import cn.itcast.entity.StudentExample;
import cn.itcast.mapper.StudentMapper;

@Service
public class StudentService {
	@Autowired
	private StudentMapper studentMapper;
	
	@Cacheable(cacheNames= {"stu"}, key="#a0")
	public Student getStuById(Integer sid) {
		return studentMapper.selectByPrimaryKey(sid);
	}
	
	@Cacheable(cacheNames= {"stuListByNameAndAge"}, keyGenerator="keyGenerator")
	public List<Student> getStuByNameAndAge(String sname, Integer age) {
		StudentExample example = new StudentExample();
		example.createCriteria().andSnameLike("%" + sname + "%").andAgeEqualTo(age);
		return studentMapper.selectByExample(example);
	}
	
	// 只有当查询的学生对象id大于3的时候，才缓存数据
	@Cacheable(cacheNames= {"stu"}, key="#sid", condition="#sid > 3")
	public Student getStuById2(Integer sid) {
		return studentMapper.selectByPrimaryKey(sid);
	}
	
	// 只有当查询的结果不是女生的时候才缓存数据
	@Cacheable(cacheNames= {"stu"}, key="#sid", unless="#result.gender == '女'")
	public Student getStuById3(Integer sid) {
		return studentMapper.selectByPrimaryKey(sid);
	}
	
	// 使用这个注解的方法，不管你设置什么条件，都会执行这个方法
	// 如果你的条件成立的话，会把这个方法的返回值放入缓存。
	// 一般来说，这个注解主要作用是更新数据库的数据以后，去更新缓存数据
	// 如果我们想要更新缓存数据，那么必须返回这个新的实体对象
	@Transactional
	@CachePut(cacheNames= {"stu"}, key="#result.sid", condition="#result != null")
	public Student updateStuById(Student stu) {
		int result = studentMapper.updateByPrimaryKeySelective(stu);
		if(result == 0) {
			return null;
		}else {
			// 可是这个新的 stu 对象又是不完整的，所以我们还需要把这个对象的属性补全了才可以
			// 所以我们最好去数据库再查一次最新的数据，然后缓存这个最新的数据
			// stu = studentMapper.selectByPrimaryKey(stu.getSid());
			return stu;
		}
	}
	

	@Transactional
	@CacheEvict(cacheNames= {"stu"}, key="#sid")
	public void deleteStuById(Integer sid) {
		studentMapper.deleteByPrimaryKey(sid);
	}
}
