package org.example.pages.issues;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.example.components.CreateIssueDialog;
import org.example.components.NavigationBar;
import org.example.pages.BasePage;
import org.example.util.RetryUtils;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$$x;

public class IssuesListPage extends BasePage<IssuesListPage> {
  private final NavigationBar nav = new NavigationBar();

  @FindBy(xpath = "//div[@data-test='ring-select search-field']//input[@data-test='ring-select__focus']")
  private SelenideElement searchInput;
  @FindBy(xpath = "//div[@data-test='toolbar-search']//button[@data-test='search-button']")
  private SelenideElement searchButton;
  @FindBy(xpath = "//*[@data-test='createIssueButton']")
  private SelenideElement newIssueButton;
  @FindBy(xpath = "//th[@data-test='ring-table-header-cell id']//div[contains(@class, 'wrapper') and contains(text(), 'ID')]")
  private SelenideElement idSortedButton;

  @Override
  public IssuesListPage waitForPageLoaded() {
    nav.shouldBeVisible();
    newIssueButton.shouldBe(Condition.visible);
    return this;
  }

  public IssuesListPage searchByTitle(String query) {
    searchInput.shouldBe(Condition.visible).setValue(query);
    searchButton.shouldBe(Condition.visible).click();
    return this;
  }

  public IssueDetailsPage openById(String query) {
    return RetryUtils.retry(3, 1000, () -> {
      searchInput.shouldBe(Condition.visible).setValue(query);
      searchButton.shouldBe(Condition.visible).click();
      return new IssueDetailsPage().waitForPageLoaded();
    });
  }

  public IssuesListPage sortById() {
    idSortedButton.shouldBe(Condition.visible).click();
    return this;
  }

  public SelenideElement getIssueRowByTitle(String title) {
    return $$x("//div[@data-test='ring-table-wrapper']//tbody/tr")
        .findBy(Condition.text(title));
  }

  public boolean checkIssueByIdExists(String id) {
    return RetryUtils.retry(3, 1000, () -> {
      openById(id);
      return true;
    }) != null;
  }

  public boolean checkIssueByTitleExists(String title) {
    return RetryUtils.retry(3, 1000, () -> {
      searchByTitle(title);
      getIssueRowByTitle(title).shouldBe(Condition.visible, Duration.ofMillis(1500));
      return true;
    }) != null;
  }

  public boolean checkIssueByTitleNotExists(String title) {
    return RetryUtils.retry(3, 1000, () -> {
      searchByTitle(title);
      getIssueRowByTitle(title).shouldNotBe(Condition.visible, Duration.ofMillis(1500));
      return true;
    }) != null;
  }

  public IssueDetailsPage openIssueByTitle(String title) {
    getIssueRowByTitle(title).click();
    return new IssueDetailsPage();
  }

  public CreateIssueDialog openCreateIssueDialog() {
    newIssueButton.click();
    return new CreateIssueDialog();
  }

  public IssuesListPage goToIssues() {
    nav.clickIssues();
    return this;
  }
}
