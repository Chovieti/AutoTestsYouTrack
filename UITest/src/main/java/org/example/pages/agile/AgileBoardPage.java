package org.example.pages.agile;

import com.codeborne.selenide.SelenideElement;
import org.example.components.NavigationBar;
import org.example.pages.BasePage;
import org.example.pages.issues.IssuesListPage;

import static com.codeborne.selenide.Selenide.$x;

public class AgileBoardPage extends BasePage<AgileBoardPage> {
  private final NavigationBar nav = new NavigationBar();
  private static final String SELECTING_ASSIGNED_USER = ".//span[contains(@class, 'yt-agile-card__user__editable')]";

  @Override
  public AgileBoardPage waitForPageLoaded() {
    nav.shouldBeVisible();
    return this;
  }

  public SelenideElement getColumnByName(String columnName) {
    String xpath = String.format(
        "//table[contains(@class,'yt-board-container')]//tbody/tr[contains(@class,'yt-agile-table__row')][1]" +
            "/td[count(//table[contains(@class,'yt-agile-table__row-container__head')]" +
            "//td[contains(@class,'yt-agile-table__row__cell_head')]" +
            "/div[contains(@class,'yt-agile-table__column-name') and normalize-space(text())='%s']" +
            "/ancestor::td/preceding-sibling::td)+1]", columnName
    );
    return $x(xpath);
  }

  public SelenideElement getIssueCardByTitle(String cardTitle) {
    String xpath = String.format(
        "//yt-agile-card[@data-test='yt-agile-board-card']" +
            "[.//div[@data-test='yt-agile-board-card__summary']" +
            "//*[contains(@aria-label, '%s')]]", cardTitle
    );
    return $x(xpath);
  }

  public void clickAssignedUserOnCard(String cardTitle) {
    getIssueCardByTitle(cardTitle).$x(SELECTING_ASSIGNED_USER).click();
  }

  public void selectAssignedUserByLogin(String login) {
    String xpath = String.format(
        "//div[@data-test='ring-popup']" +
            "//span[@data-test='ring-list-item-custom ring-list-item-action ring-list-item']" +
            "[.//div[@data-test='ring-list-item-description' and text()='%s']]", login
    );
    SelenideElement popupItem = $x(xpath);
    popupItem.click();
  }

  public IssuesListPage goToIssues() {
    nav.clickIssues();
    return new IssuesListPage();
  }

  public AgileBoardPage goToAgile() {
    nav.clickAgileBoard();
    return this;
  }
}
