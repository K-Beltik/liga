package com.liga.orsr;

import com.liga.orsr.model.LegalEntity;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ORSRParser {
    public static final String REGEX_COURT = "Extract from the.+?(Court .+)";
    public static final String REGEX_INSERT = "Insert No.:\\\\n(.+?)\\\\n";
    public static final String REGEX_BUSINESS_NAME = "Business name:\\\\n(.+?)\\\\n";

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
            System.out.println(currentEntity.getCourt());
            StringBuilder allInfo = new StringBuilder();
            doc.select("span").forEach(node ->
                allInfo.append(node.text()).append("\\n")
            );
            System.out.println(allInfo);
            currentEntity.setInsertNumber(getPatternFromText(allInfo.toString(), REGEX_INSERT));
            System.out.println(currentEntity.getInsertNumber());
            currentEntity.setBusinessName(getPatternFromText(allInfo.toString(), REGEX_BUSINESS_NAME));
            System.out.println(currentEntity.getBusinessName());
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
        return "";
    }
}
