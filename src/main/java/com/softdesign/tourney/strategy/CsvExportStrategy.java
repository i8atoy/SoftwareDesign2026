package com.softdesign.tourney.strategy;

import com.softdesign.tourney.dto.TournamentDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;

@Component
public class CsvExportStrategy implements ExportStrategy<TournamentDto> {

    @Override
    public String export(List<TournamentDto> data) {
        if (data == null || data.isEmpty()) {
            return "No data available";
        }

        StringWriter sw = new StringWriter();
        try {
            String[] headers = {"ID", "Name", "Location", "Prize Money", "VRS Points"};

            CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader(headers)
                    .build();

            try (CSVPrinter printer = new CSVPrinter(sw, format)) {
                for (TournamentDto tournament : data) {
                    printer.printRecord(
                            tournament.getId(),
                            tournament.getName(),
                            tournament.getLocation(),
                            tournament.getPrizeMoney(),
                            tournament.getVrsPoints()
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error exporting Tournaments to CSV", e);
        }

        return sw.toString();
    }
}