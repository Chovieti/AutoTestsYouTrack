package org.example.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

public class CreateIssueDialog {
  @FindBy(xpath = "//textarea[@data-test='summary']")
  private SelenideElement titleInput;
  @FindBy(xpath = "//button[@data-test='submit-button']")
  private SelenideElement submitButton;
  @FindBy(xpath = "//div[@data-test='alert' and @data-test-type='success']")
  private SelenideElement successAlert;
  @FindBy(xpath = "//div[@data-test='alert-container']//a[@data-test='ring-link']")
  private SelenideElement issueLinkInAlert;

  public CreateIssueDialog() {
    Selenide.page(this);
  }

  public CreateIssueDialog setTitle(String title) {
    titleInput.shouldBe(Condition.visible).setValue(title);
    return this;
  }

  public String create() {
    submitButton.shouldBe(Condition.visible).click();
    successAlert.shouldBe(Condition.visible);
    return issueLinkInAlert.shouldBe(Condition.visible).getText();
  }
}
