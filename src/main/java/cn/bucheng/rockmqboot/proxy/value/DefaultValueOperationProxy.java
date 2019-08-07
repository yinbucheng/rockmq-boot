package cn.bucheng.rockmqboot.proxy.value;

import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/7 19:00
 * @description：
 * @modified By：
 * @version:
 */
public class DefaultValueOperationProxy<K,V> implements ValueOperations<K, V> {

    private ValueOperations<K,V> proxy;

    public DefaultValueOperationProxy(ValueOperations<K, V> proxy) {
        this.proxy = proxy;
    }

    @Override
    public void set(K key, V value) {
        proxy.set(key,value);
    }

    @Override
    public void set(K key, V value, long timeout, TimeUnit unit) {
           proxy.set(key,value,timeout,unit);
    }

    @Override
    public Boolean setIfAbsent(K key, V value) {
        return proxy.setIfAbsent(key,value);
    }

    @Override
    public Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit) {
        return proxy.setIfAbsent(key,value,timeout,unit);
    }

    @Override
    public Boolean setIfPresent(K key, V value) {
        return proxy.setIfPresent(key,value);
    }

    @Override
    public Boolean setIfPresent(K key, V value, long timeout, TimeUnit unit) {
        return proxy.setIfPresent(key,value,timeout,unit);
    }

    @Override
    public void multiSet(Map<? extends K, ? extends V> map) {
          proxy.multiSet(map);
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map) {
        return proxy.multiSetIfAbsent(map);
    }

    @Override
    public V get(Object key) {
        return proxy.get(key);
    }

    @Override
    public V getAndSet(K key, V value) {
        return proxy.getAndSet(key,value);
    }

    @Override
    public List<V> multiGet(Collection<K> keys) {
        return proxy.multiGet(keys);
    }

    @Override
    public Long increment(K key) {
        return proxy.increment(key);
    }

    @Override
    public Long increment(K key, long delta) {
        return proxy.increment(key,delta);
    }

    @Override
    public Double increment(K key, double delta) {
        return proxy.increment(key,delta);
    }

    @Override
    public Long decrement(K key) {
        return proxy.decrement(key);
    }

    @Override
    public Long decrement(K key, long delta) {
        return proxy.decrement(key,delta);
    }

    @Override
    public Integer append(K key, String value) {
        return proxy.append(key,value);
    }

    @Override
    public String get(K key, long start, long end) {
        return proxy.get(key,start,end);
    }

    @Override
    public void set(K key, V value, long offset) {
          proxy.set(key,value, offset);
    }

    @Override
    public Long size(K key) {
        return proxy.size(key);
    }

    @Override
    public Boolean setBit(K key, long offset, boolean value) {
        return proxy.setBit(key,offset,value);
    }

    @Override
    public Boolean getBit(K key, long offset) {
        return proxy.getBit(key,offset);
    }

    @Override
    public List<Long> bitField(K key, BitFieldSubCommands subCommands) {
        return proxy.bitField(key,subCommands);
    }

    @Override
    public RedisOperations<K, V> getOperations() {
        return proxy.getOperations();
    }
}
