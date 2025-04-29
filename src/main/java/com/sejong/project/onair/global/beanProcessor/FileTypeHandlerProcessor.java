package com.sejong.project.onair.global.beanProcessor;

import com.sejong.project.onair.domain.file.annotation.FileTypeHandler;
import com.sejong.project.onair.domain.file.model.FileType;
import com.sejong.project.onair.domain.file.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FileTypeHandlerProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(FileTypeHandlerProcessor.class);
    private final Map<FileType, FileService> handlerMap = new HashMap<>();

    public Map<FileType, FileService> getHandlerMap() {
        log.info("BeanPostHandler 들어옴");
        return handlerMap;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FileService fileService) {
            FileTypeHandler annotation = bean.getClass().getAnnotation(FileTypeHandler.class);
            if (annotation != null) {
                handlerMap.put(annotation.value(), fileService);
            }
        }
        return bean;
    }
}
