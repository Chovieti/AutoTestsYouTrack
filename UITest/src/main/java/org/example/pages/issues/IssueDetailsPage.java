package org.example.pages.issues;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.example.components.CommentSection;
import org.example.components.NavigationBar;
import org.example.pages.BasePage;
import org.example.pages.agile.AgileBoardPage;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class IssueDetailsPage extends BasePage<IssueDetailsPage> {
  private final NavigationBar nav = new NavigationBar();
  private CommentSection comment = new CommentSection();

  @FindBy(xpath = "//div[@data-test='issue-toolbar']//button[@data-test='edit-issue-button']")
  private SelenideElement toolbarEdit;
  @FindBy(xpath = "//div[@data-test='issue-toolbar']//button[@aria-haspopup='true']")
  private SelenideElement toolbarMore;
  @FindBy(xpath = "//div[@data-test='ring-popup']//div[@data-test='ring-list']//div[contains(@id, ':delete')]//button")
  private SelenideElement toolbarDelete;
  @FindBy(xpath = "//button[@data-test='ring-dialog-close-button']")
  private SelenideElement closeButton;
  @FindBy(xpath = "//div[@data-test='ring-dialog-container ring-confirm']//button[@data-test='confirm-ok-button']")
  private SelenideElement confirmDialogButton;
  @FindBy(xpath = "//textarea[@data-test='summary']")
  private SelenideElement textareaForTitle;
  @FindBy(xpath = "//div[@data-test='wysiwyg-editor']//div[@contenteditable='true']")
  private SelenideElement textareaForDescription;
  @FindBy(xpath = "//button[@data-test='save-button']")
  private SelenideElement saveButton;

  @Override
  public IssueDetailsPage waitForPageLoaded() {
    nav.shouldBeVisible();
    toolbarMore.shouldBe(Condition.visible);
    return this;
  }

  public IssuesListPage deleteIssue() {
    toolbarMore.click();
    toolbarDelete.shouldBe(Condition.visible).click();
    confirmDialogButton.shouldBe(Condition.visible).click();
    return new IssuesListPage();
  }

  public IssueDetailsPage editIssue(String title, String description) {
    toolbarEdit.shouldBe(Condition.visible).click();
    textareaForTitle.shouldBe(Condition.visible);
    executeJavaScript("arguments[0].value = '';", textareaForTitle);
    textareaForTitle.setValue(title);
    textareaForDescription.shouldBe(Condition.visible);
    executeJavaScript("arguments[0].innerHTML = '';", textareaForDescription);
    textareaForDescription.setValue(description);
    saveButton.click();
    return this;
  }

  public IssuesListPage close() {
    closeButton.click();
    return new IssuesListPage();
  }

  public IssueDetailsPage addComment(String text) {
    comment.addComment(text);
    return this;
  }

  public IssueDetailsPage findComment(String text) {
    comment.checkCommentExists(text);
    return this;
  }

  public IssuesListPage goToIssues() {
    nav.clickIssues();
    return new IssuesListPage();
  }

  public AgileBoardPage goToAgile() {
    nav.clickAgileBoard();
    return new AgileBoardPage();
  }
}
