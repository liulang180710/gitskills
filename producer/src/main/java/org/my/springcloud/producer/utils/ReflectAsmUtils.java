package org.my.springcloud.producer.utils;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ASM反射工具类
 * @author huangchen
 * @date 2020-09-30 11:53
 */
public class ReflectAsmUtils {

    private ReflectAsmUtils() {}

    /**
     * 方法缓存
     * 类名（class.getName）+ methodName + paramTypeClassNames -> Method
     */
    private static final Map<String, MethodAccess> METHOD_ACCESS_CACHE_MAP = new ConcurrentHashMap<String, MethodAccess>();

    /**
     * 方法索引缓存
     * 类名（class.getName）+ methodName + paramTypeClassNames -> MethodIndex
     */
    private static final Map<String, Integer> METHOD_INDEX_CACHE_MAP = new ConcurrentHashMap<String, Integer>();

    /**
     * 属性缓存
     * 类名 (class.getName) + filedName -> fieldAccess
     */
    private static final Map<String, FieldAccess> FIELD_ACCESS_CACHE_MAP = new ConcurrentHashMap<String, FieldAccess>();

    /**
     * 属性索引缓存
     * 类名 (class.getName) + filedName -> fieldIndex
     */
    private static final Map<String, Integer> FIELD_INDEX_CACHE_MAP = new ConcurrentHashMap<String, Integer>();

    /**
     * 执行方法并获取返回值（不带参数）
     * @param obj 实例对象
     * @param methodName 方法名
     * @return 方法返回值
     */
    public static Object invokeMethod(Object obj, String methodName) {
        return invokeMethod(obj, methodName, new Class<?>[0], new Object[0]);
    }

    /**
     * 执行方法并获取返回值
     * @param obj 实例对象
     * @param methodName 方法名
     * @param paramTypeList 参数类型数组
     * @param params 参数值
     * @return 方法返回值
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypeList, Object[] params) {
        String key = buildKey(obj.getClass().getName(), methodName, paramTypeList);
        if (!METHOD_ACCESS_CACHE_MAP.containsKey(key)) {
            MethodAccess methodAccess = MethodAccess.get(obj.getClass());
            if (paramTypeList == null) {
                paramTypeList = new Class<?>[0];
            }
            int methodIndex = methodAccess.getIndex(methodName, paramTypeList);
            METHOD_ACCESS_CACHE_MAP.put(key, methodAccess);
            METHOD_INDEX_CACHE_MAP.put(key, methodIndex);
        }
        MethodAccess method = METHOD_ACCESS_CACHE_MAP.get(key);
        int index = METHOD_INDEX_CACHE_MAP.get(key);
        return method.invoke(obj, index, params);
    }

    /**
     * 执行静态方法（不带参数）
     * @param clazz 类定义
     * @param methodName 方法名
     * @return 方法返回值
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName) {
        return invokeStaticMethod(clazz, methodName, new Class<?>[0], new Object[0]);
    }

    /**
     * 执行静态方法
     * @param clazz 类定义
     * @param methodName 方法名
     * @param paramTypeList 参数类型数组
     * @param params 参数值
     * @return 方法返回值
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] paramTypeList, Object[] params) {
        String key = buildKey(clazz.getName(), methodName, paramTypeList);
        if (!METHOD_ACCESS_CACHE_MAP.containsKey(key)) {
            MethodAccess methodAccess = MethodAccess.get(clazz);
            if (paramTypeList == null) {
                paramTypeList = new Class<?>[0];
            }
            int methodIndex = methodAccess.getIndex(methodName, paramTypeList);
            METHOD_ACCESS_CACHE_MAP.put(key, methodAccess);
            METHOD_INDEX_CACHE_MAP.put(key, methodIndex);
        }
        MethodAccess method = METHOD_ACCESS_CACHE_MAP.get(key);
        int index = METHOD_INDEX_CACHE_MAP.get(key);
        return method.invoke(null, index, params);
    }

    /**
     * 获取属性值（非私有属性，私有属性请使用get方法）
     * @param obj 实例对象
     * @param fieldName 属性名称
     * @return 属性值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        String key = buildKey(obj.getClass().getName(), fieldName, null);
        cacheField(obj.getClass(), key, fieldName);
        FieldAccess access = FIELD_ACCESS_CACHE_MAP.get(key);
        int index = FIELD_INDEX_CACHE_MAP.get(key);
        return access.get(obj, index);
    }

    /**
     * 设置属性值（非私有属性，私有属性请使用set方法）
     * @param obj 实例对象
     * @param fieldName 属性名称
     * @param value 属性值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        String key = buildKey(obj.getClass().getName(), fieldName, null);
        cacheField(obj.getClass(), key, fieldName);
        FieldAccess access = FIELD_ACCESS_CACHE_MAP.get(key);
        int index = FIELD_INDEX_CACHE_MAP.get(key);
        access.set(obj, index, value);
    }

    private static void cacheField(Class<?> clazz, String key, String fieldName) {
        if (!FIELD_ACCESS_CACHE_MAP.containsKey(key)) {
            FieldAccess fieldAccess = FieldAccess.get(clazz);
            int index = fieldAccess.getIndex(fieldName);
            FIELD_ACCESS_CACHE_MAP.put(key, fieldAccess);
            FIELD_INDEX_CACHE_MAP.put(key, index);
        }
    }

    private static String buildKey(String className, String methodName, Class<?>[] paramTypeList) {
        StringBuilder keySb = new StringBuilder(className);
        keySb.append("_").append(methodName);
        if (paramTypeList != null) {
            for (Class<?> aClass : paramTypeList) {
                keySb.append("_").append(aClass.getName());
            }
        }
        return keySb.toString();
    }
}
