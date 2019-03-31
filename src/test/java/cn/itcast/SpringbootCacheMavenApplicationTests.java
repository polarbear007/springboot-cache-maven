package cn.itcast;


import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import cn.itcast.entity.Student;
import cn.itcast.mapper.StudentMapper;
import cn.itcast.service.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootCacheMavenApplicationTests {
	@Autowired
	private StringRedisTemplate stringTemplate;
	
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private SimpleKeyGenerator keyGenerator;
	
	
	// 添加一些测试数据
	@Test
	public void contextLoads() {
		Student stu1 = new Student("小明", 12, "男");
		Student stu2 = new Student("小花", 12, "女");
		Student stu3 = new Student("小黑", 12, "男");
		
		studentMapper.insertSelective(stu1);
		studentMapper.insertSelective(stu2);
		studentMapper.insertSelective(stu3);
	}

	// 查看一下是否会自动创建 cacheManager 对象
	// 如果会的话，那么就打印一下这个 cacheManager 的类型
	@Test
	public void test2() {
		System.out.println(cacheManager.getClass());
	}
	
	// 测试一下 @cacheable 功能
	@Test
	public void test3() {
		Student stu1 = studentService.getStuById(1);
		System.out.println(stu1);
		Student stu2 = studentService.getStuById(1);
		System.out.println(stu2);
	}
	
	// 测试一下 @cacheable 功能
	// keyGenerator 
	@Test
	public void test6() {
		List<Student> list1 = studentService.getStuByNameAndAge("小", 12);
		System.out.println(list1);
		List<Student> list2 = studentService.getStuByNameAndAge("小", 12);
		System.out.println(list2);
	}
	
	// 测试一下 @cacheable 功能
	// 测试一下 condition 参数
	@Test
	public void test7() {
		Student stu1 = studentService.getStuById2(1);
		System.out.println(stu1);
		Student stu2 = studentService.getStuById2(1);
		System.out.println(stu2);
		System.out.println("===============");
		Student stu3 = studentService.getStuById2(5);
		System.out.println(stu3);
		Student stu4 = studentService.getStuById2(5);
		System.out.println(stu4);
	}
	
	// 测试一下 @cacheable 功能
	// 测试一下 unless 参数
	@Test
	public void test8() {
		Student stu1 = studentService.getStuById3(1);
		System.out.println(stu1);
		Student stu2 = studentService.getStuById3(1);
		System.out.println(stu2);
		System.out.println("===============");
		Student stu3 = studentService.getStuById3(2);
		System.out.println(stu3);
		Student stu4 = studentService.getStuById3(2);
		System.out.println(stu4);
	}
	
	
	// 测试一下 @CachePut 功能
	@Test
	public void test4() {
		// 因为我们这里已经事先查询了一次 数据库，保证更新方法中的 stu 参数的完整
		// 所以其实更新方法里面更新完不需要再查询一次
		Student stu1 = studentService.getStuById(1);
		System.out.println(stu1);
		// 第二次查询应该会走缓存，不查询数据库
		Student stu2 = studentService.getStuById(1);
		System.out.println(stu2);
		// 修改stu1 的值，然后执行update 方法， 因为 updateStuById() 方法上面有
		// @CachePut 注解，所以会更新缓存
		stu1.setAge(100);
		studentService.updateStuById(stu1);
		
		// 再一次查询getStuById() 方法，因为有缓存，所以直接返回缓存数据
		// 但是这个缓存数据已经被更新了，是最新的 age = 100 的数据
		Student stu3 = studentService.getStuById(1);
		System.out.println(stu3);
	}
	
	// 测试一下 @CacheEvict 功能
	@Test
	public void test5() {
		// 先查询两个数据，保证有缓存数据
		Student stu1 = studentService.getStuById(1);
		System.out.println(stu1);
		Student stu2 = studentService.getStuById(2);
		System.out.println(stu2);
		
		// 然后删除id = 1 的数据，因为这个方法上面有 @CacheEvict 注解，所以也会删除对应缓存
		studentService.deleteStuById(1);
		
		// 然后我们再次查询数据，看看是否会 @CacheEvict  是否会清除缓存
		Student stu3 = studentService.getStuById(1);
		System.out.println(stu3);
		Student stu4 = studentService.getStuById(2);
		System.out.println(stu4);
	}
}
