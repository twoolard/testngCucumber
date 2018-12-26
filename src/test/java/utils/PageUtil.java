package utils;

import static java.util.stream.Collectors.toList;
import static org.mortbay.log.Log.info;
import static org.mortbay.log.Log.warn;
import static utils.LogUtil.debug;
import static utils.LogUtil.error;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import com.google.common.collect.Ordering;

import utils.metrics.BrowserConsoleLogEntry;

public class PageUtil {

    public static final int CYCLES_TO_WAIT = 240;

    private static Random rand = new Random();

    public static void acceptAlert(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    public String getPageTitle(WebDriver driver) {
        return driver.getTitle();
    }

    public void waitForPageLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete");

        WebDriverWait wait = new WebDriverWait(driver, 60);

        wait.until(pageLoadCondition);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.println("Failed to load page - " + getPageTitle(driver));


        }

        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

    }


    public static boolean waitFor(WebDriver driver, By by, int waitSeconds) {
        boolean doesExist = false;

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        try {
            info("Starting wait for visibility [Defined Wait Seconds: " + waitSeconds + "]");
            WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            doesExist = true;
            info("Finished wait for visibility");
        } catch (Exception e) {
            warn("Waiting for element visibility timed out. Try increasing timeout beyond [" + waitSeconds + "] seconds");
        }
        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return doesExist;
    }

    public static boolean waitForCountofWindowsToBe(WebDriver driver, int countOfWindows, int waitSeconds) {
        boolean isRequiredAmountOfWindowsAvailable;

        StopWatch stopWatch = watchHandlerStart();

        info(String.format("Starting wait for count of windows to be: %s [Defined Wait Seconds: %s sec]", countOfWindows, waitSeconds));

        isRequiredAmountOfWindowsAvailable = new WebDriverWait(driver, waitSeconds).until(ExpectedConditions.numberOfWindowsToBe(countOfWindows));

        info(String.format("Stoped wait for count of windows to be: %s [Defined Wait Seconds: %s sec]", countOfWindows, waitSeconds));

        logDuration(watchHandlerStop(stopWatch));

        return isRequiredAmountOfWindowsAvailable;

    }

    public static boolean waitFor(WebDriver driver, By by) {
        return waitFor(driver, by, 10);
    }

    /**
     * @param driver
     * @param element
     * @param waitSeconds seconds to wait
     * @return
     * @throws Exception
     */

    public static boolean waitFor(WebDriver driver, WebElement element, long waitSeconds, String text) {
        if (element == null)
            return false;

        boolean isVisible = false;

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        try {
            info("Looking for text in [Element: " + element.toString() + "] [Defined Wait Seconds: " + waitSeconds + "]");
            WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
            isVisible = wait.until(ExpectedConditions.textToBePresentInElement(element, text));
            info("Finished looking for text");
        } catch (TimeoutException e) {
            warn("Looking for element timed out. Try increasing timeout beyond [" + waitSeconds + "] seconds");
        }

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return isVisible;
    }
    public static boolean waitFor(WebDriver driver, WebElement element, long waitSeconds) {
        if (element == null)
            return false;

        boolean isVisible = false;

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        try {
            info("Starting wait for visibility [Element: " + element.toString() + "] [Defined Wait Seconds: " + waitSeconds + "]");
            WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
            WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
            isVisible = true;
            info("Finished wait for visibility");
        } catch (TimeoutException e) {
            warn("Waiting for element visibility timed out. Try increasing timeout beyond [" + waitSeconds + "] seconds");
        }

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return isVisible;
    }

    /**
     * @param stopWatch
     */
    public static void logDuration(StopWatch stopWatch) {
        info("[Duration: " + stopWatch + "]");
    }

    public static Object executeJavascript(WebDriver driver, String js) {
        if (driver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) driver).executeScript(js);
        }
        return null;
    }

    public static void javascriptScrollToTop(WebDriver driver) {
        PageUtil.executeJavascript(driver, "window.scroll(0,0);");
    }

    public static void javascriptScrollBy(WebDriver driver, int x, int y) {
        PageUtil.executeJavascript(driver, "javascript:window.scrollBy(" + x + "," + y + ")");
    }

    public static void javascriptScrollTo(WebDriver driver, int x, int y) {
        PageUtil.executeJavascript(driver, "javascript:window.scrollTo(" + x + "," + y + ")");
    }

    public static void scrollToElementIfNotDisplayed(WebDriver driver, WebElement link) {
        //Sometimes element is below navbar and it's visible, but not available for clicking.
        javascriptScrollToTop(driver);
        if (!link.isDisplayed()) {
            scrollElementIntoView(driver, link);
        }
    }

    public static void scrollElementIntoView(WebDriver driver, WebElement element) {
        scrollElementIntoView(driver, element, false);
    }

    public static void scrollElementIntoView(WebDriver driver, WebElement element, boolean alignedToTop) {
        info("Scrolling to:" + element);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript(String.format("arguments[0].scrollIntoView(%s);", alignedToTop), element);
    }

    public static boolean waitFor(WebDriver driver, List<WebElement> elements, long waitSeconds) {
        boolean isVisible = false;

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            info("Starting wait for visibility [Elements: " + elements.toString() + "] [Defined Wait Seconds: " + waitSeconds + "]");
            WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
            List<WebElement> els = wait.until(ExpectedConditions.visibilityOfAllElements(elements));
            if (!CollectionUtils.isEmpty(els)) {
                isVisible = true;
                info("Finished wait for invisibility [Defined Wait Seconds: " + waitSeconds + "]");
            }
        } catch (TimeoutException e) {
            warn("Waiting for element visibility timed out. Try increasing timeout beyond [" + waitSeconds + "] seconds");
        }

        stopWatch.stop();
        logDuration(stopWatch);

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return isVisible;
    }

    public static boolean waitFor(WebDriver driver, List<WebElement> elements) {
        return waitFor(driver, elements, 30);
    }

    public static boolean waitFor(WebDriver driver, WebElement element) {
        return waitFor(driver, element, 30);
    }

    public static boolean waitFor(WebDriver driver, WebElement element, String text) { return waitFor(driver, element, 15, text); }

    public static void waitForInvisibility(WebDriver driver, By locator) {
        waitForInvisibility(driver, locator, 30);
    }

    public static void waitForInvisibility(WebDriver driver, By locator, int waitSeconds) {
        waitForInvisibility(driver, waitSeconds, ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static void waitForInvisibility(WebDriver driver, int waitSeconds, ExpectedCondition expectedCondition) {
        driver.manage().timeouts().implicitlyWait(250, TimeUnit.MILLISECONDS);

        StopWatch stopWatch = watchHandlerStart();

        debug(String.format("Started wait for %s [Defined Wait Seconds: %d]", expectedCondition.toString(), waitSeconds));

        new WebDriverWait(driver, waitSeconds)
                .pollingEvery(250, TimeUnit.MILLISECONDS)
                .withMessage(String.format("Condition [%s] was not done in %s sec.", expectedCondition.toString(), waitSeconds))
                .until(expectedCondition);

        debug(String.format("Finished wait for %s [Defined Wait Seconds: %d]", expectedCondition.toString(), waitSeconds));

        logDuration(watchHandlerStop(stopWatch));

        WebDriverUtil.setDriverTimeoutToDefault(driver);
    }

    public static boolean isElementPresentInParent(WebDriver driver, By locator, WebElement parent) {
        return isElementPresentInParent(driver, locator, parent, 30);
    }

    public static boolean isElementPresentInParent(WebDriver driver, By locator, WebElement parent, int waitSeconds) {
        return findElementInParent(driver, locator, parent, waitSeconds) != null;
    }

    public static WebElement findElementInParent(WebDriver driver, By locator, WebElement parent) {
        return findElementInParent(driver, locator, parent, 30);
    }

    public static WebElement findElementInParent(WebDriver driver, By locator, WebElement parent, int waitSeconds) {
        WebElement el = null;

        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        try {
            el = parent.findElement(locator);
        } catch (Exception e) {
            error(locator + " was not found in " + parent.toString());
        }

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return el;
    }

    public static List<WebElement> findElementsInParent(WebDriver driver, By locator, WebElement parent) {
        return findElementsInParent(driver, locator, parent, 30);
    }

    public static List<WebElement> findElementsInParent(WebDriver driver, By locator, WebElement parent, int waitSeconds) {
        List<WebElement> els = null;

        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        try {
            els = parent.findElements(locator);
        } catch (Exception e) {
            // Element not found
        }

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return els;
    }

    public static WebElement findElement(WebDriver driver, By locator) {
        return findElement(driver, locator, 30);
    }

    public static WebElement findElement(WebDriver driver, By locator, int waitSeconds) {
        WebElement el = null;

        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        try {
            el = driver.findElement(locator);
        } catch (Exception e) {
            warn("Element was not found: " + locator.toString());
        }

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return el;
    }

    public static List<WebElement> findElements(WebDriver driver, By locator) {
        return findElements(driver, locator, 5);
    }

    public static List<WebElement> findElements(WebDriver driver, By locator, int waitSeconds) {
        List<WebElement> elements = null;

        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        try {
            elements = driver.findElements(locator);
        } catch (Exception e) {
            // Element not found
        }

        WebDriverUtil.setDriverTimeoutToDefault(driver);

        return elements;
    }

    public static String getCurrentYear() {
        return getFormattedDateTimeString("yyyy");
    }

    public static String getFormattedDateTimeForTesting() {
        return (getFormattedDateTimeString("yyyyMMdd-HHmmss", 0, false));
    }

    public static String getFormattedDateForTesting() {
        return (getFormattedDateTimeString("yyyyMMdd", 0, false));
    }

    public static String getFormattedDateForTestingWithDashes() {
        return (getFormattedDateTimeString("yyyy-MM-dd", 0, false));
    }

    public static String getFormattedDateForTesting(int days) {
        return (getFormattedDateTimeString("yyyyMMdd", days, false));
    }

    public static String getFormattedDateForTestingWithDashes(int days) {
        return (getFormattedDateTimeString("yyyy-MM-dd", days, false));
    }

    public static String getFormattedDateTimeString(String dateFormat, int days, boolean inLocalTimezone) {
        return getFormattedDateTimeString(dateFormat, days, 0, inLocalTimezone);
    }

    public static String getFormattedDateTimeString(String dateFormat, int days, int minutes, boolean inLocalTimezone) {
        Calendar cal;
        if (inLocalTimezone) {
            cal = Calendar.getInstance();
        } else {
            cal = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        }

        if (days != 0)
            cal.add(Calendar.DATE, days);
        if (minutes != 0)
            cal.add(Calendar.MINUTE, minutes);

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setCalendar(cal);

        return df.format(cal.getTime());
    }

    public static String getFormattedDateTimeString(String dateFormat) {
        return getFormattedDateTimeString(dateFormat, 0, false);
    }

    public static int generateRandomInt(int start, int end) {
        long range = (long) end - (long) start + 1;
        long fraction = (long) (range * rand.nextDouble());
        return (int) (fraction + start);
    }

    public static void chooseSelection(WebElement el, String visibleOption) {
        Select selectOption = new Select(el);
        if (visibleOption != null) {
            selectOption.selectByVisibleText(visibleOption);
        } else {
            selectOption.selectByIndex(1);
        }
    }

    public static void setDataToSelector(List<WebElement> elementList, String value) throws NoSuchElementException {
        WebElement element = elementList.stream()
                .filter(webElement -> webElement.getText().trim().contains(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("No selector with value %s", value)));
        element.click();
    }

    public static void waitForAngularPendingRequestsToComplete(WebDriver driver) {

        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        StopWatch stopWatch = watchHandlerStart();
        wait.withMessage("Angular requests were not done in required timeout.")
                .withTimeout(60, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .until((ExpectedCondition<Boolean>) waitingDriver -> {
                    long pendingRequestCount = 1;
                    try {
                        Boolean isAngularLoaded = (Boolean) executeJavascript(driver, "angular && angular!=null && angular!=undefined;");

                        if (isAngularLoaded != null && !isAngularLoaded) {
                            return false;
                        }

                        pendingRequestCount = (long) executeJavascript(waitingDriver,
                                new StringBuilder()
                                        .append("var requests = angular.element('html').injector().get('$http').pendingRequests;")
                                        .append("return requests.filter(r => !r.url.includes(`/recommended`)).length;")
                                        .toString());
                    } catch (Exception e) {
                        error("Error retrieving pending request count! " + e.getCause());
                    }
                    return pendingRequestCount <= 0;
                });

        stopWatch = watchHandlerStop(stopWatch);
        info("Time spend on waiting for Angular: " + stopWatch);
    }

    public static void waitForFilesToBeDownloaded(Path downloadFolderPath, String fileMask) throws IOException, InterruptedException {

        info("Started waiting for Files.");
        StopWatch stopWatch = watchHandlerStart();

        boolean isDownloaded = new FluentWait<>(downloadFolderPath)
                .withTimeout(120, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .withMessage(String.format("File %s was not downloaded.", fileMask))
                .until(path -> {
                    boolean isFileDownloaded = false;
                    try {
                        Map<String, Long> filesSizeBefore = fetchFilesSize(path, fileMask);
                        Thread.sleep(1000);
                        Map<String, Long> filesSizeAfter = fetchFilesSize(path, fileMask);

                        if (filesSizeBefore.keySet().size() == filesSizeAfter.keySet().size()) {
                            isFileDownloaded = filesSizeAfter
                                    .entrySet()
                                    .stream()
                                    .anyMatch(file -> filesSizeBefore.get(file.getKey()) != file.getValue());
                        }

                    } catch (IOException | InterruptedException ignored) {
                        return false;
                    }
                    return isFileDownloaded;
                });

        stopWatch = watchHandlerStop(stopWatch);
        info("Time spend on waiting for Files: " + stopWatch);
    }

    private static Map<String, Long> fetchFilesSize(Path downloadFolderPath, String fileMask) throws IOException {
        return Files.list(downloadFolderPath)
                .filter(path -> path.getFileName().toString().matches(fileMask))
                .collect(Collectors.toMap(path -> path.getFileName().toString(), path -> path.toFile().length()));
    }


    public static void waitForJqueryPendingRequestsToComplete(WebDriver driver) throws Exception {
        long count = 1;
        for (int i = 0; i < CYCLES_TO_WAIT; i++) {
            count = pendingJqueryRequests(driver);
            if (count == 0) {
                break;
            }
            Thread.sleep(250);
        }

        if (count != 0) {
            throw new Exception("Timed out waiting for Page's background requests to finish.");
        }
    }

    private static long pendingJqueryRequests(WebDriver driver) throws Exception {
        String js = "count = $.active; return count;";
        try {
            return (Long) executeJavascript(driver, js);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error retrieving pending request count!");
        }
    }

    public static void selectByVisibleText(WebElement el, String text) {
        Select s = new Select(el);
        s.selectByVisibleText(text);
    }

    public static void selectByIndex(WebElement el, int index) {
        Select s = new Select(el);
        s.selectByIndex(index);
    }

    public static String convertStringListToSQL(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (String item : list) {
            sb.append("'" + item + "',");
        }

        return (sb.length() != 0) ? sb.toString().substring(0, sb.toString().length() - 1) : null;
    }

    public static void triggerInfiniteScroll(WebDriver driver) {
        PageUtil.executeJavascript(driver, "javascript:window.scrollTo(0,document.body.scrollHeight);");
    }

    public static void triggerAngularMethod(WebDriver driver, String scrollFunction) {
        String js = String.format("angular.element('#mainAngularView').scope().%s()", scrollFunction);
        PageUtil.executeJavascript(driver, js);
    }

    public static void navigateTo(WebDriver driver, String url) throws Exception {
        if (url == null || driver == null)
            throw new Exception("Driver and URL need to be provided!");
        driver.navigate().to(url);
    }

    public static void doubleClickElement(WebDriver driver, WebElement element) {
        Actions action = new Actions(driver);
        action.doubleClick(element).perform();
    }

    public static <T> T refreshPage(WebDriver driver, Class<T> pageObj) {
        driver.getCurrentUrl();
        return PageFactory.initElements(driver, pageObj);
    }

    public static <T> T clearCookiesAndRefreshPage(WebDriver driver, Class<T> pageObj) {
        clearCookies(driver);
        return refreshPage(driver, pageObj);
    }

    // Some thoughts on storing HAR data for analysis: https://gist.github.com/klepikov/5457750
    public static void dumpAllBrowserLogs(WebDriver driver) {
        dumpAllBrowserLogs(driver, null);
    }

    // Some thoughts on storing HAR data for analysis: https://gist.github.com/klepikov/5457750
    public static List<BrowserConsoleLogEntry> dumpAllBrowserLogs(WebDriver driver, String testName) {
        List<BrowserConsoleLogEntry> allLogEntries = new ArrayList<BrowserConsoleLogEntry>();

        try {

            // https://code.google.com/p/selenium/wiki/Logging
            Set<String> logs = driver.manage().logs().getAvailableLogTypes();
            SoftAssert sa = new SoftAssert();

            for (String log : logs) {
                String pageTestName = testName == null ? driver.getCurrentUrl() : testName;
                if (!pageTestName.contains("about:blank") && !pageTestName.contains("data:")) { // No info comes from about:blank
                    info("START [" + log + "] -- Logs for page or test [" + pageTestName + "]");
                    try {
                        LogEntries logEntries = driver.manage().logs().get(log);
                        for (LogEntry entry : logEntries) {
                            info(entry.getMessage());
                            allLogEntries.add(new BrowserConsoleLogEntry(log, entry.getLevel().getName(), entry.getMessage()));
                            if (log.equalsIgnoreCase("browser")) {
                                sa.assertNull("Error in browser console [" + entry.getMessage() + "]", entry.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        // May not be supported
                    }
                    info("END [" + log + "]-- Logs for page or test [" + pageTestName + "]");
                }
            }
            // Will validate all SoftAsserts, if any SoftAssert failed, it will fail the test
            // sa.assertAll();
        } catch (Exception e) {
            // We don't want tests to fail if there was an issue getting browser logs.
            warn("Error retrieving browser logs: " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
        }

        return allLogEntries;
    }

    public static void takeDebugScreenshot(WebDriver driver, String name) {
        if (name == null) {
            name = "debug_screenshot_" + System.currentTimeMillis();
        }
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File path = new File(name + ".png");
            FileUtils.copyFile(srcFile, path);
        } catch (Exception e) {
            error("There was an error creating the screenshot. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void findVisibleFrameAndSwitchToIt(WebDriver driver, By frameLocator) {
        WebElement editorFrame = PageUtil.findElement(driver, frameLocator, 10);

        if (editorFrame == null) {
            throw new NoSuchElementException("Editor Frame was not found.");
        }

        waitFor(driver, editorFrame);
        switchToFrame(driver, editorFrame);
    }

    public static void switchToFrame(WebDriver driver, WebElement frame) {
        info("Switching to frame =[" + frame + "]");
        driver.switchTo().frame(frame);
        info("Switched to frame =[" + frame + "]");
    }

    public static void hoverElementAndClick(WebDriver driver, WebElement element) {
        Actions hover = new Actions(driver);
        hover.moveToElement(element).perform();
        PageUtil.waitForElementBeActiveAndClick(driver, element);
    }

    public static void switchToFrame(WebDriver driver, String frameId) {
        info("Switching to frame with ID=[" + frameId + "]");
        driver.switchTo().frame(frameId);
        info("Switched to frame with ID=[" + frameId + "]");
    }

    public static void switchToDefaultFrame(WebDriver driver) {
        info("Switching to main Frame.");
        driver.switchTo().defaultContent();
        info("Switched to main Frame.");
    }

    public static void switchToParentFrame(WebDriver driver) {
        driver.switchTo().parentFrame();
    }

    public static void switchBrowserWindow(WebDriver driver) {
        PageUtil.waitForCountofWindowsToBe(driver, 2, 60);
        switchBrowserWindow(driver, window -> !window.equalsIgnoreCase(driver.getWindowHandle()));
    }

    public static void switchBrowserWindow(WebDriver driver, String switchToWindow, int msWaitBeforeSwitch) {
        try {
            Thread.sleep(msWaitBeforeSwitch);
        } catch (InterruptedException ignored) {
        }

        switchBrowserWindow(driver, switchToWindow::equalsIgnoreCase);
    }

    private static void switchBrowserWindow(WebDriver driver, Predicate<String> windowPredicate) {
        String newWindow = driver.getWindowHandles()
                .stream()
                .filter(windowPredicate)
                .findFirst()
                .orElseThrow(() -> new NoSuchWindowException("Can not find required window"));

        driver.switchTo().window(newWindow);
    }

    public static void clearCookies(WebDriver driver) {
        driver.manage().deleteAllCookies();
    }

    public static void sleep(int secondsToSleep) {
        sleepInMillis(secondsToSleep * 1000);

    }

    public static void sleepInMillis(int msToSleep) {
        try {
            Thread.sleep(msToSleep);
        } catch (InterruptedException e) {
        }

    }

    public static String getElementAttribute(WebElement element, String attributeName) {
        return element.getAttribute(attributeName);
    }

    /**
     * Method is checking define check box elements.
     * List of check box elements to check is adopting as method parameter
     *
     * @param checkboxes
     */
    public static void checkAllCheckboxes(List<WebElement> checkboxes) {
        checkCheckboxItems(checkboxes, checkboxes.size());
    }

    /**
     * Method is unchecking define List of check boxes from first to number defined in method parameter
     *
     * @param checkboxes
     * @param checkboxesToUncheck
     */
    public static void uncheckCheckboxItems(List<WebElement> checkboxes, int checkboxesToUncheck) {
        clickOnCheckBoxByCondition(checkboxes, WebElement::isSelected, checkboxesToUncheck);
    }

    private static void clickOnCheckBoxByCondition(List<WebElement> checkboxes, Predicate<WebElement> condition, int checkboxCount) {
        checkboxes.stream()
                .limit(checkboxCount)
                .filter(condition)
                .forEach(WebElement::click);
    }

    /**
     * Method is checking define List of checkboxes from first to number defined in method parameter
     *
     * @param checkboxes
     * @param checkboxesToCheck
     */
    public static void checkCheckboxItems(List<WebElement> checkboxes, int checkboxesToCheck) {
        clickOnCheckBoxByCondition(checkboxes, element -> !element.isSelected(), checkboxesToCheck);
    }

    /**
     * Method gets list of checkbox elements and list of elements names
     * returns elements which were selected from List of checkboxes
     */
    public static List<String> getSelectedCheckboxes(List<WebElement> checkboxes, List<WebElement> names) {
        List<String> positions = new ArrayList<String>();
        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).isSelected()) {
                positions.add(names.get(i).getText());
            }
        }
        return positions;
    }

    public static List<String> checkAllCheckboxesAndGetCheckedCheckboxes(List<WebElement> checkboxes, List<WebElement> names) {
        checkAllCheckboxes(checkboxes);
        return getSelectedCheckboxes(checkboxes, names);
    }

    public static List<String> checkCheckboxItemsAndGetCheckedCheckboxes(List<WebElement> checkboxes, int checkboxesToCheck, List<WebElement> names) {
        checkCheckboxItems(checkboxes, checkboxesToCheck);
        return getSelectedCheckboxes(checkboxes, names);
    }

    public static List<String> uncheckCheckboxItemsAndGetCheckedCheckboxes(List<WebElement> checkboxes, int checkboxesToUncheck, List<WebElement> names) {
        uncheckCheckboxItems(checkboxes, checkboxesToUncheck);
        return getSelectedCheckboxes(checkboxes, names);
    }

    /**
     * @param element
     * @param text
     */
    public static void clearInputAndSendKeys(WebElement element, String text) {
        if (text != null) {
            element.clear();
            element.sendKeys(text);
        }
    }


    /**
     * Step#1: Use <b>null</b> as argument to start StopWatch. </br>
     * Step#2: Use returned variable as argument to stop StopWatch. </br>
     * param sWatch
     *
     * @return
     */
    public static StopWatch watchHandlerStart() {
        StopWatch sWatch = new StopWatch();
        sWatch.start();
        return sWatch;
    }

    public static StopWatch watchHandlerStop(StopWatch sWatch) {
        try {
            sWatch.stop();
        } catch (IllegalStateException e) {
            info("StopWatch was not started.");
        }
        return sWatch;
    }

    public static List<String> getTextValueFromListOfElements(List<WebElement> list) {
        return list.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public static String fetchDate(int daysDifference, String dateFormat) {
        if (daysDifference < 0) {
            return LocalDateTime.now().minusDays(daysDifference * -1).format(fetchDateFormatter(dateFormat));
        }
        if (daysDifference > 0) {
            return LocalDateTime.now().plusDays(daysDifference).format(fetchDateFormatter(dateFormat));
        }
        return LocalDateTime.now().format(fetchDateFormatter(dateFormat));
    }

    private static DateTimeFormatter fetchDateFormatter(String format) {
        return DateTimeFormatter.ofPattern(format);
    }


    public static boolean isTextPresent(WebDriver driver, String str) {
        boolean isTextInBody;
        try {
            info(String.format("Looking for text [%s] in body", str));
            isTextInBody = (boolean) PageUtil.executeJavascript(driver, String.format("return document.body.innerText.trim().includes(\"%s\")", str));
            info(String.format("Text [%s] is%spresent in body", str, isTextInBody ? " " : " NOT "));
            return isTextInBody;
        } catch (Exception e) {
            error("Exception in isTextPresent method. Message: " + e.getMessage(), e);
            return false;
        }
    }


    public static void findElementAndClick(WebDriver driver, By locator) {
        waitFor(driver, locator, 1);
        findElement(driver, locator, 5).click();
    }

    public static File[] findFiles(String regexp, String folder) {
        File dir = new File(folder);

        return dir.listFiles((dir1, name) -> name.matches(regexp));
    }

    public static List<String> checkZipFile(File file, List<String> filenames) throws IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            return zipFile.stream()
                    .map(ZipEntry::getName)
                    .map(name -> name.replaceAll("(\\..*)", ""))
                    .filter(o -> !filenames.contains(o))
                    .collect(Collectors.toList());
        }

    }

    public static void clickFirstVisibleElement(List<WebElement> webElements, String exceptionReason) {
        findFirstVisibleElement(webElements, exceptionReason).click();
    }

    private static WebElement findFirstVisibleElement(List<WebElement> webElements, String exceptionReason) {
        return webElements.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new org.openqa.selenium.NoSuchElementException(exceptionReason));
    }

    public static void typeTextToFirstVisibleElement(List<WebElement> webElements, String exceptionReason, String text) {
        findFirstVisibleElement(webElements, exceptionReason).sendKeys(text);
    }

    public static void typeIntoRichTextEditor(WebDriver driver, String content) {
        typeIntoRichTextEditor(driver, By.cssSelector("iframe.cke_wysiwyg_frame"), By.cssSelector("body.cke_editable"), content);
    }

    public static void typeIntoRichTextEditor(WebDriver driver, By editorFrameLocator, By editorBody, String content) {
        PageUtil.findVisibleFrameAndSwitchToIt(driver, editorFrameLocator);

        WebElement ckEditorBody = PageUtil.findElement(driver, editorBody);
        ckEditorBody.sendKeys(content);

        PageUtil.switchToDefaultFrame(driver);
    }

    public static String getTextFromRichTextEditor(WebDriver driver, By editorFrameLocator, By editorBody) {
        PageUtil.findVisibleFrameAndSwitchToIt(driver, editorFrameLocator);

        WebElement ckEditorBody = PageUtil.findElement(driver, editorBody);
        String textFromBlock = ckEditorBody.getText();

        PageUtil.switchToDefaultFrame(driver);
        return textFromBlock;
    }

    public static String getTextFromRichTextEditor(WebDriver driver) {
        return getTextFromRichTextEditor(driver, By.cssSelector("iframe.cke_wysiwyg_frame"), By.cssSelector("body.cke_editable"));
    }

    public static String getCssValue(WebElement element, String cssParameter) {
        return element.getCssValue(cssParameter);
    }

    public static void waitForElementBeActiveAndClick(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public static boolean waitForElementBeEnabled(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.elementToBeSelected(element));
    }

    public static void waitForElementBeVisible(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
    }

    public static String getValueAttribute(WebDriver driver, WebElement element) {
        return element.getAttribute("value");
    }

    public static void clickOnInvisibleElement(WebDriver driver, WebElement element) {

        String script = "var object = arguments[0];"
                + "var theEvent = document.createEvent(\"MouseEvent\");"
                + "theEvent.initMouseEvent(\"click\", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "object.dispatchEvent(theEvent);";

        ((JavascriptExecutor) driver).executeScript(script, element);
    }

    public static void removeHelpElement(WebDriver driver) {
        if (PageUtil.waitFor(driver, By.id("launcher"))) {
            info("Removing Help element.");
            PageUtil.executeJavascript(driver, "document.getElementById('launcher').remove()");
        } else {
            warn("Help element has not appeared.");
        }
    }

    public static long convertDateToLong(String date) {
        Date d = null;
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            error(e.getMessage());
        }
        return d.getTime();
    }

    public static List<Long> getListOfLongDates(List<String> list) {
        List<Long> datesToNumber = list.stream()
                .map(item -> convertDateToLong(item.trim()))
                .collect(toList());

        return datesToNumber;
    }

    public static <T extends Comparable> boolean isCollectionSortedDescending(List<T> collection) {
        return Ordering.natural().reverse().isOrdered(collection);
    }

    public static <T extends Comparable> boolean isCollectionSortedAscending(List<T> collection) {
        return Ordering.natural().isOrdered(collection);
    }

    public static boolean verifyDateFormat(String pattern, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf.parse(date, new ParsePosition(0)) != null;
    }

    public static void waitForTextInElementToBe(WebDriver driver, By element, String text) {
        WebDriverWait waiter = new WebDriverWait(driver, 10);
        waiter.pollingEvery(500, TimeUnit.MILLISECONDS)
                .until(ExpectedConditions.textToBePresentInElementLocated(element, text));
    }
}

