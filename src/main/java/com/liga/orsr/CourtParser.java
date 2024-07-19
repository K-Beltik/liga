package com.liga.orsr;

import com.liga.orsr.model.LegalEntity;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourtParser extends Thread {
    private final int courtId;
    private static Logger logger = LoggerFactory.getLogger(CourtParser.class);
    private final String REGEX_ENTRY_DATE = "Date of entry:\\\\n(.+?)\\\\n";
    private final String REGEX_DELETION_DATE = "Date of deletion:\\\\n(.+?)\\\\n";
    private final String REGEX_ID = "Identification number.+?(\\d+ \\d+ \\d+)\\\\n";
    private final String REGEX_COURT = "Extract from the.+?(Court .+)";
    private final String REGEX_INSERT = "Insert No.:\\\\n(.+?)\\\\n";
    private final String REGEX_BUSINESS_NAME = "Business name:\\\\n(.+?)\\\\n";
    private final String REGEX_ADDRESS = "Registered seat:\\\\n(.+?)\\(from:";
    private final String REGEX_LEGAL_FORM = "Legal form:\\\\n(.+?)\\\\n";

    public CourtParser(int courtId) {
        this.courtId = courtId;
    }

    @Override
    public void run() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        URL baseUri;
        int exceptionsCount = 0;
        try (FileWriter fw = new FileWriter("orsr.sk.courtId." + courtId + ".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw))
        {
            for (int id = 1; id <= 700; id++) {
                try {
                    baseUri = new URL("https://www.orsr.sk/vypis.asp?lan=en&ID="+ id + "&SID="+ courtId + "&P=1");
                    HtmlPage page = webClient.getPage(baseUri);
                    Document doc = Jsoup.parse(page.asXml());
                    JSONObject dataToStore = new JSONObject(buildLegalEntity(doc));
                    out.println(dataToStore);
                    if (id % 70 == 0){
                        System.out.println("Thread " + courtId + " - " + (id*100/700) + "%");
                    }
                } catch (Exception e) {
                    System.out.println("Ignoring exception in thread " + courtId);
                    exceptionsCount++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        webClient.close();
        throw new RuntimeException("Exception count in this thread was: " + exceptionsCount + " from 700 id's");
    }

    private LegalEntity buildLegalEntity(Document doc) {
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

    private String buildLegalEntityJson(Document doc) {
        JSONObject jsonParentObject = new JSONObject();
        for (Element table : doc.select("table[bgcolor=#ffffff]")) {
            //taking field name from first column
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

    private String getPatternFromText(String text, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
