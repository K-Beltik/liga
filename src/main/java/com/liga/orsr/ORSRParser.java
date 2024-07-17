package com.liga.orsr;

import com.liga.orsr.model.CourtParser;
import com.liga.orsr.model.LegalEntity;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class ORSRParser {
    private static Logger logger = LoggerFactory.getLogger(ORSRParser.class);
    private static final String REGEX_ENTRY_DATE = "Date of entry:\\\\n(.+?)\\\\n";
    private static final String REGEX_DELETION_DATE = "Date of deletion:\\\\n(.+?)\\\\n";
    private static final String REGEX_ID = "Identification number.+?(\\d+ \\d+ \\d+)\\\\n";
    public static final String REGEX_COURT = "Extract from the.+?(Court .+)";
    public static final String REGEX_INSERT = "Insert No.:\\\\n(.+?)\\\\n";
    public static final String REGEX_BUSINESS_NAME = "Business name:\\\\n(.+?)\\\\n";
    private static final String REGEX_ADDRESS = "Registered seat:\\\\n(.+?)\\(from:";
    private static final String REGEX_LEGAL_FORM = "Legal form:\\\\n(.+?)\\\\n";

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

    private static LegalEntity buildLegalEntity(Document doc) {
        LegalEntity currentEntity = new LegalEntity();
        currentEntity.setCourt(getPatternFromText(doc.select("td:not(:has(img))").first().text(), REGEX_COURT));
        StringBuilder infoBuilder = new StringBuilder();
        doc.select("span").forEach(node ->
            infoBuilder.append(node.text()).append("\\n")
        );
        String allInfo = infoBuilder.toString();
        currentEntity.setInsertNumber(getPatternFromText(allInfo, REGEX_INSERT));
        currentEntity.setIdentificationNumber(Long.parseLong(getPatternFromText(allInfo, REGEX_ID).replace(" ", "")));
        currentEntity.setBusinessName(getPatternFromText(allInfo, REGEX_BUSINESS_NAME));
        currentEntity.setRegisteredAddress(getPatternFromText(allInfo, REGEX_ADDRESS));
        currentEntity.setLegalForm(getPatternFromText(allInfo, REGEX_LEGAL_FORM));
        String entryDate = getPatternFromText(allInfo,REGEX_ENTRY_DATE);
        if (entryDate != null) {
            currentEntity.setEntryDate(LocalDate.parse(entryDate, DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        }
        String deletionDate = getPatternFromText(allInfo,REGEX_DELETION_DATE);
        if (deletionDate != null) {
            currentEntity.setEntryDate(LocalDate.parse(deletionDate, DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        }
        currentEntity.setJsonAllOtherInfo(buildLegalEntityJson(doc));
        return currentEntity;
    }

    private static String buildLegalEntityJson(Document doc) {
        JSONObject jsonParentObject = new JSONObject();
        for (Element table : doc.select("table[bgcolor=#ffffff]")) {
            //taking field name Fom first column
            String field = table.select("tr").first().select("td").first().text();
            //next iterate trhought all  subitems
            List<JSONObject> innerObjects = new ArrayList<>();
            table.select("tr").first().select("td[width=80%]").select("table").forEach(innerTable -> {
                JSONObject internalObject = new JSONObject();
                //take second column as field named "Item" && third column as field named "Effective Dates"
                internalObject.put("Item", innerTable.select("td[width=67%]").first().text());
                internalObject.put("Effective_Dates", innerTable.select("td[width=33%]").first().text());
                innerObjects.add(internalObject);
            });
            jsonParentObject.put(field,innerObjects);
        }
        return jsonParentObject.toString();
    }

    private static String getPatternFromText(String text, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
