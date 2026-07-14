package org.example.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.example.pages.LoginPage;
import org.example.pages.MainPage;
import org.example.pages.issues.IssueDetailsPage;
import org.example.pages.issues.IssuesListPage;
import org.example.util.IssueManager;
import org.example.util.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneralTests {
  private static final String BASE_URL = TestConfig.getBaseUrl();
  private static final String USERNAME = TestConfig.getUsername();
  private static final String PASSWORD = TestConfig.getPassword();

  private final List<String> createdIds = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    Configuration.browser = "chrome";
    Configuration.headless = false;
    Configuration.timeout = TestConfig.getTimeout();
    Configuration.screenshots = true;
    Configuration.savePageSource = false;

    new LoginPage()
        .openPage(BASE_URL)
        .waitForPageLoaded()
        .login(USERNAME, PASSWORD)
        .waitForPageLoaded();
  }

  @AfterEach
  public void tearDown() {
    try {
      for (String id : createdIds) {
        try {
          IssueManager.deleteIssue(id, BASE_URL);
        } catch (AssertionError e) {
          System.err.println("Не удалось удалить задачу " + id + ": " + e.getMessage());
        }
      }
    } finally {
      createdIds.clear();
      Selenide.closeWebDriver();
    }
  }

  private String uniqueTitle(String base) {
    return base + " - " + System.currentTimeMillis();
  }

  @Test
  @DisplayName("Проверка корректного логина")
  public void testSuccessfulLogin() {
    MainPage mainPage = new MainPage().waitForPageLoaded();
    assertThat(mainPage.loggedIn())
        .as("После входа не должна быть доступна кнопка перехода на окно авторизации")
        .isTrue();
  }

  @ParameterizedTest(name = "Проверка создания задачи с заголовком {0}")
  @DisplayName("Проверка создания задачи")
  @CsvFileSource(resources = "/csv-data/create-issue-data.csv", numLinesToSkip = 0)
  public void testCreateIssue(String title, String description) {
    String realTitle = uniqueTitle(title);

    String id = IssueManager.createIssue(realTitle, description);
    createdIds.add(id);

    IssuesListPage issuesPage = new MainPage()
        .goToIssues()
        .waitForPageLoaded();

    assertThat(issuesPage.checkIssueByTitleExists(realTitle))
        .as("Задача создалась и отображается в списке")
        .isTrue();
  }

  @Test
  @DisplayName("Проверка удаления задачи")
  public void testDeleteIssue() {
    String title = "Тестовая задача для удаления";
    String id = IssueManager.createIssue(title, null);
    createdIds.add(id);

    IssuesListPage issuesPage = new MainPage()
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.checkIssueByTitleExists(title);
    IssueManager.deleteIssue(id, BASE_URL);
    createdIds.remove(id);

    assertThat(issuesPage.checkIssueByTitleNotExists(title))
        .as("Задача удалена и не отображается")
        .isTrue();
  }

  @ParameterizedTest(name = "Проверка изменения имени задачи {0} -> {1} и изменения описания")
  @DisplayName("Проверка изменения имени/описания задачи")
  @CsvFileSource(resources = "/csv-data/edit-issue-data.csv", numLinesToSkip = 0)
  public void testEditIssue(String oldTitle, String newTitle, String newDescription) {
    String realTitle = uniqueTitle(oldTitle);
    String id = IssueManager.createIssue(realTitle, null);
    createdIds.add(id);

    IssuesListPage issuesPage = new MainPage()
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.checkIssueByTitleExists(realTitle);

    IssueDetailsPage issueDetails = issuesPage.openById(id).waitForPageLoaded();
    issueDetails.editIssue(newTitle, newDescription);

    assertThat(issueDetails.getTitleText())
        .as("Заголовок должен измениться на '%s'", newTitle)
        .isEqualTo(newTitle);
    assertThat(issueDetails.getDescriptionText())
        .as("Описание должно измениться на '%s'", newDescription)
        .isEqualTo(newDescription);
  }

  @Test
  @DisplayName("Проверка оставления комментария к задаче")
  public void testComments() {
    String title = "Тестовая задача для комментариев";
    String id = IssueManager.createIssue(title, null);
    createdIds.add(id);

    IssuesListPage issuesPage = new MainPage()
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.checkIssueByTitleExists(title);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id).waitForPageLoaded();
    String comment = "Some comment";
    issueDetailsPage.addComment(comment);

    assertThat(issueDetailsPage.waitForPageLoaded().checkCommentExists(comment))
        .as("Комментацрий '%s' должен отображаться на странице задачи", comment)
        .isTrue();
  }

  @Test
  @DisplayName("Проверка взятия задачи в работу")
  public void testIssueTake() {
    String title = "Тестовая задача для взятия";
    String id = IssueManager.createIssue(title, null);
    createdIds.add(id);

    IssuesListPage issuesPage = new MainPage()
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.checkIssueByTitleExists(title);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    issueDetailsPage.choiceExecutor(USERNAME);

    assertThat(issueDetailsPage.getExecutorText().contains(USERNAME))
        .as("Исполнителем должен быть '%s'", USERNAME)
        .isTrue();
  }

  @ParameterizedTest(name = "Проверка изменения статус задачи на {0}")
  @DisplayName("Проверка изменения статуса задачи")
  @CsvFileSource(resources = "/csv-data/issue-stage-data.csv", numLinesToSkip = 0)
  public void testIssueStageChange(String stage) {
    String title = "Тестовая задача для изменения";
    String realTitle = uniqueTitle(title);
    String id = IssueManager.createIssue(realTitle, null);
    createdIds.add(id);

    IssuesListPage issuesPage = new MainPage()
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.checkIssueByTitleExists(realTitle);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    issueDetailsPage.choiceStage(stage);

    assertThat(issueDetailsPage.getStageText())
        .as("Статус должен измениться на '%s'", stage)
        .isEqualTo(stage);
  }
}
