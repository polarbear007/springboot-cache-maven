package cn.itcast.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {
	// 自定义配置一个keyGenerator
	@Bean
	public SimpleKeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}
	
	// 默认情况下，如果我们使用 redis 作为缓存的话，默认的值序列化器是使用JDK序列化
	// 这种情况下，保存的数据几乎是无法阅读的。
	// 如果我们想要改成json 序列化的话，那么就要使用下面的方式来修改
	
	// 如果我们不是自己来配置 RedisCacheManager 的话，那么只要配置了RedisCacheConfiguration
	// 到时就会自动注入到 RedisCacheManager 中去了。
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration() {
		// 首先，我们还是使用默认配置，只需要改动我们想要改动的地方
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				// 设置缓存的序列化器,这是最最常用的【注意 链式编程】
				// 其实key 的默认序列化器就是字符串，可以不用改的
				.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		
		// 如果我们不使用链式调用的话，那么一定要记得把 config 重新赋值
		// config = config.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()));
		// config = config.serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		
		// 【注意】下面的这些配置一般不改
		//       而且这些配置都可以直接在 application.properties 里面通过  spring.cache.redis.xxx 配置
		// 开启前缀
		//config.usePrefix();
		
		// 默认开启，可以使用下面的方法取消使用前缀====> 建议不要这么去改，因为很可能会出现缓存覆盖问题
		//config.disableKeyPrefix();
		
		// 设置缓存的的前缀, 一般我们是不设置的，默认使用的是    缓存名::
		// config.prefixKeysWith(prefix);
		
		// 设置缓存默认的生命周期，默认是 0 ,也就是永远存在 
		// config.entryTtl(ttl)
		
		// 禁止缓存  null 值
		// config.disableCachingNullValues();
		
		// 允许缓存  null 值
		//config.getAllowCacheNullValues();
		return config;
	}
	
	// 默认情况下，系统会根据我们导入的jar 包来自动为我们配置一个 CacheManager
	// 如果我们不满意的话，也可以自己来创建这个对象
	
	// 我们这里其实没有什么可以配置的，就是演示一下如何来创建和配置
	//  RedisCacheWriter、 RedisCacheConfiguration、 RedisCacheManager
	@Bean
	public CacheManager cacheManager(@Autowired RedisConnectionFactory connectionFactory) {
		// 创建RedisCacheManager 的几种方法
		// 1、 自己new 一个       new RedisCacheManager(cacheWriter, defaultCacheConfiguration)
		// 2、 使用静态方法创建
		//          RedisCacheManager.create(connectionFactory)
		//          RedisCacheManager.builder(connectionFactory)
		//          RedisCacheManager.builder(connectionFactory)
		
		// 这个 RedisCacheManager  能配置的无非就三个对象：
		//    1、 CacheWriter       // 提供读、写redis 数据库的方法
		//    2、 RedisCacheConfiguration   // 上面已经有讲过了，配置序列化器、前缀等。
		//    3、 RedisConnectionFactory    // 可以配置连接哪个redis 服务器、连接池、使用什么redis 客户端等
		
		// 我们这里没有什么可以配置的，所以就直接返回一个默认的配置。
		RedisCacheWriter cacheWriter = RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
		return new RedisCacheManager(cacheWriter, redisCacheConfiguration());
	}
	
}
