package com.company.pages.executeAutomation;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.company.pages.BasePage;

import utils.PageUtil;

public class HomePage extends BasePage {

    public static final String PAGE_NAME = "Execute automation Home Page";

    @FindBy(id = "details")
    public WebElement formDetails;

    @FindBy(id = "menu-button")
    public WebElement menuButton;

    @FindBy(xpath = "//*[@id=\"cssmenu\"]/ul/li[1]/a")
    private WebElement logoutLink;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage(WebDriver driver, String url) throws Exception {
        super(driver);
        if (StringUtils.isBlank(url)) {
            throw new Exception("No URL was provided for " + PAGE_NAME);
        }
        driver.navigate().to(url);
        checkForIExplorerSslCertWarning();
        PageFactory.initElements(driver, this);
    }

    public void logout() {
        if(isElementDisplayed(menuButton, 10)){
            menuButton.click();
        }

        PageUtil.waitForElementBeVisible(driver, logoutLink);
        logoutLink.click();
    }

    public void reinitializePage() {
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        return formDetails.isDisplayed();
    }

}

