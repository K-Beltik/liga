package com.liga.orsr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ORSRParser {
    private static Logger logger = LoggerFactory.getLogger(ORSRParser.class);

    public static void parseFiveK() throws IOException {
        try {
            List<CourtParser> courts = new ArrayList<>();
            for (int i = 2; i <= 9; i++) {
                courts.add(new CourtParser(i));
            }
            courts.forEach(CourtParser::start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
