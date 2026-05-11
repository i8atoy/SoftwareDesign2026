package com.softdesign.tourney.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonExportStrategy<T> extends AbstractExportTemplate<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected List<T> fetchData(List<T> rawData) {
        if (rawData == null) {
            return List.of();
        }
        return rawData;
    }
    @Override
    protected String transform(List<T> data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error exporting to JSON", e);
        }
    }

}