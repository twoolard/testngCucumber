package com.company.pages;




import static utils.LogUtil.error;
import static utils.LogUtil.info;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.LogUtil;
import utils.PageUtil;
import utils.browser.BrowserType;
import utils.browser.BrowserUtil;


public class BasePage {

    // Loading spinner on button, while image is uploading
    protected By buttonSpinner = By.cssSelector("div.spinner");

    protected WebDriver driver;

    @FindBy(xpath = "//div[contains(@class,'alert-success')]")
    public WebElement successMessage;

    // Loading spinner on page
    protected final String imgSpinnerXpath = "img[src*='spinner.gif']";
    @FindBy(css = imgSpinnerXpath)
    protected WebElement imgSpinner;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }


    public void init() {
        PageFactory.initElements(driver, this);
    }

    public void reInitPage() {
        init();
    }

    protected WebElement findElementByXpath(String xpath) {
        WebElement item = null;
        try {
            item = driver.findElement(By.xpath(xpath));
        } catch (Exception e) {
            // Not found
        }

        return item;
    }

    protected WebElement findElementByLinkText(String linkText) {
        WebElement item = null;
        try {
            item = driver.findElement(By.linkText(linkText));
        } catch (Exception e) {
            // Not found
        }
        return item;
    }

    protected WebElement findElementByPartialLinkText(String linkText) {
        WebElement item = null;
        try {
            item = driver.findElement(By.partialLinkText(linkText));
        } catch (Exception e) {
            // Not found
        }
        return item;
    }

    /**
     * Gives indication if element is displayed
     *
     * @param element The WebElement whose display status should be validated
     * @return true or false
     * @throws Exception
     */
    protected boolean isElementDisplayed(WebElement element, int waitSeconds) {
        return PageUtil.waitFor(driver, element, waitSeconds);
    }

    protected boolean isElementDisplayed(List<WebElement> element, int waitSeconds) {
        return PageUtil.waitFor(driver, element, waitSeconds);
    }

    /**
     * Gives indication if element is displayed
     *
     * @param element The WebElement whose display status should be validated
     * @return true or false
     */
    protected boolean isElementDisplayed(WebElement element) {
        return isElementDisplayed(element, 30);
    }

    /**
     * Used to wait for an element to disappear. This serves the use case where an ajax call was made and we are waiting for the spinner to disappear,
     * or an overlayed form page to disappear after submission.
     *
     * @param locator Locator to WebElement that should disappear (visibility is not true)
     * @return true or false
     */
    protected boolean waitForElementToDisappear(By locator) {
        return waitForElementToDisappear(locator, 10);
    }

    /**
     * Used to wait for an element to disappear. This serves the use case where an ajax call was made and we are waiting for the spinner to disappear,
     * or an overlayed form page to disappear after submission.
     *
     * @param locator Locator to WebElement that should disappear (visibility is not true)
     * @return true or false
     */
    protected boolean waitForElementToDisappear(By locator, int seconds) {
        boolean displayed = true;
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
        try {
            WebElement el = driver.findElement(locator);
            PageUtil.waitForInvisibility(driver, locator);
            displayed = el.isDisplayed();
        } catch (Exception e) {
            displayed = false;
        }

        info("Checking that an element is invisible (visibility=false). Result [" + displayed + "]");
        return displayed;
    }

    /**
     * For IE, there is no easy to override warning for self-signed certs automatically. Call this method and it will find the override link to allow
     * the user to pass through.
     */
    protected void checkForIExplorerSslCertWarning() {
        // Only applicable to IE
        if (BrowserUtil.getCurrentBrowserType() != BrowserType.IE)
            return;

        try {
            driver.navigate().to("javascript:document.getElementById('overridelink').click()");
        } catch (Exception e) {
            // Didn't find it so move on
        }
    }

    /**
     * Given a WebElement, the method will extract the text, trim it, and will remove commas.
     *
     * @param e WebElement with text that needs to be sanitized.
     * @return
     */
    protected String sanitize(WebElement e) {
        return sanitize(e.getText());
    }

    /**
     * Given a String, the method will trim it and remove commas.
     *
     * @param s WebElement with text that needs to be sanitized.
     * @return
     */
    protected String sanitize(String s) {
        return s.trim().replaceAll(",", "");
    }

    /**
     * Given a WebElement the method will extract text, sanitize it, and convert '-' to '0' (zero)
     *
     * @param el
     * @return '0' as a String
     */
    protected String convertDashToZero(WebElement el) {
        String result = null;
        try {
            result = convertDashToZero(el.getText());
        } catch (Exception ex) {
            error(ex.getMessage());
        }
        return result;
    }

    /**
     * Given a String the method will sanitize the string, and convert '-' to '0' (zero)
     *
     * @param s
     * @return '0' as a String
     */
    protected String convertDashToZero(String s) {
        return sanitize(s).replace("-", "0");
    }

    public boolean isTextPresent(String str) {
        return PageUtil.isTextPresent(driver, str);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }


    public void waitForActionToComplete() {
        waitForActionToComplete(15);
    }

    /**
     * Wait for action to complete.
     * Uses spinner animation at button to trace
     *
     * @param timeout Wait timeout in SECONDS
     * @throws Exception
     */
    public void waitForActionToComplete(int timeout) {
        try {
            PageUtil.waitForAngularPendingRequestsToComplete(driver);
            PageUtil.waitForInvisibility(driver, buttonSpinner);
        } catch (Exception e) {
            LogUtil.error("Spinner did not disappeared in requested amount of time.");
        }
    }

    /**
     * For Admin pages.
     * Wait AJAX request to complete.
     *
     * @throws Exception
     */
    public void waitForAjaxOnAdminPage() throws Exception {
        By ajaxStatus = By.id("AjaxStatus");

        PageUtil.waitFor(driver, ajaxStatus);
        try {
            PageUtil.waitForInvisibility(driver, ajaxStatus, 120);
        } catch (Exception e) {
            throw new Exception(ajaxStatus.toString() + " did not disappeared. Possible Ajax request did not complete.");
        }
    }

    public boolean checkSuccessfulMessage() {
        PageUtil.waitForElementBeVisible(driver, successMessage);
        return successMessage.isDisplayed();
    }
}

