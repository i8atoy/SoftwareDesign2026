package com.softdesign.tourney.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlExportStrategy<T> extends AbstractExportTemplate<T> {

    private final XmlMapper xmlMapper = new XmlMapper();

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
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error exporting to XML", e);
        }
    }

    @Override
    protected String writeOutput(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + content;
    }
}