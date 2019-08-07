package cn.bucheng.rockmqboot.proxy.hash.bound;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/7 18:15
 * @description：
 * @modified By：
 * @version:
 */
public class DefaultBoundHashOperationsProxy<H, HK, HV> implements BoundHashOperations<H, HK, HV> {
    private BoundHashOperations<H, HK, HV> proxy;

    public DefaultBoundHashOperationsProxy(BoundHashOperations<H, HK, HV> proxy) {
        this.proxy = proxy;
    }

    @Override
    public Long delete(Object... keys) {
        return proxy.delete(keys);
    }

    @Override
    public Boolean hasKey(Object key) {
        return proxy.hasKey(key);
    }

    @Override
    public HV get(Object member) {
        return proxy.get(member);
    }

    @Override
    public List<HV> multiGet(Collection<HK> keys) {
        return proxy.multiGet(keys);
    }

    @Override
    public Long increment(HK key, long delta) {
        return proxy.increment(key,delta);
    }

    @Override
    public Double increment(HK key, double delta) {
        return proxy.increment(key,delta);
    }

    @Override
    public Set<HK> keys() {
        return proxy.keys();
    }

    @Override
    public Long lengthOfValue(HK hashKey) {
        return proxy.lengthOfValue(hashKey);
    }

    @Override
    public Long size() {
        return proxy.size();
    }

    @Override
    public void putAll(Map<? extends HK, ? extends HV> m) {
            proxy.putAll(m);
    }

    @Override
    public void put(HK key, HV value) {
         proxy.put(key,value);
    }

    @Override
    public Boolean putIfAbsent(HK key, HV value) {
        return proxy.putIfAbsent(key,value);
    }

    @Override
    public List<HV> values() {
        return proxy.values();
    }

    @Override
    public Map<HK, HV> entries() {
        return proxy.entries();
    }

    @Override
    public Cursor<Map.Entry<HK, HV>> scan(ScanOptions options) {
        return proxy.scan(options);
    }

    @Override
    public RedisOperations<H, ?> getOperations() {
        return proxy.getOperations();
    }

    @Override
    public H getKey() {
        return proxy.getKey();
    }

    @Override
    public DataType getType() {
        return proxy.getType();
    }

    @Override
    public Long getExpire() {
        return proxy.getExpire();
    }

    @Override
    public Boolean expire(long timeout, TimeUnit unit) {
        return proxy.expire(timeout,unit);
    }

    @Override
    public Boolean expireAt(Date date) {
        return proxy.expireAt(date);
    }

    @Override
    public Boolean persist() {
        return proxy.persist();
    }

    @Override
    public void rename(H newKey) {
            proxy.rename(newKey);
    }
}
