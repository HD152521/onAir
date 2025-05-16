package com.sejong.project.onair.global.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class NullOnInvalidDoubleDeserializer extends StdDeserializer<Double> {
    public NullOnInvalidDoubleDeserializer() {
        super(Double.class);
    }

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        try {
            // 정상적인 숫자 문자열이면 파싱
            return Double.valueOf(text);
        } catch (Exception e) {
            // "-", "통신장애" 등 파싱 불가 시 null 반환
            return null;
        }
    }
}