package cn.bucheng.rockmqboot.proxy.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ：yinchong
 * @create ：2019/8/7 16:52
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class DefaultHashOperationProxy<K, HK, HV> implements HashOperations<K, HK, HV> {

    private HashOperations<K, HK, HV> proxy;

    public DefaultHashOperationProxy(HashOperations<K, HK, HV> proxy) {
        this.proxy = proxy;
    }

    @Override
    public Long delete(K key, Object... hashKeys) {
        return proxy.delete(key, hashKeys);
    }

    @Override
    public Boolean hasKey(K key, Object hashKey) {
        return proxy.hasKey(key, hashKey);
    }

    @Override
    public HV get(K key, Object hashKey) {
        return proxy.get(key, hashKey);
    }

    @Override
    public List<HV> multiGet(K key, Collection<HK> hashKeys) {
        return proxy.multiGet(key, hashKeys);
    }

    @Override
    public Long increment(K key, HK hashKey, long delta) {
        return proxy.increment(key, hashKey, delta);
    }

    @Override
    public Double increment(K key, HK hashKey, double delta) {
        return proxy.increment(key, hashKey, delta);
    }

    @Override
    public Set<HK> keys(K key) {
        return proxy.keys(key);
    }

    @Override
    public Long lengthOfValue(K key, HK hashKey) {
        return proxy.lengthOfValue(key, hashKey);
    }

    @Override
    public Long size(K key) {
        return proxy.size(key);
    }

    @Override
    public void putAll(K key, Map<? extends HK, ? extends HV> m) {
        proxy.putAll(key, m);
    }

    @Override
    public void put(K key, HK hashKey, HV value) {
        proxy.put(key, hashKey, value);
    }

    @Override
    public Boolean putIfAbsent(K key, HK hashKey, HV value) {
        return proxy.putIfAbsent(key, hashKey, value);
    }

    @Override
    public List<HV> values(K key) {
        return proxy.values(key);
    }

    @Override
    public Map<HK, HV> entries(K key) {
        return proxy.entries(key);
    }

    @Override
    public Cursor<Map.Entry<HK, HV>> scan(K key, ScanOptions options) {
        return proxy.scan(key, options);
    }

    @Override
    public RedisOperations<K, ?> getOperations() {
        return proxy.getOperations();
    }
}
