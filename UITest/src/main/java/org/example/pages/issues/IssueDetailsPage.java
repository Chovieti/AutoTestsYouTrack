package org.example.pages.issues;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.example.components.CommentSection;
import org.example.components.NavigationBar;
import org.example.pages.BasePage;
import org.example.util.RetryUtils;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class IssueDetailsPage extends BasePage<IssueDetailsPage> {
  private final NavigationBar nav = new NavigationBar();
  private final CommentSection comment = new CommentSection();

  @FindBy(xpath = "//div[@data-test='issue-toolbar']//button[@data-test='edit-issue-button']")
  private SelenideElement toolbarEdit;
  @FindBy(xpath = "//div[@data-test='issue-toolbar']//button[@aria-haspopup='true']")
  private SelenideElement toolbarMore;
  @FindBy(xpath = "//div[@data-test='ring-popup']//div[@data-test='ring-list']//div[contains(@id, ':delete')]//button")
  private SelenideElement toolbarDelete;

  @FindBy(xpath = "//button[@data-test='ring-dialog-close-button']")
  private SelenideElement dialogCloseButton;
  @FindBy(xpath = "//div[@data-test='ring-dialog-container ring-confirm']//button[@data-test='confirm-ok-button']")
  private SelenideElement confirmDialogButton;

  @FindBy(xpath = "//textarea[@data-test='summary']")
  private SelenideElement textareaForTitle;
  @FindBy(xpath = "//div[@data-test='wysiwyg-editor']//div[@contenteditable='true']")
  private SelenideElement textareaForDescription;
  @FindBy(xpath = "//button[@data-test='save-button']")
  private SelenideElement saveButton;

  @FindBy(xpath = "(//div[@data-test='fields-sidebar']//div[@data-test='field'])[3]//button[@role='combobox']")
  private SelenideElement executorChoice;
  @FindBy(xpath = "(//div[@data-test='fields-sidebar']//div[@data-test='field'])[4]//button[@role='combobox']")
  private SelenideElement stageChoice;
  @FindBy(xpath = "//input[@data-test-custom='ring-select-popup-filter-input']")
  private SelenideElement stageFilterInput;

  @FindBy(xpath = "//h1[@data-test='ticket-summary']")
  private SelenideElement titleField;
  @FindBy(xpath = "//section[@data-test='ticket-body']//div[contains(@class, 'description')]")
  private SelenideElement descriptionField;
  @FindBy(xpath = "(//div[@data-test='fields-sidebar']//div[@data-test='field'])[3]//span[@data-test='ring-tooltip field-value']")
  private SelenideElement executorField;
  @FindBy(xpath = "(//div[@data-test='fields-sidebar']//div[@data-test='field'])[4]//span[@data-test='ring-tooltip field-value']")
  private SelenideElement stageField;

  @Override
  public IssueDetailsPage waitForPageLoaded() {
    nav.shouldBeVisible();
    toolbarMore.shouldBe(Condition.visible);
    return this;
  }

  public IssuesListPage deleteIssue() {
    toolbarMore.shouldBe(visible).click();
    toolbarDelete.shouldBe(Condition.visible).click();
    confirmDialogButton.shouldBe(Condition.visible).click();
    return new IssuesListPage();
  }

  public IssueDetailsPage editIssue(String title, String description) {
    toolbarEdit.shouldBe(Condition.visible).click();

    textareaForTitle.shouldBe(Condition.visible);
    clearTitle();
    textareaForTitle.setValue(title);

    textareaForDescription.shouldBe(Condition.visible);
    clearDescription();
    textareaForDescription.setValue(description);
    saveButton.click();

    saveButton.shouldBe(Condition.disappear);
    titleField.shouldHave(Condition.text(title));
    return this;
  }

  private void clearTitle() {
    RetryUtils.retryVoid(3, 1000, () -> {
      textareaForTitle.shouldBe(Condition.visible);
        executeJavaScript("arguments[0].value = '';", textareaForTitle);
        textareaForTitle.shouldHave(exactValue(""), Duration.ofMillis(1500));
    });
  }

  private void clearDescription() {
    RetryUtils.retryVoid(3, 1000, () -> {
        textareaForDescription.shouldBe(Condition.visible);
        executeJavaScript("arguments[0].innerHTML = '';", textareaForDescription);
        textareaForDescription.shouldBe(Condition.empty, Duration.ofMillis(1500));
    });
  }

  public IssuesListPage close() {
    dialogCloseButton.click();
    return new IssuesListPage();
  }

  public IssueDetailsPage addComment(String text) {
    comment.addComment(text);
    return this;
  }

  public boolean checkCommentExists(String text) {
    return comment.checkCommentExists(text);
  }

  public IssueDetailsPage choiceExecutor(String login) {
    executorChoice.shouldBe(Condition.visible).click();
    $$x("//span[@data-test='ring-list-item-custom ring-list-item-action ring-list-item']")
        .findBy(Condition.text(login))
        .shouldBe(Condition.visible)
        .click();

    executorField.shouldHave(Condition.text(login));
    return this;
  }

  public IssueDetailsPage choiceStage(String stage) {
    stageChoice.shouldBe(Condition.visible).click();
    stageFilterInput.shouldBe(Condition.visible).setValue(stage);
    $$x("//div[@data-test='ring-popup']//button[.//span[@data-test='ring-list-item-label']]")
        .findBy(Condition.text(stage))
        .shouldBe(Condition.visible)
        .click();

    stageField.shouldHave(Condition.text(stage));
    return this;
  }

  public String getTitleText() {
    return titleField.shouldBe(Condition.visible).getText();
  }

  public String getDescriptionText() {
    return descriptionField.shouldBe(Condition.visible).getText();
  }

  public String getExecutorText() {
    return executorField.shouldBe(Condition.visible).getText();
  }

  public String getStageText() {
    return stageField.shouldBe(Condition.visible).getText();
  }

  public IssuesListPage goToIssues() {
    nav.clickIssues();
    return new IssuesListPage();
  }
}
