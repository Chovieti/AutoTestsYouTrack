package org.example.tests;

import com.codeborne.selenide.Condition;
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

public class GeneralTests {
  private static final String BASE_URL = TestConfig.getBaseUrl();
  private static final String USERNAME = TestConfig.getUsername();
  private static final String PASSWORD = TestConfig.getPassword();

  @BeforeEach
  public void setUp() {
    Configuration.browser = "chrome";
    Configuration.headless = false;
    Configuration.timeout = TestConfig.getTimeout();
    Configuration.screenshots = true;
    Configuration.savePageSource = true;
//    System.out.println(Thread.currentThread().getName());
//    System.out.println(System.getProperty("junit.jupiter.execution.parallel.enabled"));
//    System.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism");
//    System.out.println("Parallelism limit: " + System.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism"));
  }

  @AfterEach
  public void tearDown() {
    Selenide.closeWebDriver();
  }

  private String uniqueTitle(String base) {
    return base + " " + System.currentTimeMillis();
  }

  @Test
  @DisplayName("Проверка корректного логина")
  public void testSuccessfulLogin() {
    MainPage mainPage = new LoginPage()
        .openPage(BASE_URL)
        .waitForPageLoaded()
        .login(USERNAME, PASSWORD);
    mainPage.waitForPageLoaded();
  }

  @ParameterizedTest(name = "Проверка создания задачи с заголовком {0}")
  @CsvFileSource(resources = "/csv-data/create-issue-data.csv", numLinesToSkip = 0)
  public void testCreateIssue(String title, String description) {
    String realTitle = uniqueTitle(title);
    String id = IssueManager.createIssue(realTitle, description, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(realTitle).shouldBe(Condition.visible);

    IssueManager.deleteIssue(id, BASE_URL);
  }

  @Test
  @DisplayName("Проверка удаления задачи")
  public void testDeleteIssue() {
    String title = "Тестовая задача для удаления";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueManager.deleteIssue(id, BASE_URL);
    issuesPage.searchByTitle(title);
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.not(Condition.visible));
  }

  @ParameterizedTest(name = "Проверка изменения имени задачи {0} -> {1} и изменения описания")
  @CsvFileSource(resources = "/csv-data/edit-issue-data.csv", numLinesToSkip = 0)
  public void testEditIssue(String oldTitle, String newTitle, String newDescription) {
    String realTitle = uniqueTitle(oldTitle);
    String id = IssueManager.createIssue(realTitle, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(realTitle).shouldBe(Condition.visible);
    IssueDetailsPage issueDetails = issuesPage.openById(id);
    issueDetails.editIssue(newTitle, newDescription);
    IssueManager.deleteIssue(id, BASE_URL);
  }

  @Test
  @DisplayName("Проверка оставления комментария к задаче")
  public void testComments() {
    String title = "Тестовая задача для комментариев";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    String comment = "Some comment";
    issueDetailsPage.addComment(comment).findComment(comment);

    IssueManager.deleteIssue(id, BASE_URL);
  }

  @Test
  @DisplayName("Проверка взятия задачи в работу")
  public void testIssueTake() {
    String title = "Тестовая задача для взятия";
    String id = IssueManager.createIssue(title, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(title).shouldBe(Condition.visible);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    issueDetailsPage.choiceExecutor(USERNAME);

    IssueManager.deleteIssue(id, BASE_URL);
  }

  @ParameterizedTest(name = "Проверка изменения статус задачи на {0}")
  @CsvFileSource(resources = "/csv-data/issue-stage-data.csv", numLinesToSkip = 0)
  public void testIssueStageChange(String stage) {
    String title = "Тестовая задача для изменения";
    String realTitle = uniqueTitle(title);
    String id = IssueManager.createIssue(realTitle, BASE_URL, USERNAME, PASSWORD);

    MainPage mainPage = new MainPage()
        .openPage(BASE_URL)
        .waitForPageLoaded();
    IssuesListPage issuesPage = mainPage
        .goToIssues()
        .waitForPageLoaded();
    issuesPage.getIssueRowByTitle(realTitle).shouldBe(Condition.visible);
    IssueDetailsPage issueDetailsPage = issuesPage.openById(id);
    issueDetailsPage.choiceStage(stage);

    IssueManager.deleteIssue(id, BASE_URL);
  }
}
