package cn.bucheng.rockmqboot.proxy;


import cn.bucheng.rockmqboot.proxy.hash.DefaultHashOperationProxy;
import cn.bucheng.rockmqboot.proxy.hash.DefaultHashOperations;
import cn.bucheng.rockmqboot.proxy.hash.bound.DefaultBoundHashOperations;
import cn.bucheng.rockmqboot.proxy.hash.bound.DefaultBoundHashOperationsProxy;
import cn.bucheng.rockmqboot.proxy.value.DefaultValueOperationProxy;
import cn.bucheng.rockmqboot.proxy.value.DefaultValueOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author ：yinchong
 * @create ：2019/8/7 16:46
 * @description：
 * @modified By：
 * @version:
 */
public class RedisTemplateProxy<K, V> extends RedisTemplate<K, V> {

    private volatile ValueOperations<K, V> valueOps;


    @Override
    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        DefaultHashOperations<K, HK, HV> operations = new DefaultHashOperations<>(this);
        return new DefaultHashOperationProxy<>(operations);
    }

    @Override
    public <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
        DefaultBoundHashOperations<K, HK, HV> operations = new DefaultBoundHashOperations<>(key, this);
        return new DefaultBoundHashOperationsProxy<>(operations);
    }

    @Override
    public ValueOperations<K, V> opsForValue() {
        DefaultValueOperations<K, V> operations = new DefaultValueOperations<>(this);
        if (valueOps == null) {
            valueOps = new DefaultValueOperationProxy<>(operations);
        }
        return valueOps;
    }
}
