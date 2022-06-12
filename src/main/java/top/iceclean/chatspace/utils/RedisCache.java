package top.iceclean.chatspace.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author : Ice'Clean
 * @date : 2022-01-29
 *
 * Redis 工具类
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 获得缓存的基本对象。
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     * @param key   键值
     * @return  删除是否成功
     */
    public boolean deleteObject(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 删除集合对象
     * @param collection 多个对象
     * @return  删除对象的数量
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     * @param key 键值
     * @return  缓存的set
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     * @param key 键值
     * @param dataMap 缓存的Map
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     * @param key 键值
     * @return  缓存的Map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置 hash 值
     * @param key   键
     * @param field 域
     * @param value 值
     */
    public void hashSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    /** 重载方法，在新建时指定失效时间 */
    public void hashSet(String key, String field, Object value, long time) {
        redisTemplate.opsForHash().put(key, field, value);
        expire(key, time);
    }

    /**
     * 获取指定 key 的一个 hash 值
     * @param key   键
     * @param field 域
     * @return 值
     */
    public Object hashGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取指定 key 全部的 hash 值
     * @param key 建
     * @return 全部的值 Map
     */
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除 hash 指定域的值
     * @param key   键
     * @param field 域 可以多个
     */
    public Long hashDel(String key, Object... field) {
        return redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 获取多个Hash中的数据
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 写入位图
     * @param key 位图键
     * @param offset 偏移量
     * @param value 对应的值（T/F -> 0/1）
     */
    public void setBit(String key, int offset, boolean value) {
        redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 获取位图值
     * @param key 位图键
     * @param offset 位图偏移量
     * @return 对应的值，null 表示不存在该键值对，否则为 true/false
     */
    public Boolean getBit(String key, int offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 计算位图中 1 的数量
     * @param key 位图键
     * @param start 开始位
     * @param end 结束位
     * @return 数量
     */
    public long countBit(String key, int start, int end) {
        Object execute = redisTemplate.execute((RedisCallback) con -> con.bitCount(key.getBytes(), start, end));
        return execute == null ? 0 : (long) execute;
    }
    public long countBit(String key) {
        Object execute = redisTemplate.execute((RedisCallback) con -> con.bitCount(key.getBytes()));
        return execute == null ? 0 : (long) execute;
    }


    /**
     * 向 Set 中批量添加元素
     * @param key     键
     * @param dataSet 要添加的元素集合
     */
    public <T> void setAddAll(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
    }

    /**
     * 向 Set 中添加一个值
     * @param key 键
     * @param data 值
     */
    public <T> void setAdd(final String key, final T data) {
        redisTemplate.boundSetOps(key).add(data);
    }

    /**
     * 获取 Set 所有成员
     * @param key 键
     * @return Set 成员集合
     */
    public <T> Set<T> getMembers(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 批量移除 Set 中的元素
     * @param key 键
     * @param dataSet 要移除的元素集合
     */
    public <T> void setRemoveAll(final String key, final Set<T> dataSet) {
        redisTemplate.opsForSet().remove(key, dataSet.toArray());
    }

    /**
     * 移除 Set 中指定元素
     * @param key 键
     * @param data 值
     */
    public <T> void setRemove(final String key, T data) {
        redisTemplate.opsForSet().remove(key, data);
    }

    /**
     * 删除任意一个键的全部数据
     * @param key 键
     */
    public void del(final String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除任意一个键的全部数据
     * @param keys 键集合
     */
    public void del(final Collection<String> keys) {
        redisTemplate.delete(keys);
    }
}