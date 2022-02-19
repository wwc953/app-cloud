package com.example.apputil.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description:
 * @author: wangwc
 * @date: 2020/11/30 10:45
 */
@Component
public class CaffeineCache {

    @Autowired
    Cache<String, Object> cache;

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public void del(String key) {
        cache.invalidate(key);
    }

    public String getString(String key) {
        Object value = cache.getIfPresent(key);
        return value != null ? value.toString() : null;
    }

    public Object getObject(String key) {
        Object value = cache.getIfPresent(key);
        return value;
    }

    public List<Object> getListByKey(String key) {
        Object value = cache.getIfPresent(key);
        return value != null ? (List) value : null;
    }

    public Set<Object> getSetByKey(String key) {
        Object value = cache.getIfPresent(key);
        return value != null ? (Set) value : null;
    }

    public Object hget(String key, String item) {
        Object value = cache.getIfPresent(key);
        if (value == null) {
            return null;
        }
        return ((Map) value).get(item);
    }

    public String hget(String key, String item, String option) {
        return Optional.ofNullable(cache).map(c -> {
            return (Map) c.getIfPresent(key);
        }).map(v -> {
            return (Map) v.get(item);
        }).map(p -> {
            return (String) p.get(option);
        }).orElse(null);
    }

    public Object hgetObject(String key, String item, String option) {
        return Optional.ofNullable(cache).map(c -> {
            return (Map) c.getIfPresent(key);
        }).map(v -> {
            return (Map) v.get(item);
        }).map(p -> {
            return p.get(option);
        }).orElse(null);
    }


    /**
     * 使用较多
     *
     * @param key
     * @param item
     * @param value
     */
    public void hset(String key, String item, Object value) {
        Map map = (Map) cache.getIfPresent(key);
        if (map != null && map.size() > 0) {
            map.put(item, value);
        } else {
            Map itemMap = new HashMap();
            itemMap.put(item, value);
            cache.put(key, itemMap);
        }
    }

    public Object hgetObj(String key, String item, String option) {

        return Optional.ofNullable(cache).map(c -> {
            return c.getIfPresent(key);
        }).map(v -> {
            return ((Map) v).get(item);
        }).map(p -> {
            return ((Map) p).get(option);
        }).orElse(null);
    }

    public void hset(String key, String item, String option, Object value) {
        Map map = (Map) cache.getIfPresent(key);
        HashMap itemMap;
        if (map != null) {
            itemMap = (HashMap) map.get(item);
            if (itemMap != null) {
                itemMap.put(option, value);
            } else {
                itemMap = new HashMap();
                itemMap.put(option, value);
                map.put(item, itemMap);
            }
        } else {
            map = new HashMap<>();
            itemMap = new HashMap();
            itemMap.put(option, value);
            map.put(item, itemMap);
            cache.put(key, map);
        }
    }


    /**
     * 使用较多
     *
     * @param key
     * @param inputMap
     */
    public void hset(String key, Map<String, Object> inputMap) {
        Map map = (Map) cache.getIfPresent(key);
        if (map != null && map.size() > 0) {
            map.putAll(inputMap);
        } else {
            cache.put(key, inputMap);
        }
    }

    public Map<String, Object> hmget(String key) {
        return (Map) cache.getIfPresent(key);
    }

    public Map<String, Object> hmget(String key, String item) {
        return (Map) Optional.ofNullable(cache).map(c -> {
            return (Map) c.getIfPresent(key);
        }).map(v -> {
            return (Map) v.get(item);
        }).orElse(null);
    }

    public void hdel(String key, String[] items) {
        if (items == null || items.length == 0) {
            return;
        }
        Map map = (Map) cache.getIfPresent(key);
        for (int i = 0; i < items.length; i++) {
            map.remove(items[i]);
        }
    }

    public void hdel(String key, String item, String[] options) {
        if (options == null || options.length <= 0) {
            return;
        }

        Map<String, Object> map = (Map) Optional.ofNullable(cache).map(m -> {
            return (Map) m.getIfPresent(key);
        }).map(i -> {
            return (Map) i.get(item);
        }).orElse(null);

        if (map == null || map.size() <= 0) {
            return;
        }

        for (int i = 0; i < options.length; i++) {
            map.remove(options[i]);
        }


    }

    public void delAll() {
        cache.invalidateAll();
    }

}
