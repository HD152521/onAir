package com.sejong.project.onair.domain.file.annotation;

import com.sejong.project.onair.domain.file.model.FileType;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileTypeHandler {
    FileType value();
}
