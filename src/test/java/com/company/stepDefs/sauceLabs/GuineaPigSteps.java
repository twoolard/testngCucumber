package com.company.stepDefs.sauceLabs;

import java.util.UUID;

import com.company.pages.sauceLabs.CommentTextPage;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import utils.BaseUtil;
import utils.properties.TestConstant;
import utils.properties.TestProperties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.testng.annotations.Test;

@Test(groups = {"Test"})
public class GuineaPigSteps extends BaseUtil {
    private BaseUtil base;
    public String commentInputText;
    public static CommentTextPage page;


    public GuineaPigSteps(BaseUtil base) {
        this.base = base;
    }

    @Given("^I am on the Guinea Pig homepage$")
    public void iAmOnTheGuineaPigHomepage() throws Throwable {
        page = new CommentTextPage(base.driver,  TestProperties.getProperty(TestConstant.SAUCE_URL));
    }

    @When("^I submit a comment$")
    public void iSubmitAComment() throws Throwable {
        commentInputText = UUID.randomUUID().toString();
        page.submitComment(commentInputText);
    }

    @Then("^I should see that comment displayed$")
    public void iShouldSeeThatCommentDisplayed() throws Throwable {
        assertThat(page.getSubmittedCommentText(), containsString(commentInputText));
    }
}
