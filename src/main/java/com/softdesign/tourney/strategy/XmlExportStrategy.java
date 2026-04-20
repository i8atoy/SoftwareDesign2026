package com.softdesign.tourney.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlExportStrategy<T> implements ExportStrategy<T> {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public String export(List<T> data) {
        try {
            return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error exporting to XML", e);
        }
    }
}