/*
 * Copyright 2007-2020, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package redis.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class I18nUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18nUtil.class);

    private static ConcurrentMap<String, Map<String, String>> i18nConcurrentMap = new ConcurrentHashMap<>();

    public static void renderErrorsI18n(ModelAndView modelAndView, ErrorCode errors) {
        Objects.requireNonNull(errors);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String lang = (String) request.getAttribute(Constants.LANG_KEY);

        Map<String, String> stringMap = I18nUtil.getI18nProperties("error", lang);
        modelAndView.addObject(ModelsConstants.ERROR_MSG, StringUtils.isEmpty(stringMap.get(String.valueOf(errors.getErrorCode()))) ? errors.getErrorMsg() : stringMap.get(String.valueOf(errors.getErrorCode())));
    }


    public static Map<String, String> getI18nProperties(String modelName, String language) {
        if (StringUtils.isEmpty(language)) {
            language = "cn";
        }
        String key = modelName + "_" + language;
        Map<String, String> content = i18nConcurrentMap.get(key);
        if (content == null || content.size() == 0) {
            if ("en".equals(language)) {
                content = getI18n(modelName, "en");
            } else {
                content = getI18n(modelName, "cn");
            }
            i18nConcurrentMap.put(key, content);
        }
        return content;
    }


    private static Map<String, String> getI18n(String modelName, String language) {
        String path = "i18n/" + language + "/" + modelName + ".i18n.json";
        InputStream inputStream = I18nUtil.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        try {
            String content = IOUtils.toString(inputStream, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(content);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toString());
            }
        } catch (Exception e) {
            LOGGER.error(path, e);
        }
        return map;
    }

}
