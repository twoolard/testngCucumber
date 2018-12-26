package com.company.pages.executeAutomation;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.company.pages.BasePage;

import utils.LogUtil;

public class LoginPage extends BasePage {

    public static final String PAGE_NAME = "Execute Automation Selenium Test Site";

    @FindBy(id = "userName")
    private WebElement loginForm;

    @FindBy(name = "UserName")
    public WebElement txtUserName;

    @FindBy(name = "Password")
    public WebElement txtPassword;

    @FindBy(name = "Login")
    public WebElement btnLogin;

    @FindBy(id = "Initial")
    public WebElement title;

    public LoginPage(WebDriver driver, String url) throws Exception {
        super(driver);

        if (StringUtils.isEmpty(url)) {
            throw new Exception("No URL was provided for " + PAGE_NAME);
        }

        driver.navigate().to(url);
        PageFactory.initElements(driver, this);
    }

    public HomePage login(String usrEmail, String usrPassword) throws Exception {
        if (StringUtils.isEmpty(usrEmail) || StringUtils.isEmpty(usrPassword)) {
            throw new Exception("Login requires a user email and password!");
        }

        if (btnLogin.isDisplayed()) {
            txtUserName.clear();
            txtUserName.sendKeys(usrEmail);
            txtPassword.clear();
            txtPassword.sendKeys(usrPassword);
            btnLogin.submit();
            LogUtil.info("User [" + usrEmail + "], Password [" + usrPassword + "]");
        } else {
            throw new Exception("The Login button is not enabled.");
        }

        return PageFactory.initElements(driver, HomePage.class);
    }

    public boolean isPageLoaded() {
        return isElementDisplayed(loginForm, 15);
    }


}

