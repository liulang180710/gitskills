package org.my.springcloud.producer.config;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    //配置redis的过期时间
    private static final Duration TIME_TO_LIVE = Duration.ZERO;
    private static final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();
    private static final GenericJackson2JsonRedisSerializer JSON_SERIALIZER = new GenericJackson2JsonRedisSerializer();

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig())
                .build();
        // 单机redis
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName("127.0.0.1");
        redisConfig.setPort(6379);

        return new LettuceConnectionFactory(redisConfig, lettucePoolingClientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        //key的序列方式
        template.setKeySerializer(STRING_SERIALIZER);
        //hashKey的序列方式
        template.setHashKeySerializer(STRING_SERIALIZER);
        //value的序列方式
        template.setValueSerializer(JSON_SERIALIZER);
        //value hashmap序列化
        template.setHashValueSerializer(JSON_SERIALIZER);
        return template;
    }



    @Bean
    public GenericObjectPoolConfig poolConfig(){
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        //控制一个pool可分配多少个jedis实例
        poolConfig.setMaxTotal(500);
        //最大空闲数
        poolConfig.setMaxIdle(200);
        //每次释放连接的最大数目，默认是3
        poolConfig.setNumTestsPerEvictionRun(1024);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setJmxEnabled(true);
        poolConfig.setBlockWhenExhausted(false);
        return poolConfig;
    }


    /**
     * 缓存对象集合中，缓存是以key-value形式保存的,
     * 当不指定缓存的key时，SpringBoot会使用keyGenerator生成Key。
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                //类名+方法名
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 缓存管理器
     * @param lettuceConnectionFactory 连接工厂
     * @return  cacheManager
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        //新建一个Jackson2JsonRedis的redis存储的序列化方式
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //解决查询缓存转换异常的问题
        // 配置序列化（解决乱码的问题）
        //entryTtl设置过期时间
        //serializeValuesWith设置redis存储的序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(TIME_TO_LIVE)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer))
                .disableCachingNullValues();
        //定义要返回的redis缓存管理对象
        return RedisCacheManager.builder(lettuceConnectionFactory).cacheDefaults(config).build();
    }
}
