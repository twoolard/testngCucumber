# testngCucumber
UI Automation framework using Selenium Cucumber and TestNG

No read me quite yet but I will give the maven command line arguments that I use to run the test. You can use whatever driver
or browser is available in the webdriver factory. I have a set up for sauce labs too but I don't have an account anymore
so I haven't tested it.

Run the following commands from the Root directory

All
mvn test -Pcukes -Dselenium.browser=chrome 
-Dwebdriver.chrome.driver=$WORKSPACE/src/test/resources/drivers/chrome/chromedriver-osx.exe

Regression
mvn test -Pregression -Dselenium.browser=chrome 
-Dwebdriver.chrome.driver=$WORKSPACE/src/test/resources/drivers/chrome/chromedriver-osx.exe

Smoke
mvn test -Psmoke -Dselenium.browser=chrome 
-Dwebdriver.chrome.driver=$WORKSPACE/src/test/resources/drivers/chrome/chromedriver-osx.exe
