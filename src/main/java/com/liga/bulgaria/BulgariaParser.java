package com.liga.bulgaria;

import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BulgariaParser {
    private static Logger logger = LoggerFactory.getLogger(BulgariaParser.class);

    public static void parseFiveK() throws IOException {
        try {
            List<PageParser> parsers = new ArrayList<>();
            //I will add few proxies by hand but in production env it should be automated off course
            List<Proxy> proxies = new ArrayList<>();
            proxies.add(new Proxy().setHttpProxy("149.11.58.226:3128").setSslProxy("149.11.58.226:3128"));
            proxies.add(new Proxy().setHttpProxy("72.10.160.92:5635").setSslProxy("72.10.160.92:5635"));
            proxies.add(new Proxy().setHttpProxy("200.174.198.86:8888").setSslProxy("200.174.198.86:8888"));
            proxies.add(new Proxy().setHttpProxy("50.218.57.65:80").setSslProxy("50.218.57.65:80"));
            int parsersCount = proxies.size();
            for (int i = 0; i < parsersCount; i++) {
                parsers.add(new PageParser(i, parsersCount, proxies.get(i)));
            }
            parsers.forEach(Thread::start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
