package cn.bucheng.rockmqboot.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/***
 * 配合全局结果处理器使用，解决controller返回string类型报错的问题
 */
@Configuration
public class ServerWebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
           converters.clear();
           converters.add(new FastJsonHttpMessageConverter());
    }
}