package org.example.pages;

import org.example.components.NavigationBar;
import org.example.pages.agile.AgileBoardPage;
import org.example.pages.issues.IssuesListPage;

import static com.codeborne.selenide.Selenide.open;

public class MainPage extends BasePage<MainPage> {
  private final NavigationBar nav = new NavigationBar();

  public MainPage openPage(String baseUrl) {
    open(baseUrl);
    return this;
  }

  @Override
  public MainPage waitForPageLoaded() {
    nav.shouldBeVisible();
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
