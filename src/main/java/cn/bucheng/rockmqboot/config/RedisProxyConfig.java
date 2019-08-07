package cn.bucheng.rockmqboot.config;

import cn.bucheng.rockmqboot.proxy.RedisTemplateProxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author ：yinchong
 * @create ：2019/8/7 18:02
 * @description：
 * @modified By：
 * @version:
 */
//@Configuration
public class RedisProxyConfig {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplateProxy();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
