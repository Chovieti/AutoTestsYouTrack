package org.example.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

public class NavigationBar {
  @FindBy(xpath = "//a[@data-test='ring-link issues-button']")
  private SelenideElement issuesLink;
  @FindBy(xpath = "//a[@data-test='ring-link agile-boards-button']")
  private SelenideElement agileBoardLink;

  public NavigationBar() {
    Selenide.page(this);
  }

  public void clickIssues() {
    issuesLink.shouldBe(Condition.visible).click();
  }

  public void clickAgileBoard() {
    agileBoardLink.shouldBe(Condition.visible).click();
  }

  public void shouldBeVisible() {
    issuesLink.shouldBe(Condition.visible);
    agileBoardLink.shouldBe(Condition.visible);
  }
}
