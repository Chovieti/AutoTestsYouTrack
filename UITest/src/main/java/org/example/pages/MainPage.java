package org.example.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.example.components.NavigationBar;
import org.example.pages.issues.IssuesListPage;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.open;

public class MainPage extends BasePage<MainPage> {
  private final NavigationBar nav = new NavigationBar();

  @FindBy(xpath = "//button[@data-test='ring-link login-button']")
  private SelenideElement loginButton;

  public MainPage openPage(String baseUrl) {
    open(baseUrl);
    return this;
  }

  @Override
  public MainPage waitForPageLoaded() {
    nav.shouldBeVisible();
    return this;
  }

  public boolean loggedIn() {
    try {
      loginButton.shouldNotBe(Condition.visible);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public IssuesListPage goToIssues() {
    nav.clickIssues();
    return new IssuesListPage();
  }
}
