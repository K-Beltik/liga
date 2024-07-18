# Test task #1 for LZ
Business logic is located in bulgaria package. I choose to use jsoup + JBrowserDriver for this as it is POC only.
On specified url it is very easy to get data directly by uri. Decomposition of get requests allowed me to simply iterate over court ID and record id in url and in this way to parse unlimited number of records. For a simplicity reason I've created entity to store parsed data
## How to use
Simply execute ./gradlew build after checking out repository and run Bulgaria.java from IDE of your choice (I'm using IDEA). Software takes 10-20 minute to run and parses first 700 records from each of 8 courts on specified URI.
## Known issues:
* Sometimes some
* JBrowserDriver
* JBrowserDriver
## What to improve
This project is POC only, implemented in around 6 hours (including technology investigation). For a production environment much more data investigation, normalization needed. Also, this app should be implemented using modern infrastructure techniques (docker + kubernetes in moder CI/CD way in a cloud) and should be norm

# Test task #2 for LZ
Business logic is located in orsr package. I choose to use jsoup + JBrowserDriver for this as it is POC only.
On specified url it is very easy to get data directly by uri. Decomposition of get requests allowed me to simply iterate over court ID and record id in url and in this way to parse unlimited number of records. For a simplicity reason I've created entity to store parsed data in most convenient way for POC: first parse important fields to strictly structured class, then parse whole page content to flexible json format and store it as String inside strict structure.
## How to use
Simply execute ./gradlew build after checking out repository and run ORSR.java from IDE of your choice (I'm using IDEA). Software takes 10-20 minute to run and parses first 700 records from each of 8 courts on specified URI.
## Known issues:
* Sometimes some records not parsed for some reason. Mostly because of html structure fluctuation. In real project each such exception should be carefully logged and addressed as bug report to fix issue. Amount of such cases are 4.4% which is under 95% threshold, which is enough for POC and for not wasting time addressing those for now.
* JBrowserDriver have not been updated for a 5 years at least, using old Java 8, not compatible with Java 18+ and have a number of vulnerabilities (probably because of old sl4j, there was huge breakthrough few years ago). This makes it very poor choice in production environment, but for a POC for a simplicity reasons its ok IMO.
* JBrowserDriver at first glance have no way to fix encoding issues which makes data practically un-usable, which makes another point in it's ASAP replacement with something modern, like Selenium, probably.
## What to improve
This project is POC only, implemented in around 6 hours (including technology investigation). For a production environment much more data investigation, normalization needed. Also, this app should be implemented using modern infrastructure techniques (docker + kubernetes in moder CI/CD way in a cloud) and should be normalized according to data storage needs (will it be strict RDBMS or flexible document-storage or data-stream or event-queue for real-time processing). Onw more thing is code flexibility and architecture. We should use configuration files to configure how software runs and performs. And what is obviously missing is tests.