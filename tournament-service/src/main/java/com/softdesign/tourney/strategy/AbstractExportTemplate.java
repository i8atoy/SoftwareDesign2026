package com.softdesign.tourney.strategy;

import java.util.List;


public abstract class AbstractExportTemplate<T> {

    public final String export(List<T> rawData) {
        List<T> data = fetchData(rawData);
        String content = transform(data);
        return writeOutput(content);
    }

    protected abstract List<T> fetchData(List<T> rawData);

    protected abstract String transform(List<T> data);

    protected String writeOutput(String content) {
        // Default: pass through unchanged.
        // Override to add a header/footer, BOM, XML declaration, etc.
        return content;
    }
}