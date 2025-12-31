package com.casemgr.utils;

import java.util.Map;

import org.springframework.cglib.beans.BeanMap;

public class MapUtils {
	public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return (T) beanMap.getBean();
    }
}
