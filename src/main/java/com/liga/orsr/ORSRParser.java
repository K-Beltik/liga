package com.liga.orsr;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ORSRParser {

    public static void parseFiveK() throws IOException {
        JBrowserDriver driver = new JBrowserDriver(Settings
                .builder().
                timezone(Timezone.EUROPE_KIEV).build());
        driver.get("https://www.orsr.sk/vypis.asp?lan=en&ID=1&SID=9&P=1");
        String loadedPage = driver.getPageSource();
        Document doc = Jsoup.parse(loadedPage);
        doc.select("span").addClass("ra").forEach(System.out::println);
        Elements scriptTags = doc.getElementsByTag("script");
        for (Element tag : scriptTags){
            for (DataNode node : tag.dataNodes()) {
                System.out.println(node.getWholeData());
            }
        }
    }
}
