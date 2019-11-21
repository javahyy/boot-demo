/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package boot.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description: 高性能实体拷贝工具
 */
public class GatBeanUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatBeanUtils.class);

    private static final ConcurrentMap<String, BeanCopier> BEAN_COPIER_CONCURRENT_MAP = new ConcurrentHashMap<>();

    /**
     * 用做缓存 来存放类对应的所有字段
     */
    private static final Map<String, List<Field>> CACHE_BEAN_FIELDS = new ConcurrentHashMap<>();

    /**
     * 用于存储bean copier
     */
    private static ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_HASH_MAP = new ConcurrentHashMap<>();

    public static void copyProperties(Object dest, Object src) {
        if (null == src) {
            return;
        }
        String key = dest.getClass().getName() + src.getClass().getName();
        BeanCopier beanCopier = BEAN_COPIER_CONCURRENT_MAP.get(key);
        if (beanCopier == null) {
            BeanCopier newBeanCopier = BeanCopier.create(src.getClass(), dest.getClass(), false);
            beanCopier = BEAN_COPIER_CONCURRENT_MAP.putIfAbsent(key, newBeanCopier);
            if (null == beanCopier) {
                beanCopier = newBeanCopier;
            }
        }
        try {
            beanCopier.copy(src, dest, null);
        } catch (Exception e) {
            LOGGER.error("from " + src.getClass() + "[" + src + "] to " + dest.getClass() + " [" + dest + "]", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param t 目标类
     * @param r 资源类
     * @return
     */
    public static <T, R> T copy(Class<T> t, R r) {
        if (r == null) {
            return null;
        }
        String key = r.getClass().getName() + t.getName();
        BeanCopier beanCopier = BEAN_COPIER_HASH_MAP.get(key);
        if (null == beanCopier) {
            BeanCopier copier = BeanCopier.create(r.getClass(), t, false);
            beanCopier = BEAN_COPIER_HASH_MAP.putIfAbsent(key, copier);
            if (null == beanCopier) {
                beanCopier = copier;
            }
        }
        T target = null;
        try {
            target = t.getDeclaredConstructor(t.getClasses()).newInstance();
            beanCopier.copy(r, target, null);
        } catch (Exception e) {
            LOGGER.error("GatBeanUtils cglib copy error:{}", e);
            throw new RuntimeException(e);
        }
        return target;
    }


    /**
     * @param t            目标类对象
     * @param resourceList 资源对象列表
     * @return
     */
    public static <T, R> List<T> copyList(Class<T> t, List<R> resourceList) {
        if (resourceList == null || resourceList.size() == 0) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (R resource : resourceList) {
            result.add(copy(t, resource));
        }
        return result;
    }

    /**
     * 对象转map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        List<Field> fieldList = findAllFieldsOfClass(obj.getClass());
        try {
            for (Field field : fieldList) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            throw new RuntimeException("BeanUtil objectToMap error");
        }
        return map;
    }

    /**
     * 获取对象所有的字段（自己和所有父类）
     *
     * @param clazz
     * @return
     */
    public static List<Field> findAllFieldsOfClass(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Field[] fields = null;
        String key = clazz.getName();
        //先从缓存中获取类对应的字段列表
        List<Field> fieldList = CACHE_BEAN_FIELDS.get(key);
        if (fieldList != null) {
            return fieldList;
        }
        //缓存中没有则再循环获取这个类所有的字段（包括父类）
        fieldList = new ArrayList<>();
        while (true) {
            if (clazz == null) {
                break;
            } else {
                fields = clazz.getDeclaredFields();
                if (fields != null && fields.length > 0) {
                    for (int i = 0; i < fields.length; i++) {
                        fieldList.add(fields[i]);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        CACHE_BEAN_FIELDS.putIfAbsent(key, fieldList);
        return fieldList;
    }

    public static Map<String, Object> mergeMap(Map<String, Object> dataMap, Map<String, Object> mergeMap) {

        if (dataMap == null) {
            return null;
        }

        LinkedHashMap<String, Object> addedMap = new LinkedHashMap<>();

        if (mergeMap != null && !mergeMap.isEmpty()) {
            Iterator<String> mergeIterator = mergeMap.keySet().iterator();
            while (mergeIterator.hasNext()) {
                String key = mergeIterator.next();
                Object obj = mergeMap.get(key);
                if (dataMap.containsKey(key)) {
                    dataMap.put(key, obj);
                } else {
                    addedMap.put(key, obj);
                }
            }

            if (!addedMap.isEmpty()) {
                Iterator<String> addedIterator = addedMap.keySet().iterator();
                while (addedIterator.hasNext()) {
                    String key = addedIterator.next();
                    dataMap.put(key, addedMap.get(key));
                }
            }
        }
        return dataMap;
    }

    public static Map<String, String> parseQueryStrToMap(String str) {

        if (StringUtils.isBlank(str)) {
            return null;
        }

        Map<String, String> map = new LinkedHashMap<>();
        String[] rootSplitArray = str.split("&");

        for (String s : rootSplitArray) {
            String[] pair = s.split("=");
            if (pair.length == 1) {
                map.put(pair[0], null);
            } else {
                try {
                    map.put(pair[0], URLDecoder.decode(pair[1], "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return map;
    }

}
