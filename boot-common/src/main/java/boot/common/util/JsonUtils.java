package boot.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.internal.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author: hyy
 * @time: 2019-11-09 14:35
 **/
public class JsonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    static {
        //反序列化的时候如果多了其他属性,不抛出异常
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //指定遇到date按照这种格式转换
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MAPPER.setDateFormat(fmt);
    }

    /**
     * 序列化：将对象转换成json字符串。
     */
    @Nullable
    public static String objectToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("json序列化出错：" + obj, e);
            return null;
        }
    }

    /**
     * 反序列化：json字符串转换成将对象。
     */
    @Nullable
    public static <T> T jsonToPojo(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("json解析出错：" + json, e);
            return null;
        }
    }

    @Nullable
    public static <E> List<E> jsonToList(String json, Class<E> clazz) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            LOGGER.error("json解析出错：" + json, e);
            return null;
        }
    }

    /**
     * json转map
     *
     * @param json
     * @param kClass map的键类型
     * @param vClass map的值类型,不知道就写String.class
     * @param <K>
     * @param <V>
     * @return
     */
    @Nullable
    public static <K, V> Map<K, V> jsonToMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            LOGGER.error("json解析出错：" + json, e);
            return null;
        }
    }

    /**
     * 复杂json转换： 如 List<Map<String,String>>.class 是不允许的（泛型+字节码.class）
     * 因此引入TypeReference 内部类+泛型， new TypeReference<List<Map<String,String>>>()
     */
    @Nullable
    public static <T> T jsonToComplexObject(String json, TypeReference<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            LOGGER.error("json解析出错：" + json, e);
            return null;
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        String name;
        Integer age;
    }

    public static void main(String[] args) {
        // 序列化：对象->json
        User user = new User("hyy", 18);
        System.out.println(JsonUtils.objectToJson(user));
        // 反序列化：json->对象
        String json = "{\"name\":\"hyy\",\"age\":18}";
        System.out.println(JsonUtils.jsonToPojo(json, User.class));

        // toList
        String jsonAry = "[20,-10,18]";
        System.out.println(JsonUtils.jsonToList(jsonAry, Integer.class));

        // toMap
        String jsonObj = "{\"name\":\"hyy\",\"age\":18}";
        System.out.println(JsonUtils.jsonToMap(jsonObj, String.class, String.class));

        // 复杂类型
        String jsonComplex = "[{\"name\":\"hyy\",\"age\":18},{\"name\":\"Java\",\"age\":8}]";
        System.out.println(JsonUtils.jsonToComplexObject(jsonComplex, new TypeReference<List<Map<String, String>>>() {
        }));

    }


}
