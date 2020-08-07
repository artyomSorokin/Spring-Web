package com.epam.cdp.module4.homework1.service;

import com.epam.cdp.module4.homework1.entity.AbstractEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DataLoaderForMapStorage implements BeanPostProcessor {

    private String payloadFileName;
    private String datePattern;
    private Map<String, Class> entityClassMap;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Map && Objects.equals(beanName, "storage")) {
            try {
                initStorageMap((Map<String, Object>) bean);
            } catch (IOException e) {
                log.error("Could not initialize storage", e);
            }
        }
        return bean;
    }

    private void initStorageMap(Map<String, Object> storage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(datePattern));
        File payloadFile = ResourceUtils.getFile("classpath:" + payloadFileName);
        Map<String, List<Object>> entityMap = mapper.readValue(payloadFile, new TypeReference<Map<String, List<Object>>>() {
        });
        entityMap.forEach((key, values) -> {
            Class currentEntityClass = entityClassMap.get(key);
            values.forEach(value -> {
                AbstractEntity entity = (AbstractEntity) mapper.convertValue(value, currentEntityClass);
                String modifiedKey = String.format("%s:%s", key, entity.getId());
                storage.put(modifiedKey, entity);
            });
        });
    }

    public void setPayloadFileName(String payloadFileName) {
        this.payloadFileName = payloadFileName;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public void setEntityClassMap(Map<String, Class> entityClassMap) {
        this.entityClassMap = entityClassMap;
    }
}
