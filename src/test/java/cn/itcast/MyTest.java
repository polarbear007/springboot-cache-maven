package cn.itcast;

import org.junit.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class MyTest {
	@Test
	public void test() {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				  .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
				  .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		
		System.out.println(config);
	}
}
