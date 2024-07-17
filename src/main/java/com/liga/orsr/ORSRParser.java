package com.liga.orsr;

import com.liga.orsr.model.LegalEntity;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        JBrowserDriver driver = new JBrowserDriver(Settings
                .builder().
                timezone(Timezone.EUROPE_KIEV).build());
        try {
            driver.get("https://www.orsr.sk/vypis.asp?lan=en&ID=1&SID=9&P=1");
            String loadedPage = driver.getPageSource();
            Document doc = Jsoup.parse(loadedPage);
            LegalEntity currentEntity = new LegalEntity();
            currentEntity.setCourt(getPatternFromText(doc.select("td:not(:has(img))").first().text(), REGEX_COURT));
            StringBuilder infoBuilder = new StringBuilder();
            doc.select("span").forEach(node ->
                infoBuilder.append(node.text()).append("\\n")
            );
            System.out.println(infoBuilder);
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
            System.out.println(currentEntity);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            driver.quit();
        }
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
