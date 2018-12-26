package com.company.steps.executeAutomation;

import static org.testng.Assert.assertTrue;


import com.company.pages.executeAutomation.HomePage;
import com.company.pages.executeAutomation.LoginPage;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import utils.BaseUtil;
import utils.properties.TestConstant;
import utils.properties.TestProperties;

public class LoginSteps extends BaseUtil {

    private BaseUtil base;
    public static LoginPage page;
    public static HomePage homePage;

    public LoginSteps(BaseUtil base) {
        this.base = base;
    }

    @Given("^I navigate to the login page$")
    public void iNavigateToTheLoginPage() throws Throwable {
        page = new LoginPage(base.driver, TestProperties.getProperty(TestConstant.EX_URL));
    }

    @Then("^I should see the userform page$")
    public void iShouldSeeTheUserformPage() throws Throwable {
        assertTrue(homePage.isPageLoaded());
    }

    @And("^I login using ([^\"]*) and ([^\"]*)$")
    public void iLoginUsingUsernameAndPassword(String userName, String password) throws Throwable {
        homePage = page.login(userName,password);
    }
}