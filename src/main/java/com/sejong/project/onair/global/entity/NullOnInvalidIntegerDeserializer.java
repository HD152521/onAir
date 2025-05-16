package com.sejong.project.onair.global.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class NullOnInvalidIntegerDeserializer extends StdDeserializer<Integer> {
    public NullOnInvalidIntegerDeserializer() {
        super(Integer.class);
    }

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        try {
            // 정상적인 숫자 문자열이면 파싱
            return Integer.valueOf(text);
        } catch (Exception e) {
            // '-', '통신장애' 등 숫자로 파싱 불가할 때 null 반환
            return null;
        }
    }
}