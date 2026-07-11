package org.example.pages.issues;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.example.components.CreateIssueDialog;
import org.example.components.NavigationBar;
import org.example.pages.BasePage;
import org.example.pages.agile.AgileBoardPage;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$x;

public class IssuesListPage extends BasePage<IssuesListPage> {
  private final NavigationBar nav = new NavigationBar();

  @FindBy(xpath = "//div[@data-test='ring-select search-field']//input[@data-test='ring-select__focus']")
  private SelenideElement searchInput;
  @FindBy(xpath = "//div[@data-test='toolbar-search']//button[@data-test='search-button']")
  private SelenideElement searchButton;
  @FindBy(xpath = "//a[@data-test='createIssueButton']")
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
    searchInput.setValue(query);
    searchButton.shouldBe(Condition.visible).click();
    return this;
  }

  public IssueDetailsPage searchById(String query) {
    searchInput.setValue(query);
    searchButton.shouldBe(Condition.visible).click();
    return new IssueDetailsPage();
  }

  public IssuesListPage sortById() {
    idSortedButton.shouldBe(Condition.visible).click();
    return this;
  }

  public SelenideElement getIssueRowByTitle(String title) {
    String xpath = String.format(
            "//div[@data-test='ring-table-wrapper']//tbody" +
            "/tr[.//td[@data-test='ring-table-cell summary']" +
            "//*[@data-test-title='%s']]", title
    );
    return $x(xpath);
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

  public AgileBoardPage goToAgile() {
    nav.clickAgileBoard();
    return new AgileBoardPage();
  }
}
