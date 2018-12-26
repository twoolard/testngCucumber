package com.company.pages.sauceLabs;

import static utils.PageUtil.clearInputAndSendKeys;
import static utils.PageUtil.waitFor;
import static utils.PageUtil.waitForElementBeActiveAndClick;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.company.pages.BasePage;

public class CommentTextPage extends BasePage {

    public static final String PAGE_NAME = "SAUCE LABS";

    @FindBy(linkText = "i am a link")
    private WebElement theActiveLink;

    @FindBy(id = "your_comments")
    private WebElement yourCommentsSpan;

    @FindBy(id = "comments")
    private WebElement commentsTextAreaInput;

    @FindBy(id = "submit")
    private WebElement submitButton;

    public CommentTextPage(WebDriver driver) {
        super(driver);
    }

    public boolean isPageLoaded() {
        return isElementDisplayed(theActiveLink, 15);
    }

    public CommentTextPage(WebDriver driver, String url) throws Exception {
        super(driver);
        if (StringUtils.isBlank(url)) {
            throw new Exception("No URL was provided for " + PAGE_NAME);
        }
        driver.navigate().to(url);
        checkForIExplorerSslCertWarning();
        PageFactory.initElements(driver, this);
    }

    public void reinitializePage() {
        PageFactory.initElements(driver, this);
    }


    public void submitComment(String text) {
        clearInputAndSendKeys(commentsTextAreaInput, text);
        waitForElementBeActiveAndClick(driver, submitButton);
        waitFor(driver, yourCommentsSpan, text);
    }

    public String getSubmittedCommentText() {
        return this.yourCommentsSpan.getText();
    }


}
