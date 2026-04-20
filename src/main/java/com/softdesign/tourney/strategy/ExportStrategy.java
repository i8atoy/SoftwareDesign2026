package com.softdesign.tourney.strategy;

import java.util.List;

public interface ExportStrategy<T> {
    String export(List<T> data);
}
