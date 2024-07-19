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
Business logic is located in orsr package. I choose to use jsoup + JBrowserDriver for this as it is POC only at first. But then switched to HTMLUnit. Its not ideal, but at least handles encoding much better
On specified url it is very easy to get data directly by uri. Decomposition of get requests allowed me to simply iterate over court ID and record id in url and in this way to parse unlimited number of records. For a simplicity reason I've created entity to store parsed data in most convenient way for POC: first parse important fields to strictly structured class, then parse whole page content to flexible json format and store it as String inside strict structure.
## How to use
Simply execute ./gradlew build after checking out repository and run ORSR.java from IDE of your choice (I'm using IDEA). Software takes 10-20 minute to run and parses first 700 records from each of 8 courts on specified URI.
## Known issues:
* Sometimes some records not parsed for some reason. Mostly because of html structure fluctuation. In real project each such exception should be carefully logged and addressed as bug report to fix issue. Amount of such cases are around 4% - 4.5% which is under 95% threshold, which is enough for POC for now.
* Even after switching from JBrowser to HtmlUnit there are some strange encoding errors. Seem like encoding not consistent in source, such problem should be addressed as well.
## What to improve
This project is POC only, implemented in around 6 hours (including technology investigation). For a production environment much more data investigation, normalization needed. Also, this app should be implemented using modern infrastructure techniques (docker + kubernetes in moder CI/CD way in a cloud) and should be normalized according to data storage needs (will it be strict RDBMS or flexible document-storage or data-stream or event-queue for real-time processing). Onw more thing is code flexibility and architecture. We should use configuration files to configure how software runs and performs. And what is obviously missing is tests.