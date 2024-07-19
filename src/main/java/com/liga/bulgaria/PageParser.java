package com.liga.bulgaria;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PageParser extends Thread {
    private final int counter;
    private final int threadCount;
    private Proxy proxy;
    private static Logger logger = LoggerFactory.getLogger(PageParser.class);
    private final String REGEX_ENTRY_DATE = "Date of entry:\\\\n(.+?)\\\\n";
    private final String REGEX_DELETION_DATE = "Date of deletion:\\\\n(.+?)\\\\n";
    private final String REGEX_ID = "Identification number.+?(\\d+ \\d+ \\d+)\\\\n";
    private final String REGEX_COURT = "Extract from the.+?(Court .+)";
    private final String REGEX_INSERT = "Insert No.:\\\\n(.+?)\\\\n";
    private final String REGEX_BUSINESS_NAME = "Business name:\\\\n(.+?)\\\\n";
    private final String REGEX_ADDRESS = "Registered seat:\\\\n(.+?)\\(from:";
    private final String REGEX_LEGAL_FORM = "Legal form:\\\\n(.+?)\\\\n";

    public PageParser(int counter, int threadCount, Proxy proxy) {
        this.counter = counter;
        this.threadCount = threadCount;
        this.proxy = proxy;
    }

    @Override
    public void run() {
        String baseUri;
        int exceptionsCount = 0;
        Set<String> urisToParse = new HashSet<>();
        WebDriverManager.chromedriver().setup();
        WebDriver webClient = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();
        options.setProxy(proxy);
        try (FileWriter fw = new FileWriter("Bulgaria." + counter + ".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (int id = counter; id < 4; id= id + threadCount) {
                baseUri = "https://portal.registryagency.bg/CR/en/Reports/EntriesDelitionsAnnouncements?dateFrom=2024-06-30T21%3A00%3A00.000Z&dateTo=2024-07-17T20%3A59%3A59.000Z&page=" + id;
                webClient.navigate().to(baseUri);
                sleep(8000);
                String currentPageSource = new String(webClient.getPageSource().getBytes(StandardCharsets.UTF_8));
                Document doc = Jsoup.parse(currentPageSource);
                doc.select("a[target=_blank][href]").forEach(element -> {
                    urisToParse.add("https://portal.registryagency.bg" + element.attr("href"));
                });
            }
            for (String uri : urisToParse) {
                Map<String, List<String>> entity = new HashMap<>();
                webClient.navigate().to(uri);
                sleep(8000);
                String currentPageSource = new String(webClient.getPageSource().getBytes(StandardCharsets.UTF_8));
                Document doc = Jsoup.parse(currentPageSource);
                doc.select("div[class=field-container]").forEach(element -> {
                    List<String> subEntities = new ArrayList<>();
                    element.select("p").forEach(subEntity -> subEntities.add(subEntity.text()));
                    entity.put(
                            element.select("h3").text(),
                            subEntities
                    );
                });
                ObjectMapper mapper = new ObjectMapper();
                String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(entity).formatted();
                out.println(jsonResult);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            webClient.close();
        }
    }
}
