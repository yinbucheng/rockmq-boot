package cn.bucheng.rockmqboot.proxy;

import cn.bucheng.rockmqboot.proxy.hash.DefaultHashOperationProxy;
import cn.bucheng.rockmqboot.proxy.hash.DefaultHashOperations;
import cn.bucheng.rockmqboot.proxy.hash.bound.DefaultBoundHashOperations;
import cn.bucheng.rockmqboot.proxy.hash.bound.DefaultBoundHashOperationsProxy;
import cn.bucheng.rockmqboot.proxy.value.DefaultValueOperationProxy;
import cn.bucheng.rockmqboot.proxy.value.DefaultValueOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author ：yinchong
 * @create ：2019/8/7 18:08
 * @description：
 * @modified By：
 * @version:
 */
public class StringRedisTemplateProxy extends StringRedisTemplate {
    private volatile ValueOperations<String, String> valueOps;

    @Override
    public <HK, HV> HashOperations<String, HK, HV> opsForHash() {
        DefaultHashOperations<String, HK, HV> operations = new DefaultHashOperations<>(this);
        return new DefaultHashOperationProxy<>(operations);
    }



    @Override
    public <HK, HV> BoundHashOperations<String, HK, HV> boundHashOps(String key) {
        DefaultBoundHashOperations<String, HK, HV> operations = new DefaultBoundHashOperations<>(key, this);
        return new DefaultBoundHashOperationsProxy<>(operations);
    }


    @Override
    public ValueOperations<String, String> opsForValue() {
        DefaultValueOperations<String, String> operations = new DefaultValueOperations<>(this);
        if (valueOps == null) {
            valueOps = new DefaultValueOperationProxy<String,String>(operations);
        }
        return valueOps;
    }
}
