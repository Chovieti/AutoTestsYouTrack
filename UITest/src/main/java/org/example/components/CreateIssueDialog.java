package org.example.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

public class CreateIssueDialog {
  @FindBy(xpath = "//textarea[@data-test='summary']")
  private SelenideElement titleInput;
  @FindBy(xpath = "//div[@data-test='wysiwyg-editor-content']")
  private SelenideElement descriptionInput;
  @FindBy(xpath = "//button[@data-test='submit-button']")
  private SelenideElement submitButton;
  @FindBy(xpath = "//div[@data-test='alert' and @data-test-type='success']")
  private SelenideElement successAlert;
  @FindBy(xpath = "//div[@data-test='alert-container']//a[@data-test='ring-link']")
  private SelenideElement issueLinkInAlert;
  @FindBy(xpath = "//button[@data-test='ring-dialog-close-button']")
  private SelenideElement closeDialogButton;

  public CreateIssueDialog() {
    Selenide.page(this);
  }

  public CreateIssueDialog setTitle(String title) {
    titleInput.shouldBe(Condition.visible).setValue(title);
    return this;
  }

  public CreateIssueDialog setDescription(String description) {
    descriptionInput.shouldBe(Condition.visible).setValue(description);
    return this;
  }

  public String create() {
    submitButton.shouldBe(Condition.visible).click();
    successAlert.shouldBe(Condition.visible);
    String id = issueLinkInAlert.shouldBe(Condition.visible).getText();
    closeDialogButton.shouldBe(Condition.visible).click();
    return id;
  }
}
