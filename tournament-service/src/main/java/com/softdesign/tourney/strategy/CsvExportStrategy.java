package com.softdesign.tourney.strategy;

import com.softdesign.tourney.dto.TournamentDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;


@Component
public class CsvExportStrategy extends AbstractExportTemplate<TournamentDto> {

    private static final String[] HEADERS = {"ID", "Name", "Location", "Prize Money", "VRS Points"};

    @Override
    protected List<TournamentDto> fetchData(List<TournamentDto> rawData) {
        if (rawData == null || rawData.isEmpty()) {
            return List.of();
        }
        return rawData;
    }

    @Override
    protected String transform(List<TournamentDto> data) {
        if (data.isEmpty()) {
            return "No data available";
        }

        StringWriter sw = new StringWriter();
        CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                .setHeader(HEADERS)
                .build();

        try (CSVPrinter printer = new CSVPrinter(sw, format)) {
            for (TournamentDto t : data) {
                printer.printRecord(
                        t.getId(),
                        t.getName(),
                        t.getLocation(),
                        t.getPrizeMoney(),
                        t.getVrsPoints()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error exporting Tournaments to CSV", e);
        }

        return sw.toString();
    }

}