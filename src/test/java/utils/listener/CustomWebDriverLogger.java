package utils.listener;


import static utils.LogUtil.debug;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public class CustomWebDriverLogger extends AbstractWebDriverEventListener {

    StopWatch stopWatch = new StopWatch();

    void startStopWatch() {
        stopWatch.reset();
        stopWatch.start();
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        debug("Navigating to URL - " + url);
        startStopWatch();
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        stopWatch.stop();
        debug("Navigation successfully completed within - " + stopWatch);
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        debug("Searching WebElement - " + by.toString());
        startStopWatch();
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        stopWatch.stop();
        debug("Element successfully found within - " + stopWatch);
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        debug("Perform a click on button - " + element.toString());
        startStopWatch();
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        stopWatch.stop();
        debug("Click successfully performed within - " + stopWatch);
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        if (ArrayUtils.isNotEmpty(keysToSend)) {
            if (StringUtils.isBlank(element.getText())) {
                debug("Setting to element - \'" + element.getTagName() + "\' text - " + keysToSend[0]);
            } else {
                debug("Changing \'" + element.getTagName() + "\' value from - " + element.getText() + " to - " + keysToSend[0]);
            }
        } else {
            debug("Clearing text from element: " + element.getTagName());
        }
        startStopWatch();
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        stopWatch.stop();
        debug("Text successfully send within - " + stopWatch);
    }
}

